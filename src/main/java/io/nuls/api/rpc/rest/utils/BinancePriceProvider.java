package io.nuls.api.rpc.rest.utils;


import io.nuls.api.utils.LoggerUtil;
import io.nuls.core.log.Log;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @Author: zhoulijun
 * @Time: 2020-03-08 20:17
 * @Description: 功能描述
 */
public class BinancePriceProvider extends BasePriceProvider {

    private int initTryCount = 0;

    public BinancePriceProvider(String url) {
        super(url);
    }

    @Override
    public BigDecimal queryPrice(String symbol) {
        try {
            String url = this.url + "/api/v3/ticker/price?symbol=" + symbol + "USDT";
            Map<String, Object> data = httpRequest(url);
            if (null == data) {
                LoggerUtil.commonLog.info("未能从币安获取到价格：" + url);
                return BigDecimal.ZERO;
            }
            BigDecimal res = new BigDecimal((String) data.get("price"));
            LoggerUtil.commonLog.debug("获取到当前{}兑USDT的价格:{}", symbol, res);
            return res;
        } catch (Exception e) {
            LoggerUtil.commonLog.error("调用{}接口获取{}价格失败", this.url, symbol, e);
            return BigDecimal.ZERO;
        }
    }
}
