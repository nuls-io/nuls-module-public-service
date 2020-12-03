/*
 * MIT License
 * Copyright (c) 2017-2019 nuls.io
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.nuls.api.rpc.controller;

import io.nuls.api.analysis.AnalysisHandler;
import io.nuls.api.analysis.WalletRpcHandler;
import io.nuls.api.db.BlockService;
import io.nuls.api.manager.CacheManager;
import io.nuls.api.model.po.BlockHeaderInfo;
import io.nuls.api.model.po.BlockHexInfo;
import io.nuls.api.model.po.BlockInfo;
import io.nuls.api.model.po.PageInfo;
import io.nuls.api.model.po.mini.MiniBlockHeaderInfo;
import io.nuls.api.model.rpc.RpcResult;
import io.nuls.api.utils.VerifyUtils;
import io.nuls.core.basic.Result;
import io.nuls.core.constant.CommonCodeConstanst;
import io.nuls.core.constant.ErrorCode;
import io.nuls.core.core.annotation.Autowired;
import io.nuls.core.core.annotation.Controller;
import io.nuls.core.core.annotation.RpcMethod;
import io.nuls.core.model.StringUtils;

import java.util.List;

/**
 * @author Niels
 */
@Controller
public class BlockController {

    @Autowired
    private BlockService blockService;

    @RpcMethod("getBestBlockHeader")
    public RpcResult getBestInfo(List<Object> params) {
        VerifyUtils.verifyParams(params, 1);
        int chainId;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is inValid");
        }

        if (!CacheManager.isChainExist(chainId)) {
            return RpcResult.dataNotFound();
        }
        BlockHeaderInfo localBestBlockHeader = blockService.getBestBlockHeader(chainId);
        if (localBestBlockHeader == null) {
            return RpcResult.dataNotFound();
        }
        return RpcResult.success(localBestBlockHeader);

    }

    @RpcMethod("getHeaderByHeight")
    public RpcResult getHeaderByHeight(List<Object> params) {
        VerifyUtils.verifyParams(params, 2);
        int chainId;
        long height;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            height = Long.parseLong("" + params.get(1));
        } catch (Exception e) {
            return RpcResult.paramError("[height] is invalid");
        }

        if (height < 0) {
            return RpcResult.paramError("[height] is invalid");
        }

        if (!CacheManager.isChainExist(chainId)) {
            return RpcResult.dataNotFound();
        }
        BlockHeaderInfo header = blockService.getBlockHeader(chainId, height);
        if (header == null) {
            return RpcResult.dataNotFound();
        }
        return RpcResult.success(header);
    }

    @RpcMethod("getHeaderByHash")
    public RpcResult getHeaderByHash(List<Object> params) {
        VerifyUtils.verifyParams(params, 2);
        int chainId;
        String hash;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            hash = (String) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[hash] is invalid");
        }
        if (StringUtils.isBlank(hash)) {
            return RpcResult.paramError("[hash] is required");
        }

        if (!CacheManager.isChainExist(chainId)) {
            return RpcResult.dataNotFound();
        }
        BlockHeaderInfo header = blockService.getBlockHeaderByHash(chainId, hash);
        if (header == null) {
            return RpcResult.dataNotFound();
        }
        return RpcResult.success(header);
    }

    @RpcMethod("getBlockByHash")
    public RpcResult getBlockByHash(List<Object> params) {
        VerifyUtils.verifyParams(params, 2);
        int chainId;
        String hash;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            hash = (String) params.get(1);
        } catch (Exception e) {
            return RpcResult.paramError("[hash] is invalid");
        }
        if (StringUtils.isBlank(hash)) {
            return RpcResult.paramError("[hash] is required");
        }
        if (!CacheManager.isChainExist(chainId)) {
            return RpcResult.dataNotFound();
        }

        BlockHeaderInfo blockHeaderInfo = blockService.getBlockHeaderByHash(chainId, hash);
        if (blockHeaderInfo == null) {
            return RpcResult.dataNotFound();
        }
        BlockHexInfo hexInfo = blockService.getBlockHexInfo(chainId, blockHeaderInfo.getHeight());
        if (hexInfo == null) {
            return RpcResult.dataNotFound();
        }
        try {
            BlockInfo blockInfo = AnalysisHandler.toBlockInfo(hexInfo.getBlockHex(), chainId);
            blockInfo.setHeader(blockHeaderInfo);
            return RpcResult.success(blockInfo);
        } catch (Exception e) {
            return RpcResult.failed(CommonCodeConstanst.DATA_PARSE_ERROR);
        }
    }

    @RpcMethod("getBlockByHeight")
    public RpcResult getBlockByHeight(List<Object> params) {
        VerifyUtils.verifyParams(params, 2);
        int chainId;
        long height;
        try {
            chainId = (int) params.get(0);
        } catch (Exception e) {
            return RpcResult.paramError("[chainId] is invalid");
        }
        try {
            height = Long.parseLong("" + params.get(1));
        } catch (Exception e) {
            return RpcResult.paramError("[height] is invalid");
        }
        if (height < 0) {
            return RpcResult.paramError("[height] is invalid");
        }
        if (!CacheManager.isChainExist(chainId)) {
            return RpcResult.dataNotFound();
        }

        BlockHeaderInfo blockHeaderInfo = blockService.getBlockHeader(chainId, height);
        if (blockHeaderInfo == null) {
            return RpcResult.dataNotFound();
        }
        BlockHexInfo hexInfo = blockService.getBlockHexInfo(chainId, blockHeaderInfo.getHeight());
        if (hexInfo == null) {
            return RpcResult.dataNotFound();
        }
        try {
            BlockInfo blockInfo = AnalysisHandler.toBlockInfo(hexInfo.getBlockHex(), chainId);
            blockInfo.setHeader(blockHeaderInfo);
            return RpcResult.success(blockInfo);
        } catch (Exception e) {
            return RpcResult.failed(CommonCodeConstanst.DATA_PARSE_ERROR);
        }
    }

    @RpcMethod("getBlockHeaderList")
    public RpcResult getBlockHeaderList(List<Object> params) {
        VerifyUtils.verifyParams(params, 3);
        int chainId, pageNumber, pageSize;
        boolean filterEmptyBlocks;
        String packingAddress = null;
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
            filterEmptyBlocks = (boolean) params.get(3);
        } catch (Exception e) {
            return RpcResult.paramError("[filterEmptyBlocks] is invalid");
        }
        try {
            if (params.size() > 4) {
                packingAddress = (String) params.get(4);
            }
        } catch (Exception e) {
            return RpcResult.paramError("[packingAddress] is invalid");
        }

        if (pageNumber <= 0) {
            pageNumber = 1;
        }
        if (pageSize <= 0 || pageSize > 100) {
            pageSize = 10;
        }

        PageInfo<MiniBlockHeaderInfo> pageInfo;
        if (!CacheManager.isChainExist(chainId)) {
            pageInfo = new PageInfo<>(pageNumber, pageSize);
        } else {
            pageInfo = blockService.pageQuery(chainId, pageNumber, pageSize, packingAddress, filterEmptyBlocks);
        }
        RpcResult result = new RpcResult();
        result.setResult(pageInfo);
        return result;
    }
//
//    @RpcMethod("rollbackBestBlocks")
//    public RpcResult rollbackBestBlocks(List<Object> params) {
//        VerifyUtils.verifyParams(params, 1);
//        int count = (int) params.get(0);
//        BlockHeaderInfo localBestBlockHeader;
//        long useNanoTime = 0;
//        for (; count > 0; count--) {
//            localBestBlockHeader = blockHeaderService.getBestBlockHeader();
//            if (null != localBestBlockHeader && localBestBlockHeader.getHeight() >= 0L) {
//                try {
//                    long start = System.nanoTime();
//                    rollbackBlock.rollbackBlock(localBestBlockHeader.getHeight());
//                    useNanoTime += System.nanoTime() - start;
//                } catch (Exception e) {
//                    Log.error(e);
//                    throw new JsonRpcException(new RpcResultError(RpcErrorCode.SYS_UNKNOWN_EXCEPTION, "Rollback is failed"));
//                }
//            }
//        }
//        Log.info("rollback " + count + " use:" + useNanoTime/1000000 + "ms.");
//
//        RpcResult rpcResult = new RpcResult();
//        rpcResult.setResult(true);
//        return rpcResult;
//    }
//
//    @RpcMethod("stopSync")
//    public RpcResult stopSync(List<Object> params) {
//        ApiContext.doSync = false;
//        RpcResult result = new RpcResult();
//        result.setResult(true);
//        return result;
//    }
//
//    @RpcMethod("recoverySync")
//    public RpcResult recoverySync(List<Object> params) {
//        ApiContext.doSync = true;
//        RpcResult result = new RpcResult();
//        result.setResult(true);
//        return result;
//    }

}
