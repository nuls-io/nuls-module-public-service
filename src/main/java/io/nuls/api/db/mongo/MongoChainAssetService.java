package io.nuls.api.db.mongo;

import com.mongodb.client.model.*;
import io.nuls.api.cache.AssetSystemCache;
import io.nuls.api.db.ChainAssetService;
import io.nuls.api.manager.CacheManager;
import io.nuls.api.model.dto.AssetsSystemTokenInfoVo;
import io.nuls.api.model.po.AssetInfo;
import io.nuls.api.model.po.PageInfo;
import io.nuls.api.model.po.asset.ChainAssetHolderInfo;
import io.nuls.api.model.po.asset.ChainAssetInfo;
import io.nuls.api.model.po.asset.ChainAssetTx;
import io.nuls.api.utils.DocumentTransferTool;
import io.nuls.core.core.annotation.Autowired;
import io.nuls.core.core.annotation.Component;
import io.nuls.core.log.Log;
import io.nuls.core.model.StringUtils;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.nuls.api.constant.DBTableConstant.*;

@Component
public class MongoChainAssetService implements ChainAssetService {
    @Autowired
    private MongoDBService mongoDBService;

    @Override
    public List<ChainAssetInfo> getList() {
        List<Document> docsList = this.mongoDBService.query(CHAIN_ASSET_TABLE);
        List<ChainAssetInfo> infoList = new ArrayList<>();
        for (Document document : docsList) {
            infoList.add(DocumentTransferTool.toInfo(document, "id", ChainAssetInfo.class));
        }
        return infoList;
    }

    @Override
    public PageInfo<ChainAssetTx> getTxList(String assetKey, int pageNumber, int pageSize, Integer type, String from, String to) {
        List<Document> docsList = this.mongoDBService.pageQuery(CHAIN_ASSET_TX_TABLE, Filters.eq("assetId", assetKey), Sorts.descending("createTime"), pageNumber, pageSize);
        List<ChainAssetTx> infoList = new ArrayList<>();
        long totalCount = mongoDBService.getCount(CHAIN_ASSET_TX_TABLE, Filters.eq("assetId", assetKey));
        for (Document document : docsList) {
            infoList.add(DocumentTransferTool.toInfo(document, "id", ChainAssetTx.class));
        }
        PageInfo<ChainAssetTx> pageInfo = new PageInfo<>(pageNumber, pageSize, totalCount, infoList);
        return pageInfo;
    }

    @Override
    public void updateAssetInfo(ChainAssetInfo info) {
        this.mongoDBService.updateOne(CHAIN_ASSET_TABLE, Filters.eq("_id", info.getId()), DocumentTransferTool.toDocument(info, "id"));
    }

    @Override
    public ChainAssetInfo get(String assetKey) {
        if (StringUtils.isBlank(assetKey)) {
            return null;
        }
        Document document = mongoDBService.findOne(CHAIN_ASSET_TABLE, Filters.eq("_id", assetKey));
        return DocumentTransferTool.toInfo(document, "id", ChainAssetInfo.class);
    }

    @Override
    public PageInfo<ChainAssetHolderInfo> getHoldersByAssetKey(Integer chainId, String assetKey, Integer pageNumber, Integer pageSize) {
        String arr[] = assetKey.split("-");

        return null;
    }

    @Override
    public void save(int chainId, Map<String, ChainAssetTx> chainAssetTxMap, Set<String> chainAssetCountList) {
        try {
            this.updateCount(chainId, chainAssetCountList);
        } catch (Exception e) {
            Log.error(e);
        }
        try {
            this.saveTxList(chainAssetTxMap);
        } catch (Exception e) {
            Log.error(e);
        }
    }

    @Override
    public void rollback(int chainId, Map<String, ChainAssetTx> chainAssetTxMap, Set<String> chainAssetCountList) {
        try {
            for (String assetKey : chainAssetCountList) {
                Document document = mongoDBService.findOne(CHAIN_ASSET_TABLE, Filters.eq("_id", assetKey));
                if (null == document) {
                    continue;
                }
                long txCount = Long.parseLong("" + document.get("txCount"));
                document.put("txCount", txCount - 1);
                this.mongoDBService.updateOne(CHAIN_ASSET_TABLE, Filters.eq("_id", assetKey), document);
            }
        } catch (Exception e) {
            Log.error(e);
        }
        try {
            if (chainAssetTxMap.isEmpty()) {
                return;
            }
            BulkWriteOptions options = new BulkWriteOptions();
            options.ordered(false);
            List<WriteModel<Document>> modelList = new ArrayList<>();
            int i = 0;
            for (ChainAssetTx info : chainAssetTxMap.values()) {

                modelList.add(new DeleteOneModel<>(Filters.eq("_id", info.getId())));

                i++;
                if (i == 1000) {
                    mongoDBService.bulkWrite(CHAIN_ASSET_TX_TABLE, modelList, options);
                    modelList.clear();
                    i = 0;
                }
            }
            if (modelList.size() > 0) {
                mongoDBService.bulkWrite(CHAIN_ASSET_TX_TABLE, modelList, options);
            }
        } catch (Exception e) {
            Log.error(e);
        }
    }

    private AssetInfo getAssetRegInfo(int chainId, String assetKey) {
        AssetInfo info = CacheManager.getAssetInfoMap().get(assetKey);
        if (null != info) {
            return info;
        }

        return info;
    }

    private void updateCount(int chainId, Set<String> chainAssetCountList) {
        for (String assetKey : chainAssetCountList) {
            Document document = mongoDBService.findOne(CHAIN_ASSET_TABLE, Filters.eq("_id", assetKey));
            boolean insert = false;
            if (null == document) {
                //如果不存在，则
                AssetInfo info = getAssetRegInfo(chainId, assetKey);
                if(null==info){
                    System.out.println();
                }
                ChainAssetInfo po = new ChainAssetInfo();
                po.setAddresses(0);
                po.setDecimals(info.getDecimals());
                po.setId(assetKey);
                AssetsSystemTokenInfoVo assetVo = AssetSystemCache.getAssetCache(assetKey);
                if(null!=assetVo){
                    Integer registerChainId = Math.toIntExact(assetVo.getSourceChainId());
                    if (null == registerChainId) {
                        registerChainId = info.getChainId();
                    }
                    po.setSourceChainId(registerChainId);
                    po.setName(assetVo.getName());
                    po.setAssetType(registerChainId != chainId ? 1 : 0);
                }else {
                    po.setName(info.getSymbol());
                }
                po.setInAmount("0");
                po.setSymbol(info.getSymbol());
                po.setTxCount(0);
                po.setOutAmount("0");
                po.setCrossTxCount(0);
                document = DocumentTransferTool.toDocument(po, "id");
                insert = true;
            }
            long txCount = Long.parseLong("" + document.get("txCount"));
            document.put("txCount", txCount + 1);
            if (insert) {
                this.mongoDBService.insertOne(CHAIN_ASSET_TABLE, document);
            } else {
                this.mongoDBService.updateOne(CHAIN_ASSET_TABLE, Filters.eq("_id", assetKey), document);
            }
        }
    }

    private void saveTxList(Map<String, ChainAssetTx> chainAssetTxMap) {
        if (chainAssetTxMap.isEmpty()) {
            return;
        }
        BulkWriteOptions options = new BulkWriteOptions();
        options.ordered(false);
        List<WriteModel<Document>> modelList = new ArrayList<>();
        int i = 0;
        for (ChainAssetTx info : chainAssetTxMap.values()) {
            Document document = DocumentTransferTool.toDocument(info, "id");

            modelList.add(new InsertOneModel(document));

            i++;
            if (i == 1000) {
                mongoDBService.bulkWrite(CHAIN_ASSET_TX_TABLE, modelList, options);
                modelList.clear();
                i = 0;
            }
        }
        if (modelList.size() > 0) {
            mongoDBService.bulkWrite(CHAIN_ASSET_TX_TABLE, modelList, options);
        }
    }
}
