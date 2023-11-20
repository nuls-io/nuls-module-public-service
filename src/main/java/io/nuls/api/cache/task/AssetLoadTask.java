package io.nuls.api.cache.task;

import io.nuls.api.cache.AssetSystemCache;
import io.nuls.api.cache.ChainAssetCache;
import io.nuls.api.db.ChainAssetService;
import io.nuls.api.model.dto.AssetsSystemTokenInfoVo;
import io.nuls.api.model.po.asset.ChainAssetInfo;
import io.nuls.core.core.ioc.SpringLiteContext;
import io.nuls.core.log.Log;

import java.util.Collection;

public class AssetLoadTask implements Runnable {

    private ChainAssetService service;

    @Override
    public void run() {
        try {
            if (service == null) {
                service = SpringLiteContext.getBean(ChainAssetService.class);
            }
            ChainAssetCache.initCache(service.getList());
            while (!AssetSystemCache.isCached()) {
                Thread.sleep(5000L);
            }
            updateAssetsInfo();
        } catch (Exception e) {
            Log.error("", e);
        }
    }

    private void updateAssetsInfo() {

        Collection<ChainAssetInfo> collection = ChainAssetCache.getAssetInfoList();
        for (ChainAssetInfo info : collection) {
            AssetsSystemTokenInfoVo vo = AssetSystemCache.getAssetCache(info.getId());
            if (null == vo) {
                continue;
            }
            info.setTotalSupply(vo.getTotalSupply());
            info.setWebsite(vo.getWebSite());
            info.setCommunity(vo.getCommunity());
            this.service.updateAssetInfo(info);
        }
    }

}
