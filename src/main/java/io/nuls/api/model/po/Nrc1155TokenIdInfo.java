package io.nuls.api.model.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.nuls.core.model.StringUtils;

import java.math.BigInteger;

public class Nrc1155TokenIdInfo {

    private String key;

    private String contractAddress;

    private String name;

    private String symbol;

    private String tokenId;

    private String totalSupply;

    private String tokenURI;

    private Long time;

    @JsonIgnore
    private boolean isNew;

    public Nrc1155TokenIdInfo() {
    }

    public Nrc1155TokenIdInfo(String contractAddress, String name, String symbol, String tokenId, String tokenURI, Long time) {
        this.key = contractAddress + tokenId;
        this.contractAddress = contractAddress;
        this.name = name;
        this.symbol = symbol;
        this.tokenId = tokenId;
        this.tokenURI = tokenURI;
        this.time = time;
        this.isNew = true;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public void addTotalSupply(String totalSupply) {
        if (StringUtils.isBlank(this.totalSupply)) {
            this.totalSupply = totalSupply;
        } else {
            this.totalSupply = new BigInteger(this.totalSupply).add(new BigInteger(totalSupply)).toString();
        }
    }

    public void subTotalSupply(String totalSupply) {
        BigInteger thisValueBig = new BigInteger(this.totalSupply);
        BigInteger valueBig = new BigInteger(totalSupply);
        if (thisValueBig.compareTo(valueBig) < 0) {
            throw new RuntimeException("Insufficient totalSupply");
        }
        this.totalSupply = thisValueBig.subtract(valueBig).toString();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
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

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getTotalSupply() {
        return totalSupply;
    }

    public void setTotalSupply(String totalSupply) {
        this.totalSupply = totalSupply;
    }

    public String getTokenURI() {
        return tokenURI;
    }

    public void setTokenURI(String tokenURI) {
        this.tokenURI = tokenURI;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
