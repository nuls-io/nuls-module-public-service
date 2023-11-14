package io.nuls.api.cache.task.utls;

import io.nuls.api.model.dto.AssetsSystemTokenInfoVo;
import io.nuls.api.model.dto.NerveChainVo;
import io.nuls.api.utils.LoggerUtil;
import io.nuls.core.core.annotation.Component;
import io.nuls.core.model.StringUtils;
import io.nuls.core.parse.JSONUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: zhoulijun
 * @Time: 2020/8/10 17:49
 * @Description: 功能描述
 */
@Component
public class NerveDexPriceProvider extends BasePriceProvider {

    private final static Long SUCCESS = 200L;

    @Override
    public BigDecimal queryPrice(String symbol) {
        symbol = symbol.toUpperCase();
        String param = "price/" + symbol;
        String url = this.url + param;
        try {
            String res = realHttpRequest(url);
            if (null == res) {
                return BigDecimal.ZERO;
            }
            return new BigDecimal(res);
        } catch (Throwable e) {
            LoggerUtil.commonLog.error("调用{}接口获取{}价格失败", url, symbol, e);
            return BigDecimal.ZERO;
        }

    }

    public List<AssetsSystemTokenInfoVo> getAllTokenList() {
        String path = "/asset/listWithExtend";
        String response = this.realHttpRequest(this.url + path);
        if (StringUtils.isBlank(response)) {
            return null;
        }
        List<AssetsSystemTokenInfoVo> list = null;
        try {
            list = JSONUtils.json2list(response, AssetsSystemTokenInfoVo.class);
        } catch (IOException e) {
            LoggerUtil.commonLog.error("", e);
        }
        return list;
    }

    public List<NerveChainVo> getChainList() {
        String path = "/chains";
        String response = this.realHttpRequest(this.url + path);
        if (StringUtils.isBlank(response)) {
            return null;
        }
        List<NerveChainVo> list = null;
        try {
            list = JSONUtils.json2list(response, NerveChainVo.class);
        } catch (IOException e) {
            LoggerUtil.commonLog.error("", e);
        }
        return list;
    }
}
