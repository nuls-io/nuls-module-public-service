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

/**
 * Store interface commands provided externally
 *
 * @author captain
 * @version 1.0
 * @date 18-11-9 afternoon2:15
 */
public interface CommandConstant {

    //Obtain blocks based on block height
    String GET_BLOCK_BY_HEIGHT = "getBlockByHeight";
    //Based on blockshashGet blocks
    String GET_BLOCK_BY_HASH = "getBlockByHash";

    String INFO = "info";
    //Obtain account balance
    String GET_BALANCE = "getBalanceNonce";
    //
    String IS_ALAIS_USABLE= "ac_isAliasUsable";
    //Obtain account lock list
    String GET_FREEZE = "getFreezeList";

    //Query transaction details
    String GET_TX = "tx_getTxClient";
    //Transaction verification
    String TX_VALIEDATE = "tx_verifyTx";
    //New transaction confirmation and broadcast
    String TX_NEWTX = "tx_newTx";
    //Directly broadcast new transactions
    String TX_BROADCAST = "tx_broadcast";
    //Send cross chain transactions
    String SEND_CROSS_TX = "newApiModuleCrossTx";
    //Query node details
    String GET_AGENT = "cs_getAgentInfo";
    //Obtain consensus configuration
    String GET_CONSENSUS_CONFIG = "cs_getConsensusConfig";
    //Query smart contract details
    String CONTRACT_INFO = "sc_contract_info";
    //Query smart contract execution results
    String CONTRACT_RESULT = "sc_contract_result";
    //Query smart contract constructor
    String CONSTRUCTOR = "sc_constructor";
    //Token Cross Chain System Contract Address
    String GET_CROSS_TOKEN_SYSTEM_CONTRACT = "sc_get_cross_token_system_contract";
    //Verify contract creation
    String VALIDATE_CREATE = "sc_validate_create";
    //Verify Call Contract
    String VALIDATE_CALL = "sc_validate_call";
    //Verify deletion of contract
    String VALIDATE_DELETE = "sc_validate_delete";
    //Estimating the creation of contractsgas
    String IMPUTED_CREATE_GAS = "sc_imputed_create_gas";
    //Estimating the Call Contractgas
    String IMPUTED_CALL_GAS = "sc_imputed_call_gas";
    //Upload contract codejarpackage
    String UPLOAD = "sc_upload";
    //Get the collection of smart contract results
    String CONTRACT_RESULT_LIST = "sc_contract_result_list";
    //Call the contract not on chain method
    String INVOKE_VIEW = "sc_invoke_view";
    //Query registered cross chain information
    String GET_REGISTERED_CHAIN = "getRegisteredChainInfoList";
    //Get Address Prefix Mapping Table
    String GET_ALL_ADDRESS_PREFIX = "ac_getAllAddressPrefix";

    String GET_BYZANTINE_COUNT = "getByzantineCount";

    String GET_NETWORK_GROUP = "nw_getGroupByChainId";

    String PREVIEW_CALL = "sc_preview_call";

    /**
     * Obtain asset information
     */
    String CMD_GET_ASSET_BY_ID = "getAssetById";

    /**
     * Acquiring contract assetsID
     */
    String CMD_CHAIN_ASSET_CONTRACT_ASSETID = "getAssetContractAssetId";
    String CMD_CHAIN_LG_GET_ALL_ASSET = "lg_get_all_asset";

    /**
     * Obtain cross chain asset registration information
     */
    String CMD_ASSET = "cm_asset";

    String CODE_HASH = "sc_code_hash";
    String CONTRACT_CODE = "sc_contract_code";
    String COMPUTE_ADDRESS = "sc_compute_address";
}
