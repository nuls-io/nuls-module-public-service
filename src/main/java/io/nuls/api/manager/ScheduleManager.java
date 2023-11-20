package io.nuls.api.manager;

import io.nuls.api.ApiContext;
import io.nuls.api.cache.task.AssetHoldersRefreshTask;
import io.nuls.api.cache.task.AssetLoadTask;
import io.nuls.api.cache.task.AssetSystemCacheTask;
import io.nuls.api.db.mongo.MongoAccountLedgerServiceImpl;
import io.nuls.api.db.mongo.MongoAgentServiceImpl;
import io.nuls.api.task.*;
import io.nuls.api.utils.LoggerUtil;
import io.nuls.core.core.annotation.Component;
import io.nuls.core.core.ioc.SpringLiteContext;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class ScheduleManager {

    public void start() {
        LoggerUtil.commonLog.info("init tasks......");
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(9);
        executorService.scheduleAtFixedRate(new DeleteTxsTask(ApiContext.defaultChainId), 2, 60, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(new QueryChainInfoTask(ApiContext.defaultChainId), 2, 60, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(new SyncBlockTask(ApiContext.defaultChainId), 5, 10, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(new StatisticalNulsTask(ApiContext.defaultChainId), 1, 10, TimeUnit.MINUTES);
        executorService.scheduleAtFixedRate(new StatisticalTask(ApiContext.defaultChainId), 1, 60, TimeUnit.MINUTES);
        executorService.scheduleAtFixedRate(new UnConfirmTxTask(ApiContext.defaultChainId), 1, 2, TimeUnit.MINUTES);
        executorService.scheduleAtFixedRate(new StatisticalRewardTask(ApiContext.defaultChainId), 1, 60, TimeUnit.MINUTES);
        executorService.scheduleAtFixedRate(new GetGlobalInfoTask(ApiContext.defaultChainId), 5, 10, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(new LastDayRewardStatTask(ApiContext.defaultChainId), 0, 1, TimeUnit.HOURS);

        MongoAgentServiceImpl mongoAgentService = SpringLiteContext.getBean(MongoAgentServiceImpl.class);
        MongoAccountLedgerServiceImpl accountLedgerService = SpringLiteContext.getBean(MongoAccountLedgerServiceImpl.class);
        executorService.scheduleAtFixedRate(new RefreshCacheTask(ApiContext.defaultChainId, mongoAgentService, accountLedgerService), 10, 10, TimeUnit.MINUTES);


        executorService.scheduleAtFixedRate(new AssetSystemCacheTask(), 1, 600, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(new AssetHoldersRefreshTask(ApiContext.defaultChainId), 1, 300, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(new AssetLoadTask(), 10, 300, TimeUnit.SECONDS);


        LoggerUtil.commonLog.info("init tasks finished");
    }
}
