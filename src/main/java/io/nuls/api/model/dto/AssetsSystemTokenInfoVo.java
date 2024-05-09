package io.nuls.api.model.dto;


public class AssetsSystemTokenInfoVo {

    private boolean nerveCross;
    private String crossChainNames;
    /**
     * name
     */
    private String name;

    /**
     * Asset abbreviation
     */
    private String symbol;

    /**
     * Decimal places of assets
     */
    private Long decimals;

    /**
     * Asset ChainID
     */
    private Long assetChainId;

    /**
     * assetID
     */
    private Long assetId;

    /**
     * Contract address
     */
    private String contractAddress;

    private String imageUrl;

    private String price;

    private Long sourceChainId;

    private boolean nulsCross;

    private String crossChainIds;

    private String crossInfo;

    private String totalSupply;
    private String webSite;
    private String community;
    private String extend1;
    private String extend2;
    private String extend3;
    private String extend4;

    public boolean isNerveCross() {
        return nerveCross;
    }

    public String getCrossChainNames() {
        return crossChainNames;
    }

    public void setCrossChainNames(String crossChainNames) {
        this.crossChainNames = crossChainNames;
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

    public Long getDecimals() {
        return decimals;
    }

    public void setDecimals(Long decimals) {
        this.decimals = decimals;
    }

    public Long getAssetChainId() {
        return assetChainId;
    }

    public void setAssetChainId(Long assetChainId) {
        this.assetChainId = assetChainId;
    }

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Long getSourceChainId() {
        return sourceChainId;
    }

    public void setSourceChainId(Long sourceChainId) {
        this.sourceChainId = sourceChainId;
    }

    public boolean isNulsCross() {
        return nulsCross;
    }

    public void setNulsCross(boolean nulsCross) {
        this.nulsCross = nulsCross;
    }

    public String getCrossChainIds() {
        return crossChainIds;
    }

    public void setCrossChainIds(String crossChainIds) {
        this.crossChainIds = crossChainIds;
    }

    public String getCrossInfo() {
        return crossInfo;
    }

    public void setCrossInfo(String crossInfo) {
        this.crossInfo = crossInfo;
    }

    public String getTotalSupply() {
        return totalSupply;
    }

    public void setTotalSupply(String totalSupply) {
        this.totalSupply = totalSupply;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getExtend1() {
        return extend1;
    }

    public void setExtend1(String extend1) {
        this.extend1 = extend1;
    }

    public String getExtend2() {
        return extend2;
    }

    public void setExtend2(String extend2) {
        this.extend2 = extend2;
    }

    public String getExtend3() {
        return extend3;
    }

    public void setExtend3(String extend3) {
        this.extend3 = extend3;
    }

    public String getExtend4() {
        return extend4;
    }

    public void setExtend4(String extend4) {
        this.extend4 = extend4;
    }

    public String getAssetKey() {
        return this.assetChainId + "-" + this.assetId;
    }
}
