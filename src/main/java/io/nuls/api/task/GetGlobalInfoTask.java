package io.nuls.api.task;

import io.nuls.api.ApiContext;
import io.nuls.api.analysis.WalletRpcHandler;
import io.nuls.base.api.provider.ServiceManager;
import io.nuls.base.api.provider.protocol.ProtocolProvider;
import io.nuls.base.api.provider.protocol.facade.GetVersionReq;
import io.nuls.base.api.provider.protocol.facade.VersionInfo;
import io.nuls.core.basic.Result;

import java.util.Map;

public class GetGlobalInfoTask implements Runnable {

    private int chainId;

    public GetGlobalInfoTask(int chainId) {
        this.chainId = chainId;
    }

    ProtocolProvider transferService = ServiceManager.get(ProtocolProvider.class);

    @Override
    public void run() {
        Result<Map<String, Object>> result = WalletRpcHandler.getBlockGlobalInfo(chainId);
        if (result == null || result.isFailed()) {
            return;
        }
        Map<String, Object> map = result.getData();
        ApiContext.localHeight = Long.parseLong(map.get("localHeight").toString());
        ApiContext.networkHeight = Long.parseLong(map.get("networkHeight").toString());

        if (ApiContext.magicNumber == 0) {
            result = WalletRpcHandler.getNetworkInfo(chainId);
            map = result.getData();
            ApiContext.magicNumber = Integer.parseInt(map.get("magicNumber").toString());
        }

        io.nuls.base.api.provider.Result<VersionInfo> res = transferService.getVersion(new GetVersionReq());
        if (res.isSuccess()) {
            VersionInfo info = res.getData();
            ApiContext.localProtocolVersion = info.getLocalProtocolVersion();
        }
    }
}
