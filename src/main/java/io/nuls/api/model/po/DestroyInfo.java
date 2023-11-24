package io.nuls.api.model.po;

public class DestroyInfo {

    private String type;
    private String address;

    private String reason;

    private String value;

    private String proportion;

    public DestroyInfo(String address, String type, String reason, String value, String proportion) {
        this.address = address;
        this.reason = reason;
        this.value = value;
        this.type = type;
        this.proportion = proportion;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProportion() {
        return proportion;
    }

    public void setProportion(String proportion) {
        this.proportion = proportion;
    }
}
