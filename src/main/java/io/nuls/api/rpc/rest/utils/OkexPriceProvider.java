/**
 * MIT License
 * <p>
 * Copyright (c) 2019-2020 nerve.network
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.nuls.api.rpc.rest.utils;

import io.nuls.core.log.Log;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author: Loki
 * @date: 2019/12/10
 */
public class OkexPriceProvider extends BasePriceProvider {

    private String CMD_FORMAT ="/api/spot/v3/instruments/%s-USDT/ticker";

    public OkexPriceProvider(String url) {
        super(url);
    }

    public BigDecimal queryPrice(String symbol) {
        String whole = String.format(this.url + CMD_FORMAT, symbol.toUpperCase());

        try {
            Map<String, Object> data = httpRequest( whole);
            if(null == data){
                return null;
            }
            BigDecimal res = new BigDecimal((String) data.get("last"));
            Log.info("Okex 获取到交易对[{}]价格:{}", symbol.toUpperCase(), res);
            return res;
        } catch (Throwable e) {
            Log.error("Okex, 调用接口 {}, symbol:{} 获取价格失败", whole, symbol);
            return null;
        }
    }

}
