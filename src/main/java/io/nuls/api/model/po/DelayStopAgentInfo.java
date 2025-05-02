package io.nuls.api.model.po;

import io.nuls.base.data.NulsHash;

public class DelayStopAgentInfo extends TxDataInfo {

    private NulsHash agentHash;

    private long height;

    public NulsHash getAgentHash() {
        return agentHash;
    }

    public void setAgentHash(NulsHash agentHash) {
        this.agentHash = agentHash;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }
}
