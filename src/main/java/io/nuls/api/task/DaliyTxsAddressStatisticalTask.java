/*
 * MIT License
 * Copyright (c) 2017-2019 nuls.io
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.nuls.api.task;

import com.mongodb.client.model.Filters;
import io.nuls.api.analysis.WalletRpcHandler;
import io.nuls.api.constant.DBTableConstant;
import io.nuls.api.constant.PublicServiceConstant;
import io.nuls.api.db.BlockService;
import io.nuls.api.db.mongo.MongoDBService;
import io.nuls.api.exception.JsonRpcException;
import io.nuls.api.model.po.*;
import io.nuls.api.utils.DocumentTransferTool;
import io.nuls.api.utils.LoggerUtil;
import io.nuls.core.basic.InitializingBean;
import io.nuls.core.basic.Result;
import io.nuls.core.core.annotation.Autowired;
import io.nuls.core.core.annotation.Component;
import io.nuls.core.exception.NulsException;
import io.nuls.core.model.DateUtils;
import org.bson.Document;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Niels
 */
@Component
public class DaliyTxsAddressStatisticalTask implements Runnable, InitializingBean {

    private static final BlockingQueue<BlockInfo> blockInfoQueue = new LinkedBlockingQueue<>();
    private long lastHeight;

    public static final void offer(BlockInfo info) {
        blockInfoQueue.offer(info);
    }

    private static final String bestKey = "latest";

    private static int currentDayIndex;
    private static Set<String> currentAddrSet = new HashSet<>();
    @Autowired
    private MongoDBService dbService;
    @Autowired
    private BlockService blockService;


    @Override
    public void run() {
        while (true) {
            try {
                BlockInfo info = blockInfoQueue.take();
                //判断是不是另一天了，如果是，则统计前一天数据，并存储
                execute(info);
            } catch (Exception e) {
                LoggerUtil.commonLog.error(e);
            }
        }
    }

    private void execute(BlockInfo block) {
        if (lastHeight >= block.getHeader().getHeight()) {
            return;
        }
        long blockTime = block.getHeader().getCreateTime();
        int blockDayIndex = getDayIndex(blockTime);
        if (0 == currentDayIndex) {
            currentDayIndex = blockDayIndex;
        } else if (blockDayIndex > currentDayIndex) {
            saveActiveAccount(getDate(blockTime - 10), block.getHeader().getHeight() - 1);
            currentDayIndex = blockDayIndex;
            currentAddrSet.clear();
        } else if (blockDayIndex < currentDayIndex) {
            throw new RuntimeException("Data error: statistical day index wrong.");
        }
        for (TransactionInfo tx : block.getTxList()) {
            if (null != tx.getCoinFroms()) {
                for (CoinFromInfo from : tx.getCoinFroms()) {
                    currentAddrSet.add(from.getAddress());
                }
            }
            if (null != tx.getCoinTos()) {
                for (CoinToInfo to : tx.getCoinTos()) {
                    currentAddrSet.add(to.getAddress());
                }
            }
        }
        lastHeight = block.getHeader().getHeight();
        LoggerUtil.commonLog.info("exec block : {}", lastHeight);
    }

    private void saveActiveAccount(String date, long endHeight) {
        ActiveAddressPo po = new ActiveAddressPo();
        po.setCount(currentAddrSet.size());
        po.setDate(date);
        po.setDayIndex(currentDayIndex);
        po.setEndHeight(endHeight);
        this.dbService.insertOne(DBTableConstant.ACTIVE_ADDRESS_TABLE, DocumentTransferTool.toDocument(po, "date"));
        LoggerUtil.commonLog.info("save aa data : {}", po.getDate());
    }

    private int getDayIndex(long blockTime) {
        return (int) (blockTime / (24 * 3600));
    }

    private String getDate(long blockTime) {
        return DateUtils.convertDate(new Date(blockTime * 1000), "yyyy-MM-dd");
    }

    public static void main(String[] args) {
        long blockTime = System.currentTimeMillis() / 1000;
        long blockTime1 = blockTime + 10;
        System.out.println(new DaliyTxsAddressStatisticalTask().getDayIndex(blockTime));
        System.out.println(new DaliyTxsAddressStatisticalTask().getDayIndex(blockTime1));
    }

    @Override
    public void afterPropertiesSet() throws NulsException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    realAfterPropertiesSet();
                } catch (NulsException e) {
                    LoggerUtil.commonLog.error(e);
                }
            }
        }).start();
    }

    public void realAfterPropertiesSet() throws NulsException {
        //启动时检查1、历史数据，2、当前高度
        BlockHeaderInfo header = blockService.getBestBlockHeader(PublicServiceConstant.defaultChainId);
        while (header == null) {
            try {
                Thread.sleep(10000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            header = blockService.getBestBlockHeader(PublicServiceConstant.defaultChainId);
        }
        LoggerUtil.commonLog.info("Best block :{} -- {}", PublicServiceConstant.defaultChainId, header.getHeight());
        Document doc = null;
        try {
            doc = dbService.findOne("active_address", Filters.eq("_id", bestKey));
        } catch (Exception e) {
            LoggerUtil.commonLog.error(e);
        }
        long startHeight;
        if (null != header) {
            if (null == doc) {
//            扫描最近一个月的数据并插入数据库
                long val = header.getHeight() - 32 * 8640;//多算两天
                startHeight = val <= 0 ? 0 : val;
            } else {
                startHeight = doc.getLong("endHeight") + 1;
            }
//        完成从endHeight到当前高度的统计
            for (long i = startHeight; i <= header.getHeight(); i++) {
                try {
                    execute(download(i));
                } catch (Exception e) {
                    LoggerUtil.commonLog.error(e);
                }
            }
        }
        new Thread(this).start();
    }

    private BlockInfo download(long height) {
        Result<BlockInfo> result = WalletRpcHandler.getBlockInfo(PublicServiceConstant.defaultChainId, height);
        if (result.isFailed()) {
            throw new JsonRpcException(result.getErrorCode());
        }
        LoggerUtil.commonLog.info("download block : {}", height);
        return result.getData();
    }
}
