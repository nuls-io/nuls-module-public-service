package io.nuls.api.constant;

import java.util.List;
import java.util.Map;

public class PublicServiceConstant {
    public static int defaultChainId = 0;

    public static List<Map<String,Object>> MAIN_NET_SETTING =  FeeUtils.getMainNetSetting();
    public static List<Map<String,Object>> TEST_NET_SETTING =  FeeUtils.getTestNetSetting();
}
