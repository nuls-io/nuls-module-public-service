package io.nuls.api.task;

import io.nuls.api.ApiContext;
import io.nuls.api.analysis.WalletRpcHandler;
import io.nuls.api.cache.AssetSystemCache;
import io.nuls.api.model.po.BlockHeaderInfo;
import io.nuls.api.model.po.BlockInfo;
import io.nuls.api.model.po.SyncInfo;
import io.nuls.api.service.RollbackService;
import io.nuls.api.service.SyncService;
import io.nuls.api.utils.LoggerUtil;
import io.nuls.core.basic.Result;
import io.nuls.core.core.ioc.SpringLiteContext;
import io.nuls.core.log.Log;

public class SyncBlockTask implements Runnable {

    private int chainId;

    private boolean running;

    private SyncService syncService;

    private RollbackService rollbackService;
    //记录同步出错次数
    private int syncErrorCount = 0;
    private boolean first = true;

    public SyncBlockTask(int chainId) {
        this.chainId = chainId;
        syncService = SpringLiteContext.getBean(SyncService.class);
        rollbackService = SpringLiteContext.getBean(RollbackService.class);
        LoggerUtil.commonLog.info("------SyncBlockTask init:" + chainId);
    }

    @Override
    public void run() {
        if (running) {
            return;
        }
        try {
            running = true;
            process();
        } finally {
            running = false;
        }
    }


    private void process() {
        if (syncErrorCount >= 10) {
            LoggerUtil.commonLog.info("------- syncErrorCount > 10,  sync block stop --------");
            return;
        }
        if (!ApiContext.isReady) {
            LoggerUtil.commonLog.info("------- ApiModule wait for successful cross-chain networking  --------");
            return;
        }
        //每次同步数据前都查看一下最新的同步信息，如果最新块的数据并没有在一次事务中完全处理，需要对区块数据进行回滚
        //Check the latest synchronization information before each entity synchronization.
        //If the latest block entity is not completely processed in one transaction, you need to roll back the block entity.
        try {
            SyncInfo syncInfo = syncService.getSyncInfo(chainId);
            if (syncInfo != null && !syncInfo.isFinish()) {
                rollbackService.rollbackBlock(chainId, syncInfo.getBestHeight());
            }
        } catch (Exception e) {
            syncErrorCount++;
            Log.error(e);
            return;
        }

        boolean syncable = true;
        while (syncable) {
            try {
                syncable = syncBlock();
            } catch (Exception e) {
                Log.error(e.getMessage(), e);
                syncErrorCount++;
                syncable = false;
            }
        }
    }

    /**
     * 同步逻辑
     * 1.Take the record of the latest block saved from the local
     * 2.According to the height of the latest local block, to synchronize the next block of the wallet (local does not start from the 0th block)
     * 3.After syncing to the latest block, the task ends, waiting for the next 10 seconds, resynchronizing
     * 4.Each synchronization needs to be verified with the previous one. If the verification fails, it means local fork and needs to be rolled back.
     * <p>
     * 1. 从本地取出已保存的最新块的记录
     * 2. 根据本地最新块的高度，去同步钱包的下一个块（本地没有则从第0块开始）
     * 3. 同步到最新块后，任务结束，等待下个10秒，重新同步
     * 4. 每次同步都需要和上一块做连续性验证，如果验证失败，说明本地分叉，需要做回滚处理
     *
     * @return boolean 是否还继续同步
     */
    private boolean syncBlock() {
        ApiContext.locker.lock();
        try {
            BlockHeaderInfo localBestBlockHeader = syncService.getBestBlockHeader(chainId);
            return process(localBestBlockHeader);
        } catch (Exception e) {
            LoggerUtil.commonLog.error(e);
            return false;
        } finally {
            ApiContext.locker.unlock();
        }
    }

    private boolean process(BlockHeaderInfo localBestBlockHeader) throws Exception {

        while (!AssetSystemCache.isCached()) {
            Thread.sleep(1000L);
        }

        long nextHeight = 0;
        if (localBestBlockHeader != null) {
            nextHeight = localBestBlockHeader.getHeight() + 1;
        }
        if (first) {
            LoggerUtil.commonLog.info("------localBestBlock:" + (nextHeight - 1));
            first = false;
        }
        Result<BlockInfo> result = WalletRpcHandler.getBlockInfo(chainId, nextHeight);
        if (result.isFailed()) {
            LoggerUtil.commonLog.info("------get block info failed: {},{}", chainId, nextHeight);
            return false;
        }
        BlockInfo newBlock = result.getData();
        if (null == newBlock) {
            Thread.sleep(5000L);
//            LoggerUtil.commonLog.info("------block info is null: {},{}", chainId, nextHeight);
            return false;
        }
        if (checkBlockContinuity(localBestBlockHeader, newBlock.getHeader())) {
            return syncService.syncNewBlock(chainId, newBlock);
        } else if (localBestBlockHeader != null) {
            return rollbackService.rollbackBlock(chainId, localBestBlockHeader.getHeight());
        }
        return false;
    }

    /**
     * 区块连续性验证
     * Block continuity verification
     *
     * @param localBest
     * @param newest
     * @return
     */
    private boolean checkBlockContinuity(BlockHeaderInfo localBest, BlockHeaderInfo newest) {
//        return false;
        if (localBest == null) {
            if (newest.getHeight() == 0) {
                return true;
            } else {
                return false;
            }
        } else {
            if (newest.getHeight() == localBest.getHeight() + 1) {
                if (newest.getPreHash().equals(localBest.getHash())) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }
}
