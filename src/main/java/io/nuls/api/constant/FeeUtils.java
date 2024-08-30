package io.nuls.api.constant;

import io.nuls.core.parse.JSONUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeeUtils {
    public static void main(String[] args) {
        List<Map<String, Object>> list = getMainNetSetting();
        System.out.println(list.size());
    }


    public static List<Map<String, Object>> getMainNetSetting() {
        try {
            List MAIN_NET_SETTING = JSONUtils.json2list("[\n" +
                    "    {\n" +
                    "        \"assetId\": \"1-1\",\n" +
                    "        \"symbol\": \"NULS\",\n" +
                    "        \"decimals\": 8,\n" +
                    "        \"feePerKB\": \"100000\",\n" +
                    "        \"scFeeFoefficient\": \"1\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"assetId\": \"9-787\",\n" +
                    "        \"symbol\": \"BTC\",\n" +
                    "        \"decimals\": 8,\n" +
                    "        \"feePerKB\": \"100\",\n" +
                    "        \"scFeeFoefficient\": \"0.0001\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"assetId\": \"9-2\",\n" +
                    "        \"symbol\": \"ETH\",\n" +
                    "        \"decimals\": 18,\n" +
                    "        \"feePerKB\": \"20000000000000\",\n" +
                    "        \"scFeeFoefficient\": \"10000000\"\n" +
                    "    }\n" +
                    "]", HashMap.class);
            return MAIN_NET_SETTING;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }



    public static List<Map<String, Object>> getTestNetSetting() {
        try {
            List MAIN_NET_SETTING = JSONUtils.json2list("[\n" +
                    "    {\n" +
                    "        \"assetId\": \"2-1\",\n" +
                    "        \"symbol\": \"NULS\",\n" +
                    "        \"decimals\": 8,\n" +
                    "        \"feePerKB\": \"100000\",\n" +
                    "        \"scFeeFoefficient\": \"1\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"assetId\": \"2-201\",\n" +
                    "        \"symbol\": \"BTC\",\n" +
                    "        \"decimals\": 8,\n" +
                    "        \"feePerKB\": \"100\",\n" +
                    "        \"scFeeFoefficient\": \"0.0001\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"assetId\": \"2-202\",\n" +
                    "        \"symbol\": \"ETH\",\n" +
                    "        \"decimals\": 18,\n" +
                    "        \"feePerKB\": \"20000000000000\",\n" +
                    "        \"scFeeFoefficient\": \"10000000\"\n" +
                    "    }\n" +
                    "]", HashMap.class);
            return MAIN_NET_SETTING;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
