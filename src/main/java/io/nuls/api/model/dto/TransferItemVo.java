package io.nuls.api.model.dto;

public class TransferItemVo {
    private String address;
    private String assetType;
    private String amount;
    private String assetKey;

    private int decimals;
    private String symbol;
    private Boolean locked;
    private String tokenId;

    private String contract;

    public TransferItemVo() {
    }

    public TransferItemVo(String address, String assetType, String amount, String assetKey,int decimals, String symbol, Boolean locked, String tokenId,String contract) {
        this.address = address;
        this.assetType = assetType;
        this.amount = amount;
        this.assetKey = assetKey;
        this.decimals = decimals;
        this.symbol = symbol;
        this.locked = locked;
        this.tokenId = tokenId;
        this.contract = contract;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAssetKey() {
        return assetKey;
    }

    public void setAssetKey(String assetKey) {
        this.assetKey = assetKey;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public int getDecimals() {
        return decimals;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Boolean getLocked() {
        return locked;
    }
}
