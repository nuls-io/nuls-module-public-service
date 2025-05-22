package io.nuls.api.rpc.rest.utils;

import java.math.BigDecimal;

/**
 * @Author: zhoulijun
 * @Time: 2020-03-08 19:56
 * @Description: 功能描述
 */
public interface PriceProvider {

    BigDecimal queryPrice(String symbol);
}
