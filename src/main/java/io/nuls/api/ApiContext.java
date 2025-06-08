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

package io.nuls.api;

import io.nuls.api.model.po.AgentInfo;
import io.nuls.api.model.po.BlockHeaderInfo;
import io.nuls.api.model.po.CurrentRound;
import io.nuls.api.model.po.PageInfo;
import io.nuls.api.model.po.mini.MiniAccountInfo;
import io.nuls.api.model.po.mini.MiniBlockHeaderInfo;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Niels
 */
public class ApiContext {

    public static Lock locker = new ReentrantLock();

    public static int mainChainId;

    public static int mainAssetId;

    public static String mainSymbol;

    public static int defaultChainId;

    public static int defaultAssetId;

    public static String defaultChainName;

    public static String defaultSymbol;

    public static int defaultDecimals;

    public static int agentChainId;

    public static int agentAssetId;

    public static int awardAssetId;

    public static BigInteger minDeposit;

    public static String databaseUrl;

    public static int databasePort;

    public static String listenerIp;

    public static int rpcPort;

    public static String logLevel;

    public static String VERSION = "1.0";

    public static int protocolVersion = 1;

    public static int localProtocolVersion = 1;

    public static int maxAliveConnect;

    public static int maxWaitTime;

    public static int socketTimeout;

    public static int connectTimeOut;

    public static boolean isRunSmartContract;

    public static boolean isRunCrossChain;

    public static boolean isReady;

    public static long localHeight;

    public static long networkHeight;

    public static int magicNumber;

    public static boolean syncCoinBase;

    public static List<String> syncAddress = new ArrayList<>();
    //开发者节点地址
    public static Set<String> DEVELOPER_NODE_ADDRESS = new HashSet<>();
    //大使节点地址
    public static Set<String> AMBASSADOR_NODE_ADDRESS = new HashSet<>();
    //映射地址
    public static Set<String> MAPPING_ADDRESS = new HashSet<>();
    //商务地址
    public static Set<String> BUSINESS_ADDRESS = new HashSet<>();
    //团队地址
    public static String TEAM_ADDRESS = "";
    //社区地址
    public static Set<String> COMMUNITY_ADDRESS = new HashSet<>();
    //销毁地址公钥
    public static byte[] blackHolePublicKey;

    public static List<MiniBlockHeaderInfo> blockList;

    public static PageInfo<AgentInfo> agentPageInfo;

    public static PageInfo<MiniAccountInfo> miniAccountPageInfo;

    public static List<CurrentRound> roundList;

    // 手续费额外收益地址
    public static String TEAM_FEE_ADDRESS = "NULSd6HghHBMJ3AoQKx5tmThGBwK334LcVG32";
    public static String multicall = "NULSd6Hgrnv1oxcdyhzZmsu7HWgk7vcaR6nMR";

    public static void addAndRemoveLastBlockHeader(BlockHeaderInfo headerInfo) {
        MiniBlockHeaderInfo mini = new MiniBlockHeaderInfo(headerInfo);
        if (blockList.size() >= 15) {
            blockList.remove(blockList.size() - 1);
        }
        blockList.add(0, mini);
    }

    public static void addAndRemoveLastRound(CurrentRound round) {
        boolean has = false;
        for (int i = 0; i < roundList.size(); i++) {
            CurrentRound item = roundList.get(i);
            if (item.getIndex() == round.getIndex()) {
                roundList.remove(i);
                roundList.add(i, round);
                has = true;
                break;
            }
        }
        if (has) return;
        if (roundList.size() >= 5) {
            roundList.remove(roundList.size() - 1);
        }
        roundList.add(0, round);
    }
}
