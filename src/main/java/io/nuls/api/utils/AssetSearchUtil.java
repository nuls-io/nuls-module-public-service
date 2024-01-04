package io.nuls.api.utils;

import io.nuls.api.cache.ApiCache;
import io.nuls.api.cache.ChainAssetCache;
import io.nuls.api.manager.CacheManager;
import io.nuls.api.model.dto.SearchAssetInfo;
import io.nuls.api.model.po.Nrc1155Info;
import io.nuls.api.model.po.Nrc20Info;
import io.nuls.api.model.po.Nrc721Info;
import io.nuls.api.model.po.asset.ChainAssetInfo;
import io.nuls.api.model.po.asset.ChainAssetInfoVo;
import io.nuls.core.model.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AssetSearchUtil {

    public static final List<SearchAssetInfo> search(int chainId, String text) {
        List<SearchAssetInfo> resultList = new ArrayList<>();
        List<ChainAssetInfo> chainAssetList = ChainAssetCache.search(text);
        Set<String> set = new HashSet<>();
        if (null != chainAssetList && !chainAssetList.isEmpty()) {
            chainAssetList.forEach(v -> {
                resultList.add(new SearchAssetInfo(v));
                set.add(v.getContract());
                LoggerUtil.commonLog.info("Search asset - {}", v.getContract());
            });
        }
        //搜索其他类型资产
        ApiCache apiCache = CacheManager.getCache(chainId);

        List<Nrc20Info> nrc20List = searchNRC20(apiCache.getNrc20InfoList(), text);
        if (null != nrc20List && !nrc20List.isEmpty()) {
            nrc20List.forEach(v -> {
                if (!set.contains(v.getContractAddress())) {
                    LoggerUtil.commonLog.info("Search nrc20 - {}", v.getContractAddress());
                    resultList.add(new SearchAssetInfo(v));
                }
            });
        }
        List<Nrc721Info> nrc721List = searchNRC721(apiCache.getNrc721InfoList(), text);
        if (null != nrc721List && !nrc721List.isEmpty()) {
            nrc721List.forEach(v -> resultList.add(new SearchAssetInfo(v)));
        }
        List<Nrc1155Info> nrc1155List = searchNRC1155(apiCache.getNrc1155InfoList(), text);
        if (null != nrc1155List && !nrc1155List.isEmpty()) {
            nrc1155List.forEach(v -> resultList.add(new SearchAssetInfo(v)));
        }


        return resultList;
    }


    public static final List<Nrc20Info> searchNRC20(List<Nrc20Info> nrc20InfoList, String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        LoggerUtil.commonLog.info("Search nrc20 list size : {}", nrc20InfoList.size());
        List<Nrc20Info> infoList = new ArrayList<>();
        for (Nrc20Info info : nrc20InfoList) {
            if (info.getSymbol().toUpperCase().startsWith(text.toUpperCase())) {
                infoList.add(info);
            }
        }
        return infoList;
    }

    public static final List<Nrc721Info> searchNRC721(List<Nrc721Info> tokenInfoList, String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        LoggerUtil.commonLog.info("Search nrc721 list size : {}", tokenInfoList.size());
        List<Nrc721Info> infoList = new ArrayList<>();
        for (Nrc721Info info : tokenInfoList) {
            if (info.getSymbol().toUpperCase().startsWith(text.toUpperCase())) {
                infoList.add(info);
            }
        }
        return infoList;
    }

    public static final List<Nrc1155Info> searchNRC1155(List<Nrc1155Info> tokenInfoList, String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        LoggerUtil.commonLog.info("Search nrc1155 list size : {}", tokenInfoList.size());
        List<Nrc1155Info> infoList = new ArrayList<>();
        for (Nrc1155Info info : tokenInfoList) {
            if (info.getSymbol().toUpperCase().startsWith(text.toUpperCase())) {
                infoList.add(info);
            }
        }
        return infoList;
    }

}
