package io.nuls.api.model.po;

import io.nuls.api.manager.CacheManager;
import io.nuls.api.utils.DBUtil;
import io.nuls.api.utils.DocumentTransferTool;
import io.nuls.core.constant.TxType;
import org.bson.Document;

import java.math.BigInteger;
import java.util.List;

public class TransactionInfo {

    private String hash;

    private int type;

    private long height;

    private int size;

    private FeeInfo fee;

    private long createTime;

    private String remark;

    private String txDataHex;

    private TxDataInfo txData;

    private List<TxDataInfo> txDataList;

    private List<CoinFromInfo> coinFroms;

    private List<CoinToInfo> coinTos;

    private BigInteger value;

    private int status;

    private String symbol;

    private int decimal;

    public void calcValue(int chainId) {
        BigInteger value = BigInteger.ZERO;
        boolean calc = type == 10 || type == 26;
        if (coinTos != null && !coinTos.isEmpty()) {
            if (calc) {
                for (CoinToInfo output : coinTos) {
                    value = value.add(output.getAmount());
                    this.symbol = output.getSymbol();
                    this.decimal = output.getDecimal();
                }
            } else {
                for (CoinToInfo output : coinTos) {
                    if (output.getChainId() == chainId && output.getAssetsId() == 1) {
                        value = value.add(output.getAmount());
                        this.symbol = output.getSymbol();
                        this.decimal = output.getDecimal();
                    }
                }
            }
        }
        if (coinFroms != null && !coinFroms.isEmpty()) {
            if (calc) {
                CoinFromInfo input = coinFroms.get(0);
                this.symbol = input.getSymbol();
                this.decimal = input.getDecimal();
            } else {
                for (CoinFromInfo fromInfo : coinFroms) {
                    if (fromInfo.getChainId() == chainId && fromInfo.getAssetsId() == 1) {
                        this.symbol = fromInfo.getSymbol();
                        this.decimal = fromInfo.getDecimal();
                        break;
                    }
                }
            }
        }
        this.value = value;
//        if (type == TxType.COIN_BASE ||
//                type == TxType.STOP_AGENT ||
//                type == TxType.CANCEL_DEPOSIT ||
//                type == TxType.CONTRACT_RETURN_GAS ||
//                type == TxType.CONTRACT_STOP_AGENT ||
//                type == TxType.CONTRACT_CANCEL_DEPOSIT) {
//            if (coinTos != null) {
//                for (CoinToInfo output : coinTos) {
//                    value = value.add(output.getAmount());
//                }
//            }
//        } else if (type == TxType.TRANSFER ||
//                type == TxType.CALL_CONTRACT ||
//                type == TxType.CONTRACT_TRANSFER
//            //        type == TxType.TX_TYPE_DATA
//        ) {
//            Set<String> addressSet = new HashSet<>();
//            for (CoinFromInfo input : coinFroms) {
//                addressSet.add(input.getAddress());
//            }
//            for (CoinToInfo output : coinTos) {
//                if (!addressSet.contains(output.getAddress())) {
//                    value = value.add(output.getAmount());
//                }
//            }
//        } else if (type == TxType.REGISTER_AGENT ||
//                type == TxType.DEPOSIT ||
//                type == TxType.CONTRACT_CREATE_AGENT ||
//                type == TxType.CONTRACT_DEPOSIT) {
//            for (CoinToInfo output : coinTos) {
//                if (output.getLockTime() == -1) {
//                    value = value.add(output.getAmount());
//                }
//            }
//        } else if (type == TxType.ACCOUNT_ALIAS) {
//            value = ApiConstant.ALIAS_AMOUNT;
//        } else {
//            value = this.fee;
//        }
//        this.value = value.abs();
    }

    public FeeInfo calcFee(int chainId) {
        AssetInfo assetInfo = CacheManager.getCacheChain(chainId).getDefaultAsset();
        ChainConfigInfo configInfo = CacheManager.getCache(chainId).getConfigInfo();
        FeeInfo feeInfo;
        if (type == TxType.COIN_BASE || type == TxType.YELLOW_PUNISH || type == TxType.RED_PUNISH ||
                type == TxType.CONTRACT_RETURN_GAS || type == TxType.CONTRACT_STOP_AGENT || type == TxType.CONTRACT_CANCEL_DEPOSIT ||
                type == TxType.CONTRACT_CREATE_AGENT || type == TxType.CONTRACT_DEPOSIT) {
            //There is no transaction fee for system transactions
            feeInfo = new FeeInfo(assetInfo.getChainId(), assetInfo.getAssetId(), assetInfo.getSymbol());
//        } else if (type == TxType.CROSS_CHAIN) {
//            //Retrieve the transfer chain and receive chainid
//            int fromChainId = AddressTool.getChainIdByAddress(coinFroms.get(0).getAddress());
//            int toChainId = AddressTool.getChainIdByAddress(coinTos.get(0).getAddress());
//
//            //If the current chain isNULSMain chain, transaction fees are collected from the main assets of the main networkNULS
//            if (chainId == ApiContext.mainChainId) {
//                feeInfo = new FeeInfo(ApiContext.mainChainId, ApiContext.mainAssetId, ApiContext.mainSymbol);
//                if (toChainId == ApiContext.mainChainId) {
//                    //If the receiving address is the main chain,Then chargeNULSof100%As a handling fee
//                    BigInteger feeValue = calcFeeValue(ApiContext.mainChainId, ApiContext.mainAssetId);
//                    feeInfo.setValue(feeValue);
//                } else {
//                    //Other situations, main chain chargesNULSof60%As a handling fee
//                    BigInteger feeValue = calcFeeValue(ApiContext.mainChainId, ApiContext.mainAssetId);
//                    feeValue = feeValue.multiply(new BigInteger("60")).divide(new BigInteger("100"));
//                    feeInfo.setValue(feeValue);
//                }
//            } else {                        //If the current chain is notNULSMain chain
//                //If the asset is initiated from this chain, the default asset of this chain will be charged as a handling fee
//                if (fromChainId == chainId) {
//                    feeInfo = new FeeInfo(assetInfo.getChainId(), assetInfo.getAssetId(), assetInfo.getSymbol());
//                    feeInfo.setValue(calcFeeValue(assetInfo.getChainId(), assetInfo.getAssetId()));
//                } else {
//                    //If this chain is the target chain for receiving transfer transactions, the main network will be chargedNULSAssets40%As a handling fee
//                    feeInfo = new FeeInfo(ApiContext.mainChainId, ApiContext.mainAssetId, ApiContext.mainSymbol);
//                    BigInteger feeValue = calcFeeValue(ApiContext.mainChainId, ApiContext.mainAssetId);
//                    feeValue = feeValue.multiply(new BigInteger("40")).divide(new BigInteger("100"));
//                    feeInfo.setValue(feeValue);
//                }
//            }
        } else if (type == TxType.REGISTER_AGENT || type == TxType.DEPOSIT || type == TxType.CANCEL_DEPOSIT || type == TxType.STOP_AGENT) {
            //If it is a consensus related transaction, a handling fee for consensus configuration will be charged
            assetInfo = CacheManager.getRegisteredAsset(DBUtil.getAssetKey(configInfo.getChainId(), configInfo.getAwardAssetId()));
            feeInfo = new FeeInfo(assetInfo.getChainId(), assetInfo.getAssetId(), assetInfo.getSymbol());
            BigInteger feeValue = calcFeeValue(assetInfo.getChainId(), assetInfo.getAssetId());
            feeInfo.setValue(feeValue);
        } else if (type == TxType.CREATE_CONTRACT || type == TxType.CALL_CONTRACT) {
            ContractResultInfo resultInfo;
            if (type == TxType.CREATE_CONTRACT) {
                ContractInfo contractInfo = (ContractInfo) this.txData;
                resultInfo = contractInfo.getResultInfo();
            } else {
                ContractCallInfo callInfo = (ContractCallInfo) this.txData;
                resultInfo = callInfo.getResultInfo();
            }
            feeInfo = new FeeInfo(assetInfo.getChainId(), assetInfo.getAssetId(), assetInfo.getSymbol());
            if (resultInfo != null) {
                BigInteger feeValue = new BigInteger(resultInfo.getActualContractFee()).add(new BigInteger(resultInfo.getTxSizeFee()));
                feeInfo.setValue(feeValue);
            }
        } else {
            //Other types of transactions,Default asset handling fee for removing this chain
            feeInfo = new FeeInfo(assetInfo.getChainId(), assetInfo.getAssetId(), assetInfo.getSymbol());
            feeInfo.setValue(calcFeeValue(assetInfo.getChainId(), assetInfo.getAssetId()));
        }
        this.fee = feeInfo;
        return feeInfo;
    }

    private BigInteger calcFeeValue(int chainId, int assetId) {
        BigInteger feeValue = BigInteger.ZERO;
        if (coinFroms != null && !coinFroms.isEmpty()) {
            for (CoinFromInfo fromInfo : coinFroms) {
                if (fromInfo.getChainId() == chainId && fromInfo.getAssetsId() == assetId) {
                    feeValue = feeValue.add(fromInfo.getAmount());
                }
            }
        }
        if (coinTos != null && !coinTos.isEmpty()) {
            for (CoinToInfo toInfo : coinTos) {
                if (toInfo.getChainId() == chainId && toInfo.getAssetsId() == assetId) {
                    feeValue = feeValue.subtract(toInfo.getAmount());
                }
            }
        }
        return feeValue;
    }

    public Document toDocument() {
        Document document = new Document();
        document.append("_id", hash).append("height", height).append("createTime", createTime).append("type", type).append("decimal", decimal)
                .append("value", value.toString()).append("fee", DocumentTransferTool.toDocument(fee)).append("status", status).append("symbol", symbol);
        return document;
    }

    public static TransactionInfo fromDocument(Document document) {
        TransactionInfo info = new TransactionInfo();
        info.setHash(document.getString("_id"));
        info.setHeight(document.getLong("height"));
        info.setCreateTime(document.getLong("createTime"));
        info.setType(document.getInteger("type"));
        info.setFee(DocumentTransferTool.toInfo((Document) document.get("fee"), FeeInfo.class));
        info.setValue(new BigInteger(document.getString("value")));
        info.setStatus(document.getInteger("status"));
        info.setSymbol(document.getString("symbol"));
        info.setDecimal(document.getInteger("decimal"));
        return info;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public FeeInfo getFee() {
        return fee;
    }

    public void setFee(FeeInfo fee) {
        this.fee = fee;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTxDataHex() {
        return txDataHex;
    }

    public void setTxDataHex(String txDataHex) {
        this.txDataHex = txDataHex;
    }

    public TxDataInfo getTxData() {
        return txData;
    }

    public void setTxData(TxDataInfo txData) {
        this.txData = txData;
    }

    public List<TxDataInfo> getTxDataList() {
        return txDataList;
    }

    public void setTxDataList(List<TxDataInfo> txDataList) {
        this.txDataList = txDataList;
    }

    public List<CoinFromInfo> getCoinFroms() {
        return coinFroms;
    }

    public void setCoinFroms(List<CoinFromInfo> coinFroms) {
        this.coinFroms = coinFroms;
    }

    public List<CoinToInfo> getCoinTos() {
        return coinTos;
    }

    public void setCoinTos(List<CoinToInfo> coinTos) {
        this.coinTos = coinTos;
    }

    public BigInteger getValue() {
        return value;
    }

    public void setValue(BigInteger value) {
        this.value = value;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getDecimal() {
        return decimal;
    }

    public void setDecimal(int decimal) {
        this.decimal = decimal;
    }
}
