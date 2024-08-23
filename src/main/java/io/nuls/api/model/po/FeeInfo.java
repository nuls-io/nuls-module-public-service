package io.nuls.api.model.po;

import java.math.BigInteger;

public class FeeInfo {

    private int chainId;

    private int assetId;

    private String symbol;

    private BigInteger value;
    private int decimals;

    public FeeInfo() {

    }

    public FeeInfo(int chainId, int assetId, String symbol, int decimals) {
        this.chainId = chainId;
        this.assetId = assetId;
        this.symbol = symbol;
        this.value = BigInteger.ZERO;
        this.decimals = decimals;
    }

    public int getChainId() {
        return chainId;
    }

    public void setChainId(int chainId) {
        this.chainId = chainId;
    }

    public int getAssetId() {
        return assetId;
    }

    public void setAssetId(int assetId) {
        this.assetId = assetId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigInteger getValue() {
        return value;
    }

    public void setValue(BigInteger value) {
        this.value = value;
    }

    public int getDecimals() {
        return decimals;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }
}
