package io.nuls.api.rpc.rest.model;

/**
 * @author Niels
 */
public class IpEntity {
    private long from;
    private long end;

    public long getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }
}
