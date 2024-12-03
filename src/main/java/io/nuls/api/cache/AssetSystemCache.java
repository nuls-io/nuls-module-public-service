package io.nuls.api.cache;

import io.nuls.api.model.dto.AssetSystemDictItem;
import io.nuls.api.model.dto.AssetsSystemTokenInfoVo;
import io.nuls.api.model.dto.NerveChainVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetSystemCache {

    private static final Map<String, AssetsSystemTokenInfoVo> assetMap = new HashMap();
    private static final Map<Long, NerveChainVo> chainMap = new HashMap();
    private static final Map<String, String> addressTagMap = new HashMap();

    public static void putCache(String assetKey, AssetsSystemTokenInfoVo vo) {
        assetMap.put(assetKey, vo);
    }

    public static void putCacheList(List<AssetsSystemTokenInfoVo> voList) {
        voList.forEach(vo -> putCache(vo.getAssetKey(), vo));
    }

    public static void putChainList(List<NerveChainVo> voList) {
        voList.forEach(vo -> chainMap.put(vo.getId(), vo));
        chainMap.put(1L, new NerveChainVo("NULS","https://nuls-cf.oss-us-west-1.aliyuncs.com/icon/NULS.png"));
        chainMap.put(9L, new NerveChainVo("Nerve","https://nerve.network/img/NERVE.0a64fa4f.png"));
        chainMap.put(-1L, new NerveChainVo("NULS","https://nuls-cf.oss-us-west-1.aliyuncs.com/icon/NULS.png"));
        chainMap.put(-2L, new NerveChainVo("Nerve","https://nerve.network/img/NERVE.0a64fa4f.png"));
        chainMap.put(2L, new NerveChainVo("NULS-Testnet","https://nuls-cf.oss-us-west-1.aliyuncs.com/icon/NULS.png"));
        chainMap.put(5L, new NerveChainVo("NERVE-Testnet","https://nerve.network/img/NERVE.0a64fa4f.png"));
        chainMap.put(108L, new NerveChainVo("Tron","http://nassets.oss-us-west-1.aliyuncs.com/TRX_9.png"));
    }

    public static AssetsSystemTokenInfoVo getAssetCache(String assetKey) {
        return assetMap.get(assetKey);
    }

    public static boolean isCached() {
        return !assetMap.isEmpty();
    }

    public static NerveChainVo getChain(Long id) {
        return chainMap.get(id);
    }

    public static void putAddressTag(List<AssetSystemDictItem> dictionary) {
        dictionary.forEach(v -> {
            addressTagMap.put(v.getDictLabel(), v.getDictValue());
        });
    }

    public static String getAddressTag(String address) {
        return addressTagMap.get(address);
    }

    public static AssetsSystemTokenInfoVo getAssetCacheByContract(String contractAddress) {
        for (AssetsSystemTokenInfoVo vo : assetMap.values()) {
            if (contractAddress.equals(vo.getContractAddress())) {
                return vo;
            }
        }
        return null;
    }
}
