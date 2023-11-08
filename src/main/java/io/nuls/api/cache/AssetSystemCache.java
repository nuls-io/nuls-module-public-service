package io.nuls.api.cache;

import io.nuls.api.model.dto.AssetsSystemTokenInfoVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetSystemCache {

    private static final Map<String, AssetsSystemTokenInfoVo> assetMap = new HashMap();

    public static void putCache(String assetKey, AssetsSystemTokenInfoVo vo) {
        assetMap.put(assetKey, vo);
    }

    public static void putCacheList(List<AssetsSystemTokenInfoVo> voList) {
        voList.forEach(vo -> putCache(vo.getAssetKey(), vo));
    }

    public static AssetsSystemTokenInfoVo getAssetCache(String assetKey) {
        return assetMap.get(assetKey);
    }

    public static boolean isCached(){
        return !assetMap.isEmpty();
    }

}
