package io.nuls.api.model.po;

public class Nrc1155Info {

    private String contractAddress;

    private String name;

    private String symbol;

    private String tokenURI;

    public Nrc1155Info() {
    }

    public Nrc1155Info(String contractAddress, String name, String symbol, String tokenURI) {
        this.contractAddress = contractAddress;
        this.name = name;
        this.symbol = symbol;
        this.tokenURI = tokenURI;
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

    public String getTokenURI() {
        return tokenURI;
    }

    public void setTokenURI(String tokenURI) {
        this.tokenURI = tokenURI;
    }
}
