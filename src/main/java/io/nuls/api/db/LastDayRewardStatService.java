package io.nuls.api.db;

import io.nuls.api.model.po.LastDayRewardStatInfo;

public interface LastDayRewardStatService {

    LastDayRewardStatInfo getInfo(int chainId);

    void save(int chainId, LastDayRewardStatInfo statInfo);

    void update(int chainId, LastDayRewardStatInfo statInfo);
}
