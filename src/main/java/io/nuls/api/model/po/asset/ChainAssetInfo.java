package io.nuls.api.model.po.asset;

import java.math.BigInteger;

public class ChainAssetInfo {
    private String id;//9-1

    private int assetType;// This chain asset-0Cross chain assets-1
    private int status;//0=enable,1=disable

    private String totalSupply;//total
    private String name, symbol;
    private int decimals;
    private int addresses;
    private int addressesYesterday;
    private Long addressesTime;
    private long txCount = 0;
    private long crossTxCount = 0;
    private String inAmount = "0";
    private String outAmount = "0";
    private int sourceChainId;
    private String contract;
    private String website, community;

    private boolean update;
    private String nulsChainSupply = "0";

    private String sourceChainName;
    private String sourceChainLogo;
    private String sourceChainExplorerUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAssetType() {
        return assetType;
    }

    public void setAssetType(int assetType) {
        this.assetType = assetType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTotalSupply() {
        return totalSupply;
    }

    public void setTotalSupply(String totalSupply) {
        this.totalSupply = totalSupply;
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

    public long getCrossTxCount() {
        return crossTxCount;
    }

    public void setCrossTxCount(long crossTxCount) {
        this.crossTxCount = crossTxCount;
    }

    public String getInAmount() {
        return inAmount;
    }

    public void setInAmount(String inAmount) {
        this.inAmount = inAmount;
    }

    public String getOutAmount() {
        return outAmount;
    }

    public void setOutAmount(String outAmount) {
        this.outAmount = outAmount;
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

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public int getAddressesYesterday() {
        return addressesYesterday;
    }

    public void setAddressesYesterday(int addressesYesterday) {
        this.addressesYesterday = addressesYesterday;
    }

    public Long getAddressesTime() {
        return addressesTime;
    }

    public void setAddressesTime(Long addressesTime) {
        this.addressesTime = addressesTime;
    }

    public void setNulsChainSupply(String nulsChainSupply) {
        this.nulsChainSupply = nulsChainSupply;
    }

    public String getNulsChainSupply() {
        return nulsChainSupply;
    }

    public String getSourceChainName() {
        return sourceChainName;
    }

    public void setSourceChainName(String sourceChainName) {
        this.sourceChainName = sourceChainName;
    }

    public String getSourceChainLogo() {
        return sourceChainLogo;
    }

    public void setSourceChainLogo(String sourceChainLogo) {
        this.sourceChainLogo = sourceChainLogo;
    }

    public String getSourceChainExplorerUrl() {
        return sourceChainExplorerUrl;
    }

    public void setSourceChainExplorerUrl(String sourceChainExplorerUrl) {
        this.sourceChainExplorerUrl = sourceChainExplorerUrl;
    }
}
