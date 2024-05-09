package io.nuls.api.rpc.rest.utils;


import io.nuls.api.utils.LoggerUtil;
import io.nuls.core.log.Log;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @Author: zhoulijun
 * @Time: 2020-03-08 20:17
 * @Description: Function Description
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
                LoggerUtil.commonLog.info("Unable to obtain price from Binance：" + url);
                return BigDecimal.ZERO;
            }
            BigDecimal res = new BigDecimal((String) data.get("price"));
            LoggerUtil.commonLog.debug("Get the current{}ExchangeUSDTThe price of:{}", symbol, res);
            return res;
        } catch (Exception e) {
            LoggerUtil.commonLog.error("call{}Interface acquisition{}Price failure", this.url, symbol, e);
            return BigDecimal.ZERO;
        }
    }
}
