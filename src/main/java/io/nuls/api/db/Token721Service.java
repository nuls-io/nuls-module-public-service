package io.nuls.api.db;

import io.nuls.api.model.po.*;
import io.nuls.api.model.po.AccountToken721Info;

import java.util.List;
import java.util.Map;

public interface Token721Service {

    AccountToken721Info getAccountTokenInfo(int chainId, String key);

    void saveAccountTokens(int chainId, Map<String, AccountToken721Info> accountTokenInfos);

    PageInfo<AccountToken721Info> getAccountTokens(int chainId, String address, int pageNumber, int pageSize);

    PageInfo<AccountToken721Info> getContractTokens(int chainId, String contractAddress, int pageNumber, int pageSize);

    void saveTokenTransfers(int chainId, List<Token721Transfer> tokenTransfers);

    void rollbackTokenTransfers(int chainId, List<String> tokenTxHashs, long height);

    PageInfo<Token721Transfer> getTokenTransfers(int chainId, String address, String contractAddress, int pageIndex, int pageSize);

}
