package io.nuls.api.model.po;

public class LastDayRewardStatInfo {

    private String lastDayRewardKey;

    private long lastStatHeight;

    private long lastStatTime;

    public long getLastStatHeight() {
        return lastStatHeight;
    }

    public void setLastStatHeight(long lastStatHeight) {
        this.lastStatHeight = lastStatHeight;
    }

    public long getLastStatTime() {
        return lastStatTime;
    }

    public void setLastStatTime(long lastStatTime) {
        this.lastStatTime = lastStatTime;
    }

    public String getLastDayRewardKey() {
        return lastDayRewardKey;
    }

    public void setLastDayRewardKey(String lastDayRewardKey) {
        this.lastDayRewardKey = lastDayRewardKey;
    }
}
