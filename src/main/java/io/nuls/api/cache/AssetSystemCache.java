package io.nuls.api.cache;

import io.nuls.api.model.dto.AssetsSystemTokenInfoVo;
import io.nuls.api.model.dto.NerveChainVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetSystemCache {

    private static final Map<String, AssetsSystemTokenInfoVo> assetMap = new HashMap();
    private static final Map<Long, NerveChainVo> chainMap = new HashMap();

    public static void putCache(String assetKey, AssetsSystemTokenInfoVo vo) {
        assetMap.put(assetKey, vo);
    }

    public static void putCacheList(List<AssetsSystemTokenInfoVo> voList) {
        voList.forEach(vo -> putCache(vo.getAssetKey(), vo));
    }
    public static void putChainList(List<NerveChainVo> voList) {
        voList.forEach(vo -> chainMap.put(vo.getId(),vo));
        chainMap.put(1L,new NerveChainVo("NULS"));
        chainMap.put(9L,new NerveChainVo("Nerve"));
        chainMap.put(-1L,new NerveChainVo("NULS"));
        chainMap.put(-2L,new NerveChainVo("Nerve"));
        chainMap.put(2L,new NerveChainVo("NULS-Testnet"));
        chainMap.put(5L,new NerveChainVo("NERVE-Testnet"));
        chainMap.put(108L,new NerveChainVo("Tron"));
    }
    public static AssetsSystemTokenInfoVo getAssetCache(String assetKey) {
        return assetMap.get(assetKey);
    }

    public static boolean isCached(){
        return !assetMap.isEmpty();
    }

    public static NerveChainVo getChain(Long id){
        return chainMap.get(id);
    }
}
