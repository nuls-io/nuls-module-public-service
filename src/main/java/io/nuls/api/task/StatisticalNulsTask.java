package io.nuls.api.task;


import io.nuls.api.ApiContext;
import io.nuls.api.analysis.WalletRpcHandler;
import io.nuls.api.cache.ApiCache;
import io.nuls.api.cache.AssetSystemCache;
import io.nuls.api.db.AccountService;
import io.nuls.api.db.AgentService;
import io.nuls.api.db.ChainService;
import io.nuls.api.manager.CacheManager;
import io.nuls.api.model.dto.AssetsSystemTokenInfoVo;
import io.nuls.api.model.po.AssetInfo;
import io.nuls.api.model.po.CoinContextInfo;
import io.nuls.api.model.po.DestroyInfo;
import io.nuls.api.model.po.SyncInfo;
import io.nuls.api.model.rpc.BalanceInfo;
import io.nuls.api.utils.AssetTool;
import io.nuls.api.utils.LoggerUtil;
import io.nuls.base.basic.AddressTool;
import io.nuls.core.core.ioc.SpringLiteContext;
import io.nuls.core.model.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;

public class StatisticalNulsTask implements Runnable {

    private int chainId;

    private AccountService accountService;

    private AgentService agentService;

    private ChainService chainService;

    public StatisticalNulsTask(int chainId) {
        this.chainId = chainId;
        accountService = SpringLiteContext.getBean(AccountService.class);
        agentService = SpringLiteContext.getBean(AgentService.class);
        chainService = SpringLiteContext.getBean(ChainService.class);
    }

    @Override
    public void run() {
        try {
            BigInteger totalCoin = BigInteger.ZERO;         //total
            SyncInfo syncInfo = chainService.getSyncInfo(chainId);
            if (syncInfo != null) {
                totalCoin = syncInfo.getTotalSupply();
            }
            BigInteger consensusTotal = agentService.getConsensusCoinTotal(chainId);

            ApiCache apiCache = CacheManager.getCache(chainId);
            CoinContextInfo contextInfo = apiCache.getCoinContextInfo();
            //Number of team holdings
            BigInteger teamNuls = BigInteger.ZERO;
            BalanceInfo balanceInfo = null;
            if (!StringUtils.isBlank(ApiContext.TEAM_ADDRESS)) {
                teamNuls = accountService.getAccountTotalBalance(chainId, ApiContext.TEAM_ADDRESS);
                AssetInfo defaultAsset = apiCache.getChainInfo().getDefaultAsset();
                balanceInfo = WalletRpcHandler.getAccountBalance(chainId, ApiContext.TEAM_ADDRESS, defaultAsset.getChainId(), defaultAsset.getAssetId());
            }
            contextInfo.setTeam(teamNuls);
            //Destruction quantity
            byte[] address = AddressTool.getAddress(ApiContext.blackHolePublicKey, chainId);
            String destroyAddress = AddressTool.getStringAddressByBytes(address);
            BigInteger destroyNuls = accountService.getAccountTotalBalance(chainId, destroyAddress);

            for (String blackAddress : AddressTool.BLOCK_HOLE_ADDRESS_SET) {
                BigInteger blackNuls = accountService.getAccountTotalBalance(chainId, blackAddress);
                destroyNuls = destroyNuls.add(blackNuls);
            }
            // add by pierre at 2020-04-02 Protocol upgrade black hole address
            if (ApiContext.protocolVersion >= 5) {
                for (String blackAddress : AddressTool.BLOCK_HOLE_ADDRESS_SET_5) {
                    BigInteger blackNuls = accountService.getAccountTotalBalance(chainId, blackAddress);
                    destroyNuls = destroyNuls.add(blackNuls);
                }
            }
            // end code by pierre
            //Business Holding Quantity
            BigInteger businessNuls = BigInteger.ZERO;
            for (String businessAddress : ApiContext.BUSINESS_ADDRESS) {
                BigInteger amount = accountService.getAccountTotalBalance(chainId, businessAddress);
                businessNuls = businessNuls.add(amount);
            }
            contextInfo.setBusiness(businessNuls);
            //Community holdings
            BigInteger communityNuls = BigInteger.ZERO;
            for (String communityAddress : ApiContext.COMMUNITY_ADDRESS) {
                BigInteger amount = accountService.getAccountTotalBalance(chainId, communityAddress);
                communityNuls = communityNuls.add(amount);
            }
            contextInfo.setCommunity(communityNuls);

            BigInteger unmapped = BigInteger.ZERO;
            if (ApiContext.MAPPING_ADDRESS != null) {
                for (String mapAddress : ApiContext.MAPPING_ADDRESS) {
                    unmapped = unmapped.add(accountService.getAccountTotalBalance(chainId, mapAddress));
                }
            }
            contextInfo.setUnmapped(unmapped);
            contextInfo.setTotal(totalCoin);
            contextInfo.setConsensusTotal(consensusTotal);
            contextInfo.setDestroy(destroyNuls);

            BigInteger circulation = totalCoin.subtract(destroyNuls);
//            if (balanceInfo != null) {
//                circulation = circulation.subtract(balanceInfo.getTimeLock());
//            }
//            circulation = circulation.subtract(businessNuls);
            circulation = circulation.subtract(communityNuls);
//            circulation = circulation.subtract(unmapped);
            contextInfo.setCirculation(circulation);

            setDestroyInfo(contextInfo);

            setDeflationInfo(contextInfo);
        } catch (Exception e) {
            LoggerUtil.commonLog.error(e);
        }
    }


    //          #Initial inflation amount500w/365*30
    long inflationAmount = 41095890410959L;
    //          #Inflation start calculation time(unit:S)2020-07-12 00:00:00
    long initTime = 1594483200;
    //          #Deflationary ratio(If there is no deflation, set to100)
    double deflationRatio = 0.996;
    //          #Deflation interval time(unit：S),30day
    long deflationTimeInterval = 2592000;

    private void setDeflationInfo(CoinContextInfo contextInfo) {
        long nowTime = System.currentTimeMillis() / 1000;
        Long times = (nowTime - initTime) / deflationTimeInterval;
        BigDecimal blockRewardAmount = BigDecimal.valueOf(inflationAmount).multiply(BigDecimal.valueOf(deflationRatio).pow(times.intValue())).divide(BigDecimal.valueOf(30 * 24 * 360));
        BigDecimal afertBlockRewardAmount = BigDecimal.valueOf(inflationAmount).multiply(BigDecimal.valueOf(deflationRatio).pow(times.intValue() + 1)).divide(BigDecimal.valueOf(30 * 24 * 360));
        contextInfo.setBlockRewardBeforeDeflation(blockRewardAmount.longValue());
        contextInfo.setBlockRewardAfterDeflation(afertBlockRewardAmount.longValue());
        long next = initTime + deflationTimeInterval * times + deflationTimeInterval;
        contextInfo.setNextDeflationTime(next * 1000);
    }


    private void setDestroyInfo(CoinContextInfo contextInfo) {
        List<DestroyInfo> list = new LinkedList<>();
        //Destruction quantity
        byte[] address = AddressTool.getAddress(ApiContext.blackHolePublicKey, chainId);
        AssetInfo assetInfo = CacheManager.getRegisteredAsset(chainId + "-" + 1);

        BigInteger total = null;
        if (null != assetInfo) {
            total = assetInfo.getLocalTotalCoins();
        }
        AssetsSystemTokenInfoVo token = AssetSystemCache.getAssetCache(chainId + "-" + 1);
        if (null != token) {
            total = new BigInteger(token.getTotalSupply());
        }

        String destroyAddress = AddressTool.getStringAddressByBytes(address);
        BigInteger destroyNuls = accountService.getAccountTotalBalance(chainId, destroyAddress);
        String reason = "account set alias destroy nuls";
        String type = "Black Hole Address";
        String aproportion = null;
        if (total != null && total.compareTo(BigInteger.ZERO) != 0) {
            double _proportion = new BigDecimal(destroyNuls).divide(new BigDecimal(total), 6, RoundingMode.HALF_UP).doubleValue() * 100;
            aproportion = _proportion + "%";
        }
        DestroyInfo destroyInfo = new DestroyInfo(destroyAddress, type, reason, AssetTool.toCoinString(destroyNuls), aproportion);
        list.add(destroyInfo);

        reason = "stolen blacklist";
        type = "Permanenty Locked";
        for (String blackAddress : AddressTool.BLOCK_HOLE_ADDRESS_SET) {
            if (chainId != 1 && blackAddress.startsWith("NULS")) {
                continue;
            }
            BigInteger blackNuls = accountService.getAccountTotalBalance(chainId, blackAddress);
            destroyNuls = destroyNuls.add(blackNuls);
            String proportion = null;
            if (total != null && total.compareTo(BigInteger.ZERO)!=0) {
                double _proportion = new BigDecimal(blackNuls).divide(new BigDecimal(total), 6, RoundingMode.HALF_UP).doubleValue() * 100;
                proportion = _proportion + "%";
            }
            destroyInfo = new DestroyInfo(blackAddress, type, reason, AssetTool.toCoinString(blackNuls), proportion);
            list.add(destroyInfo);
        }
        // add by pierre at 2020-04-02 Protocol upgrade black hole address
        if (ApiContext.protocolVersion >= 5) {
            for (String blackAddress : AddressTool.BLOCK_HOLE_ADDRESS_SET_5) {
                if (chainId != 1 && blackAddress.startsWith("NULS")) {
                    continue;
                }
                BigInteger blackNuls = accountService.getAccountTotalBalance(chainId, blackAddress);
                destroyNuls = destroyNuls.add(blackNuls);
                String proportion = null;
                if (total != null && total.compareTo(BigInteger.ZERO) != 0) {
                    double _proportion = new BigDecimal(blackNuls).divide(new BigDecimal(total), 6, RoundingMode.HALF_UP).doubleValue() * 100;
                    proportion = _proportion + "%";
                }
                destroyInfo = new DestroyInfo(blackAddress, type, reason, AssetTool.toCoinString(blackNuls), proportion);
                list.add(destroyInfo);
            }
        }
        // end code by pierre
        contextInfo.setDestroyInfoList(list);
    }
}
