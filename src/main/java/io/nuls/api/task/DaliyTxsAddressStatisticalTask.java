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

import java.util.*;
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
    private static boolean bestExist = false;

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
            String date = getDate(blockTime);
            long endHeight = block.getHeader().getHeight() - 1;
            LoggerUtil.commonLog.info("To save : {}, {}, {}-{}", date, block.getHeader().getHeight(), currentDayIndex, blockDayIndex);
            saveActiveAccount(date, endHeight);
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
    }

    private void saveActiveAccount(String date, long endHeight) {
        ActiveAddressPo po = new ActiveAddressPo();
        po.setCount(currentAddrSet.size());
        po.setDate(date);
        po.setDayIndex(currentDayIndex);
        po.setEndHeight(endHeight);
        this.dbService.insertOne(DBTableConstant.ACTIVE_ADDRESS_TABLE, DocumentTransferTool.toDocument(po, "date"));
        po.setDate(bestKey);
        if (bestExist) {
            this.dbService.updateOne(DBTableConstant.ACTIVE_ADDRESS_TABLE, Filters.eq("_id", bestKey), DocumentTransferTool.toDocument(po, "date"));
        } else {
            this.dbService.insertOne(DBTableConstant.ACTIVE_ADDRESS_TABLE, DocumentTransferTool.toDocument(po, "date"));
            bestExist = true;
        }
        LoggerUtil.commonLog.info("save aa data : {}", date);
    }

    private int getDayIndex(long blockTime) {
        return (int) (blockTime / (24 * 3600));
    }

    private Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

    private String getDate(long blockTime) {
        calendar.setTime(new Date(blockTime * 1000));
        calendar.add(Calendar.DATE,-1);
        int m = calendar.get(Calendar.MONTH) + 1;
        String month = m + "";
        if (m < 10) {
            month = 0 + month;
        }
        int d = calendar.get(Calendar.DATE);
        String day = d + "";
        if (d < 10) {
            day = 0 + day;
        }
        return calendar.get(Calendar.YEAR) + "-" + month + "-" + day;
//        return DateUtils.convertDate(calendar.getTime(), "yyyy-MM-dd");
    }

    public static void main(String[] args) {
        DaliyTxsAddressStatisticalTask task = new DaliyTxsAddressStatisticalTask();
        long blockTime = 1695395320;
        long blockTime1 = blockTime-10;
        System.out.println(task.getDate(blockTime));
        System.out.println(task.getDate(blockTime1));
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
                bestExist = true;
            }
//        完成从endHeight到当前高度的统计
            LoggerUtil.commonLog.info("Start active address statistical: {}", startHeight);
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
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return download(height);
        }
        return result.getData();
    }
}
