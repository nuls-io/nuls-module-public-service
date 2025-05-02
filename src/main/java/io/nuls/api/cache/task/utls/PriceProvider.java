package io.nuls.api.cache.task.utls;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @Author: zhoulijun
 * @Time: 2020-03-08 19:56
 * @Description: 功能描述
 */
public interface PriceProvider {

    void setURL(String url);

    BigDecimal queryPrice(String symbol);

    void setWeight(Map<String,Double> weight);

    Double getWeight(String symbol);

}
