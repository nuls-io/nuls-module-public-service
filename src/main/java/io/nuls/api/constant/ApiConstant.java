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

package io.nuls.api.constant;

import java.math.BigInteger;

/**
 * constant
 *
 * @author captain
 * @version 1.0
 * @date 19-1-22 afternoon3:34
 */
public interface ApiConstant {


    /**
     * Module configuration file name
     * Module configuration file name.
     */
    String MODULES_CONFIG_FILE = "module.json";
    /**
     * chainID
     */
    String CHAIN_ID = "chainId";

    String ASSET_ID = "assetId";

    int INIT_CAPACITY_8 = 8;

    String DB_MODULE_CONFIG = "api-config";

    /**
     * Default scan package path
     */
    String DEFAULT_SCAN_PACKAGE = "io.nuls";

    /**
     * log level
     */
    String LOG_LEVEL = "logLevel";

    //Yellow card punishment
    int PUBLISH_YELLOW = 1;
    //Red card punishment
    int PUBLISH_RED = 2;

    //Attempting to fork
    int TRY_FORK = 1;
    //Package Double Flower Trading
    int DOUBLE_SPEND = 2;
    //Too many yellow card punishments
    int TOO_MUCH_YELLOW_PUNISH = 3;

    //Commission consensus
    int JOIN_CONSENSUS = 0;
    //Cancel delegation consensus
    int CANCEL_CONSENSUS = 1;
    //Delete consensus node
    int STOP_AGENT = 2;

    //Successfully created contract
    int CONTRACT_STATUS_NORMAL = 0;
    //Contract creation failed
    int CONTRACT_STATUS_FAIL = -1;

    //Contract code under review
    int CONTRACT_STATUS_APPROVING = 1;
    //Contract code review passed
    int CONTRACT_STATUS_PASSED = 2;
    //The contract has expired
    int CONTRACT_STATUS_DELETE = 3;

    //Time height boundary
    long BlOCK_HEIGHT_TIME_DIVIDE = 1000000000000L;
    //Highly frozen type
    int FREEZE_HEIGHT_LOCK_TYPE = 1;
    //Time freeze type
    int FREEZE_TIME_LOCK_TYPE = 2;
    //Consensus lock freeze type
    int FREEZE_CONSENSUS_LOCK_TYPE = 3;

    //There is no error code in the contract
    int CONTRACT_NOT_EXIST = 100002;
    //Asset transfer out type
    int TRANSFER_FROM_TYPE = -1;
    //Asset transfer type
    int TRANSFER_TO_TYPE = 1;

    //Unconfirmed transaction
    int TX_UNCONFIRM = 0;
    //Confirmed transaction
    int TX_CONFIRM = 1;

    int ENABLE = 1;

    int DISABLE = 0;

    //Set alias amount
    BigInteger ALIAS_AMOUNT = BigInteger.valueOf(100000000L);

    long CROSS_CHAIN_GASLIMIT = 300000;
    long CONTRACT_MINIMUM_PRICE = 25;
    String CROSS_CHAIN_SYSTEM_CONTRACT_TRANSFER_IN_METHOD_NAME = "crossChainTokenTransfer";

    int TOKEN_TYPE_NRC20 = 1;
    int TOKEN_TYPE_NRC721 = 2;
    int TOKEN_TYPE_NRC1155 = 3;
    String EMPTY_STRING = "";
    int cacheSize = 5000;
}
