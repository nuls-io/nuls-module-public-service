package io.nuls.api.cache.task;

import io.nuls.api.ApiContext;
import io.nuls.api.cache.AssetSystemCache;
import io.nuls.api.cache.task.utls.NerveDexPriceProvider;
import io.nuls.core.log.Log;

public class AssetSystemCacheTask implements Runnable {

    public AssetSystemCacheTask() {
        super();
        priceProvider = new NerveDexPriceProvider();
        priceProvider.setURL("https://assets.nabox.io/api/");
    }

    private NerveDexPriceProvider priceProvider;


    @Override
    public void run() {
        try {
            if (ApiContext.defaultChainId == 2) {
                priceProvider.setURL("https://beta.assets.nabox.io/api/");
            }
            AssetSystemCache.putCacheList(priceProvider.getAllTokenList());
            AssetSystemCache.putChainList(priceProvider.getChainList());
            AssetSystemCache.putAddressTag(priceProvider.getDictionary());
        } catch (Exception e) {
            Log.error("", e);
        }
    }

    public static void main(String[] args) {
        NerveDexPriceProvider priceProvider = new NerveDexPriceProvider();
        priceProvider.setURL("https://assets.nabox.io/api/");
        System.out.println(priceProvider.getAllTokenList().size());
    }
}
