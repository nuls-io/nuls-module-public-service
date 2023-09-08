package io.nuls.api.model.po.asset;

import java.math.BigInteger;

public class ChainAssetBalance {
    private String id;
    private String address;
    private String assetId;
    private BigInteger balance;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public void setBalance(BigInteger balance) {
        this.balance = balance;
    }

}
