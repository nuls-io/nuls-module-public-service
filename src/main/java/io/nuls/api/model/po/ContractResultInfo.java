package io.nuls.api.model.po;

import io.nuls.api.utils.DocumentTransferTool;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class ContractResultInfo {

    private String txHash;

    private String contractAddress;

    private boolean success;

    private String errorMessage;

    private String result;

    private long gasLimit;

    private long gasUsed;

    private long price;

    private String totalFee;

    private String txSizeFee;

    private String actualContractFee;

    private String refundFee;

    private String value;

    //private String balance;

    private List<NulsTransfer> nulsTransfers;

    private List<TokenTransfer> tokenTransfers;

    private List<Token721Transfer> token721Transfers;

    private String remark;

    private List<String> contractTxList;

    private List<String> events;

    private List<ContractInternalCreateInfo> internalCreates;

    private List<Token1155Transfer> token1155Transfers;
    // 增加跨链资产合约内部转账的数据
    private List<CrossAssetTransfer> crossAssetTransfers;
    private String feeAsset;

    public Document toDocument() {
        Document document = DocumentTransferTool.toDocument(this, "txHash");
        List<Document> nulsTransferList = new ArrayList<>();
        for (NulsTransfer transfer : nulsTransfers) {
            Document doc = DocumentTransferTool.toDocument(transfer);
            nulsTransferList.add(doc);
        }

        List<Document> tokenTransferList = new ArrayList<>();
        for (TokenTransfer transfer : tokenTransfers) {
            Document doc = DocumentTransferTool.toDocument(transfer);
            tokenTransferList.add(doc);
        }

        List<Document> token721TransferList = new ArrayList<>();
        for (Token721Transfer transfer721 : token721Transfers) {
            Document doc = DocumentTransferTool.toDocument(transfer721);
            token721TransferList.add(doc);
        }

        List<Document> token1155TransferList = new ArrayList<>();
        for (Token1155Transfer transfer1155 : token1155Transfers) {
            Document doc = DocumentTransferTool.toDocument(transfer1155);
            token1155TransferList.add(doc);
        }

        List<Document> internalCreateList = new ArrayList<>();
        for (ContractInternalCreateInfo internalCreate : internalCreates) {
            Document doc = DocumentTransferTool.toDocument(internalCreate);
            internalCreateList.add(doc);
        }

        List<Document> crossAssetTransferList = new ArrayList<>();
        for (CrossAssetTransfer crossAssetTransfer : crossAssetTransfers) {
            Document doc = DocumentTransferTool.toDocument(crossAssetTransfer);
            crossAssetTransferList.add(doc);
        }

        document.put("nulsTransfers", nulsTransferList);
        document.put("tokenTransfers", tokenTransferList);
        document.put("token721Transfers", token721TransferList);
        document.put("token1155Transfers", token1155TransferList);
        document.put("internalCreates", internalCreateList);
        document.put("crossAssetTransfers", crossAssetTransferList);
        return document;
    }

    public static ContractResultInfo toInfo(Document document) {
        List<Document> documentList = (List<Document>) document.get("nulsTransfers");
        List<NulsTransfer> nulsTransferList = new ArrayList<>();
        for (Document doc : documentList) {
            NulsTransfer nulsTransfer = DocumentTransferTool.toInfo(doc, NulsTransfer.class);
            nulsTransferList.add(nulsTransfer);
        }

        documentList = (List<Document>) document.get("tokenTransfers");
        List<TokenTransfer> tokenTransferList = new ArrayList<>();
        for (Document doc : documentList) {
            TokenTransfer tokenTransfer = DocumentTransferTool.toInfo(doc, TokenTransfer.class);
            tokenTransferList.add(tokenTransfer);
        }

        documentList = (List<Document>) document.get("token721Transfers");
        List<Token721Transfer> token721TransferList = new ArrayList<>();
        for (Document doc : documentList) {
            Token721Transfer token721Transfer = DocumentTransferTool.toInfo(doc, Token721Transfer.class);
            token721TransferList.add(token721Transfer);
        }

        documentList = (List<Document>) document.get("token1155Transfers");
        List<Token1155Transfer> token1155TransferList = new ArrayList<>();
        for (Document doc : documentList) {
            Token1155Transfer token1155Transfer = DocumentTransferTool.toInfo(doc, Token1155Transfer.class);
            token1155TransferList.add(token1155Transfer);
        }

        documentList = (List<Document>) document.get("internalCreates");
        List<ContractInternalCreateInfo> internalCreateList = new ArrayList<>();
        for (Document doc : documentList) {
            ContractInternalCreateInfo internalCreate = DocumentTransferTool.toInfo(doc, ContractInternalCreateInfo.class);
            internalCreateList.add(internalCreate);
        }

        documentList = (List<Document>) document.get("crossAssetTransfers");
        List<CrossAssetTransfer> crossAssetTransferList = new ArrayList<>();
        for (Document doc : documentList) {
            CrossAssetTransfer crossAssetTransfer = DocumentTransferTool.toInfo(doc, CrossAssetTransfer.class);
            crossAssetTransferList.add(crossAssetTransfer);
        }

        document.remove("nulsTransfers");
        document.remove("tokenTransfers");
        document.remove("token721Transfers");
        document.remove("token1155Transfers");
        document.remove("internalCreates");
        document.remove("crossAssetTransfers");

        ContractResultInfo resultInfo = DocumentTransferTool.toInfo(document, "txHash", ContractResultInfo.class);
        resultInfo.setNulsTransfers(nulsTransferList);
        resultInfo.setTokenTransfers(tokenTransferList);
        resultInfo.setToken721Transfers(token721TransferList);
        resultInfo.setToken1155Transfers(token1155TransferList);
        resultInfo.setInternalCreates(internalCreateList);
        resultInfo.setCrossAssetTransfers(crossAssetTransferList);
        return resultInfo;
    }

    public List<CrossAssetTransfer> getCrossAssetTransfers() {
        return crossAssetTransfers;
    }

    public void setCrossAssetTransfers(List<CrossAssetTransfer> crossAssetTransfers) {
        this.crossAssetTransfers = crossAssetTransfers;
    }

    public List<ContractInternalCreateInfo> getInternalCreates() {
        return internalCreates;
    }

    public void setInternalCreates(List<ContractInternalCreateInfo> internalCreates) {
        this.internalCreates = internalCreates;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public long getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(long gasLimit) {
        this.gasLimit = gasLimit;
    }

    public long getGasUsed() {
        return gasUsed;
    }

    public void setGasUsed(long gasUsed) {
        this.gasUsed = gasUsed;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getTxSizeFee() {
        return txSizeFee;
    }

    public void setTxSizeFee(String txSizeFee) {
        this.txSizeFee = txSizeFee;
    }

    public String getActualContractFee() {
        return actualContractFee;
    }

    public void setActualContractFee(String actualContractFee) {
        this.actualContractFee = actualContractFee;
    }

    public String getRefundFee() {
        return refundFee;
    }

    public void setRefundFee(String refundFee) {
        this.refundFee = refundFee;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<NulsTransfer> getNulsTransfers() {
        return nulsTransfers;
    }

    public void setNulsTransfers(List<NulsTransfer> nulsTransfers) {
        this.nulsTransfers = nulsTransfers;
    }

    public List<TokenTransfer> getTokenTransfers() {
        return tokenTransfers;
    }

    public void setTokenTransfers(List<TokenTransfer> tokenTransfers) {
        this.tokenTransfers = tokenTransfers;
    }

    public List<Token721Transfer> getToken721Transfers() {
        return token721Transfers;
    }

    public void setToken721Transfers(List<Token721Transfer> token721Transfers) {
        this.token721Transfers = token721Transfers;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<String> getContractTxList() {
        return contractTxList;
    }

    public void setContractTxList(List<String> contractTxList) {
        this.contractTxList = contractTxList;
    }

    public List<String> getEvents() {
        return events;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }

    public List<Token1155Transfer> getToken1155Transfers() {
        return token1155Transfers;
    }

    public void setToken1155Transfers(List<Token1155Transfer> token1155Transfers) {
        this.token1155Transfers = token1155Transfers;
    }

    public String getFeeAsset() {
        return feeAsset;
    }

    public void setFeeAsset(String feeAsset) {
        this.feeAsset = feeAsset;
    }
}
