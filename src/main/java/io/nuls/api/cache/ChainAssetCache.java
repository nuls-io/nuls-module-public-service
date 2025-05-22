package io.nuls.api.cache;

import io.nuls.api.model.po.asset.ChainAssetInfo;
import io.nuls.core.model.StringUtils;

import java.util.*;

public class ChainAssetCache {
    private static final Map<String, ChainAssetInfo> idInfoMap = new HashMap<>();

    public static final void initCache(List<ChainAssetInfo> list) {
        for (ChainAssetInfo info : list) {
            idInfoMap.put(info.getId(), info);
        }
    }

    public static final List<ChainAssetInfo> search(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        List<ChainAssetInfo> infoList = new ArrayList<>();
        for (ChainAssetInfo info : idInfoMap.values()) {
            if (info.getSymbol().toUpperCase().startsWith(text.toUpperCase())) {
                infoList.add(info);
            }else if(info.getId().equals(text)){
                infoList.add(info);
            }
        }
        infoList.sort(new Comparator<ChainAssetInfo>() {
            @Override
            public int compare(ChainAssetInfo o1, ChainAssetInfo o2) {
                return o1.getSymbol().compareToIgnoreCase(o2.getSymbol());
            }
        });
        return infoList;
    }

    public static ChainAssetInfo getAssetInfo(String assetKey) {
        return idInfoMap.get(assetKey);
    }

    public static Collection<ChainAssetInfo> getAssetInfoList() {
        return idInfoMap.values();
    }
}
