package io.nuls.api.task;

import io.nuls.api.cache.ApiCache;
import io.nuls.api.db.BlockService;
import io.nuls.api.db.StatisticalService;
import io.nuls.api.manager.CacheManager;
import io.nuls.api.model.po.BlockHeaderInfo;
import io.nuls.api.model.po.ChainStatisticalInfo;
import io.nuls.core.core.ioc.SpringLiteContext;
import io.nuls.core.log.Log;

import java.math.BigInteger;

public class StatisticalRewardTask implements Runnable {

    private int chainId;

    private BlockService blockService;

    private StatisticalService statisticalService;

    public StatisticalRewardTask(int chainId) {
        this.chainId = chainId;
        blockService = SpringLiteContext.getBean(BlockService.class);
        statisticalService = SpringLiteContext.getBean(StatisticalService.class);
    }

    @Override
    public void run() {
        try {
            BigInteger reward = blockService.getLast24HourRewards(chainId);
            ApiCache apiCache = CacheManager.getCache(chainId);
            if (apiCache != null) {
                apiCache.getCoinContextInfo().setDailyReward(reward);
            }
            //Query the current latestblockheight
            BlockHeaderInfo headerInfo = blockService.getBestBlockHeader(chainId);
            if (headerInfo == null) {
                return;
            }
            ChainStatisticalInfo statisticalInfo = statisticalService.getChainStatisticalInfo(chainId);
            if (statisticalInfo == null) {
                statisticalInfo = new ChainStatisticalInfo();
                statisticalInfo.setChainId(chainId);
                statisticalInfo.setLastStatisticalHeight(0);
            }
            //Counting the number of transactions in packaged blocks
            //Obtain the block height as of the last statistical deadline, obtain the latest block height, and accumulate the transaction quantity of all blocks between them
            //exceed1000After data entry, count each iteration1000strip
            long startHeight = statisticalInfo.getLastStatisticalHeight();
            long endHeight = headerInfo.getHeight();
            while (endHeight - startHeight > 1000) {
                long count = blockService.getBlockPackageTxCount(chainId, startHeight, startHeight + 1000);
                statisticalInfo.setLastStatisticalHeight(startHeight + 1000);
                statisticalInfo.setTxCount(statisticalInfo.getTxCount() + count);
                statisticalService.saveChainStatisticalInfo(statisticalInfo);
                apiCache.getCoinContextInfo().setTxCount(statisticalInfo.getTxCount());
                startHeight += 1000;
                Thread.sleep(100);
//                LoggerUtil.commonLog.info("chain statistical info calc......");
            }
            long count = blockService.getBlockPackageTxCount(chainId, startHeight, endHeight);
            statisticalInfo.setLastStatisticalHeight(endHeight);
            statisticalInfo.setTxCount(statisticalInfo.getTxCount() + count);
            statisticalService.saveChainStatisticalInfo(statisticalInfo);

            apiCache.getCoinContextInfo().setTxCount(statisticalInfo.getTxCount());
        } catch (Exception e) {
            Log.error(e);
        }
    }
}
