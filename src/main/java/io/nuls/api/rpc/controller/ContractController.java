package io.nuls.api.rpc.controller;

import io.nuls.api.analysis.WalletRpcHandler;
import io.nuls.api.cache.ApiCache;
import io.nuls.api.constant.ApiConstant;
import io.nuls.api.db.ContractService;
import io.nuls.api.db.Token1155Service;
import io.nuls.api.db.Token721Service;
import io.nuls.api.db.TokenService;
import io.nuls.api.manager.CacheManager;
import io.nuls.api.model.po.*;
import io.nuls.api.model.po.mini.MiniContractInfo;
import io.nuls.api.model.rpc.BalanceInfo;
import io.nuls.api.model.rpc.RpcErrorCode;
import io.nuls.api.model.rpc.RpcResult;
import io.nuls.api.model.rpc.RpcResultError;
import io.nuls.api.utils.LoggerUtil;
import io.nuls.api.utils.VerifyUtils;
import io.nuls.base.basic.AddressTool;
import io.nuls.core.basic.Result;
import io.nuls.core.constant.CommonCodeConstanst;
import io.nuls.core.core.annotation.Autowired;
import io.nuls.core.core.annotation.Controller;
import io.nuls.core.core.annotation.RpcMethod;
import io.nuls.core.exception.NulsException;
import io.nuls.core.log.Log;
import io.nuls.core.model.StringUtils;
import io.nuls.core.parse.JSONUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Controller
public class ContractController {

    @Autowired
    private ContractService contractService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private Token721Service token721Service;
    @Autowired
    private Token1155Service token1155Service;

    @RpcMethod("getContract")
    public RpcResult getContract(List<Object> params) {
        VerifyUtils.verifyParams(params, 2);
        int chainId;
        String contractAddress;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            contractAddress = (String) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }

        if (!AddressTool.validAddress(chainId, contractAddress)) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }

        if (!CacheManager.isChainExist(chainId)) {
            return RpcResult.dataNotFound();
        }
        RpcResult rpcResult = new RpcResult();
        ContractInfo contractInfo = contractService.getContractInfo(chainId, contractAddress);
        if (contractInfo == null) {
            rpcResult.setError(new RpcResultError(RpcErrorCode.DATA_NOT_EXISTS));
        } else {
            if (ApiConstant.TOKEN_TYPE_NRC20 == contractInfo.getTokenType()) {
                Integer assetIdOfNRC20 = WalletRpcHandler.getAssetIdOfNRC20(contractAddress);
                if (assetIdOfNRC20 != null && assetIdOfNRC20.intValue() != 0) {
                    boolean crossAssets = WalletRpcHandler.isCrossAssets(chainId, assetIdOfNRC20);
                    contractInfo.setCrossAsset(crossAssets);
                    Result<BigInteger> result = WalletRpcHandler.tokenTotalSupply(chainId, contractAddress);
                    if (result.isSuccess()) {
                        BigInteger totalSupply = result.getData();
                        contractInfo.setTotalSupply(totalSupply.toString());
                    }
                }
            }
            ApiCache apiCache = CacheManager.getCache(chainId);
            AssetInfo defaultAsset = apiCache.getChainInfo().getDefaultAsset();
            BalanceInfo balanceInfo = WalletRpcHandler.getAccountBalance(chainId, contractAddress, defaultAsset.getChainId(), defaultAsset.getAssetId());
            contractInfo.setTotalBalance(balanceInfo.getTotalBalance());
            contractInfo.setBalance(balanceInfo.getBalance());
            rpcResult.setResult(contractInfo);
        }
        return rpcResult;
    }

    @RpcMethod("getContractTxResult")
    public RpcResult getContractTxResult(List<Object> params) {
        VerifyUtils.verifyParams(params, 2);
        int chainId;
        String txHash;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            txHash = (String) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[txHash] is invalid");
        }

        try {
            Result<ContractResultInfo> result = WalletRpcHandler.getContractResultInfo(chainId, txHash);
            if (result.isSuccess()) {
                return RpcResult.success(result.getData());
            } else {
                return RpcResult.failed(result.getErrorCode());
            }
        } catch (NulsException e) {
            return RpcResult.failed(e.getErrorCode());
        }
    }

    @RpcMethod("getContractCodeHash")
    public RpcResult getContractCodeHash(List<Object> params) {
        VerifyUtils.verifyParams(params, 2);
        int chainId;
        String contractAddress;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            contractAddress = (String) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }
        Result<Map> result = WalletRpcHandler.getContractCodeHash(chainId, contractAddress);
        if (result.isSuccess()) {
            return RpcResult.success(result.getData());
        } else {
            return RpcResult.failed(result.getErrorCode());
        }
    }

    @RpcMethod("getContractCode")
    public RpcResult getContractCode(List<Object> params) {
        VerifyUtils.verifyParams(params, 2);
        int chainId;
        String contractAddress;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            contractAddress = (String) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }
        Result<Map> result = WalletRpcHandler.getContractCode(chainId, contractAddress);
        if (result.isSuccess()) {
            return RpcResult.success(result.getData());
        } else {
            return RpcResult.failed(result.getErrorCode());
        }
    }

    @RpcMethod("computeContractAddress")
    public RpcResult computeContractAddress(List<Object> params) {
        VerifyUtils.verifyParams(params, 4);
        int chainId;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        int i = 1;
        Result<Map> result = WalletRpcHandler.computeContractAddress(chainId, params.get(i++), params.get(i++), params.get(i++));
        if (result.isSuccess()) {
            return RpcResult.success(result.getData());
        } else {
            return RpcResult.failed(result.getErrorCode());
        }
    }

    @RpcMethod("getNrc20List")
    public RpcResult getNrc20List(List<Object> params) {
        int chainId;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        if (!CacheManager.isChainExist(chainId)) {
            return RpcResult.dataNotFound();
        }
        return RpcResult.success(CacheManager.getCache(chainId).getNrc20InfoList());
    }


    @RpcMethod("getAccountTokens")
    public RpcResult getAccountTokens(List<Object> params) {
        VerifyUtils.verifyParams(params, 4);
        int chainId, pageNumber, pageSize;
        String address;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            pageNumber = (int) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[pageIndex] is invalid");
        }
        try {
            pageSize = (int) params.get(2);
        } catch (Exception e) {
            return RpcResult.paramError("[pageSize] is invalid");
        }
        try {
            address = (String) params.get(3);
        } catch (Exception e) {
            return RpcResult.paramError("[address] is invalid");
        }

        if (!AddressTool.validAddress(chainId, address)) {
            return RpcResult.paramError("[address] is invalid");
        }
        if (pageNumber <= 0) {
            pageNumber = 1;
        }
        if (pageSize <= 0 || pageSize > 100) {
            pageSize = 10;
        }

        PageInfo<AccountTokenInfo> pageInfo;
        if (!CacheManager.isChainExist(chainId)) {
            pageInfo = new PageInfo<>(pageNumber, pageSize);
        } else {
            pageInfo = tokenService.getAccountTokens(chainId, address, pageNumber, pageSize);
        }
        if (pageInfo != null && pageInfo.getList() != null && pageInfo.getList().size() > 0) {
            List<AccountTokenInfo> list = pageInfo.getList();
            List contracts = new ArrayList();
            List methods = new ArrayList();
            List pars = new ArrayList();
            for (AccountTokenInfo tokenInfo : list) {
                contracts.add(tokenInfo.getContractAddress());
                methods.add("balanceOf");
                params.add(tokenInfo.getAddress());
            }
            Result<List<String>> multicall = WalletRpcHandler.multicall(chainId, contracts, methods, pars);
            List<String> datas = multicall.getData();
            if (datas.size() == list.size()) {
                for (int i = 0; i < list.size(); i++) {
                    AccountTokenInfo tokenInfo = list.get(i);
                    BigInteger available = new BigInteger(datas.get(i));
                    BigInteger total = tokenInfo.getBalance();
                    BigInteger locked = total.subtract(available);
                    tokenInfo.setLockedBalance(locked);
                }
            }

            //for (AccountTokenInfo tokenInfo : list) {
            //    Result<BigInteger> result = WalletRpcHandler.tokenBalance(chainId, tokenInfo.getContractAddress(), tokenInfo.getAddress());
            //    if (null == result) {
            //        continue;
            //    }
            //    BigInteger available = result.getData();
            //    BigInteger total = tokenInfo.getBalance();
            //    BigInteger locked = total.subtract(available);
            //    tokenInfo.setLockedBalance(locked);
            //}
        }

        RpcResult result = new RpcResult();
        result.setResult(pageInfo);
        return result;
    }

    @RpcMethod("getAccountToken")
    public RpcResult getAccountToken(List<Object> params) {
        RpcResult result = new RpcResult();
        VerifyUtils.verifyParams(params, 3);
        int chainId;
        String address, contract;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            address = (String) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[address] is invalid");
        }
        try {
            contract = (String) params.get(2);
        } catch (Exception e) {
            return RpcResult.paramError("[contract] is invalid");
        }
        if (!AddressTool.validAddress(chainId, address)) {
            return RpcResult.paramError("[address] is invalid");
        }
        if (!AddressTool.validContractAddress(AddressTool.getAddress(contract), chainId)) {
            return RpcResult.paramError("[contract] is invalid");
        }
        AccountTokenInfo tokenInfo = tokenService.getAccountTokenInfo(chainId, address + contract);
        if (tokenInfo == null) {
            result.setError(new RpcResultError(RpcErrorCode.DATA_NOT_EXISTS));
            return result;
        }
        BigInteger available = WalletRpcHandler.tokenBalance(chainId, tokenInfo.getContractAddress(), tokenInfo.getAddress()).getData();
        BigInteger total = tokenInfo.getBalance();
        BigInteger locked = total.subtract(available);
        tokenInfo.setLockedBalance(locked);
        result.setResult(tokenInfo);
        return result;
    }

    @RpcMethod("getTokenHolderInfo")
    public RpcResult getTokenHolderInfo(List<Object> params) {
        VerifyUtils.verifyParams(params, 3);
        int chainId;
        String contractAddress, address;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            contractAddress = (String) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }
        if (!AddressTool.validAddress(chainId, contractAddress)) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }

        try {
            address = (String) params.get(2);
        } catch (Exception e) {
            return RpcResult.paramError("[address] is invalid");
        }
        if (!AddressTool.validAddress(chainId, address)) {
            return RpcResult.paramError("[address] is invalid");
        }

        try {
            AccountTokenInfo info = null;
            if (CacheManager.isChainExist(chainId)) {
                info = tokenService.getAccountTokenInfo(chainId, address + contractAddress);
            }
            RpcResult result = new RpcResult();
            result.setResult(info);
            return result;
        } catch (Exception e) {
            LoggerUtil.commonLog.error(e);
            return RpcResult.failed(RpcErrorCode.SYS_UNKNOWN_EXCEPTION);
        }
    }

    @RpcMethod("getContractTokens")
    public RpcResult getContractTokens(List<Object> params) {
        VerifyUtils.verifyParams(params, 4);
        int chainId, pageNumber, pageSize;
        String contractAddress;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            pageNumber = (int) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[pageNumber] is invalid");
        }
        try {
            pageSize = (int) params.get(2);
        } catch (Exception e) {
            return RpcResult.paramError("[pageSize] is invalid");
        }
        try {
            contractAddress = (String) params.get(3);
        } catch (Exception e) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }
        if (!AddressTool.validAddress(chainId, contractAddress)) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }
        if (pageNumber <= 0) {
            pageNumber = 1;
        }
        if (pageSize <= 0 || pageSize > 100) {
            pageSize = 10;
        }

        try {
            PageInfo<AccountTokenInfo> pageInfo;
            if (!CacheManager.isChainExist(chainId)) {
                pageInfo = new PageInfo<>(pageNumber, pageSize);
            } else {
                pageInfo = tokenService.getContractTokens(chainId, contractAddress, pageNumber, pageSize);
            }
            RpcResult result = new RpcResult();
            result.setResult(pageInfo);
            return result;
        } catch (Exception e) {
            LoggerUtil.commonLog.error(e);
            return RpcResult.failed(RpcErrorCode.SYS_UNKNOWN_EXCEPTION);
        }
    }

    @RpcMethod("getTokenTransfers")
    public RpcResult getTokenTransfers(List<Object> params) {
        VerifyUtils.verifyParams(params, 5);
        int chainId, pageNumber, pageSize;
        long startHeight = 0, endHeight = 0;
        String address, contractAddress;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            pageNumber = (int) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[pageNumber] is invalid");
        }
        try {
            pageSize = (int) params.get(2);
        } catch (Exception e) {
            return RpcResult.paramError("[pageSize] is invalid");
        }
        try {
            address = (String) params.get(3);
        } catch (Exception e) {
            return RpcResult.paramError("[address] is invalid");
        }
        try {
            contractAddress = (String) params.get(4);
        } catch (Exception e) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }

        if (params.size() > 5) {
            try {
                startHeight = (int) params.get(5);
            } catch (Exception e) {

            }
        }

        if (params.size() > 6) {
            try {
                endHeight = (int) params.get(6);
            } catch (Exception e) {

            }
        }


        if (!StringUtils.isBlank(address) && !AddressTool.validAddress(chainId, address)) {
            return RpcResult.paramError("[address] is invalid");
        }
        if (!StringUtils.isBlank(contractAddress) && !AddressTool.validAddress(chainId, contractAddress)) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }

        if (pageNumber <= 0) {
            pageNumber = 1;
        }
        if (pageSize <= 0 || pageSize > 100) {
            pageSize = 10;
        }

        try {
            PageInfo<TokenTransfer> pageInfo;
            if (!CacheManager.isChainExist(chainId)) {
                pageInfo = new PageInfo<>(pageNumber, pageSize);
            } else {
                pageInfo = tokenService.getTokenTransfers(chainId, address, contractAddress, pageNumber, pageSize, startHeight, endHeight);
            }
            RpcResult result = new RpcResult();
            result.setResult(pageInfo);
            return result;
        } catch (Exception e) {
            LoggerUtil.commonLog.error(e);
            return RpcResult.failed(RpcErrorCode.SYS_UNKNOWN_EXCEPTION);
        }
    }

    @RpcMethod("getNrc721List")
    public RpcResult getNrc721List(List<Object> params) {
        int chainId;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        if (!CacheManager.isChainExist(chainId)) {
            return RpcResult.dataNotFound();
        }
        params.add(ApiConstant.TOKEN_TYPE_NRC721);
        params.add(false);
        return this.getContractList(params);
    }

    @RpcMethod("getAccountToken721s")
    public RpcResult getAccountToken721s(List<Object> params) {
        VerifyUtils.verifyParams(params, 4);
        int chainId, pageNumber, pageSize;
        String address, contractAddress = null;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            pageNumber = (int) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[pageIndex] is invalid");
        }
        try {
            pageSize = (int) params.get(2);
        } catch (Exception e) {
            return RpcResult.paramError("[pageSize] is invalid");
        }
        try {
            address = (String) params.get(3);
        } catch (Exception e) {
            return RpcResult.paramError("[address] is invalid");
        }

        if (params.size() > 4) {
            contractAddress = (String) params.get(4);
        }

        if (!AddressTool.validAddress(chainId, address)) {
            return RpcResult.paramError("[address] is invalid");
        }
        if (pageNumber <= 0) {
            pageNumber = 1;
        }
        if (pageSize <= 0 || pageSize > 100) {
            pageSize = 10;
        }

        PageInfo<AccountToken721Info> pageInfo;
        if (!CacheManager.isChainExist(chainId)) {
            pageInfo = new PageInfo<>(pageNumber, pageSize);
        } else {
            pageInfo = token721Service.getAccountTokens(chainId, address, contractAddress, pageNumber, pageSize);
        }
        RpcResult result = new RpcResult();
        result.setResult(pageInfo);
        return result;
    }

    @RpcMethod("getAccountToken721")
    public RpcResult getAccountToken721(List<Object> params) {
        RpcResult result = new RpcResult();
        VerifyUtils.verifyParams(params, 3);
        int chainId;
        String address, contract;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            address = (String) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[address] is invalid");
        }
        try {
            contract = (String) params.get(2);
        } catch (Exception e) {
            return RpcResult.paramError("[contract] is invalid");
        }
        if (!AddressTool.validAddress(chainId, address)) {
            return RpcResult.paramError("[address] is invalid");
        }
        if (!AddressTool.validContractAddress(AddressTool.getAddress(contract), chainId)) {
            return RpcResult.paramError("[contract] is invalid");
        }
        AccountToken721Info tokenInfo = token721Service.getAccountTokenInfo(chainId, address + contract);
        if (tokenInfo == null) {
            result.setError(new RpcResultError(RpcErrorCode.DATA_NOT_EXISTS));
            return result;
        }
        result.setResult(tokenInfo);
        return result;
    }


    @RpcMethod("getToken721HolderInfo")
    public RpcResult getToken721HolderInfo(List<Object> params) {
        VerifyUtils.verifyParams(params, 3);
        int chainId;
        String contractAddress, address;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            contractAddress = (String) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }
        if (!AddressTool.validAddress(chainId, contractAddress)) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }

        try {
            address = (String) params.get(2);
        } catch (Exception e) {
            return RpcResult.paramError("[address] is invalid");
        }
        if (!AddressTool.validAddress(chainId, address)) {
            return RpcResult.paramError("[address] is invalid");
        }

        try {
            AccountToken721Info info = null;
            if (CacheManager.isChainExist(chainId)) {
                info = token721Service.getAccountTokenInfo(chainId, address + contractAddress);
            }
            RpcResult result = new RpcResult();
            result.setResult(info);
            return result;
        } catch (Exception e) {
            LoggerUtil.commonLog.error(e);
            return RpcResult.failed(RpcErrorCode.SYS_UNKNOWN_EXCEPTION);
        }
    }


    @RpcMethod("getContractToken721s")
    public RpcResult getContractToken721s(List<Object> params) {
        VerifyUtils.verifyParams(params, 4);
        int chainId, pageNumber, pageSize;
        String contractAddress;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            pageNumber = (int) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[pageNumber] is invalid");
        }
        try {
            pageSize = (int) params.get(2);
        } catch (Exception e) {
            return RpcResult.paramError("[pageSize] is invalid");
        }
        try {
            contractAddress = (String) params.get(3);
        } catch (Exception e) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }
        if (!AddressTool.validAddress(chainId, contractAddress)) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }
        if (pageNumber <= 0) {
            pageNumber = 1;
        }
        if (pageSize <= 0 || pageSize > 100) {
            pageSize = 10;
        }

        try {
            PageInfo<AccountToken721Info> pageInfo;
            if (!CacheManager.isChainExist(chainId)) {
                pageInfo = new PageInfo<>(pageNumber, pageSize);
            } else {
                pageInfo = token721Service.getContractTokens(chainId, contractAddress, pageNumber, pageSize);
            }
            RpcResult result = new RpcResult();
            result.setResult(pageInfo);
            return result;
        } catch (Exception e) {
            LoggerUtil.commonLog.error(e);
            return RpcResult.failed(RpcErrorCode.SYS_UNKNOWN_EXCEPTION);
        }
    }

    @RpcMethod("getToken721Transfers")
    public RpcResult getToken721Transfers(List<Object> params) {
        VerifyUtils.verifyParams(params, 5);
        int chainId, pageNumber, pageSize;
        String address, contractAddress;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            pageNumber = (int) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[pageNumber] is invalid");
        }
        try {
            pageSize = (int) params.get(2);
        } catch (Exception e) {
            return RpcResult.paramError("[pageSize] is invalid");
        }
        try {
            address = (String) params.get(3);
        } catch (Exception e) {
            return RpcResult.paramError("[address] is invalid");
        }
        try {
            contractAddress = (String) params.get(4);
        } catch (Exception e) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }

        if (!StringUtils.isBlank(address) && !AddressTool.validAddress(chainId, address)) {
            return RpcResult.paramError("[address] is invalid");
        }
        if (!StringUtils.isBlank(contractAddress) && !AddressTool.validAddress(chainId, contractAddress)) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }

        if (pageNumber <= 0) {
            pageNumber = 1;
        }
        if (pageSize <= 0 || pageSize > 100) {
            pageSize = 10;
        }

        try {
            PageInfo<Token721Transfer> pageInfo;
            if (!CacheManager.isChainExist(chainId)) {
                pageInfo = new PageInfo<>(pageNumber, pageSize);
            } else {
                pageInfo = token721Service.getTokenTransfers(chainId, address, contractAddress, pageNumber, pageSize);
            }
            RpcResult result = new RpcResult();
            result.setResult(pageInfo);
            return result;
        } catch (Exception e) {
            LoggerUtil.commonLog.error(e);
            return RpcResult.failed(RpcErrorCode.SYS_UNKNOWN_EXCEPTION);
        }
    }

    @RpcMethod("getToken721Ids")
    public RpcResult getToken721Ids(List<Object> params) {
        VerifyUtils.verifyParams(params, 4);
        int chainId, pageNumber, pageSize;
        String contractAddress;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            pageNumber = (int) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[pageNumber] is invalid");
        }
        try {
            pageSize = (int) params.get(2);
        } catch (Exception e) {
            return RpcResult.paramError("[pageSize] is invalid");
        }
        try {
            contractAddress = (String) params.get(3);
        } catch (Exception e) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }
        if (!AddressTool.validAddress(chainId, contractAddress)) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }
        if (pageNumber <= 0) {
            pageNumber = 1;
        }
        if (pageSize <= 0 || pageSize > 100) {
            pageSize = 10;
        }

        try {
            PageInfo<Nrc721TokenIdInfo> pageInfo;
            if (!CacheManager.isChainExist(chainId)) {
                pageInfo = new PageInfo<>(pageNumber, pageSize);
            } else {
                pageInfo = token721Service.getContractTokenIds(chainId, contractAddress, pageNumber, pageSize);
            }
            RpcResult result = new RpcResult();
            result.setResult(pageInfo);
            return result;
        } catch (Exception e) {
            LoggerUtil.commonLog.error(e);
            return RpcResult.failed(RpcErrorCode.SYS_UNKNOWN_EXCEPTION);
        }
    }

    @RpcMethod("getToken721Id")
    public RpcResult getToken721Id(List<Object> params) {
        VerifyUtils.verifyParams(params, 3);
        int chainId;
        String contractAddress, tokenId;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            contractAddress = (String) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }
        try {
            tokenId = String.valueOf(params.get(2));
        } catch (Exception e) {
            return RpcResult.paramError("[tokenId] is invalid");
        }
        if (!AddressTool.validAddress(chainId, contractAddress)) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }
        if (StringUtils.isBlank(tokenId)) {
            return RpcResult.paramError("[tokenId] is invalid");
        }

        try {
            RpcResult result = new RpcResult();
            Nrc721TokenIdInfo tokenIdInfo = token721Service.getContractTokenId(chainId, contractAddress, tokenId);
            if (tokenIdInfo == null) {
                result.setError(new RpcResultError(RpcErrorCode.DATA_NOT_EXISTS));
                return result;
            }
            result.setResult(tokenIdInfo);
            return result;
        } catch (Exception e) {
            LoggerUtil.commonLog.error(e);
            return RpcResult.failed(RpcErrorCode.SYS_UNKNOWN_EXCEPTION);
        }
    }

    @RpcMethod("getContractTxList")
    public RpcResult getContractTxList(List<Object> params) {
        VerifyUtils.verifyParams(params, 5);
        int chainId, pageNumber, pageSize, type;
        String contractAddress;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            pageNumber = (int) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[pageNumber] is invalid");
        }
        try {
            pageSize = (int) params.get(2);
        } catch (Exception e) {
            return RpcResult.paramError("[pageSize] is invalid");
        }
        try {
            type = (int) params.get(3);
        } catch (Exception e) {
            return RpcResult.paramError("[type] is invalid");
        }
        try {
            contractAddress = (String) params.get(4);
        } catch (Exception e) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }

        if (!AddressTool.validAddress(chainId, contractAddress)) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }
        if (pageNumber <= 0) {
            pageNumber = 1;
        }
        if (pageSize <= 0 || pageSize > 100) {
            pageSize = 10;
        }

        try {
            PageInfo<ContractTxInfo> pageInfo;
            if (!CacheManager.isChainExist(chainId)) {
                pageInfo = new PageInfo<>(pageNumber, pageSize);
            } else {
                pageInfo = contractService.getContractTxList(chainId, contractAddress, type, pageNumber, pageSize);
            }
            RpcResult result = new RpcResult();
            result.setResult(pageInfo);
            return result;
        } catch (Exception e) {
            LoggerUtil.commonLog.error(e);
            return RpcResult.failed(RpcErrorCode.SYS_UNKNOWN_EXCEPTION);
        }
    }

    @RpcMethod("getContractList")
    public RpcResult getContractList(List<Object> params) {
        VerifyUtils.verifyParams(params, 5);
        int chainId, pageNumber, pageSize, tokenType;
        boolean isHidden;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            pageNumber = (int) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[pageNumber] is invalid");
        }
        try {
            pageSize = (int) params.get(2);
        } catch (Exception e) {
            return RpcResult.paramError("[pageSize] is invalid");
        }
        try {
            tokenType = (int) params.get(3);
        } catch (Exception e) {
            return RpcResult.paramError("[tokenType] is invalid");
        }
        try {
            isHidden = (boolean) params.get(4);
        } catch (Exception e) {
            return RpcResult.paramError("[isHidden] is invalid");
        }

        if (pageNumber <= 0) {
            pageNumber = 1;
        }
        if (pageSize <= 0 || pageSize > 100) {
            pageSize = 10;
        }

        PageInfo<MiniContractInfo> pageInfo;
        if (!CacheManager.isChainExist(chainId)) {
            pageInfo = new PageInfo<>(pageNumber, pageSize);
        } else {
            pageInfo = contractService.getContractList(chainId, pageNumber, pageSize, tokenType, isHidden);
        }
        RpcResult result = new RpcResult();
        result.setResult(pageInfo);
        return result;
    }

    @RpcMethod("getAccountContractList")
    public RpcResult getAccountContractList(List<Object> params) {
        VerifyUtils.verifyParams(params, 6);
        int chainId, pageNumber, pageSize, tokenType;
        boolean isHidden;
        String address;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            pageNumber = (int) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[pageNumber] is invalid");
        }
        try {
            pageSize = (int) params.get(2);
        } catch (Exception e) {
            return RpcResult.paramError("[pageSize] is invalid");
        }
        try {
            address = (String) params.get(3);
        } catch (Exception e) {
            return RpcResult.paramError("[address] is invalid");
        }

        try {
            tokenType = (int) params.get(4);
        } catch (Exception e) {
            return RpcResult.paramError("[tokenType] is invalid");
        }
        try {
            isHidden = (boolean) params.get(5);
        } catch (Exception e) {
            return RpcResult.paramError("[isHidden] is invalid");
        }

        if (pageNumber <= 0) {
            pageNumber = 1;
        }
        if (pageSize <= 0 || pageSize > 100) {
            pageSize = 10;
        }
        if (!AddressTool.validAddress(chainId, address)) {
            return RpcResult.paramError("[address] is invalid");
        }

        PageInfo<MiniContractInfo> pageInfo;
        if (!CacheManager.isChainExist(chainId)) {
            pageInfo = new PageInfo<>(pageNumber, pageSize);
        } else {
            pageInfo = contractService.getContractList(chainId, pageNumber, pageSize, address, tokenType, isHidden);
        }
        RpcResult result = new RpcResult();
        result.setResult(pageInfo);
        return result;
    }


    @RpcMethod("getContractListById")
    public RpcResult getContractListById(List<Object> params) {
        VerifyUtils.verifyParams(params, 5);
        int chainId, pageNumber, pageSize, totalCount;
        List<String> contractAddressList;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            pageNumber = (int) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[pageNumber] is invalid");
        }
        try {
            pageSize = (int) params.get(2);
        } catch (Exception e) {
            return RpcResult.paramError("[pageSize] is invalid");
        }
        try {
            totalCount = (int) params.get(3);
        } catch (Exception e) {
            return RpcResult.paramError("[totalCount] is invalid");
        }
        try {
            contractAddressList = (List<String>) params.get(4);
        } catch (Exception e) {
            return RpcResult.paramError("[contractAddressArrays] is invalid");
        }

        PageInfo<MiniContractInfo> pageInfo = new PageInfo<>(pageNumber, pageSize);
        if (CacheManager.isChainExist(chainId)) {
            pageInfo.setTotalCount(totalCount);
            pageInfo.setList(contractService.getContractList(chainId, contractAddressList));
        }
        RpcResult result = new RpcResult();
        result.setResult(pageInfo);
        return result;
    }

    /**
     * 上传合约代码jar包
     */
    @RpcMethod("uploadContractJar")
    public RpcResult upload(List<Object> params) throws NulsException {
        VerifyUtils.verifyParams(params, 2);
        int chainId;
        String jarFileData;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            jarFileData = (String) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[jarFileData] is invalid");
        }

        if (!CacheManager.isChainExist(chainId)) {
            return RpcResult.dataNotFound();
        }
        RpcResult rpcResult = new RpcResult();
        Result<Map> mapResult = WalletRpcHandler.uploadContractJar(chainId, jarFileData);
        rpcResult.setResult(mapResult.getData());
        return rpcResult;
    }

    /**
     * 获取合约代码构造函数
     */
    @RpcMethod("getContractConstructor")
    public RpcResult getContractConstructor(List<Object> params) throws NulsException {
        VerifyUtils.verifyParams(params, 2);
        int chainId;
        String contractCode;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            contractCode = (String) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[contractCode] is invalid");
        }
        if (!CacheManager.isChainExist(chainId)) {
            return RpcResult.dataNotFound();
        }
        RpcResult rpcResult = new RpcResult();
        Result<Map> mapResult = WalletRpcHandler.getContractConstructor(chainId, contractCode);
        Map resultData = mapResult.getData();
        if (resultData == null) {
            rpcResult.setError(new RpcResultError(RpcErrorCode.DATA_NOT_EXISTS));
        } else {
            rpcResult.setResult(resultData);
        }
        return rpcResult;
    }


    /**
     * 获取合约方法信息
     *
     * @param params
     * @return
     */
    @RpcMethod("getContractMethod")
    public RpcResult getContractMethod(List<Object> params) {
        VerifyUtils.verifyParams(params, 3);
        int chainId;
        String contractAddress;
        String methodName;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            contractAddress = (String) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }
        try {
            methodName = (String) params.get(2);
        } catch (Exception e) {
            return RpcResult.paramError("[methodName] is invalid");
        }
        String methodDesc = null;
        if (params.size() > 3) {
            methodDesc = (String) params.get(3);
        }

        if (!AddressTool.validAddress(chainId, contractAddress)) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }
        if (!CacheManager.isChainExist(chainId)) {
            return RpcResult.dataNotFound();
        }
        if (StringUtils.isBlank(methodName)) {
            return RpcResult.paramError("[methodName] is invalid");
        }
        RpcResult rpcResult = new RpcResult();
        ContractInfo contractInfo = contractService.getContractInfo(chainId, contractAddress);
        if (contractInfo == null) {
            return rpcResult.setError(new RpcResultError(RpcErrorCode.DATA_NOT_EXISTS));
        }
        List<ContractMethod> methods = contractInfo.getMethods();
        ContractMethod resultMethod = null;
        boolean isEmptyMethodDesc = StringUtils.isBlank(methodDesc);
        for (ContractMethod method : methods) {
            if (method.getName().equals(methodName)) {
                if (isEmptyMethodDesc) {
                    resultMethod = method;
                    break;
                } else if (methodDesc.equals(method.getDesc())) {
                    resultMethod = method;
                    break;
                }
            }
        }
        if (resultMethod == null) {
            return RpcResult.dataNotFound();
        }
        rpcResult.setResult(resultMethod);
        return rpcResult;
    }


    /**
     * 获取合约方法参数类型
     */
    @RpcMethod("getContractMethodArgsTypes")
    public RpcResult getContractMethodArgsTypes(List<Object> params) {
        RpcResult result = this.getContractMethod(params);
        if (result.getError() != null) {
            return result;
        }
        ContractMethod resultMethod = (ContractMethod) result.getResult();
        if (resultMethod == null) {
            return RpcResult.dataNotFound();
        }
        List<String> argsTypes;
        List<ContractMethodArg> args = resultMethod.getParams();
        argsTypes = new ArrayList<>();
        for (ContractMethodArg arg : args) {
            argsTypes.add(arg.getType());
        }
        RpcResult rpcResult = new RpcResult();
        rpcResult.setResult(argsTypes);
        return rpcResult;
    }

    /**
     * 验证创建合约
     *
     * @param params
     * @return
     */
    @RpcMethod("validateContractCreate")
    public RpcResult validateContractCreate(List<Object> params) throws NulsException {
        VerifyUtils.verifyParams(params, 6);
        int chainId;
        String contractCode;
        Object[] args;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            contractCode = params.get(4).toString();
        } catch (Exception e) {
            return RpcResult.paramError("[contractCode] is invalid");
        }
        try {
            List argsList = (List) params.get(5);
            args = argsList != null ? argsList.toArray() : null;
        } catch (Exception e) {
            return RpcResult.paramError("[args] is invalid");
        }
        if (!CacheManager.isChainExist(chainId)) {
            return RpcResult.dataNotFound();
        }
        RpcResult rpcResult = new RpcResult();
        Result<Map> constructorResult = WalletRpcHandler.getContractConstructor(chainId, contractCode);
        Map constructorData = constructorResult.getData();
        if (constructorData == null) {
            rpcResult.setError(new RpcResultError(RpcErrorCode.DATA_NOT_EXISTS));
            return rpcResult;
        }
        Map constructor = (Map) constructorData.get("constructor");
        ContractProgramMethod constructorMethod = new ContractProgramMethod(constructor);
        String[] types = constructorMethod.argsType2Array();
        convertArgsToObjectArray(args, types);

        Result<Map> mapResult = WalletRpcHandler.validateContractCreate(chainId,
                params.get(1),
                params.get(2),
                params.get(3),
                contractCode,
                args
        );
        rpcResult.setResult(mapResult.getData());
        return rpcResult;
    }

    /**
     * 验证调用合约
     *
     * @param params
     * @return
     */
    @RpcMethod("validateContractCall")
    public RpcResult validateContractCall(List<Object> params) throws NulsException {
        VerifyUtils.verifyParams(params, 9);
        int chainId;
        String contractAddress, methodName, methodDesc;
        Object[] args;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            contractAddress = params.get(5).toString();
        } catch (Exception e) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }
        try {
            methodName = params.get(6).toString();
        } catch (Exception e) {
            return RpcResult.paramError("[methodName] is invalid");
        }
        try {
            methodDesc = params.get(7).toString();
        } catch (Exception e) {
            return RpcResult.paramError("[methodDesc] is invalid");
        }
        try {
            List argsList = (List) params.get(8);
            args = argsList != null ? argsList.toArray() : null;
        } catch (Exception e) {
            return RpcResult.paramError("[args] is invalid");
        }
        Object[] multyAssetValues = null;
        if (params.size() > 9) {
            try {
                List list1 = (List) params.get(9);
                multyAssetValues = new Object[list1.size()];
                for (int i = 0; i < list1.size(); i++) {
                    multyAssetValues[i] = list1.get(i);
                }
            } catch (Exception e) {
                return RpcResult.paramError("[multyAssetValues] is invalid");
            }
        }
        if (!CacheManager.isChainExist(chainId)) {
            return RpcResult.dataNotFound();
        }
        RpcResult contractMethodArgsTypesResult = this.getContractMethodArgsTypes(List.of(chainId, contractAddress, methodName, methodDesc));
        if (contractMethodArgsTypesResult.getError() != null) {
            return contractMethodArgsTypesResult;
        }
        List<String> typeList = (List<String>) contractMethodArgsTypesResult.getResult();
        String[] types = new String[typeList.size()];
        types = typeList.toArray(types);
        convertArgsToObjectArray(args, types);

        RpcResult rpcResult = new RpcResult();
        Result<Map> mapResult = WalletRpcHandler.validateContractCall(chainId,
                params.get(1),
                params.get(2),
                params.get(3),
                params.get(4),
                contractAddress,
                methodName,
                methodDesc,
                args,
                multyAssetValues
        );
        rpcResult.setResult(mapResult.getData());
        return rpcResult;
    }

    /**
     * 验证删除合约
     *
     * @param params
     * @return
     */
    @RpcMethod("validateContractDelete")
    public RpcResult validateContractDelete(List<Object> params) throws NulsException {
        VerifyUtils.verifyParams(params, 3);
        int chainId;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        if (!CacheManager.isChainExist(chainId)) {
            return RpcResult.dataNotFound();
        }
        RpcResult rpcResult = new RpcResult();
        Result<Map> mapResult = WalletRpcHandler.validateContractDelete(chainId,
                params.get(1),
                params.get(2)
        );
        rpcResult.setResult(mapResult.getData());
        return rpcResult;
    }

    /**
     * 预估创建合约交易的gas
     *
     * @param params
     * @return
     */
    @RpcMethod("imputedContractCreateGas")
    public RpcResult imputedContractCreateGas(List<Object> params) throws NulsException {
        VerifyUtils.verifyParams(params, 4);
        int chainId;
        String contractCode;
        Object[] args;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            contractCode = params.get(2).toString();
        } catch (Exception e) {
            return RpcResult.paramError("[contractCode] is invalid");
        }
        try {
            List argsList = (List) params.get(3);
            args = argsList != null ? argsList.toArray() : null;
        } catch (Exception e) {
            return RpcResult.paramError("[args] is invalid");
        }
        if (!CacheManager.isChainExist(chainId)) {
            return RpcResult.dataNotFound();
        }
        RpcResult rpcResult = new RpcResult();
        Result<Map> constructorResult = WalletRpcHandler.getContractConstructor(chainId, contractCode);
        Map constructorData = constructorResult.getData();
        if (constructorData == null) {
            rpcResult.setError(new RpcResultError(RpcErrorCode.DATA_NOT_EXISTS));
            return rpcResult;
        }
        Map constructor = (Map) constructorData.get("constructor");
        ContractProgramMethod constructorMethod = new ContractProgramMethod(constructor);
        String[] types = constructorMethod.argsType2Array();
        convertArgsToObjectArray(args, types);

        Result<Map> mapResult = WalletRpcHandler.imputedContractCreateGas(chainId,
                params.get(1),
                contractCode,
                args
        );
        rpcResult.setResult(mapResult.getData());
        return rpcResult;
    }

    /**
     * 预估调用合约交易的gas
     *
     * @param params
     * @return
     */
    @RpcMethod("imputedContractCallGas")
    public RpcResult imputedContractCallGas(List<Object> params) throws NulsException {
        VerifyUtils.verifyParams(params, 7);
        int chainId;
        String contractAddress, methodName, methodDesc;
        Object[] args;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            contractAddress = params.get(3).toString();
        } catch (Exception e) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }
        try {
            methodName = params.get(4).toString();
        } catch (Exception e) {
            return RpcResult.paramError("[methodName] is invalid");
        }
        try {
            methodDesc = params.get(5).toString();
        } catch (Exception e) {
            return RpcResult.paramError("[methodDesc] is invalid");
        }
        try {
            List argsList = (List) params.get(6);
            args = argsList != null ? argsList.toArray() : null;
        } catch (Exception e) {
            return RpcResult.paramError("[args] is invalid");
        }
        Object[] multyAssetValues = null;
        if (params.size() > 7) {
            try {
                List list1 = (List) params.get(7);
                multyAssetValues = new Object[list1.size()];
                for (int i = 0; i < list1.size(); i++) {
                    multyAssetValues[i] = list1.get(i);
                }
            } catch (Exception e) {
                return RpcResult.paramError("[multyAssetValues] is invalid");
            }
        }
        if (!CacheManager.isChainExist(chainId)) {
            return RpcResult.dataNotFound();
        }
        RpcResult contractMethodArgsTypesResult = this.getContractMethodArgsTypes(List.of(chainId, contractAddress, methodName, methodDesc));
        if (contractMethodArgsTypesResult.getError() != null) {
            return contractMethodArgsTypesResult;
        }
        List<String> typeList = (List<String>) contractMethodArgsTypesResult.getResult();
        String[] types = new String[typeList.size()];
        types = typeList.toArray(types);
        convertArgsToObjectArray(args, types);

        if (!CacheManager.isChainExist(chainId)) {
            return RpcResult.dataNotFound();
        }
        RpcResult rpcResult = new RpcResult();
        Result<Map> mapResult = WalletRpcHandler.imputedContractCallGas(chainId,
                params.get(1),
                params.get(2),
                contractAddress,
                methodName,
                methodDesc,
                args,
                multyAssetValues
        );
        rpcResult.setResult(mapResult.getData());
        return rpcResult;
    }

    /**
     * 调用合约不上链方法
     *
     * @param params
     * @return
     */
    @RpcMethod("invokeView")
    public RpcResult invokeView(List<Object> params) throws NulsException {
        VerifyUtils.verifyParams(params, 5);
        int chainId;
        String contractAddress, methodName, methodDesc;
        Object[] args;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            contractAddress = params.get(1).toString();
        } catch (Exception e) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }
        try {
            methodName = params.get(2).toString();
        } catch (Exception e) {
            return RpcResult.paramError("[methodName] is invalid");
        }
        try {
            methodDesc = params.get(3).toString();
        } catch (Exception e) {
            return RpcResult.paramError("[methodDesc] is invalid");
        }
        try {
            List argsList = (List) params.get(4);
            args = argsList != null ? argsList.toArray() : null;
        } catch (Exception e) {
            return RpcResult.paramError("[args] is invalid");
        }
        if (!CacheManager.isChainExist(chainId)) {
            return RpcResult.dataNotFound();
        }
        RpcResult contractMethodArgsTypesResult = this.getContractMethodArgsTypes(List.of(chainId, contractAddress, methodName, methodDesc));
        if (contractMethodArgsTypesResult.getError() != null) {
            return contractMethodArgsTypesResult;
        }
        List<String> typeList = (List<String>) contractMethodArgsTypesResult.getResult();
        String[] types = new String[typeList.size()];
        types = typeList.toArray(types);
        convertArgsToObjectArray(args, types);

        if (!CacheManager.isChainExist(chainId)) {
            return RpcResult.dataNotFound();
        }
        RpcResult rpcResult = new RpcResult();
        Result<Map> mapResult = WalletRpcHandler.invokeView(chainId,
                contractAddress,
                methodName,
                methodDesc,
                args
        );
        rpcResult.setResult(mapResult.getData());
        return rpcResult;
    }


    @RpcMethod("previewCall")
    public RpcResult previewCall(List<Object> params) throws NulsException {
        VerifyUtils.verifyParams(params, 9);
        int chainId;
        String sender, contractAddress, methodName, methodDesc, value;
        BigInteger valueBigInteger = BigInteger.ZERO;
        long gasLimit, price;
        Object[] args;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        if (!CacheManager.isChainExist(chainId)) {
            return RpcResult.dataNotFound();
        }
        try {
            sender = (String) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[sender] is invalid");
        }
        try {
            contractAddress = (String) params.get(2);
        } catch (Exception e) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }
        try {
            methodName = (String) params.get(3);
        } catch (Exception e) {
            return RpcResult.paramError("[methodName] is invalid");
        }
        try {
            methodDesc = (String) params.get(4);
        } catch (Exception e) {
            return RpcResult.paramError("[methodDesc] is invalid");
        }
        try {
            value = (String) params.get(5);
            if (StringUtils.isNotBlank(value)) {
                valueBigInteger = new BigInteger(value);
            }
        } catch (Exception e) {
            return RpcResult.paramError("[value] is invalid");
        }
        try {
            gasLimit = Long.parseLong(params.get(6).toString());
        } catch (Exception e) {
            return RpcResult.paramError("[gasLimit] is invalid");
        }
        try {
            price = Long.parseLong(params.get(7).toString());
        } catch (Exception e) {
            return RpcResult.paramError("[price] is invalid");
        }
        try {
            List list = (List) params.get(8);
            args = new Object[list.size()];
            for (int i = 0; i < list.size(); i++) {
                args[i] = list.get(i);
            }
        } catch (Exception e) {
            return RpcResult.paramError("[args] is invalid");
        }
        Object[] multyAssetValues = null;
        if (params.size() > 9) {
            try {
                List list1 = (List) params.get(9);
                multyAssetValues = new Object[list1.size()];
                for (int i = 0; i < list1.size(); i++) {
                    multyAssetValues[i] = list1.get(i);
                }
            } catch (Exception e) {
                return RpcResult.paramError("[multyAssetValues] is invalid");
            }
        }
        RpcResult contractMethodArgsTypesResult = this.getContractMethodArgsTypes(List.of(chainId, contractAddress, methodName, methodDesc));
        if (contractMethodArgsTypesResult.getError() != null) {
            return contractMethodArgsTypesResult;
        }
        List<String> typeList = (List<String>) contractMethodArgsTypesResult.getResult();
        String[] types = new String[typeList.size()];
        types = typeList.toArray(types);
        convertArgsToObjectArray(args, types);

        RpcResult rpcResult = new RpcResult();
        Result<Map> mapResult = WalletRpcHandler.contractPreviewCall(chainId, sender, valueBigInteger, gasLimit, price, contractAddress, methodName, methodDesc, args, multyAssetValues);
        rpcResult.setResult(mapResult.getData());
        return rpcResult;
    }

    /**
     * chainId
     * pageNumber
     * pageSize
     */
    @RpcMethod("getNrc1155List")
    public RpcResult getNrc1155List(List<Object> params) {
        int chainId;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        if (!CacheManager.isChainExist(chainId)) {
            return RpcResult.dataNotFound();
        }
        params.add(ApiConstant.TOKEN_TYPE_NRC1155);
        params.add(false);
        return this.getContractList(params);
    }

    /**
     * chainId
     * pageNumber
     * pageSize
     * address
     */
    @RpcMethod("getAccountToken1155s")
    public RpcResult getAccountToken1155s(List<Object> params) {
        VerifyUtils.verifyParams(params, 4);
        int chainId, pageNumber, pageSize;
        String address, contractAddress = null;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            pageNumber = (int) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[pageNumber] is invalid");
        }
        try {
            pageSize = (int) params.get(2);
        } catch (Exception e) {
            return RpcResult.paramError("[pageSize] is invalid");
        }
        try {
            address = (String) params.get(3);
        } catch (Exception e) {
            return RpcResult.paramError("[address] is invalid");
        }

        if (params.size() > 4) {
            contractAddress = (String) params.get(4);
        }

        if (!AddressTool.validAddress(chainId, address)) {
            return RpcResult.paramError("[address] is invalid");
        }
        if (pageNumber <= 0) {
            pageNumber = 1;
        }
        if (pageSize <= 0 || pageSize > 100) {
            pageSize = 10;
        }

        PageInfo<AccountToken1155Info> pageInfo;
        if (!CacheManager.isChainExist(chainId)) {
            pageInfo = new PageInfo<>(pageNumber, pageSize);
        } else {
            pageInfo = token1155Service.getAccountTokens(chainId, address, contractAddress, pageNumber, pageSize);
        }
        RpcResult result = new RpcResult();
        result.setResult(pageInfo);
        return result;
    }

    /**
     * chainId
     * address
     * contract
     * tokenId
     */
    @RpcMethod("getAccountToken1155")
    public RpcResult getAccountToken1155(List<Object> params) {
        RpcResult result = new RpcResult();
        VerifyUtils.verifyParams(params, 4);
        int chainId;
        String address, contract, tokenId;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            address = (String) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[address] is invalid");
        }
        try {
            contract = (String) params.get(2);
        } catch (Exception e) {
            return RpcResult.paramError("[contract] is invalid");
        }
        try {
            tokenId = (String) params.get(3);
        } catch (Exception e) {
            return RpcResult.paramError("[tokenId] is invalid");
        }
        if (!AddressTool.validAddress(chainId, address)) {
            return RpcResult.paramError("[address] is invalid");
        }
        if (!AddressTool.validContractAddress(AddressTool.getAddress(contract), chainId)) {
            return RpcResult.paramError("[contract] is invalid");
        }
        AccountToken1155Info tokenInfo = token1155Service.getAccountTokenInfo(chainId, address + contract + tokenId);
        if (tokenInfo == null) {
            result.setError(new RpcResultError(RpcErrorCode.DATA_NOT_EXISTS));
            return result;
        }
        result.setResult(tokenInfo);
        return result;
    }

    @RpcMethod("getToken1155HolderInfo")
    public RpcResult getToken1155HolderInfo(List<Object> params) {
        VerifyUtils.verifyParams(params, 3);
        int chainId;
        String contractAddress, address;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            contractAddress = (String) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }
        if (!AddressTool.validAddress(chainId, contractAddress)) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }

        try {
            address = (String) params.get(2);
        } catch (Exception e) {
            return RpcResult.paramError("[address] is invalid");
        }
        if (!AddressTool.validAddress(chainId, address)) {
            return RpcResult.paramError("[address] is invalid");
        }
        try {
            List<AccountToken1155Info> info = null;
            if (CacheManager.isChainExist(chainId)) {
                info = token1155Service.getAccountTokens(chainId, address, contractAddress);
            }
            RpcResult result = new RpcResult();
            result.setResult(info);
            return result;
        } catch (Exception e) {
            LoggerUtil.commonLog.error(e);
            return RpcResult.failed(RpcErrorCode.SYS_UNKNOWN_EXCEPTION);
        }
    }


    /**
     * chainId
     * pageNumber
     * pageSize
     * contractAddress
     */
    @RpcMethod("getContractToken1155s")
    public RpcResult getContractToken1155s(List<Object> params) {
        VerifyUtils.verifyParams(params, 4);
        int chainId, pageNumber, pageSize;
        String contractAddress;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            pageNumber = (int) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[pageNumber] is invalid");
        }
        try {
            pageSize = (int) params.get(2);
        } catch (Exception e) {
            return RpcResult.paramError("[pageSize] is invalid");
        }
        try {
            contractAddress = (String) params.get(3);
        } catch (Exception e) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }
        if (!AddressTool.validAddress(chainId, contractAddress)) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }
        if (pageNumber <= 0) {
            pageNumber = 1;
        }
        if (pageSize <= 0 || pageSize > 100) {
            pageSize = 10;
        }

        try {
            PageInfo<AccountToken1155Info> pageInfo;
            if (!CacheManager.isChainExist(chainId)) {
                pageInfo = new PageInfo<>(pageNumber, pageSize);
            } else {
                pageInfo = token1155Service.getContractTokens(chainId, contractAddress, pageNumber, pageSize);
            }
            RpcResult result = new RpcResult();
            result.setResult(pageInfo);
            return result;
        } catch (Exception e) {
            LoggerUtil.commonLog.error(e);
            return RpcResult.failed(RpcErrorCode.SYS_UNKNOWN_EXCEPTION);
        }
    }

    /**
     * chainId
     * pageNumber
     * pageSize
     * address
     * contractAddress
     * tokenId
     */
    @RpcMethod("getToken1155Transfers")
    public RpcResult getToken1155Transfers(List<Object> params) {
        VerifyUtils.verifyParams(params, 6);
        int chainId, pageNumber, pageSize;
        String address, contractAddress, tokenId;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            pageNumber = (int) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[pageNumber] is invalid");
        }
        try {
            pageSize = (int) params.get(2);
        } catch (Exception e) {
            return RpcResult.paramError("[pageSize] is invalid");
        }
        try {
            address = (String) params.get(3);
        } catch (Exception e) {
            return RpcResult.paramError("[address] is invalid");
        }
        try {
            contractAddress = (String) params.get(4);
        } catch (Exception e) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }
        try {
            tokenId = (String) params.get(5);
        } catch (Exception e) {
            return RpcResult.paramError("[tokenId] is invalid");
        }

        if (!StringUtils.isBlank(address) && !AddressTool.validAddress(chainId, address)) {
            return RpcResult.paramError("[address] is invalid");
        }
        if (!StringUtils.isBlank(contractAddress) && !AddressTool.validAddress(chainId, contractAddress)) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }

        if (pageNumber <= 0) {
            pageNumber = 1;
        }
        if (pageSize <= 0 || pageSize > 100) {
            pageSize = 10;
        }

        try {
            PageInfo<Token1155Transfer> pageInfo;
            if (!CacheManager.isChainExist(chainId)) {
                pageInfo = new PageInfo<>(pageNumber, pageSize);
            } else {
                pageInfo = token1155Service.getTokenTransfers(chainId, address, contractAddress, tokenId, pageNumber, pageSize);
            }
            RpcResult result = new RpcResult();
            result.setResult(pageInfo);
            return result;
        } catch (Exception e) {
            LoggerUtil.commonLog.error(e);
            return RpcResult.failed(RpcErrorCode.SYS_UNKNOWN_EXCEPTION);
        }
    }

    /**
     * chainId
     * pageNumber
     * pageSize
     * contractAddress
     */
    @RpcMethod("getToken1155Ids")
    public RpcResult getToken1155Ids(List<Object> params) {
        VerifyUtils.verifyParams(params, 4);
        int chainId, pageNumber, pageSize;
        String contractAddress;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            pageNumber = (int) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[pageNumber] is invalid");
        }
        try {
            pageSize = (int) params.get(2);
        } catch (Exception e) {
            return RpcResult.paramError("[pageSize] is invalid");
        }
        try {
            contractAddress = (String) params.get(3);
        } catch (Exception e) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }
        if (!AddressTool.validAddress(chainId, contractAddress)) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }
        if (pageNumber <= 0) {
            pageNumber = 1;
        }
        if (pageSize <= 0 || pageSize > 100) {
            pageSize = 10;
        }

        try {
            PageInfo<Nrc1155TokenIdInfo> pageInfo;
            if (!CacheManager.isChainExist(chainId)) {
                pageInfo = new PageInfo<>(pageNumber, pageSize);
            } else {
                pageInfo = token1155Service.getContractTokenIds(chainId, contractAddress, pageNumber, pageSize);
            }
            RpcResult result = new RpcResult();
            result.setResult(pageInfo);
            return result;
        } catch (Exception e) {
            LoggerUtil.commonLog.error(e);
            return RpcResult.failed(RpcErrorCode.SYS_UNKNOWN_EXCEPTION);
        }
    }

    /**
     * chainId
     * contractAddress
     * tokenId
     */
    @RpcMethod("getToken1155Id")
    public RpcResult getToken1155Id(List<Object> params) {
        VerifyUtils.verifyParams(params, 3);
        int chainId;
        String contractAddress, tokenId;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            contractAddress = (String) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }
        try {
            tokenId = String.valueOf(params.get(2));
        } catch (Exception e) {
            return RpcResult.paramError("[tokenId] is invalid");
        }
        if (!AddressTool.validAddress(chainId, contractAddress)) {
            return RpcResult.paramError("[contractAddress] is invalid");
        }
        if (StringUtils.isBlank(tokenId)) {
            return RpcResult.paramError("[tokenId] is invalid");
        }

        try {
            RpcResult result = new RpcResult();
            Nrc1155TokenIdInfo tokenIdInfo = token1155Service.getContractTokenId(chainId, contractAddress, tokenId);
            if (tokenIdInfo == null) {
                result.setError(new RpcResultError(RpcErrorCode.DATA_NOT_EXISTS));
                return result;
            }
            result.setResult(tokenIdInfo);
            return result;
        } catch (Exception e) {
            LoggerUtil.commonLog.error(e);
            return RpcResult.failed(RpcErrorCode.SYS_UNKNOWN_EXCEPTION);
        }
    }

    static void convertArgsToObjectArray(Object[] args, String[] types) throws NulsException {
        if (args == null || args.length == 0) {
            return;
        }
        try {
            Object temp;
            for (int i = 0, length = types.length; i < length; i++) {
                temp = args[i];
                if (temp == null) {
                    continue;
                }
                if (types[i].contains("[]") && temp instanceof String && StringUtils.isNotBlank((String) temp)) {
                    args[i] = JSONUtils.json2pojo((String) temp, ArrayList.class);
                }
            }
        } catch (Exception e) {
            Log.error("parse args error.", e);
            throw new NulsException(CommonCodeConstanst.PARSE_JSON_FAILD, "parse contract args error.");
        }
    }

    static class ContractProgramMethod {
        private List<ContractProgramMethodArg> args;

        public ContractProgramMethod(Map result) {
            List<Map> args = (List<Map>) result.get("args");
            this.args = new LinkedList<>();
            if (args == null || args.isEmpty()) {
                return;
            }
            for (Map arg : args) {
                this.args.add(new ContractProgramMethodArg(arg));
            }
        }

        public String[] argsType2Array() {
            if (args != null && args.size() > 0) {
                int size = args.size();
                String[] result = new String[size];
                for (int i = 0; i < size; i++) {
                    result[i] = args.get(i).getType();
                }
                return result;
            } else {
                return null;
            }
        }
    }

    static class ContractProgramMethodArg {
        private String type;

        public ContractProgramMethodArg(Map result) {
            this.type = (String) result.get("type");
        }

        public String getType() {
            return type;
        }
    }
}
