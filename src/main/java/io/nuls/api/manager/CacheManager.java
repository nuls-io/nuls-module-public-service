package io.nuls.api.manager;

import io.nuls.api.ApiContext;
import io.nuls.api.cache.ApiCache;
import io.nuls.api.model.po.AssetInfo;
import io.nuls.api.model.po.ChainConfigInfo;
import io.nuls.api.model.po.ChainInfo;
import io.nuls.api.model.po.CoinContextInfo;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.nuls.api.constant.ApiConstant.ENABLE;

public class CacheManager {

    public static BigInteger NonCirculatingAmount = BigInteger.valueOf(1907019924594354L);
    public static BigInteger TotalNulsAmount = BigInteger.ZERO;

    /**
     * 缓存每条当前节点运行链的数据
     */
    private static Map<Integer, ApiCache> apiCacheMap = new ConcurrentHashMap<>();
    /**
     * 缓存所有已注册跨链的链信息
     */
    private static Map<Integer, ChainInfo> chainInfoMap = new ConcurrentHashMap<>();
    /**
     * 缓存所有已注册跨链的资产信息
     */
    private static Map<String, AssetInfo> assetInfoMap = new ConcurrentHashMap<>();

    private static boolean inited = false;


    public static void addApiCache(int chainID, ApiCache apiCache) {
        apiCacheMap.put(chainID, apiCache);
    }

    public static ApiCache getCache(int chainID) {
        return apiCacheMap.get(chainID);
    }

    public static void initCache(ChainInfo chainInfo, ChainConfigInfo configInfo) {
        ApiCache apiCache = new ApiCache();
        apiCache.setChainInfo(chainInfo);
        apiCache.setConfigInfo(configInfo);
        chainInfoMap.put(chainInfo.getChainId(), chainInfo);
        assetInfoMap.put(chainInfo.getDefaultAsset().getKey(), chainInfo.getDefaultAsset());

        CoinContextInfo contextInfo = new CoinContextInfo();
        apiCache.setCoinContextInfo(contextInfo);
        apiCacheMap.put(chainInfo.getChainId(), apiCache);

        if(null != apiCache.getCoinContextInfo()) {
            TotalNulsAmount = apiCache.getCoinContextInfo().getTotal();
        }
        inited = true;

    }

    public static boolean isInited() {
        return inited;
    }

    public static void removeApiCache(int chainId) {
        apiCacheMap.remove(chainId);
    }

    public static ChainInfo getCacheChain(int chainId) {
        ApiCache apiCache = apiCacheMap.get(chainId);
        if (apiCache == null) {
            return null;
        }
        return apiCacheMap.get(chainId).getChainInfo();
    }

    public static Map<Integer, ApiCache> getApiCaches() {
        return apiCacheMap;
    }

    public static boolean isChainExist(int chainId) {
        ApiCache cache = apiCacheMap.get(chainId);
        return cache != null;
    }

    public static int getAssetInfoCount() {
        return CacheManager.assetInfoMap.size();
    }

    public static void putAssetInfo(AssetInfo info) {
        putAssetInfo(info.getKey(), info);
    }

    public static void putAssetInfo(String key, AssetInfo info) {
        if (CacheManager.assetInfoMap == null) {
            CacheManager.assetInfoMap = new ConcurrentHashMap<>();
        }
        CacheManager.assetInfoMap.put(info.getKey(), info);
    }

    public static boolean containsAssetKey(String key) {
        return CacheManager.assetInfoMap.containsKey(key);
    }
    public static AssetInfo getRegisteredAsset(String key) {
        AssetInfo info = assetInfoMap.get(key);
        if (null != info) {
            return info;
        }
        if ("2-201".equals(key)) {
            info = new AssetInfo();
            info.setChainId(2);
            info.setAssetId(201);
            info.setSymbol("BTC");
            info.setDecimals(8);
            info.setStatus(ENABLE);
            return info;
        }
        if ("2-202".equals(key)) {
            info = new AssetInfo();
            info.setChainId(2);
            info.setAssetId(202);
            info.setSymbol("ETH");
            info.setDecimals(18);
            info.setStatus(ENABLE);
            return info;
        }
        return null;
    }

    public static Map<Integer, ChainInfo> getChainInfoMap() {
        return chainInfoMap;
    }

    public static void setChainInfoMap(Map<Integer, ChainInfo> chainInfoMap) {
        CacheManager.chainInfoMap = chainInfoMap;
    }

    public static void putAssetInfoMap(Map<String, AssetInfo> assetInfoMap) {
        CacheManager.assetInfoMap = assetInfoMap;
    }
}
