package io.nuls.api.model.po.asset;

import io.nuls.api.cache.AssetSystemCache;
import io.nuls.api.model.dto.AssetsSystemTokenInfoVo;
import io.nuls.core.model.StringUtils;

public class ChainAssetInfoVo {
    private String id;//9-1

    private String totalSupply;//总量
    private String nulsChainSupply;//总量
    private String name, symbol;
    private int decimals;
    private int addresses;
    private long txCount = 0;
    private int sourceChainId;
    private String contract;
    private String website, community;

    public ChainAssetInfoVo(ChainAssetInfo info) {
        //todo nulsChainAmount
        this(info.getId(), info.getTotalSupply(), "0", info.getName(), info.getSymbol(), info.getDecimals(), info.getAddresses(), info.getTxCount(), info.getSourceChainId(), info.getContract(), info.getWebsite(), info.getCommunity());
    }

    public ChainAssetInfoVo(String id, String totalSupply, String nulsChainSupply, String name, String symbol, int decimals, int addresses, long txCount, int sourceChainId, String contract, String website, String community) {
        AssetsSystemTokenInfoVo vo = AssetSystemCache.getAssetCache(id);
        if (null == vo) {
            vo = new AssetsSystemTokenInfoVo();
            vo.setSourceChainId((long) sourceChainId);
        }
        this.id = id;
        this.totalSupply = totalSupply;
        this.nulsChainSupply = nulsChainSupply;
        this.name = name;
        this.symbol = symbol;
        this.decimals = decimals;
        this.addresses = addresses;
        this.txCount = txCount;
        this.sourceChainId = Math.toIntExact(vo.getSourceChainId());
        this.contract = contract;
        this.website = website;
        this.community = community;
        if (vo != null && StringUtils.isBlank(contract)) {
            this.contract = vo.getContractAddress();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTotalSupply() {
        return totalSupply;
    }

    public void setTotalSupply(String totalSupply) {
        this.totalSupply = totalSupply;
    }

    public String getNulsChainSupply() {
        return nulsChainSupply;
    }

    public void setNulsChainSupply(String nulsChainSupply) {
        this.nulsChainSupply = nulsChainSupply;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getAddresses() {
        return addresses;
    }

    public void setAddresses(int addresses) {
        this.addresses = addresses;
    }

    public long getTxCount() {
        return txCount;
    }

    public void setTxCount(long txCount) {
        this.txCount = txCount;
    }

    public int getSourceChainId() {
        return sourceChainId;
    }

    public void setSourceChainId(int sourceChainId) {
        this.sourceChainId = sourceChainId;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }
}
