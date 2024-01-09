package io.nuls.api.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetSystemDictItem {
    //{"dictLabel":"NULSd6HgeV64HLRvTP1FacTcpCchdLUH6VVti","dictValue":"binance.com","dictType":"special_address","default":true}
    private String dictLabel, dictValue;

    public AssetSystemDictItem(String dictLabel, String dictValue) {
        this.dictLabel = dictLabel;
        this.dictValue = dictValue;
    }

    public AssetSystemDictItem() {
    }

    public String getDictLabel() {
        return dictLabel;
    }

    public String getDictValue() {
        return dictValue;
    }

    public void setDictLabel(String dictLabel) {
        this.dictLabel = dictLabel;
    }

    public void setDictValue(String dictValue) {
        this.dictValue = dictValue;
    }
}
