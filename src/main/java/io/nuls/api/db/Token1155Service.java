package io.nuls.api.db;

import io.nuls.api.model.po.AccountToken1155Info;
import io.nuls.api.model.po.Nrc1155TokenIdInfo;
import io.nuls.api.model.po.PageInfo;
import io.nuls.api.model.po.Token1155Transfer;

import java.util.List;
import java.util.Map;

public interface Token1155Service {

    AccountToken1155Info getAccountTokenInfo(int chainId, String key);

    void saveAccountTokens(int chainId, Map<String, AccountToken1155Info> accountTokenInfos);

    PageInfo<AccountToken1155Info> getAccountTokens(int chainId, String address, String contractAddress, int pageNumber, int pageSize);

    List<AccountToken1155Info> getAccountTokens(int chainId, String address, String contractAddress);

    PageInfo<AccountToken1155Info> getContractTokens(int chainId, String contractAddress, int pageNumber, int pageSize);

    void saveTokenTransfers(int chainId, List<Token1155Transfer> tokenTransfers);

    void rollbackTokenTransfers(int chainId, List<String> tokenTxHashs, long height);

    PageInfo<Token1155Transfer> getTokenTransfers(int chainId, String address, String contractAddress, String tokenId, int pageIndex, int pageSize);

    void saveTokenIds(int chainId, Map<String, Nrc1155TokenIdInfo> tokenIDInfos);

    void rollbackTokenIds(int chainId, Map<String, Nrc1155TokenIdInfo> tokenIDInfos);

    Nrc1155TokenIdInfo getContractTokenId(int chainId, String contractAddress, String tokenId);

    PageInfo<Nrc1155TokenIdInfo> getContractTokenIds(int chainId, String contractAddress, int pageNumber, int pageSize);

}
