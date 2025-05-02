package io.nuls.api.model.po;

public class ActiveAddressPo {
    private String date;
    private int dayIndex;
    private int count;
    private long endHeight;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDayIndex() {
        return dayIndex;
    }

    public void setDayIndex(int dayIndex) {
        this.dayIndex = dayIndex;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getEndHeight() {
        return endHeight;
    }

    public void setEndHeight(long endHeight) {
        this.endHeight = endHeight;
    }
}
