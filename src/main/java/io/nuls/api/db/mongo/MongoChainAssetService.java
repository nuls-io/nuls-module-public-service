package io.nuls.api.db.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.client.model.*;
import io.nuls.api.cache.AssetSystemCache;
import io.nuls.api.db.AccountLedgerService;
import io.nuls.api.db.ChainAssetService;
import io.nuls.api.manager.CacheManager;
import io.nuls.api.model.dto.AssetsSystemTokenInfoVo;
import io.nuls.api.model.po.AccountLedgerInfo;
import io.nuls.api.model.po.AssetInfo;
import io.nuls.api.model.po.PageInfo;
import io.nuls.api.model.po.asset.ChainAssetHolderInfo;
import io.nuls.api.model.po.asset.ChainAssetInfo;
import io.nuls.api.model.po.asset.ChainAssetInfoVo;
import io.nuls.api.model.po.asset.ChainAssetTx;
import io.nuls.api.model.po.mini.MiniAccountInfo;
import io.nuls.api.utils.DBUtil;
import io.nuls.api.utils.DocumentTransferTool;
import io.nuls.core.core.annotation.Autowired;
import io.nuls.core.core.annotation.Component;
import io.nuls.core.log.Log;
import io.nuls.core.model.DoubleUtils;
import io.nuls.core.model.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.nuls.api.constant.DBTableConstant.*;

@Component
public class MongoChainAssetService implements ChainAssetService {
    @Autowired
    private AccountLedgerService accountLedgerService;
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
    public PageInfo<ChainAssetInfoVo> getList(int pageNumber, int pageSize) {
        List<Document> docsList = this.mongoDBService.pageQuery(CHAIN_ASSET_TABLE, Sorts.descending("addresses"), pageNumber, pageSize);
        long totalCount = mongoDBService.getCount(CHAIN_ASSET_TABLE);
        List<ChainAssetInfoVo> infoList = new ArrayList<>();
        for (Document document : docsList) {
            ChainAssetInfo info = DocumentTransferTool.toInfo(document, "id", ChainAssetInfo.class);
            infoList.add(new ChainAssetInfoVo(info));
        }
        PageInfo<ChainAssetInfoVo> pageInfo = new PageInfo<>(pageNumber, pageSize, totalCount, infoList);
        return pageInfo;
    }

    @Override
    public PageInfo<ChainAssetTx> getTxList(String assetKey, int pageNumber, int pageSize, Integer type, String from, String to) {
        Bson filter = Filters.eq("assetId", assetKey);
        if (null != type) {
            filter = Filters.and(filter, Filters.eq("txType", type));
        }
        if (StringUtils.isNotBlank(from) && StringUtils.isNotBlank(to)) {
            filter = Filters.and(filter, Filters.or(Filters.eq("from", from), Filters.eq("to", to)));
        } else if (StringUtils.isNotBlank(from)) {
            filter = Filters.and(filter, Filters.eq("from", from));
        } else if (StringUtils.isNotBlank(to)) {
            filter = Filters.and(filter, Filters.eq("to", to));
        }

        List<Document> docsList = this.mongoDBService.pageQuery(CHAIN_ASSET_TX_TABLE, filter, Sorts.descending("createTime"), pageNumber, pageSize);
        List<ChainAssetTx> infoList = new ArrayList<>();
//        long totalCount = mongoDBService.getCount(CHAIN_ASSET_TX_TABLE, Filters.eq("assetId", assetKey));
        for (Document document : docsList) {
            infoList.add(DocumentTransferTool.toInfo(document, "id", ChainAssetTx.class));
        }
        PageInfo<ChainAssetTx> pageInfo = new PageInfo<>(pageNumber, pageSize, 0, infoList);
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
        PageInfo<MiniAccountInfo> list = accountLedgerService.getAssetRanking(chainId, Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), pageNumber, pageSize);
        List<ChainAssetHolderInfo> infoList = new ArrayList<>();
        for (MiniAccountInfo accountInfo : list.getList()) {
            ChainAssetHolderInfo vo = new ChainAssetHolderInfo();
            vo.setAddress(accountInfo.getAddress());
            vo.setBalance(accountInfo.getTotalBalance().toString());
            AssetsSystemTokenInfoVo info = AssetSystemCache.getAssetCache(assetKey);
            BigDecimal balance = new BigDecimal(accountInfo.getTotalBalance());
            if (null != info && StringUtils.isNotBlank(info.getTotalSupply())) {
                BigDecimal total = new BigDecimal(info.getTotalSupply());
                BigDecimal rate = DoubleUtils.div(balance, total);
                vo.setRate(DoubleUtils.getRoundStr(rate.doubleValue() * 100, 4));
            }
            if (null != info && StringUtils.isNotBlank(info.getPrice())) {
                double price = Double.parseDouble(info.getPrice());
                double val = DoubleUtils.mul(price, balance.divide(BigDecimal.TEN.pow(Math.toIntExact(info.getDecimals()))));
                vo.setValue(DoubleUtils.getRoundStr(val, 2));
                AssetsSystemTokenInfoVo nulsInfo = AssetSystemCache.getAssetCache(chainId + "-1");
                if (null != nulsInfo && StringUtils.isNotBlank(nulsInfo.getPrice())) {
                    vo.setNulsValue(DoubleUtils.getRoundStr(DoubleUtils.div(val, Double.parseDouble(nulsInfo.getPrice()))));
                }
            }
            vo.setTag(AssetSystemCache.getAddressTag(accountInfo.getAddress()));
            infoList.add(vo);
        }
        PageInfo<ChainAssetHolderInfo> pageInfo = new PageInfo<>(pageNumber, pageSize, list.getTotalCount(), infoList);
        return pageInfo;
    }

    @Override
    public ChainAssetHolderInfo getOneHolderByAssetKey(Integer chainId, String assetKey, String address) {
        String arr[] = assetKey.split("-");
        String key = DBUtil.getAccountAssetKey(address, Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
        AccountLedgerInfo accountInfo = accountLedgerService.getAccountLedgerInfo(chainId, key);
        if (null == accountInfo) {
            return null;
        }
        ChainAssetHolderInfo vo = new ChainAssetHolderInfo();
        vo.setAddress(accountInfo.getAddress());
        vo.setBalance(accountInfo.getTotalBalance().toString());
        AssetsSystemTokenInfoVo info = AssetSystemCache.getAssetCache(assetKey);
        BigDecimal balance = new BigDecimal(accountInfo.getTotalBalance());
        if (null != info && StringUtils.isNotBlank(info.getTotalSupply())) {
            BigDecimal total = new BigDecimal(info.getTotalSupply());
            BigDecimal rate = DoubleUtils.div(balance, total);
            vo.setRate(DoubleUtils.getRoundStr(rate.doubleValue() * 100, 4));
        }
        if (null != info && StringUtils.isNotBlank(info.getPrice())) {
            double price = Double.parseDouble(info.getPrice());
            double val = DoubleUtils.mul(price, balance.divide(BigDecimal.TEN.pow(Math.toIntExact(info.getDecimals())), RoundingMode.DOWN));
            vo.setValue(DoubleUtils.getRoundStr(val, 2));
            AssetsSystemTokenInfoVo nulsInfo = AssetSystemCache.getAssetCache(chainId + "-1");
            if (null != nulsInfo && StringUtils.isNotBlank(nulsInfo.getPrice())) {
                vo.setNulsValue(DoubleUtils.getRoundStr(DoubleUtils.div(val, Double.parseDouble(nulsInfo.getPrice()))));
            }
        }
        vo.setTag(AssetSystemCache.getAddressTag(accountInfo.getAddress()));
        return vo;
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
                //If it does not exist, then
                AssetInfo info = getAssetRegInfo(chainId, assetKey);
                if (null == info) {
                    System.out.println();
                }
                ChainAssetInfo po = new ChainAssetInfo();
                po.setAddresses(0);
                po.setDecimals(info.getDecimals());
                po.setId(assetKey);
                AssetsSystemTokenInfoVo assetVo = AssetSystemCache.getAssetCache(assetKey);
                if (null != assetVo) {
                    Integer registerChainId = Math.toIntExact(assetVo.getSourceChainId());
                    if (null == registerChainId) {
                        registerChainId = info.getChainId();
                    }
                    po.setSourceChainId(registerChainId);
                    po.setName(assetVo.getName());
                    po.setAssetType(registerChainId != chainId ? 1 : 0);
                } else {
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

    @Override
    public void updateHolderCount(int chainId) {
        List<ChainAssetInfo> list = this.getList();
        for (ChainAssetInfo assetInfo : list) {
            long txCount = mongoDBService.getCount(CHAIN_ASSET_TX_TABLE, Filters.eq("assetId", assetInfo.getId()));
            assetInfo.setTxCount(txCount);
            String[] arr = assetInfo.getId().split("-");
            Bson filter = Filters.and(Filters.eq("chainId", Integer.parseInt(arr[0])), Filters.eq("assetId", Integer.parseInt(arr[1])));
            BasicDBObject fields = new BasicDBObject();
            fields.append("_id", 1).append("totalBalance", 1);
            List<Document> docList = mongoDBService.query(ACCOUNT_LEDGER_TABLE + chainId, filter, fields);
            BigInteger nulsChainSupply = BigInteger.ZERO;
            for (Document doc : docList) {
                nulsChainSupply = nulsChainSupply.add(new BigInteger(doc.getString("totalBalance")));
            }
            assetInfo.setNulsChainSupply(nulsChainSupply.toString());
            long nowTime = System.currentTimeMillis();
            if (changeDay(assetInfo.getAddressesTime(), nowTime)) {
                //If it is not the same day as last time, record the last quantity as yesterday's quantity
                assetInfo.setAddressesYesterday(assetInfo.getAddresses());
            }
            assetInfo.setAddresses(docList.size());
            assetInfo.setAddressesTime(nowTime);
            this.mongoDBService.updateOne(CHAIN_ASSET_TABLE, Filters.eq("_id", assetInfo.getId()), DocumentTransferTool.toDocument(assetInfo, "id"));
        }
    }

    private static final long dayTime = 24 * 3600000;

    private static boolean changeDay(Long addressesTime, long nowTime) {
        if (null == addressesTime) {
            return true;
        }
        return addressesTime / dayTime != nowTime / dayTime;
    }

}
