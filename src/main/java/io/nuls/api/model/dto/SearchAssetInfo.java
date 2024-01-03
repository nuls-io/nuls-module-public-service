package io.nuls.api.model.dto;

import io.nuls.api.cache.AssetSystemCache;
import io.nuls.api.model.po.Nrc1155Info;
import io.nuls.api.model.po.Nrc20Info;
import io.nuls.api.model.po.Nrc721Info;
import io.nuls.api.model.po.asset.ChainAssetInfo;
import io.nuls.core.model.DoubleUtils;
import io.nuls.core.model.StringUtils;

public class SearchAssetInfo {
    private String id;//9-1
    private int type;
    private String iconUrl, symbol;
    private int decimals;
    private String website, price;
    private String contract;

    public SearchAssetInfo() {
    }

    public SearchAssetInfo(ChainAssetInfo info) {
        AssetsSystemTokenInfoVo vo = AssetSystemCache.getAssetCache(info.getId());
        if (null == vo) {
            vo = new AssetsSystemTokenInfoVo();
        } else {
            this.price = vo.getPrice();
            this.iconUrl = vo.getImageUrl();
        }
        this.id = info.getId();
        this.type = 1;
        this.symbol = info.getSymbol();
        this.decimals = info.getDecimals();
        this.contract = info.getContract();
        this.website = info.getWebsite();
        if (StringUtils.isBlank(this.contract)) {
            this.contract = vo.getContractAddress();
        }
    }

    public SearchAssetInfo(Nrc20Info info) {
        AssetsSystemTokenInfoVo vo = AssetSystemCache.getAssetCacheByContract(info.getContractAddress());
        if (null == vo) {
            vo = new AssetsSystemTokenInfoVo();
        } else {
            this.price = vo.getPrice();
            this.iconUrl = vo.getImageUrl();
        }
        this.type = 20;
        this.symbol = info.getSymbol();
        this.decimals = info.getDecimal();
        this.contract = info.getContractAddress();
        if (StringUtils.isBlank(this.contract)) {
            this.contract = vo.getContractAddress();
        }
    }

    public SearchAssetInfo(Nrc721Info info) {
        this.type = 721;
        this.symbol = info.getSymbol();
        this.decimals = 0;
        this.contract = info.getContractAddress();
    }
    public SearchAssetInfo(Nrc1155Info info) {
        this.type = 721;
        this.symbol = info.getSymbol();
        this.decimals = 0;
        this.contract = info.getContractAddress();
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getDecimals() {
        return decimals;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }
}
