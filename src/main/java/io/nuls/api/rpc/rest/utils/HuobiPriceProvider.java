package io.nuls.api.rpc.rest.utils;

import io.nuls.api.utils.LoggerUtil;
import io.nuls.core.log.Log;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhoulijun
 * @Time: 2020/8/10 17:49
 * @Description: 功能描述
 */
public class HuobiPriceProvider extends BasePriceProvider {

    private final static String SUCCESS = "ok";

    public HuobiPriceProvider(String url) {
        super(url);
    }

    @Override
    public BigDecimal queryPrice(String symbol) {
        String param = "/market/trade?symbol=" + symbol.toLowerCase() + "usdt";
        String wholeUrl = this.url + param;
        try {
            Map<String, Object> res = httpRequest(wholeUrl);
            if (null == res) {
                LoggerUtil.commonLog.error("hbg获取" + symbol + "价格失败,status:nuls-data" );
                return BigDecimal.ZERO;
            }
            String status = res.get("status").toString();
            if (!SUCCESS.equals(status)) {
                LoggerUtil.commonLog.error("hbg获取" + symbol + "价格失败,status:" + status);
                return BigDecimal.ZERO;
            }
            Map<String, Object> tick = (Map<String, Object>) res.get("tick");
            List<Map<String, Object>> data = (List<Map<String, Object>>) tick.get("data");
            return new BigDecimal(data.get(0).get("price").toString());
        } catch (Throwable e) {
            LoggerUtil.commonLog.error("调用{}接口获取{}价格失败", wholeUrl, symbol, e);
            return BigDecimal.ZERO;
        }

    }
}
