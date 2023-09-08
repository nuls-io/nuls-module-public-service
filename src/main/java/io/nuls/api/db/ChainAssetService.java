package io.nuls.api.db;

import io.nuls.api.model.po.PageInfo;
import io.nuls.api.model.po.asset.ChainAssetInfo;
import io.nuls.api.model.po.asset.ChainAssetTx;

import java.util.List;
import java.util.Set;

public interface ChainAssetService {

    void initCache();

    PageInfo<ChainAssetInfo> getList(int pageNumber, int pageSize);

    PageInfo<ChainAssetTx> getTxList(String assetKey, int pageNumber, int pageSize);

    void saveList(List<ChainAssetInfo> list);

    void saveTxList(List<ChainAssetTx> list);

    void updateCount(int chainId,Set<String> chainAssetCountList);
}
