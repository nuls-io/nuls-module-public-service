package io.nuls.api.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(allowSetters = true)
public class NerveChainVo {

    /**
     * ID
     */
    private Long id;

    /**
     * name
     */
    private String name;

    /**
     * nativeId
     */
    private Long nativeId;

    /**
     * net_Type
     */
    private Integer netType;

    /**
     * mainAsset
     */
    private String mainAssetSymbol;

    /**
     * mainAssetDecimals
     */
    private Integer mainAssetDecimals;

    /**
     * iconUrl
     */
    private String iconUrl;

    /**
     * mainRpcUrl
     */
    private String mainRpcUrl;

    /**
     * commonRpcUrl
     */
    private String commonRpcUrl;

    /**
     * multySignContract
     */
    private String multySignContractAddress;

    /**
     * multySignContractOwner
     */
    private String multySignContractOwner;

    /**
     * erc20DeployContract
     */
    private String erc20DeployContractAddress;

    /**
     * erc20DeployContractOwner
     */
    private String erc20DeployContractOwner;

    /**
     * explorerUrl
     */
    private String explorerUrl;

    /**
     * multiCallAddress
     */
    private String multiCallAddress;

    public NerveChainVo() {
    }

    public NerveChainVo(String name) {
        this.name = name;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getNativeId() {
        return nativeId;
    }

    public void setNativeId(Long nativeId) {
        this.nativeId = nativeId;
    }

    public Integer getNetType() {
        return netType;
    }

    public void setNetType(Integer netType) {
        this.netType = netType;
    }

    public String getMainAssetSymbol() {
        return mainAssetSymbol;
    }

    public void setMainAssetSymbol(String mainAssetSymbol) {
        this.mainAssetSymbol = mainAssetSymbol;
    }

    public Integer getMainAssetDecimals() {
        return mainAssetDecimals;
    }

    public void setMainAssetDecimals(Integer mainAssetDecimals) {
        this.mainAssetDecimals = mainAssetDecimals;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getMainRpcUrl() {
        return mainRpcUrl;
    }

    public void setMainRpcUrl(String mainRpcUrl) {
        this.mainRpcUrl = mainRpcUrl;
    }

    public String getCommonRpcUrl() {
        return commonRpcUrl;
    }

    public void setCommonRpcUrl(String commonRpcUrl) {
        this.commonRpcUrl = commonRpcUrl;
    }

    public String getMultySignContractAddress() {
        return multySignContractAddress;
    }

    public void setMultySignContractAddress(String multySignContractAddress) {
        this.multySignContractAddress = multySignContractAddress;
    }

    public String getMultySignContractOwner() {
        return multySignContractOwner;
    }

    public void setMultySignContractOwner(String multySignContractOwner) {
        this.multySignContractOwner = multySignContractOwner;
    }

    public String getErc20DeployContractAddress() {
        return erc20DeployContractAddress;
    }

    public void setErc20DeployContractAddress(String erc20DeployContractAddress) {
        this.erc20DeployContractAddress = erc20DeployContractAddress;
    }

    public String getErc20DeployContractOwner() {
        return erc20DeployContractOwner;
    }

    public void setErc20DeployContractOwner(String erc20DeployContractOwner) {
        this.erc20DeployContractOwner = erc20DeployContractOwner;
    }

    public String getExplorerUrl() {
        return explorerUrl;
    }

    public void setExplorerUrl(String explorerUrl) {
        this.explorerUrl = explorerUrl;
    }

    public String getMultiCallAddress() {
        return multiCallAddress;
    }

    public void setMultiCallAddress(String multiCallAddress) {
        this.multiCallAddress = multiCallAddress;
    }
}
