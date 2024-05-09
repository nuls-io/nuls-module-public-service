package io.nuls.api.task;

import io.nuls.api.ApiContext;
import io.nuls.api.db.mongo.MongoAccountLedgerServiceImpl;
import io.nuls.api.db.mongo.MongoAgentServiceImpl;
import io.nuls.api.model.po.AgentInfo;
import io.nuls.api.model.po.PageInfo;
import io.nuls.api.model.po.mini.MiniAccountInfo;
import io.nuls.api.utils.LoggerUtil;

/**
 * Refresh homepage cachetask
 */
public class RefreshCacheTask implements Runnable {

    private int chainId;

    private MongoAgentServiceImpl agentService;

    private MongoAccountLedgerServiceImpl accountLedgerService;

    public RefreshCacheTask(int chainId, MongoAgentServiceImpl agentService, MongoAccountLedgerServiceImpl accountLedgerService) {
        this.chainId = chainId;
        this.agentService = agentService;
        this.accountLedgerService = accountLedgerService;
    }

    @Override
    public void run() {
        try {
            PageInfo<AgentInfo> agentPageInfo = agentService.getAgentList(chainId, 0, 1, 200);
            ApiContext.agentPageInfo = agentPageInfo;

            PageInfo<MiniAccountInfo> miniAccountPageInfo = accountLedgerService.getAssetRanking(chainId, chainId, 1, 1, 15);
            ApiContext.miniAccountPageInfo = miniAccountPageInfo;
        } catch (Exception e) {
            LoggerUtil.commonLog.error(e);
        }
    }
}
