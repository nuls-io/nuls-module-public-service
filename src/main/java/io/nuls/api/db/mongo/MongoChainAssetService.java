package io.nuls.api.db.mongo;

import com.mongodb.client.model.*;
import io.nuls.api.db.ChainAssetService;
import io.nuls.api.model.po.PageInfo;
import io.nuls.api.model.po.asset.ChainAssetInfo;
import io.nuls.api.model.po.asset.ChainAssetTx;
import io.nuls.api.utils.DocumentTransferTool;
import io.nuls.core.core.annotation.Autowired;
import io.nuls.core.core.annotation.Component;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static io.nuls.api.constant.DBTableConstant.*;

@Component
public class MongoChainAssetService implements ChainAssetService {
    @Autowired
    private MongoDBService mongoDBService;

    @Override
    public void initCache() {
        //todo 更新资产管理系统功能，获取资产详情需要的信息
    }

    @Override
    public PageInfo<ChainAssetInfo> getList(int pageNumber, int pageSize) {
        List<Document> docsList = this.mongoDBService.pageQuery(CHAIN_ASSET_TABLE, Sorts.descending("addresses"), pageNumber, pageSize);
        List<ChainAssetInfo> infoList = new ArrayList<>();
        long totalCount = mongoDBService.getEstimateCount(CHAIN_ASSET_TABLE);
        for (Document document : docsList) {
            infoList.add(DocumentTransferTool.toInfo(document, "id", ChainAssetInfo.class));
        }
        PageInfo<ChainAssetInfo> pageInfo = new PageInfo<>(pageNumber, pageSize, totalCount, infoList);
        return pageInfo;
    }

    @Override
    public PageInfo<ChainAssetTx> getTxList(String assetKey, int pageNumber, int pageSize) {
        List<Document> docsList = this.mongoDBService.pageQuery(CHAIN_ASSET_TX_TABLE, Filters.eq("assetId", assetKey), Sorts.descending("createTime"), pageNumber, pageSize);
        List<ChainAssetTx> infoList = new ArrayList<>();
        long totalCount = mongoDBService.getCount(CHAIN_ASSET_TX_TABLE, Filters.eq("assetId", assetKey));
        for (Document document : docsList) {
            infoList.add(DocumentTransferTool.toInfo(document, "hash", ChainAssetTx.class));
        }
        PageInfo<ChainAssetTx> pageInfo = new PageInfo<>(pageNumber, pageSize, totalCount, infoList);
        return pageInfo;
    }

    @Override
    public void saveList(List<ChainAssetInfo> list) {
        if (list.isEmpty()) {
            return;
        }
        BulkWriteOptions options = new BulkWriteOptions();
        options.ordered(false);
        List<WriteModel<Document>> modelList = new ArrayList<>();
        int i = 0;
        for (ChainAssetInfo info : list) {
            Document document = DocumentTransferTool.toDocument(info, "id");

            if (!info.isUpdate()) {
                modelList.add(new InsertOneModel(document));
            } else {
                modelList.add(new ReplaceOneModel<>(Filters.eq("_id", info.getId()), document));
            }
            i++;
            if (i == 1000) {
                mongoDBService.bulkWrite(CHAIN_ASSET_TABLE, modelList, options);
                modelList.clear();
                i = 0;
            }
        }
        if (modelList.size() > 0) {
            mongoDBService.bulkWrite(CHAIN_ASSET_TABLE, modelList, options);
        }
    }

    @Override
    public void saveTxList(List<ChainAssetTx> list) {
        if (list.isEmpty()) {
            return;
        }
        BulkWriteOptions options = new BulkWriteOptions();
        options.ordered(false);
        List<WriteModel<Document>> modelList = new ArrayList<>();
        int i = 0;
        for (ChainAssetTx info : list) {
            Document document = DocumentTransferTool.toDocument(info, "hash");

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

    @Override
    public void updateCount(int chainId, Set<String> chainAssetCountList) {
        for (String assetKey : chainAssetCountList) {
            Document document = mongoDBService.findOne(CHAIN_ASSET_TABLE, Filters.eq("_id", assetKey));
            long txCount = mongoDBService.getCount(CHAIN_ASSET_TX_TABLE, Filters.eq("assetId", assetKey));
            document.put("txCount", (int) txCount);
            String[] arr = assetKey.split("-");
            long holderCount = mongoDBService.getCount(ACCOUNT_LEDGER_TABLE + chainId, Filters.and(Filters.eq("chainId", Integer.parseInt(arr[0])), Filters.eq("assetId", Integer.parseInt(arr[1]))));
            document.put("addresses", (int) holderCount);
            this.mongoDBService.updateOne(CHAIN_ASSET_TABLE, Filters.eq("_id", assetKey), document);
        }

    }
}
