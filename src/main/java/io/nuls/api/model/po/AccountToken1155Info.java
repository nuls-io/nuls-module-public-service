package io.nuls.api.model.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.nuls.core.model.StringUtils;

import java.math.BigInteger;

public class AccountToken1155Info {

    private String key;

    private String address;

    private String tokenName;

    private String tokenSymbol;

    private String contractAddress;

    private String tokenId;

    private String value;

    private int status;

    @JsonIgnore
    private boolean isNew;
    private String tag;

    public AccountToken1155Info() {
    }

    public AccountToken1155Info(String address, String tokenName, String tokenSymbol, String contractAddress, String tokenId) {
        this.key = address + contractAddress + tokenId;
        this.address = address;
        this.tokenName = tokenName;
        this.tokenSymbol = tokenSymbol;
        this.contractAddress = contractAddress;
        this.tokenId = tokenId;
        this.isNew = true;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public String getTokenSymbol() {
        return tokenSymbol;
    }

    public void setTokenSymbol(String tokenSymbol) {
        this.tokenSymbol = tokenSymbol;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public void addValue(String value) {
        if (StringUtils.isBlank(this.value)) {
            this.value = value;
        } else {
            this.value = new BigInteger(this.value).add(new BigInteger(value)).toString();
        }
    }

    public void subValue(String value) {
        BigInteger thisValueBig = new BigInteger(this.value);
        BigInteger valueBig = new BigInteger(value);
        if (thisValueBig.compareTo(valueBig) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        this.value = thisValueBig.subtract(valueBig).toString();
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}
