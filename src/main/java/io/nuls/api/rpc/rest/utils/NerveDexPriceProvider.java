package io.nuls.api.rpc.rest.utils;

import io.nuls.core.log.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * @Author: zhoulijun
 * @Time: 2020/8/10 17:49
 * @Description: 功能描述
 */
public class NerveDexPriceProvider extends BasePriceProvider {

    private final static Long  SUCCESS = 200L;

    public NerveDexPriceProvider(String url) {
        super(url);
    }

    @Override
    public BigDecimal queryPrice(String symbol) {
        symbol = symbol.toUpperCase();
        String param = "/coin/trading/get/" + symbol + "eUSDT";
        String wholeUrl = this.url + param;
        try {
            Map<String, Object> res = httpRequest(wholeUrl);
            if (null == res) {
                return BigDecimal.ZERO;
            }
            Long code = Long.parseLong(res.get("code").toString());
            if (!SUCCESS.equals(code)) {
                Log.error("nerve dex获取"+symbol+"价格失败,code:" + code + " message : " + res.get("msg"));
                return BigDecimal.ZERO;
            }
            Map<String, Object> data = (Map<String, Object>) res.get("data");
            Map<String, Object> ticker = (Map<String, Object>) data.get("result");
            Integer decimal = Integer.parseInt(ticker.get("quoteDecimal").toString());
            return new BigDecimal(ticker.get("newPrice").toString()).movePointLeft(decimal).setScale(decimal, RoundingMode.HALF_DOWN);
        } catch (Throwable e) {
            Log.error("调用{}接口获取{}价格失败",wholeUrl,symbol,e);
            return BigDecimal.ZERO;
        }

    }
}
