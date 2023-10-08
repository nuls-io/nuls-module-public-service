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
import io.nuls.api.db.AgentService;
import io.nuls.api.db.BlockService;
import io.nuls.api.db.DepositService;
import io.nuls.api.db.StatisticalService;
import io.nuls.api.db.mongo.*;
import io.nuls.api.exception.JsonRpcException;
import io.nuls.api.model.po.*;
import io.nuls.api.utils.DocumentTransferTool;
import io.nuls.api.utils.LoggerUtil;
import io.nuls.core.basic.InitializingBean;
import io.nuls.core.basic.Result;
import io.nuls.core.core.annotation.Autowired;
import io.nuls.core.core.annotation.Component;
import io.nuls.core.core.ioc.SpringLiteContext;
import io.nuls.core.exception.NulsException;
import io.nuls.core.model.DateUtils;
import io.nuls.core.model.DoubleUtils;
import org.bson.Document;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    private static int currentDayIndex;
    private static String currentDate;
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
        String date = getDate(blockTime);
        int blockDayIndex = getDayIndex(blockTime);
        if (0 == currentDayIndex) {
            currentDayIndex = blockDayIndex;
            currentDate = date;
        } else if (blockDayIndex > currentDayIndex) {
            saveActiveAccount(block.getHeader().getHeight() - 1);
            currentDayIndex = blockDayIndex;
            currentDate = date;
            currentAddrSet.clear();
        } else if (blockDayIndex < currentDayIndex) {
            throw new RuntimeException("Data error: statistical day index wrong.");
        }
        for (TransactionInfo tx : block.getTxList()) {
            for (CoinFromInfo from : tx.getCoinFroms()) {
                currentAddrSet.add(from.getAddress());
            }
            for (CoinToInfo to : tx.getCoinTos()) {
                currentAddrSet.add(to.getAddress());
            }
        }
        lastHeight = block.getHeader().getHeight();
    }

    private void saveActiveAccount(long endHeight) {
        ActiveAddressPo po = new ActiveAddressPo();
        po.setCount(currentAddrSet.size());
        po.setDate(currentDate);
        po.setDayIndex(currentDayIndex);
        po.setEndHeight(endHeight);
        this.dbService.insertOne(DBTableConstant.ACTIVE_ADDRESS_TABLE, DocumentTransferTool.toDocument(po, "date"));
    }

    private int getDayIndex(long blockTime) {
        return (int) (blockTime / 24 * 3600);
    }

    private String getDate(long blockTime) {
        return DateUtils.convertDate(new Date(blockTime), "yyyy-MM-dd");
    }


    @Override
    public void afterPropertiesSet() throws NulsException {
        //启动时检查1、历史数据，2、当前高度
        Document doc = dbService.findOne("active_address", Filters.eq("_id", bestKey));
        BlockHeaderInfo header = blockService.getBestBlockHeader(PublicServiceConstant.defaultChainId);
        long startHeight;
        if (null != header) {
            if (null == doc) {
//            扫描最近一个月的数据并插入数据库
                long val = header.getHeight() - 32 * 43200;//多算两天
                startHeight = val <= 0 ? 0 : val;
            } else {
                startHeight = doc.getLong("endHeight") + 1;
            }
//        完成从endHeight到当前高度的统计
            for (long i = startHeight; i <= header.getHeight(); i++) {
                execute(download(i));
            }
        }
        new Thread(this).start();
    }

    private BlockInfo download(long height) {
        Result<BlockInfo> result = WalletRpcHandler.getBlockInfo(PublicServiceConstant.defaultChainId, height);
        if (result.isFailed()) {
            throw new JsonRpcException(result.getErrorCode());
        }
        return result.getData();
    }
}
