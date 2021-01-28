package io.nuls.api.task;

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
//        try {
//            LastDayRewardStatInfo statInfo = lastDayRewardStatService.getInfo(chainId);
//            if (statInfo == null) {
//                statInfo = new LastDayRewardStatInfo();
//                statInfo.setLastDayRewardKey(DBTableConstant.LastDayRewardKey);
//                lastDayRewardStatService.save(chainId, statInfo);
//            }
//            BlockHeaderInfo headerInfo = blockService.getBestBlockHeader(chainId);
//            if (headerInfo == null) {
//                return;
//            }
//            //判断当前块时间和上次统计块时间日期相差了一天
//            if (headerInfo.getCreateTime() - statInfo.getLastStatTime() < DateUtils.DATE_TIME / 1000) {
//                return;
//            }
//            //已超过一天，将所有账户的昨日收益转变为今日收益，今日收益清空后，重新获取
//            accountService.updateAllAccountLastReward(chainId);
//
//            statInfo.setLastStatHeight(headerInfo.getHeight());
//            statInfo.setLastStatTime(headerInfo.getCreateTime());
//            lastDayRewardStatService.update(chainId, statInfo);
//        } catch (Exception e) {
//            Log.error("------统计昨日收益异常-----", e);
//        }
    }
}
