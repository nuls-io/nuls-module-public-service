package io.nuls.api.db.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.WriteModel;
import io.nuls.api.cache.ApiCache;
import io.nuls.api.db.AgentService;
import io.nuls.api.db.BlockService;
import io.nuls.api.manager.CacheManager;
import io.nuls.api.model.po.*;
import io.nuls.api.model.po.mini.MiniBlockHeaderInfo;
import io.nuls.api.utils.DocumentTransferTool;
import io.nuls.core.core.annotation.Autowired;
import io.nuls.core.core.annotation.Component;
import io.nuls.core.model.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static io.nuls.api.constant.DBTableConstant.BLOCK_HEADER_TABLE;
import static io.nuls.api.constant.DBTableConstant.BLOCK_HEX_TABLE;

@Component
public class MongoBlockServiceImpl implements BlockService {

    @Autowired
    private MongoDBService mongoDBService;
    @Autowired
    private MongoChainServiceImpl mongoChainServiceImpl;
    @Autowired
    AgentService agentService;

    public BlockHeaderInfo getBestBlockHeader(int chainId) {
        ApiCache apiCache = CacheManager.getCache(chainId);
        if (apiCache == null) {
            return null;
        }
        if (apiCache.getBestHeader() == null) {
            SyncInfo syncInfo = mongoChainServiceImpl.getSyncInfo(chainId);
            if (syncInfo == null) {
                return null;
            }
            apiCache.setBestHeader(getBlockHeader(chainId, syncInfo.getBestHeight()));
        }
        return apiCache.getBestHeader();
    }

    public BlockHeaderInfo getBlockHeader(int chainId, long height) {
        Document document = mongoDBService.findOne(BLOCK_HEADER_TABLE + chainId, Filters.eq("_id", height));
        if (document == null) {
            return null;
        }
        return DocumentTransferTool.toInfo(document, "height", BlockHeaderInfo.class);
    }

    public BlockHeaderInfo getBlockHeaderByHash(int chainId, String hash) {
        Document document = mongoDBService.findOne(BLOCK_HEADER_TABLE + chainId, Filters.eq("hash", hash));
        if (document == null) {
            return null;
        }
        return DocumentTransferTool.toInfo(document, "height", BlockHeaderInfo.class);
    }

    public void saveBLockHeaderInfo(int chainId, BlockHeaderInfo blockHeaderInfo) {
        Document document = DocumentTransferTool.toDocument(blockHeaderInfo, "height");
        document.remove("mainVersion");
        mongoDBService.insertOne(BLOCK_HEADER_TABLE + chainId, document);
    }

    public void saveBlockHexInfo(int chainId, BlockHexInfo hexInfo) {
        Document document = DocumentTransferTool.toDocument(hexInfo, "height");
        mongoDBService.insertOne(BLOCK_HEX_TABLE + chainId, document);
    }

    public BlockHexInfo getBlockHexInfo(int chainId, long height) {
        Document document = mongoDBService.findOne(BLOCK_HEX_TABLE + chainId, Filters.eq("_id", height));
        if (document == null) {
            return null;
        }
        return DocumentTransferTool.toInfo(document, "height", BlockHexInfo.class);
    }

    @Override
    public BlockHexInfo getBlockHexInfo(int chainId, String hash) {
        Document document = mongoDBService.findOne(BLOCK_HEADER_TABLE + chainId, Filters.eq("hash", hash));
        if (document == null) {
            return null;
        }
        long height = document.getLong("_id");
        document = mongoDBService.findOne(BLOCK_HEX_TABLE + chainId, Filters.eq("_id", height));
        if (document == null) {
            return null;
        }
        return DocumentTransferTool.toInfo(document, "height", BlockHexInfo.class);
    }

    @Override
    public BigInteger getLast24HourRewards(int chainId) {
        long time = System.currentTimeMillis() / 1000;
        time = time - 24 * 60 * 60;
        Bson filter = Filters.gte("createTime", time);
        BasicDBObject fields = new BasicDBObject();
        fields.append("reward", 1);
        BigInteger reward = BigInteger.ZERO;
        List<Document> docsList = this.mongoDBService.query(BLOCK_HEADER_TABLE + chainId, filter, fields);
        for (Document document : docsList) {
            reward = reward.add(new BigInteger(document.getString("reward")));
        }
        return reward;
    }

    public void saveList(int chainId, List<BlockHeaderInfo> blockHeaderInfos) {
        List<Document> documentList = new ArrayList<>();
        for (BlockHeaderInfo headerInfo : blockHeaderInfos) {
            Document document = DocumentTransferTool.toDocument(headerInfo);
            documentList.add(document);
        }
        long time1 = System.currentTimeMillis();
        mongoDBService.insertMany(BLOCK_HEADER_TABLE + chainId, documentList);
        long time2 = System.currentTimeMillis();
        System.out.println("---------------------use:" + (time2 - time1));
    }

    public void saveBulkList(int chainId, List<BlockHeaderInfo> blockHeaderInfos) {
        List<WriteModel<Document>> modelList = new ArrayList<>();
        for (BlockHeaderInfo headerInfo : blockHeaderInfos) {
            Document document = DocumentTransferTool.toDocument(headerInfo, "height");
            modelList.add(new InsertOneModel(document));
        }
        long time1 = System.currentTimeMillis();
        mongoDBService.bulkWrite(BLOCK_HEADER_TABLE + chainId, modelList);
        long time2 = System.currentTimeMillis();
        System.out.println("---------------------use:" + (time2 - time1));

    }

    public PageInfo<MiniBlockHeaderInfo> pageQuery(int chainId, int pageIndex, int pageSize, String packingAddress, boolean filterEmptyBlocks) {
        if (!CacheManager.isChainExist(chainId)) {
            return new PageInfo<>(pageIndex, pageSize);
        }
        Bson filter = null;
        if (StringUtils.isNotBlank(packingAddress)) {
            filter = Filters.eq("packingAddress", packingAddress);
        }
        if (filterEmptyBlocks) {
            if (filter == null) {
                filter = Filters.gt("txCount", 1);
            } else {
                filter = Filters.and(filter, Filters.gt("txCount", 1));
            }
        }
        long totalCount;
        if(StringUtils.isNotBlank(packingAddress)){
            AgentInfo agentInfo = agentService.getAgentByPackingAddress(chainId,packingAddress);
            if(agentInfo == null){
                totalCount = 0;
            }else {
                totalCount = agentInfo.getTotalPackingCount();
            }
        }else{
            totalCount = mongoDBService.getEstimateCount(BLOCK_HEADER_TABLE + chainId);
        }
        BasicDBObject fields = new BasicDBObject();
        fields.append("_id", 1).append("createTime", 1).append("txCount", 1).append("agentHash", 1).
                append("agentId", 1).append("agentAlias", 1).append("size", 1).append("reward", 1);

        List<Document> docsList = this.mongoDBService.pageQuery(BLOCK_HEADER_TABLE + chainId, filter, fields, Sorts.descending("_id"), pageIndex, pageSize);
        List<MiniBlockHeaderInfo> list = new ArrayList<>();
        for (Document document : docsList) {
            list.add(DocumentTransferTool.toInfo(document, "height", MiniBlockHeaderInfo.class));
        }
        PageInfo<MiniBlockHeaderInfo> pageInfo = new PageInfo<>(pageIndex, pageSize, totalCount, list);
        return pageInfo;
    }

    @Override
    public List<MiniBlockHeaderInfo> getBlockList(int chainId, long startTime, long endTime) {
        if (!CacheManager.isChainExist(chainId)) {
            return new ArrayList<>();
        }
        BasicDBObject fields = new BasicDBObject();
        fields.append("_id", 1).append("createTime", 1).append("txCount", 1).append("agentHash", 1).
                append("agentId", 1).append("agentAlias", 1).append("size", 1).append("reward", 1);
        Bson filter = Filters.and(Filters.gt("createTime", startTime), Filters.lte("createTime", endTime));
        List<Document> docsList = this.mongoDBService.query(BLOCK_HEADER_TABLE + chainId, filter, fields, Sorts.descending("_id"));
        List<MiniBlockHeaderInfo> list = new ArrayList<>();
        for (Document document : docsList) {
            list.add(DocumentTransferTool.toInfo(document, "height", MiniBlockHeaderInfo.class));
        }
        return list;
    }

    @Override
    public int getBlockPackageTxCount(int chainId, long startHeight, long endHeight) {
        if (!CacheManager.isChainExist(chainId)) {
            return 0;
        }
        BasicDBObject fields = new BasicDBObject();
        fields.append("txCount", 1);
        Bson filter = Filters.and(Filters.gt("_id", startHeight), Filters.lte("_id", endHeight));
        List<Document> docsList = this.mongoDBService.query(BLOCK_HEADER_TABLE + chainId, filter, fields, Sorts.descending("_id"));
        int count = 0;
        for (Document document : docsList) {
            count += document.getInteger("txCount");
        }
        return count;
    }

    public long getMaxHeight(int chainId, long endTime) {
        return this.mongoDBService.getMax(BLOCK_HEADER_TABLE + chainId, "_id", Filters.lte("createTime", endTime));
    }

    public void deleteBlockHeader(int chainId, long height) {
        mongoDBService.delete(BLOCK_HEADER_TABLE + chainId, Filters.eq("_id", height));
        mongoDBService.delete(BLOCK_HEX_TABLE + chainId, Filters.eq("_id", height));
        ApiCache apiCache = CacheManager.getCache(chainId);
        apiCache.setBestHeader(null);
    }

}
