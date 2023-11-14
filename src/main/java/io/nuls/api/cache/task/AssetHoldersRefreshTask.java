package io.nuls.api.cache.task;

import io.nuls.api.db.ChainAssetService;
import io.nuls.api.utils.LoggerUtil;
import io.nuls.core.core.ioc.SpringLiteContext;

public class AssetHoldersRefreshTask implements Runnable {

    private final int chainId;
    private ChainAssetService service;

    public AssetHoldersRefreshTask(int chainId) {
        this.chainId = chainId;
    }

    @Override
    public void run() {
        try {
            if (null == service) {
                service = SpringLiteContext.getBean(ChainAssetService.class);
            }
            service.updateHolderCount(chainId);
        } catch (Exception e) {
            LoggerUtil.commonLog.error(e);
        }
    }
}
