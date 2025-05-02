package io.nuls.api.db;

import io.nuls.api.model.po.PageInfo;
import io.nuls.api.model.po.asset.ChainAssetHolderInfo;
import io.nuls.api.model.po.asset.ChainAssetInfo;
import io.nuls.api.model.po.asset.ChainAssetInfoVo;
import io.nuls.api.model.po.asset.ChainAssetTx;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ChainAssetService {

    List<ChainAssetInfo> getList();

    PageInfo<ChainAssetTx> getTxList(String assetKey, int pageNumber, int pageSize, Integer type, String from, String to);

    void updateAssetInfo(ChainAssetInfo info);

    ChainAssetInfo get(String assetKey);

    PageInfo<ChainAssetHolderInfo> getHoldersByAssetKey(Integer chainId, String assetKey, Integer pageNumber, Integer pageSize);

    void save(int chainId, Map<String, ChainAssetTx> chainAssetTxMap, Set<String> chainAssetCountList);

    void rollback(int chainId, Map<String, ChainAssetTx> chainAssetTxMap, Set<String> chainAssetCountList);

    void updateHolderCount(int chainId);

    PageInfo<ChainAssetInfoVo> getList(int pageNumber, int pageSize);

    ChainAssetHolderInfo getOneHolderByAssetKey(Integer chainId, String assetKey, String address);
}
