package io.nuls.api.model.po;

public class ActiveAddressVo {
    private String date;
    private int count;

    public ActiveAddressVo() {
    }

    public ActiveAddressVo(String date, int count) {
        this.date = date;
        this.count = count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
