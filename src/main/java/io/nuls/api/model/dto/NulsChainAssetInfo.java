package io.nuls.api.model.dto;

import io.nuls.base.basic.AddressTool;
import io.nuls.core.rpc.util.NulsDateUtils;

import java.math.BigInteger;
import java.util.Map;

public class NulsChainAssetInfo {
    private int chainId = 0;
    private int assetId = 0;
    private String symbol;
    private String assetName;
    private short assetType = 1; //资产类型 [1-链内普通资产 2-链内合约资产 3-平行链资产 4-异构链资产 5-链内普通资产绑定异构链资产 6-平行链资产绑定异构链资产 7-链内普通资产绑定多异构链资产 8-平行链资产绑定多异构链资产 9-异构链资产绑定多异构链资产]
    private BigInteger initNumber = BigInteger.ZERO;
    private short decimalPlace = 8;
    private String ownerAddress;
    private long createTime = 0;

    public void fromMap(Map<String, Object> map) {
        this.setChainId(Integer.valueOf(map.get("chainId").toString()));
        this.setAssetName(String.valueOf(map.get("assetName")));
        BigInteger initNumber = new BigInteger(String.valueOf(map.get("initNumber")));
        this.setInitNumber(initNumber);
        this.setDecimalPlace(Short.valueOf(map.get("decimalPlace").toString()));
        this.setSymbol(String.valueOf(map.get("assetSymbol")));
        this.setOwnerAddress(map.get("address").toString());
        this.setCreateTime(NulsDateUtils.getCurrentTimeSeconds());
        this.assetType = Short.parseShort(map.get("assetType").toString());
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

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public short getAssetType() {
        return assetType;
    }

    public void setAssetType(short assetType) {
        this.assetType = assetType;
    }

    public BigInteger getInitNumber() {
        return initNumber;
    }

    public void setInitNumber(BigInteger initNumber) {
        this.initNumber = initNumber;
    }

    public short getDecimalPlace() {
        return decimalPlace;
    }

    public void setDecimalPlace(short decimalPlace) {
        this.decimalPlace = decimalPlace;
    }

    public String getOwnerAddress() {
        return ownerAddress;
    }

    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}

