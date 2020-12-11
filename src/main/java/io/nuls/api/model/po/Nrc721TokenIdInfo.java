package io.nuls.api.model.po;

public class Nrc721TokenIdInfo {

    private String key;

    private String contractAddress;

    private String name;

    private String symbol;

    private String tokenId;

    private String tokenURI;

    private Long time;

    private String owner;

    public Nrc721TokenIdInfo() {
    }

    public Nrc721TokenIdInfo(String contractAddress, String name, String symbol, String tokenId, String tokenURI, Long time, String owner) {
        this.key = contractAddress + tokenId;
        this.contractAddress = contractAddress;
        this.name = name;
        this.symbol = symbol;
        this.tokenId = tokenId;
        this.tokenURI = tokenURI;
        this.time = time;
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
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

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getTokenURI() {
        return tokenURI;
    }

    public void setTokenURI(String tokenURI) {
        this.tokenURI = tokenURI;
    }
}
