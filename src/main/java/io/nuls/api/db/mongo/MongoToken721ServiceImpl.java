package io.nuls.api.db.mongo;

import com.mongodb.client.model.*;
import io.nuls.api.db.Token721Service;
import io.nuls.api.model.po.AccountToken721Info;
import io.nuls.api.model.po.PageInfo;
import io.nuls.api.model.po.Token721Transfer;
import io.nuls.api.utils.DocumentTransferTool;
import io.nuls.core.core.annotation.Autowired;
import io.nuls.core.core.annotation.Component;
import io.nuls.core.model.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.nuls.api.constant.DBTableConstant.*;

@Component
public class MongoToken721ServiceImpl implements Token721Service {

    @Autowired
    private MongoDBService mongoDBService;

    public AccountToken721Info getAccountTokenInfo(int chainId, String key) {
        Bson query = Filters.eq("_id", key);

        Document document = mongoDBService.findOne(ACCOUNT_TOKEN721_TABLE + chainId, query);
        if (document == null) {
            return null;
        }
        AccountToken721Info tokenInfo = DocumentTransferTool.toInfo(document, "key", AccountToken721Info.class);
        return tokenInfo;
    }

    public void saveAccountTokens(int chainId, Map<String, AccountToken721Info> accountTokenInfos) {
        if (accountTokenInfos.isEmpty()) {
            return;
        }
        List<WriteModel<Document>> modelList = new ArrayList<>();
        for (AccountToken721Info tokenInfo : accountTokenInfos.values()) {
            Document document = DocumentTransferTool.toDocument(tokenInfo, "key");
            //TODO pierre 余额存储
            //document.put("balance", BigIntegerUtils.bigIntegerToString(tokenInfo.getBalance(), 32));
            if (tokenInfo.isNew()) {
                modelList.add(new InsertOneModel(document));
            } else {
                modelList.add(new ReplaceOneModel<>(Filters.eq("_id", tokenInfo.getKey()), document));
            }
        }
        BulkWriteOptions options = new BulkWriteOptions();
        options.ordered(false);
        mongoDBService.bulkWrite(ACCOUNT_TOKEN_TABLE + chainId, modelList, options);
    }

    public PageInfo<AccountToken721Info> getAccountTokens(int chainId, String address, int pageNumber, int pageSize) {
        Bson query = Filters.eq("address", address);
        Bson sort = Sorts.descending("balance");
        List<Document> docsList = this.mongoDBService.pageQuery(ACCOUNT_TOKEN721_TABLE + chainId, query, sort, pageNumber, pageSize);
        List<AccountToken721Info> accountTokenList = new ArrayList<>();
        long totalCount = mongoDBService.getCount(ACCOUNT_TOKEN721_TABLE + chainId, query);

        for (Document document : docsList) {
            AccountToken721Info tokenInfo = DocumentTransferTool.toInfo(document, "key", AccountToken721Info.class);
            accountTokenList.add(tokenInfo);
            document = mongoDBService.findOne(CONTRACT_TABLE + chainId, Filters.eq("_id", tokenInfo.getContractAddress()));
            tokenInfo.setStatus(document.getInteger("status"));
        }

        PageInfo<AccountToken721Info> pageInfo = new PageInfo<>(pageNumber, pageSize, totalCount, accountTokenList);
        return pageInfo;
    }

    public PageInfo<AccountToken721Info> getContractTokens(int chainId, String contractAddress, int pageNumber, int pageSize) {
        Bson query = Filters.eq("contractAddress", contractAddress);
        //TODO pierre 余额处理
        Bson sort = Sorts.descending("balance");
        List<Document> docsList = this.mongoDBService.pageQuery(ACCOUNT_TOKEN721_TABLE + chainId, query, sort, pageNumber, pageSize);
        List<AccountToken721Info> accountTokenList = new ArrayList<>();
        long totalCount = mongoDBService.getCount(ACCOUNT_TOKEN721_TABLE + chainId, query);
        for (Document document : docsList) {
            accountTokenList.add(DocumentTransferTool.toInfo(document, "key", AccountToken721Info.class));
        }
        PageInfo<AccountToken721Info> pageInfo = new PageInfo<>(pageNumber, pageSize, totalCount, accountTokenList);
        return pageInfo;
    }


    public void saveTokenTransfers(int chainId, List<Token721Transfer> tokenTransfers) {
        if (tokenTransfers.isEmpty()) {
            return;
        }
        List<Document> documentList = new ArrayList<>();
        for (Token721Transfer tokenTransfer : tokenTransfers) {
            Document document = DocumentTransferTool.toDocument(tokenTransfer);
            documentList.add(document);
        }
        InsertManyOptions options = new InsertManyOptions();
        options.ordered(false);
        mongoDBService.insertMany(TOKEN721_TRANSFER_TABLE + chainId, documentList, options);
    }

    public void rollbackTokenTransfers(int chainId, List<String> tokenTxHashs, long height) {
        if (tokenTxHashs.isEmpty()) {
            return;
        }
        mongoDBService.delete(TOKEN721_TRANSFER_TABLE + chainId, Filters.eq("height", height));
    }

    public PageInfo<Token721Transfer> getTokenTransfers(int chainId, String address, String contractAddress, int pageIndex, int pageSize) {
        Bson filter;
        if (StringUtils.isNotBlank(address) && StringUtils.isNotBlank(contractAddress)) {
            Bson addressFilter = Filters.or(Filters.eq("fromAddress", address), Filters.eq("toAddress", address));
            filter = Filters.and(Filters.eq("contractAddress", contractAddress), addressFilter);
        } else if (StringUtils.isNotBlank(contractAddress)) {
            filter = Filters.eq("contractAddress", contractAddress);
        } else {
            filter = Filters.or(Filters.eq("fromAddress", address), Filters.eq("toAddress", address));
        }
        Bson sort = Sorts.descending("time");
        List<Document> docsList = this.mongoDBService.pageQuery(TOKEN721_TRANSFER_TABLE + chainId, filter, sort, pageIndex, pageSize);
        List<Token721Transfer> tokenTransfers = new ArrayList<>();
        long totalCount = mongoDBService.getCount(TOKEN721_TRANSFER_TABLE + chainId, filter);
        for (Document document : docsList) {
            tokenTransfers.add(DocumentTransferTool.toInfo(document, Token721Transfer.class));
        }

        PageInfo<Token721Transfer> pageInfo = new PageInfo<>(pageIndex, pageSize, totalCount, tokenTransfers);
        return pageInfo;
    }
}
