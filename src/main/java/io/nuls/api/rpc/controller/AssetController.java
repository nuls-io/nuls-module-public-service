package io.nuls.api.rpc.controller;

import io.nuls.api.cache.ChainAssetCache;
import io.nuls.api.db.ChainAssetService;
import io.nuls.api.model.po.PageInfo;
import io.nuls.api.model.po.asset.ChainAssetHolderInfo;
import io.nuls.api.model.po.asset.ChainAssetInfo;
import io.nuls.api.model.po.asset.ChainAssetInfoVo;
import io.nuls.api.model.po.asset.ChainAssetTx;
import io.nuls.api.model.rpc.RpcResult;
import io.nuls.api.utils.VerifyUtils;
import io.nuls.core.core.annotation.Autowired;
import io.nuls.core.core.annotation.Controller;
import io.nuls.core.core.annotation.RpcMethod;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AssetController {
    @Autowired
    private ChainAssetService assetService;

    @RpcMethod("getChainAssetInfo")
    public RpcResult getChainAssetInfo(List<Object> params) {
        VerifyUtils.verifyParams(params, 1);
        String assetKey;
        try {
            assetKey = (String) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[assetKey] is inValid");
        }
        ChainAssetInfo info = ChainAssetCache.getAssetInfo(assetKey);
        if (null == info) {
            info = assetService.get(assetKey);
        }
        return new RpcResult().setResult(new ChainAssetInfoVo(info));
    }


    @RpcMethod("getOneHolderByAssetKey")
    public RpcResult getOneHolderByAssetKey(List<Object> params) {
        VerifyUtils.verifyParams(params, 3);
        Integer chainId;
        String assetKey, address;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is inValid");
        }
        try {
            assetKey = (String) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[assetKey] is inValid");
        }
        try {
            address = (String) params.get(2);
        } catch (Exception e) {
            return RpcResult.paramError("[address] is inValid");
        }
        ChainAssetHolderInfo info = this.assetService.getOneHolderByAssetKey(chainId, assetKey, address);
        return new RpcResult().setResult(info);
    }

    @RpcMethod("getHoldersByAssetKey")
    public RpcResult getHoldersByAssetKey(List<Object> params) {
        VerifyUtils.verifyParams(params, 4);
        Integer chainId, pageNumber, pageSize;
        String assetKey;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is inValid");
        }
        try {
            assetKey = (String) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[assetKey] is inValid");
        }
        try {
            pageNumber = (int) params.get(2);
        } catch (Exception e) {
            return RpcResult.paramError("[pageNumber] is inValid");
        }
        try {
            pageSize = (int) params.get(3);
        } catch (Exception e) {
            return RpcResult.paramError("[pageSize] is inValid");
        }
        PageInfo<ChainAssetHolderInfo> pageInfo = this.assetService.getHoldersByAssetKey(chainId, assetKey, pageNumber, pageSize);
        return new RpcResult().setResult(pageInfo);
    }

    @RpcMethod("getTopAssets")
    public RpcResult getTopAssets(List<Object> params) {
        VerifyUtils.verifyParams(params, 2);
        Integer pageNumber, pageSize;
        try {
            pageNumber = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[pageNumber] is inValid");
        }
        try {
            pageSize = (int) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[pageSize] is inValid");
        }
        PageInfo<ChainAssetInfoVo> list = this.assetService.getList(pageNumber, pageSize);
        return new RpcResult().setResult(list);
    }

    @RpcMethod("getTxsByAssetKey")
    public RpcResult getTxsByAssetKey(List<Object> params) {
        VerifyUtils.verifyParams(params, 3);
        Integer pageNumber, pageSize, type = null;
        String assetKey, from = null, to = null;
        try {
            assetKey = (String) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[assetKey] is inValid");
        }
        try {
            pageNumber = (int) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[pageNumber] is inValid");
        }
        try {
            pageSize = (int) params.get(2);
        } catch (Exception e) {
            return RpcResult.paramError("[pageSize] is inValid");
        }
        try {
            if (params.size() > 3) {
                type = Integer.parseInt("" + params.get(3));
            }
        } catch (Exception e) {
        }
        try {
            if (params.size() > 4) {
                from = (String) params.get(4);
            }
        } catch (Exception e) {
        }
        try {
            if (params.size() > 5) {
                to = (String) params.get(5);
            }
        } catch (Exception e) {
        }

        PageInfo<ChainAssetTx> pageInfo = this.assetService.getTxList(assetKey, pageNumber, pageSize, type, from, to);
        return new RpcResult().setResult(pageInfo);
    }
}
