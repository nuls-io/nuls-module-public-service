package io.nuls.api.model.dto;

import io.nuls.api.model.po.TransactionInfo;

import java.util.List;

public class TransactionInfoVo {

    private TransactionInfoVo(){
    }

    public TransactionInfoVo(TransactionInfo tx, List<TransferItemVo> fromList, List<TransferItemVo> toList) {
        this.tx = tx;
        this.fromList = fromList;
        this.toList = toList;
    }

    private TransactionInfo tx;
    private List<TransferItemVo> fromList;
    private List<TransferItemVo> toList;

    public TransactionInfo getTx() {
        return tx;
    }

    public void setTx(TransactionInfo tx) {
        this.tx = tx;
    }

    public List<TransferItemVo> getFromList() {
        return fromList;
    }

    public void setFromList(List<TransferItemVo> fromList) {
        this.fromList = fromList;
    }

    public List<TransferItemVo> getToList() {
        return toList;
    }

    public void setToList(List<TransferItemVo> toList) {
        this.toList = toList;
    }
}
