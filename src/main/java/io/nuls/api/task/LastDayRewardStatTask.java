package io.nuls.api.task;

import io.nuls.api.ApiContext;
import io.nuls.api.constant.DBTableConstant;
import io.nuls.api.db.AccountService;
import io.nuls.api.db.BlockService;
import io.nuls.api.db.LastDayRewardStatService;
import io.nuls.api.db.mongo.MongoLastDayRewardStatServiceImpl;
import io.nuls.api.model.po.BlockHeaderInfo;
import io.nuls.api.model.po.LastDayRewardStatInfo;
import io.nuls.core.core.ioc.SpringLiteContext;
import io.nuls.core.log.Log;
import io.nuls.core.model.DateUtils;

public class LastDayRewardStatTask implements Runnable {

    private int chainId;

    private LastDayRewardStatService lastDayRewardStatService;

    private BlockService blockService;

    private AccountService accountService;

    public LastDayRewardStatTask(int chainId) {
        this.chainId = chainId;
        lastDayRewardStatService = SpringLiteContext.getBean(MongoLastDayRewardStatServiceImpl.class);
        blockService = SpringLiteContext.getBean(BlockService.class);
        accountService = SpringLiteContext.getBean(AccountService.class);
    }

    @Override
    public void run() {
        ApiContext.locker.lock();
        try {
            LastDayRewardStatInfo statInfo = lastDayRewardStatService.getInfo(chainId);
            if (statInfo == null) {
                statInfo = new LastDayRewardStatInfo();
                statInfo.setLastDayRewardKey(DBTableConstant.LastDayRewardKey);
                lastDayRewardStatService.save(chainId, statInfo);
            }
            BlockHeaderInfo headerInfo = blockService.getBestBlockHeader(chainId);
            if (headerInfo == null) {
                return;
            }
            //Judging that the current block time differs by one day from the last statistical block time and date
            if (headerInfo.getCreateTime() - statInfo.getLastStatTime() < DateUtils.DATE_TIME / 1000) {
                return;
            }
            //It has been over a day, and all accounts have been converted from yesterday's earnings to today's earnings. After clearing today's earnings, they will be reacquired
            accountService.updateAllAccountLastReward(chainId);

            statInfo.setLastStatHeight(headerInfo.getHeight());
            statInfo.setLastStatTime(headerInfo.getCreateTime());
            lastDayRewardStatService.update(chainId, statInfo);
        } catch (Exception e) {
            Log.error("------Statistics of abnormal returns from yesterday-----", e);
        } finally {
            ApiContext.locker.unlock();
        }
    }
}
