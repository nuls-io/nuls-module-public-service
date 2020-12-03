package io.nuls.api.analysis;

import io.nuls.api.ApiContext;
import io.nuls.api.constant.ApiConstant;
import io.nuls.api.constant.ApiErrorCode;
import io.nuls.api.constant.CommandConstant;
import io.nuls.api.model.po.*;
import io.nuls.api.model.rpc.BalanceInfo;
import io.nuls.api.model.rpc.FreezeInfo;
import io.nuls.api.rpc.RpcCall;
import io.nuls.base.RPCUtil;
import io.nuls.base.basic.NulsByteBuffer;
import io.nuls.base.data.Transaction;
import io.nuls.core.basic.Result;
import io.nuls.core.constant.CommonCodeConstanst;
import io.nuls.core.constant.TxStatusEnum;
import io.nuls.core.exception.NulsException;
import io.nuls.core.log.Log;
import io.nuls.core.model.StringUtils;
import io.nuls.core.rpc.info.Constants;
import io.nuls.core.rpc.model.ModuleE;
import io.nuls.core.rpc.model.message.Response;
import io.nuls.core.rpc.netty.processor.ResponseMessageProcessor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.nuls.api.constant.ApiConstant.*;

public class WalletRpcHandler {


    public static Result<BlockInfo> getBlockInfo(int chainID, long height) {
        Map<String, Object> params = new HashMap<>(ApiConstant.INIT_CAPACITY_8);
        params.put(Constants.VERSION_KEY_STR, ApiContext.VERSION);
        params.put(Constants.CHAIN_ID, chainID);
        params.put("height", height);
        try {
            Map map = (Map) RpcCall.request(ModuleE.BL.abbr, CommandConstant.GET_BLOCK_BY_HEIGHT, params);
            if (null == map || map.isEmpty()) {
                return Result.getSuccess(null);
            }

            BlockInfo blockInfo = AnalysisHandler.toBlockInfo((String) map.get("value"), chainID);
            return Result.getSuccess(null).setData(blockInfo);
        } catch (Exception e) {
            Log.error(e);
            return Result.getFailed(ApiErrorCode.DATA_PARSE_ERROR);
        }
    }

    public static Result<BlockInfo> getBlockInfo(int chainID, String hash) {
        Map<String, Object> params = new HashMap<>(ApiConstant.INIT_CAPACITY_8);
        params.put(Constants.VERSION_KEY_STR, ApiContext.VERSION);
        params.put(Constants.CHAIN_ID, chainID);
        params.put("hash", hash);
        try {
            Map map = (Map) RpcCall.request(ModuleE.BL.abbr, CommandConstant.GET_BLOCK_BY_HASH, params);
            if (null == map || map.isEmpty()) {
                return Result.getSuccess(null);
            }
            BlockInfo blockInfo = AnalysisHandler.toBlockInfo((String) map.get("value"), chainID);
            return Result.getSuccess(null).setData(blockInfo);
        } catch (Exception e) {
            Log.error(e);
        }
        return Result.getFailed(ApiErrorCode.DATA_PARSE_ERROR);
    }

    public static Result<Map<String, Object>> getBlockGlobalInfo(int chainId) {
        Map<String, Object> params = new HashMap<>(ApiConstant.INIT_CAPACITY_8);
        params.put(Constants.VERSION_KEY_STR, ApiContext.VERSION);
        params.put(Constants.CHAIN_ID, chainId);
        try {
            Map map = (Map) RpcCall.request(ModuleE.BL.abbr, CommandConstant.INFO, params);
            return Result.getSuccess(null).setData(map);
        } catch (Exception e) {
            Log.error(e);
        }
        return null;
    }

    public static BalanceInfo getAccountBalance(int chainId, String address, int assetChainId, int assetId) {
        Map<String, Object> params = new HashMap<>(ApiConstant.INIT_CAPACITY_8);
        params.put(Constants.VERSION_KEY_STR, ApiContext.VERSION);
        params.put(Constants.CHAIN_ID, chainId);
        params.put("address", address);
        params.put("assetChainId", assetChainId);
        params.put("assetId", assetId);
        try {
            Map map = (Map) RpcCall.request(ModuleE.LG.abbr, CommandConstant.GET_BALANCE, params);
            BalanceInfo balanceInfo = new BalanceInfo();
            balanceInfo.setBalance(new BigInteger(map.get("available").toString()));
            balanceInfo.setTimeLock(new BigInteger(map.get("timeHeightLocked").toString()));
            balanceInfo.setConsensusLock(new BigInteger(map.get("permanentLocked").toString()));
            balanceInfo.setFreeze(new BigInteger(map.get("freeze").toString()));
            balanceInfo.setNonce((String) map.get("nonce"));
            balanceInfo.setTotalBalance(balanceInfo.getBalance().add(balanceInfo.getConsensusLock()).add(balanceInfo.getTimeLock()));
            balanceInfo.setNonceType((Integer) map.get("nonceType"));
            return balanceInfo;
        } catch (Exception e) {
            Log.error(e);
        }
        return null;
    }

//    public static BalanceInfo getBalance(int chainId, String address, int assetChainId, int assetId) {
//        Map<String, Object> params = new HashMap<>(ApiConstant.INIT_CAPACITY_8);
//        params.put(Constants.VERSION_KEY_STR, ApiContext.VERSION);
//        params.put(Constants.CHAIN_ID, chainId);
//        params.put("address", address);
//        params.put("assetChainId", assetChainId);
//        params.put("assetId", assetId);
//        try {
//            Map map = (Map) RpcCall.request(ModuleE.LG.abbr, CommandConstant.GET_BALANCE, params);
//            BalanceInfo balanceInfo = new BalanceInfo();
//            balanceInfo.setTotalBalance(new BigInteger(map.get("total").toString()));
//            balanceInfo.setBalance(new BigInteger(map.get("available").toString()));
//            balanceInfo.setTimeLock(new BigInteger(map.get("timeHeightLocked").toString()));
//            balanceInfo.setConsensusLock(new BigInteger(map.get("permanentLocked").toString()));
//
//            return balanceInfo;
//        } catch (Exception e) {
//            Log.error(e);
//        }
//        return null;
//    }

    public static Result<PageInfo<FreezeInfo>> getFreezeList(int chainId, int assetChainId, int assetId, String address, int pageIndex, int pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put(Constants.VERSION_KEY_STR, ApiContext.VERSION);
        params.put(Constants.CHAIN_ID, chainId);
        params.put("pageNumber", pageIndex);
        params.put("pageSize", pageSize);
        params.put("address", address);
        params.put("assetChainId", assetChainId);
        params.put("assetId", assetId);
        try {
            Map map = (Map) RpcCall.request(ModuleE.LG.abbr, CommandConstant.GET_FREEZE, params);
            PageInfo<FreezeInfo> pageInfo = new PageInfo(pageIndex, pageSize);
            pageInfo.setTotalCount((int) map.get("totalCount"));
            List<Map> maps = (List<Map>) map.get("list");
            List<FreezeInfo> freezeInfos = new ArrayList<>();
            for (Map map1 : maps) {
                FreezeInfo freezeInfo = new FreezeInfo();
                freezeInfo.setAmount(map1.get("amount").toString());
                freezeInfo.setLockedValue(Long.parseLong(map1.get("lockedValue").toString()));
                freezeInfo.setTime(Long.parseLong(map1.get("time").toString()));
                freezeInfo.setTxHash((String) map1.get("txHash"));
                Result<TransactionInfo> result = getTx(chainId, freezeInfo.getTxHash());
                if (result.isSuccess()) {
                    TransactionInfo txInfo = result.getData();
                    freezeInfo.setType(txInfo.getType());
                }
                freezeInfos.add(freezeInfo);
            }
            pageInfo.setList(freezeInfos);
            return Result.getSuccess(null).setData(pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.getFailed(ApiErrorCode.DATA_PARSE_ERROR);
        }
    }

    public static Result<TransactionInfo> getTx(int chainId, String hash) {
        Map<String, Object> params = new HashMap<>();
        params.put(Constants.VERSION_KEY_STR, ApiContext.VERSION);
        params.put(Constants.CHAIN_ID, chainId);
        params.put("txHash", hash);
        try {
            Map map = (Map) RpcCall.request(ModuleE.TX.abbr, CommandConstant.GET_TX, params);
            if (map == null || map.isEmpty()) {
                return null;
            }
            String txHex = (String) map.get("tx");
            if (null == txHex) {
                return null;
            }
            Transaction tx = new Transaction();
            tx.parse(new NulsByteBuffer(RPCUtil.decode(txHex)));
            long height = Long.parseLong(map.get("height").toString());
            int status = (int) map.get("status");
            if (status == 1) {
                tx.setStatus(TxStatusEnum.CONFIRMED);
            }
            tx.setBlockHeight(height);
            TransactionInfo txInfo = AnalysisHandler.toTransaction(chainId, tx, ApiContext.protocolVersion);

            return Result.getSuccess(null).setData(txInfo);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode());
        } catch (Exception e) {
            Log.error(e);
            return Result.getFailed(ApiErrorCode.DATA_PARSE_ERROR);
        }
    }

    public static Result<AgentInfo> getAgentInfo(int chainId, String hash) {
        Map<String, Object> params = new HashMap<>();
        params.put(Constants.CHAIN_ID, chainId);
        params.put("agentHash", hash);
        try {
            Map map = (Map) RpcCall.request(ModuleE.CS.abbr, CommandConstant.GET_AGENT, params);
            AgentInfo agentInfo = new AgentInfo();
            agentInfo.setCreditValue(Double.parseDouble(map.get("creditVal").toString()));
            agentInfo.setDepositCount((Integer) map.get("memberCount"));
            agentInfo.setStatus((Integer) map.get("status"));

            return Result.getSuccess(null).setData(agentInfo);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode());
        }
    }

    public static Result<Map> getConsensusConfig(int chainId) {
        Map<String, Object> params = new HashMap<>();
        params.put(Constants.CHAIN_ID, chainId);
        try {
            Map map = (Map) RpcCall.request(ModuleE.CS.abbr, CommandConstant.GET_CONSENSUS_CONFIG, params);
            return Result.getSuccess(null).setData(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode());
        }
    }

    public static Result<ContractInfo> getContractInfo(int chainId, ContractInfo contractInfo) throws NulsException {
        Map<String, Object> params = new HashMap<>();
        params.put(Constants.CHAIN_ID, chainId);
        params.put("contractAddress", contractInfo.getContractAddress());
        params.put("hash", contractInfo.getCreateTxHash());
        //查询智能合约详情之前，先查询创建智能合约的执行结果是否成功
        Result<ContractResultInfo> result = getContractResultInfo(params);
        ContractResultInfo resultInfo = result.getData();
        contractInfo.setResultInfo(resultInfo);
        if (!resultInfo.isSuccess()) {
            contractInfo.setSuccess(false);
            contractInfo.setStatus(ApiConstant.CONTRACT_STATUS_FAIL);
            contractInfo.setErrorMsg(resultInfo.getErrorMessage());
            return Result.getSuccess(null).setData(contractInfo);
        }
        contractInfo.setStatus(ApiConstant.CONTRACT_STATUS_NORMAL);
        contractInfo.setSuccess(true);
        Map map = (Map) RpcCall.request(ModuleE.SC.abbr, CommandConstant.CONTRACT_INFO, params);

        contractInfo.setCreater(map.get("creater").toString());
        contractInfo.setNrc20((Boolean) map.get("nrc20"));
        contractInfo.setTokenType((Integer) map.get("tokenType"));
        contractInfo.setDirectPayable((Boolean) map.get("directPayable"));
        if (contractInfo.isNrc20()) {
            contractInfo.setTokenName(map.get("nrc20TokenName").toString());
            contractInfo.setSymbol(map.get("nrc20TokenSymbol").toString());
            contractInfo.setDecimals((Integer) map.get("decimals"));
            contractInfo.setTotalSupply(map.get("totalSupply").toString());
            contractInfo.setOwners(new ArrayList<>());
        }

        List<Map<String, Object>> methodMap = (List<Map<String, Object>>) map.get("method");
        List<ContractMethod> methodList = new ArrayList<>();
        List<Map<String, Object>> argsList;
        List<ContractMethodArg> paramList;
        for (Map<String, Object> map1 : methodMap) {
            ContractMethod method = new ContractMethod();
            method.setName((String) map1.get("name"));
            method.setDesc((String) map1.get("desc"));
            method.setReturnType((String) map1.get("returnArg"));
            method.setView((boolean) map1.get("view"));
            method.setPayable((boolean) map1.get("payable"));
            method.setEvent((boolean) map1.get("event"));
            method.setJsonSerializable((boolean) map1.get("jsonSerializable"));
            argsList = (List<Map<String, Object>>) map1.get("args");
            paramList = new ArrayList<>();
            for (Map<String, Object> arg : argsList) {
                paramList.add(makeContractMethodArg(arg));
            }
            method.setParams(paramList);
            methodList.add(method);
        }
        contractInfo.setMethods(methodList);
        return Result.getSuccess(null).setData(contractInfo);
    }

    private static ContractMethodArg makeContractMethodArg(Map<String, Object> arg) {
        return new ContractMethodArg((String) arg.get("type"), (String) arg.get("name"), (boolean) arg.get("required"));
    }

    public static Result<Map> getContractConstructor(int chainId, String contractCode) throws NulsException {
        Map<String, Object> params = new HashMap<>();
        params.put(Constants.CHAIN_ID, chainId);
        params.put("contractCode", contractCode);
        Map map = (Map) RpcCall.request(ModuleE.SC.abbr, CommandConstant.CONSTRUCTOR, params);
        return Result.getSuccess(null).setData(map);
    }

    private static String crossTokenSystemContract = null;

    public static String getCrossTokenSystemContract(int chainId) throws NulsException {
        if (StringUtils.isBlank(crossTokenSystemContract)) {
            Map<String, Object> params = new HashMap<>();
            params.put(Constants.CHAIN_ID, chainId);
            Map map = (Map) RpcCall.request(ModuleE.SC.abbr, CommandConstant.GET_CROSS_TOKEN_SYSTEM_CONTRACT, params);
            crossTokenSystemContract = (String) map.get("value");
        }
        return crossTokenSystemContract;
    }

    public static Result<Map> validateContractCreate(int chainId, Object sender, Object gasLimit, Object price, Object contractCode, Object args) throws NulsException {
        Map<String, Object> params = new HashMap<>();
        params.put(Constants.CHAIN_ID, chainId);
        params.put("sender", sender);
        params.put("gasLimit", gasLimit);
        params.put("price", price);
        params.put("contractCode", contractCode);
        params.put("args", args);
        Response response = RpcCall.requestAndResponse(ModuleE.SC.abbr, CommandConstant.VALIDATE_CREATE, params);
        boolean bool = response.isSuccess();
        String msg = "";
        String code = "";
        if (!bool) {
            msg = response.getResponseComment();
            code = response.getResponseErrorCode();
        }
        Map map = new HashMap(8);
        map.put("success", bool);
        map.put("code", code);
        map.put("msg", msg);
        return Result.getSuccess(null).setData(map);
    }

    public static Result<Map> validateContractCall(int chainId, Object sender, Object value, Object gasLimit, Object price,
                                                   Object contractAddress, Object methodName, Object methodDesc, Object args) throws NulsException {
        Map<String, Object> params = new HashMap<>();
        params.put(Constants.CHAIN_ID, chainId);
        params.put("sender", sender);
        params.put("value", value);
        params.put("gasLimit", gasLimit);
        params.put("price", price);
        params.put("contractAddress", contractAddress);
        params.put("methodName", methodName);
        params.put("methodDesc", methodDesc);
        params.put("args", args);
        Response response = RpcCall.requestAndResponse(ModuleE.SC.abbr, CommandConstant.VALIDATE_CALL, params);
        boolean bool = response.isSuccess();
        String msg = "";
        String code = "";
        if (!bool) {
            msg = response.getResponseComment();
            code = response.getResponseErrorCode();
        }
        Map map = new HashMap(8);
        map.put("success", bool);
        map.put("code", code);
        map.put("msg", msg);
        return Result.getSuccess(null).setData(map);
    }

    public static Result<Map> validateContractDelete(int chainId, Object sender, Object contractAddress) throws NulsException {
        Map<String, Object> params = new HashMap<>();
        params.put(Constants.CHAIN_ID, chainId);
        params.put("sender", sender);
        params.put("contractAddress", contractAddress);
        Response response = RpcCall.requestAndResponse(ModuleE.SC.abbr, CommandConstant.VALIDATE_DELETE, params);
        boolean bool = response.isSuccess();
        String msg = "";
        String code = "";
        if (!bool) {
            msg = response.getResponseComment();
            code = response.getResponseErrorCode();
        }
        Map map = new HashMap(8);
        map.put("success", bool);
        map.put("code", code);
        map.put("msg", msg);
        return Result.getSuccess(null).setData(map);
    }

    public static Result<Map> imputedContractCreateGas(int chainId, Object sender, Object contractCode, Object args) throws NulsException {
        Map<String, Object> params = new HashMap<>();
        params.put(Constants.CHAIN_ID, chainId);
        params.put("sender", sender);
        params.put("contractCode", contractCode);
        params.put("args", args);
        Map map = (Map) RpcCall.request(ModuleE.SC.abbr, CommandConstant.IMPUTED_CREATE_GAS, params);
        return Result.getSuccess(null).setData(map);
    }

    public static Result<Map> uploadContractJar(int chainId, Object jarFileData) throws NulsException {
        Map<String, Object> params = new HashMap<>();
        params.put(Constants.CHAIN_ID, chainId);
        params.put("jarFileData", jarFileData);
        Map map = (Map) RpcCall.request(ModuleE.SC.abbr, CommandConstant.UPLOAD, params);
        return Result.getSuccess(null).setData(map);
    }

    public static Result<Map> imputedContractCallGas(int chainId, Object sender, Object value,
                                                     Object contractAddress, Object methodName, Object methodDesc, Object args) throws NulsException {
        Map<String, Object> params = new HashMap<>();
        params.put(Constants.CHAIN_ID, chainId);
        params.put("sender", sender);
        params.put("value", value);
        params.put("contractAddress", contractAddress);
        params.put("methodName", methodName);
        params.put("methodDesc", methodDesc);
        params.put("args", args);
        Map map = (Map) RpcCall.request(ModuleE.SC.abbr, CommandConstant.IMPUTED_CALL_GAS, params);
        return Result.getSuccess(null).setData(map);
    }

    public static Result<Map> invokeView(int chainId, Object contractAddress, Object methodName, Object methodDesc, Object args) throws NulsException {
        Map<String, Object> params = new HashMap<>();
        params.put(Constants.CHAIN_ID, chainId);
        params.put("contractAddress", contractAddress);
        params.put("methodName", methodName);
        params.put("methodDesc", methodDesc);
        params.put("args", args);
        Map map = (Map) RpcCall.request(ModuleE.SC.abbr, CommandConstant.INVOKE_VIEW, params);
        return Result.getSuccess(null).setData(map);
    }

    public static Result<BigInteger> tokenBalance(int chainid, Object contractAddress, Object address) {
        try {
            Result<Map> result = invokeView(chainid, contractAddress, "balanceOf", null, new Object[]{address});
            Map map = result.getData();
            if (map == null) {
                return Result.getSuccess(null).setData(BigInteger.ZERO);
            }
            Object balance = map.get("result");
            if (balance == null) {
                return Result.getSuccess(null).setData(BigInteger.ZERO);
            }
            return Result.getSuccess(null).setData(new BigInteger(balance.toString()));
        } catch (NulsException e) {
            Log.error(e.format());
            return Result.getSuccess(null).setData(BigInteger.ZERO);
        }
    }

    public static Result<BigInteger> tokenTotalSupply(int chainid, Object contractAddress) {
        try {
            Result<Map> result = invokeView(chainid, contractAddress, "totalSupply", null, null);
            Map map = result.getData();
            if (map == null) {
                return Result.getSuccess(null).setData(BigInteger.ZERO);
            }
            Object totalSupply = map.get("result");
            if (totalSupply == null) {
                return Result.getSuccess(null).setData(BigInteger.ZERO);
            }
            return Result.getSuccess(null).setData(new BigInteger(totalSupply.toString()));
        } catch (NulsException e) {
            Log.error(e.format());
            return Result.getSuccess(null).setData(BigInteger.ZERO);
        }
    }

    public static Result<ContractResultInfo> getContractResultInfo(int chainId, String hash) throws NulsException {
        Map<String, Object> params = new HashMap<>();
        params.put(Constants.CHAIN_ID, chainId);
        params.put("hash", hash);
        return getContractResultInfo(params);
    }

    private static Result<ContractResultInfo> getContractResultInfo(Map<String, Object> params) throws NulsException {
        Map map = null;
        try {
            map = (Map) RpcCall.request(ModuleE.SC.abbr, CommandConstant.CONTRACT_RESULT, params);
        } catch (NulsException e) {
            return Result.getFailed(CommonCodeConstanst.DATA_NOT_FOUND);
        }
        map = (Map) RpcCall.request(ModuleE.SC.abbr, CommandConstant.CONTRACT_RESULT, params);
        map = (Map) map.get("data");
        if (map == null || map.isEmpty()) {
            return Result.getFailed(ApiErrorCode.DATA_NOT_FOUND);
        }

        String hash = (String) params.get("hash");
        ContractResultInfo resultInfo = AnalysisHandler.toContractResultInfo(hash, map);
        return Result.getSuccess(null).setData(resultInfo);
    }

    public static Result validateTx(int chainId, String txHex) {
        Map<String, Object> params = new HashMap<>();
        params.put(Constants.CHAIN_ID, chainId);
        params.put("tx", txHex);

        try {
            Map map = (Map) RpcCall.request(ModuleE.TX.abbr, CommandConstant.TX_VALIEDATE, params);
            return Result.getSuccess(null).setData(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode());
        }
    }

    public static Result broadcastTx(int chainId, String txHex) {
        Map<String, Object> params = new HashMap<>();
        params.put(Constants.CHAIN_ID, chainId);
        params.put("tx", txHex);

        try {
            Map map = (Map) RpcCall.request(ModuleE.TX.abbr, CommandConstant.TX_NEWTX, params);
            return Result.getSuccess(null).setData(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode());
        }
    }

    public static Result broadcastTxWithoutAnyValidation(int chainId, String txHex) {
        Map<String, Object> params = new HashMap<>();
        params.put(Constants.CHAIN_ID, chainId);
        params.put("tx", txHex);

        try {
            Map map = (Map) RpcCall.request(ModuleE.TX.abbr, CommandConstant.TX_BROADCAST, params);
            return Result.getSuccess(null).setData(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode());
        }
    }

    public static Result sendCrossTx(int chainId, String txHex) {
        Map<String, Object> params = new HashMap<>();
        params.put(Constants.CHAIN_ID, chainId);
        params.put("tx", txHex);
        try {
            Map map = (Map) RpcCall.request(ModuleE.CC.abbr, CommandConstant.SEND_CROSS_TX, params);
            return Result.getSuccess(null).setData(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode());
        }
    }

    public static Result isAliasUsable(int chainId, String alias) {
        Map<String, Object> params = new HashMap<>();
        params.put(Constants.CHAIN_ID, chainId);
        params.put("alias", alias);
        try {
            Map map = (Map) RpcCall.request(ModuleE.AC.abbr, CommandConstant.IS_ALAIS_USABLE, params);
            return Result.getSuccess(null).setData(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode());
        }
    }

    public static Result<Map<String, ContractResultInfo>> getContractResults(int chainId, List<String> hashList) {
        Map<String, Object> params = new HashMap<>();
        params.put(Constants.CHAIN_ID, chainId);
        params.put("hashList", hashList);

        try {
            Map<String, Object> map = (Map) RpcCall.request(ModuleE.SC.abbr, CommandConstant.CONTRACT_RESULT_LIST, params);

            Map<String, ContractResultInfo> resultInfoMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                ContractResultInfo resultInfo = AnalysisHandler.toContractResultInfo(entry.getKey(), (Map<String, Object>) entry.getValue());
                resultInfoMap.put(resultInfo.getTxHash(), resultInfo);
            }
            return Result.getSuccess(null).setData(resultInfoMap);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode());
        }
    }

    public static Result getRegisteredChainInfoList() {
        try {
            Map<String, Object> map = (Map) RpcCall.request(ModuleE.CC.abbr, CommandConstant.GET_REGISTERED_CHAIN, null);
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) map.get("list");

            Map<String, AssetInfo> assetInfoMap = new HashMap<>();
            Map<Integer, ChainInfo> chainInfoMap = new HashMap<>();

            for (Map<String, Object> resultMap : resultList) {
                ChainInfo chainInfo = new ChainInfo();
                chainInfo.setChainId((Integer) resultMap.get("chainId"));
                chainInfo.setChainName((String) resultMap.get("chainName"));
                chainInfoMap.put(chainInfo.getChainId(), chainInfo);

                List<Map<String, Object>> assetList = (List<Map<String, Object>>) resultMap.get("assetInfoList");
                if (assetList != null) {
                    for (Map<String, Object> assetMap : assetList) {
                        AssetInfo assetInfo = new AssetInfo();
                        assetInfo.setChainId((Integer) resultMap.get("chainId"));
                        assetInfo.setAssetId((Integer) assetMap.get("assetId"));
                        assetInfo.setSymbol((String) assetMap.get("symbol"));
                        assetInfo.setDecimals((Integer) assetMap.get("decimalPlaces"));
                        boolean usable = (boolean) assetMap.get("usable");
                        if (usable) {
                            assetInfo.setStatus(ENABLE);
                        } else {
                            assetInfo.setStatus(DISABLE);
                        }
                        assetInfoMap.put(assetInfo.getKey(), assetInfo);
                    }
                }
            }

            if (assetInfoMap.isEmpty()) {
                AssetInfo assetInfo = new AssetInfo();
                assetInfo.setChainId(ApiContext.defaultChainId);
                assetInfo.setAssetId(ApiContext.defaultAssetId);
                assetInfo.setSymbol(ApiContext.defaultSymbol);
                assetInfo.setDecimals(ApiContext.defaultDecimals);
                assetInfo.setStatus(ENABLE);
                assetInfoMap.put(assetInfo.getKey(), assetInfo);
            }

            map.clear();
            map.put("chainInfoMap", chainInfoMap);
            map.put("assetInfoMap", assetInfoMap);

            return Result.getSuccess(null).setData(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode());
        }
    }

    public static Result getAllAddressPrefix() {
        try {
            List list = (List) RpcCall.request(ModuleE.AC.abbr, CommandConstant.GET_ALL_ADDRESS_PREFIX, null);
            return Result.getSuccess(null).setData(list);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode());
        }
    }


    public static Result getByzantineCount(int chainId, String txHash) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("chainId", chainId);
            params.put("txHash", txHash);
            Map<String, Object> map = (Map<String, Object>) RpcCall.request(ModuleE.CC.abbr, CommandConstant.GET_BYZANTINE_COUNT, params);
            return Result.getSuccess(null).setData(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode());
        }
    }

    public static Result getNetworkInfo(int chainId) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("chainId", chainId);
            Map<String, Object> map = (Map<String, Object>) RpcCall.request(ModuleE.NW.abbr, CommandConstant.GET_NETWORK_GROUP, params);
            return Result.getSuccess(null).setData(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode());
        }
    }


    public static Result contractPreviewCall(int chainId, String sender, BigInteger value, long gasLimit, long price, String contractAddress, String methodName, String methodDesc, Object[] args) {

        try {
            Map<String, Object> params = new HashMap<>();
            params.put(Constants.CHAIN_ID, chainId);
            params.put("sender", sender);
            params.put("value", value);
            params.put("gasLimit", gasLimit);
            params.put("price", price);
            params.put("contractAddress", contractAddress);
            params.put("methodName", methodName);
            params.put("methodDesc", methodDesc);
            params.put("args", args);
            Map map = (Map) RpcCall.request(ModuleE.SC.abbr, CommandConstant.PREVIEW_CALL, params);
            return Result.getSuccess(null).setData(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode());
        }

    }

    public static Result getChainAssetInfo(int assetChainId, int assetId) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put(Constants.CHAIN_ID, ApiContext.defaultChainId);
            params.put("assetChainId", assetChainId);
            params.put("assetId", assetId);
            Map map = (Map) RpcCall.request(ModuleE.LG.abbr, CommandConstant.CMD_GET_ASSET_BY_ID, params);
            return Result.getSuccess(null).setData(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode());
        }
    }

    /**
     * 查询NRC20的资产ID
     */
    public static Integer getAssetIdOfNRC20(String contractAddress) {
        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("contractAddress", contractAddress);
            Map result = (Map) RpcCall.request(ModuleE.LG.abbr, CommandConstant.CMD_CHAIN_ASSET_CONTRACT_ASSETID, parameters);
            Integer assetId = Integer.parseInt(result.get("assetId").toString());
            return assetId;
        } catch (NulsException e) {
            Log.warn("查询NRC20资产ID异常, msg: {}", e.format());
            return null;
        }
    }

    /**
     * 查询是否为跨链资产
     */
    public static boolean isCrossAssets(int chainId, int assetId) {
        Map<String, Object> params = new HashMap(4);
        params.put(Constants.CHAIN_ID, chainId);
        params.put("assetId", assetId);
        try {
            Response callResp = ResponseMessageProcessor.requestAndResponse(ModuleE.CM.abbr, CommandConstant.CMD_ASSET, params);
            return callResp.isSuccess();
        } catch (Exception e) {
            Log.warn("查询是否为跨链资产异常, msg: {}", e.getMessage());
            return false;
        }
    }
}
