package io.nuls.api.db.mongo;

import com.mongodb.client.model.*;
import io.nuls.api.cache.AssetSystemCache;
import io.nuls.api.db.Token1155Service;
import io.nuls.api.model.po.AccountToken1155Info;
import io.nuls.api.model.po.Nrc1155TokenIdInfo;
import io.nuls.api.model.po.PageInfo;
import io.nuls.api.model.po.Token1155Transfer;
import io.nuls.api.utils.DocumentTransferTool;
import io.nuls.core.core.annotation.Autowired;
import io.nuls.core.core.annotation.Component;
import io.nuls.core.model.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.math.BigInteger;
import java.util.*;

import static io.nuls.api.constant.DBTableConstant.*;

@Component
public class MongoToken1155ServiceImpl implements Token1155Service {

    @Autowired
    private MongoDBService mongoDBService;

    public AccountToken1155Info getAccountTokenInfo(int chainId, String key) {
        Bson query = Filters.eq("_id", key);

        Document document = mongoDBService.findOne(ACCOUNT_TOKEN1155_TABLE + chainId, query);
        if (document == null) {
            return null;
        }
        AccountToken1155Info tokenInfo = DocumentTransferTool.toInfo(document, "key", AccountToken1155Info.class);
        tokenInfo.setTag(AssetSystemCache.getAddressTag(tokenInfo.getAddress()));
        return tokenInfo;
    }

    public void saveAccountTokens(int chainId, Map<String, AccountToken1155Info> accountTokenInfos) {
        if (accountTokenInfos.isEmpty()) {
            return;
        }
        List<WriteModel<Document>> modelList = new ArrayList<>();
        for (AccountToken1155Info tokenInfo : accountTokenInfos.values()) {
            Document document = DocumentTransferTool.toDocument(tokenInfo, "key");
            if (tokenInfo.isNew()) {
                modelList.add(new InsertOneModel(document));
            } else {
                modelList.add(new ReplaceOneModel<>(Filters.eq("_id", tokenInfo.getKey()), document));
            }
        }
        BulkWriteOptions options = new BulkWriteOptions();
        options.ordered(false);
        mongoDBService.bulkWrite(ACCOUNT_TOKEN1155_TABLE + chainId, modelList, options);
    }

    public PageInfo<AccountToken1155Info> getAccountTokens(int chainId, String address, String contractAddress, int pageNumber, int pageSize) {
        Bson query;
        if (StringUtils.isNotBlank(contractAddress)) {
            query = Filters.and(Filters.eq("address", address), Filters.eq("contractAddress", contractAddress), Filters.ne("value", "0"));
        } else {
            query = Filters.and(Filters.eq("address", address), Filters.ne("value", "0"));
        }
        Bson sort = Sorts.descending("tokenCount");
        List<Document> docsList = this.mongoDBService.pageQuery(ACCOUNT_TOKEN1155_TABLE + chainId, query, sort, pageNumber, pageSize);
        List<AccountToken1155Info> accountTokenList = new ArrayList<>();
        long totalCount = mongoDBService.getCount(ACCOUNT_TOKEN1155_TABLE + chainId, query);

        for (Document document : docsList) {
            AccountToken1155Info tokenInfo = DocumentTransferTool.toInfo(document, "key", AccountToken1155Info.class);
            accountTokenList.add(tokenInfo);
            document = mongoDBService.findOne(CONTRACT_TABLE + chainId, Filters.eq("_id", tokenInfo.getContractAddress()));
            tokenInfo.setStatus(document.getInteger("status"));
            tokenInfo.setTag(AssetSystemCache.getAddressTag(tokenInfo.getAddress()));
        }

        PageInfo<AccountToken1155Info> pageInfo = new PageInfo<>(pageNumber, pageSize, totalCount, accountTokenList);
        return pageInfo;
    }
    @Override
    public List<AccountToken1155Info> getAccountTokens(int chainId, String address, String contractAddress) {
        Bson query;
        if (StringUtils.isNotBlank(contractAddress)) {
            query = Filters.and(Filters.eq("address", address), Filters.eq("contractAddress", contractAddress), Filters.ne("value", "0"));
        } else {
            query = Filters.and(Filters.eq("address", address), Filters.ne("value", "0"));
        }
        Bson sort = Sorts.descending("tokenCount");
        List<Document> docsList = this.mongoDBService.query(ACCOUNT_TOKEN1155_TABLE + chainId, query, sort);
        List<AccountToken1155Info> accountTokenList = new ArrayList<>();

        for (Document document : docsList) {
            AccountToken1155Info tokenInfo = DocumentTransferTool.toInfo(document, "key", AccountToken1155Info.class);
            accountTokenList.add(tokenInfo);
            document = mongoDBService.findOne(CONTRACT_TABLE + chainId, Filters.eq("_id", tokenInfo.getContractAddress()));
            tokenInfo.setStatus(document.getInteger("status"));
            tokenInfo.setTag(AssetSystemCache.getAddressTag(tokenInfo.getAddress()));
        }

        return accountTokenList;
    }

    public PageInfo<AccountToken1155Info> getContractTokens(int chainId, String contractAddress, int pageNumber, int pageSize) {
        Bson query = Filters.and(Filters.eq("contractAddress", contractAddress), Filters.ne("value", "0"));
        Bson sort = Sorts.descending("tokenId");
        List<Document> docsList = this.mongoDBService.pageQuery(ACCOUNT_TOKEN1155_TABLE + chainId, query, sort, pageNumber, pageSize);
        List<AccountToken1155Info> accountTokenList = new ArrayList<>();
        long totalCount = mongoDBService.getCount(ACCOUNT_TOKEN1155_TABLE + chainId, query);
        for (Document document : docsList) {
            AccountToken1155Info tokenInfo = DocumentTransferTool.toInfo(document, "key", AccountToken1155Info.class);
            tokenInfo.setTag(AssetSystemCache.getAddressTag(tokenInfo.getAddress()));
            accountTokenList.add(tokenInfo);
        }
        PageInfo<AccountToken1155Info> pageInfo = new PageInfo<>(pageNumber, pageSize, totalCount, accountTokenList);
        return pageInfo;
    }


    public void saveTokenTransfers(int chainId, List<Token1155Transfer> tokenTransfers) {
        if (tokenTransfers.isEmpty()) {
            return;
        }
        List<Document> documentList = new ArrayList<>();
        for (Token1155Transfer tokenTransfer : tokenTransfers) {
            Document document = DocumentTransferTool.toDocument(tokenTransfer);
            documentList.add(document);
        }
        InsertManyOptions options = new InsertManyOptions();
        options.ordered(false);
        mongoDBService.insertMany(TOKEN1155_TRANSFER_TABLE + chainId, documentList, options);
    }

    public void rollbackTokenTransfers(int chainId, List<String> tokenTxHashs, long height) {
        if (tokenTxHashs.isEmpty()) {
            return;
        }
        mongoDBService.delete(TOKEN1155_TRANSFER_TABLE + chainId, Filters.eq("height", height));
    }

    public PageInfo<Token1155Transfer> getTokenTransfers(int chainId, String address, String contractAddress, String tokenId, int pageIndex, int pageSize) {
        Bson filter = null;
        if (StringUtils.isNotBlank(address) && StringUtils.isNotBlank(contractAddress)) {
            Bson addressFilter = Filters.or(Filters.eq("fromAddress", address), Filters.eq("toAddress", address));
            filter = Filters.and(Filters.eq("contractAddress", contractAddress), addressFilter);
            if (StringUtils.isNotBlank(tokenId)) {
                filter = Filters.and(Filters.eq("tokenId", tokenId), filter);
            }
        } else if (StringUtils.isNotBlank(contractAddress)) {
            filter = Filters.eq("contractAddress", contractAddress);
            if (StringUtils.isNotBlank(tokenId)) {
                filter = Filters.and(Filters.eq("tokenId", tokenId), filter);
            }
        } else if (StringUtils.isNotBlank(address)) {
            filter = Filters.or(Filters.eq("fromAddress", address), Filters.eq("toAddress", address));
        }
        Bson sort = Sorts.descending("time");
        List<Document> docsList = this.mongoDBService.pageQuery(TOKEN1155_TRANSFER_TABLE + chainId, filter, sort, pageIndex, pageSize);
        List<Token1155Transfer> tokenTransfers = new ArrayList<>();
        long totalCount = mongoDBService.getCount(TOKEN1155_TRANSFER_TABLE + chainId, filter);
        for (Document document : docsList) {
            tokenTransfers.add(DocumentTransferTool.toInfo(document, Token1155Transfer.class));
        }

        PageInfo<Token1155Transfer> pageInfo = new PageInfo<>(pageIndex, pageSize, totalCount, tokenTransfers);
        return pageInfo;
    }

    @Override
    public void saveTokenIds(int chainId, Map<String, Nrc1155TokenIdInfo> tokenIdInfos) {
        if (tokenIdInfos.isEmpty()) {
            return;
        }
        Collection<Nrc1155TokenIdInfo> infos = tokenIdInfos.values();
        Set<String> insertKeys = new HashSet<>();
        LinkedHashMap<String, Document> insertDocuments = new LinkedHashMap<>();
        List<WriteModel<Document>> modelList = new ArrayList<>();
        for (Nrc1155TokenIdInfo tokenIdInfo : infos) {
            String tokenKey = tokenIdInfo.getKey();
            Bson tokenKeyBson = Filters.eq("_id", tokenKey);
            Document document = DocumentTransferTool.toDocument(tokenIdInfo, "key");
            Document currentDocument = mongoDBService.findOne(TOKEN1155_IDS_TABLE + chainId, tokenKeyBson);
            if (currentDocument != null) {
                if (new BigInteger(tokenIdInfo.getTotalSupply()).compareTo(BigInteger.ZERO) == 0) {
                    // 销毁
                    modelList.add(new DeleteOneModel<>(tokenKeyBson));
                } else {
                    modelList.add(new ReplaceOneModel<>(tokenKeyBson, document));
                }
            } else if (new BigInteger(tokenIdInfo.getTotalSupply()).compareTo(BigInteger.ZERO) > 0) {
                insertDocuments.put(tokenKey, document);
            }
        }
        for (Document document : insertDocuments.values()) {
            modelList.add(new InsertOneModel(document));
        }
        if (modelList.isEmpty()) {
            return;
        }
        BulkWriteOptions options = new BulkWriteOptions();
        options.ordered(false);
        mongoDBService.bulkWrite(TOKEN1155_IDS_TABLE + chainId, modelList, options);
    }

    @Override
    public void rollbackTokenIds(int chainId, Map<String, Nrc1155TokenIdInfo> tokenIdInfos) {
        this.saveTokenIds(chainId, tokenIdInfos);
    }

    @Override
    public Nrc1155TokenIdInfo getContractTokenId(int chainId, String contractAddress, String tokenId) {
        Bson query = Filters.eq("_id", contractAddress + tokenId);
        Document document = mongoDBService.findOne(TOKEN1155_IDS_TABLE + chainId, query);
        if (document == null) {
            return null;
        }
        Nrc1155TokenIdInfo tokenIdInfo = DocumentTransferTool.toInfo(document, "key", Nrc1155TokenIdInfo.class);
        return tokenIdInfo;
    }

    @Override
    public PageInfo<Nrc1155TokenIdInfo> getContractTokenIds(int chainId, String contractAddress, int pageNumber, int pageSize) {
        Bson query = Filters.eq("contractAddress", contractAddress);
        Bson sort = Sorts.descending("time");
        List<Document> docsList = this.mongoDBService.pageQuery(TOKEN1155_IDS_TABLE + chainId, query, sort, pageNumber, pageSize);
        List<Nrc1155TokenIdInfo> tokenIdList = new ArrayList<>();
        long totalCount = mongoDBService.getCount(TOKEN1155_IDS_TABLE + chainId, query);
        for (Document document : docsList) {
            tokenIdList.add(DocumentTransferTool.toInfo(document, "key", Nrc1155TokenIdInfo.class));
        }
        PageInfo<Nrc1155TokenIdInfo> pageInfo = new PageInfo<>(pageNumber, pageSize, totalCount, tokenIdList);
        return pageInfo;
    }
}
