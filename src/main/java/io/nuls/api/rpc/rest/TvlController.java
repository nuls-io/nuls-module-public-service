package io.nuls.api.rpc.rest;

import io.nuls.api.cache.ApiCache;
import io.nuls.api.manager.CacheManager;
import io.nuls.api.rpc.rest.utils.*;
import io.nuls.api.utils.LoggerUtil;
import io.nuls.core.log.Log;
import io.nuls.core.model.DoubleUtils;
import io.nuls.core.parse.JSONUtils;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Niels
 */
public class TvlController implements Runnable {
    private static double nulsPrice = 0D;

    static {
        TvlController runner = new TvlController();
        new Thread(runner).start();
    }

    public static void doGet(Response response) {
        Log.info("TvlController.doGet");
        ApiCache apiCache = CacheManager.getCache(1);
        BigInteger consensusAmount = apiCache.getCoinContextInfo().getConsensusTotal();

        BigDecimal tvl = new BigDecimal((consensusAmount)).movePointLeft(8).multiply(BigDecimal.valueOf(nulsPrice));

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("tvl", DoubleUtils.getRoundStr(tvl.doubleValue(), 2));
        try {
            response.getWriter().write(JSONUtils.obj2json(result));
        } catch (IOException e) {
            Log.error(e);
        }
    }

    public static BinancePriceProvider binancePriceProvider = new BinancePriceProvider("https://api.binance.com");
    public static HuobiPriceProvider huobiPriceProvider = new HuobiPriceProvider("https://api-aws.huobi.pro");
    public static OkexPriceProvider okexPriceProvider = new OkexPriceProvider("https://aws.okex.com");
    public static NerveDexPriceProvider dexPriceProvider = new NerveDexPriceProvider("https://api.nervedex.com");

    public static Double getNulsPriceFromEx(PriceProvider priceProvider) {
        try {
            BigDecimal price = priceProvider.queryPrice("NULS");
            if (null != price) {
                return price.doubleValue();
            }
            return null;
        } catch (Throwable t) {
            Log.error(t);
            return null;
        }
    }

    @Override
    public void run() {
        while (true) {
            Double price = getNulsPriceFromEx(binancePriceProvider);
//                    price = getNulsPriceFromEx(dexPriceProvider);
            if (null == price || price == 0) {
                price = getNulsPriceFromEx(huobiPriceProvider);
            }
            if (null == price || price == 0) {
                price = getNulsPriceFromEx(okexPriceProvider);
            }
            if (null != price && price > 0) {
                nulsPrice = price;
            }
            LoggerUtil.commonLog.info("nuls-price for tvl : " + nulsPrice);
            try {
                long sleep = 30 * 60000L;
                if (nulsPrice > 0) {
                    sleep = 120000L;
                }
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                Log.error(e);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(nulsPrice);
    }
}
