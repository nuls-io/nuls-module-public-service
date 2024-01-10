package io.nuls.api.constant;

public interface DBTableConstant {

    String DATABASE_NAME = "nuls-api";

    String TEST_TABLE = "test-table";

    String CHAIN_INFO_TABLE = "chain_info_table";
    String CHAIN_ASSET_TABLE = "chain_asset_table";
    String CHAIN_ASSET_TX_TABLE = "chain_asset_tx_table";
    String ACTIVE_ADDRESS_TABLE = "active_address_table";

    String CHAIN_CONFIG_TABLE = "chain_config_table";

    String SYNC_INFO_TABLE = "sync_info_table";
    //Block Information Table
    String BLOCK_HEADER_TABLE = "block_header_table_";

    String BLOCK_HEX_TABLE = "block_hex_table_";
    //Account Information Table
    String ACCOUNT_TABLE = "account_table_";
    //Asset Information Table
    String ACCOUNT_LEDGER_TABLE = "account_ledger_table_";
    //Consensus Node Information Table
    String AGENT_TABLE = "agent_table_";
    //Alias information table
    String ALIAS_TABLE = "alias_table_";
    //Entrustment Record Form
    String DEPOSIT_TABLE = "deposit_table_";
    //Transaction table
    String TX_TABLE = "tx_table_";
    //Transaction Relationship Record Table
    String TX_RELATION_TABLE = "tx_relation_table_";

    String CROSS_TX_RELATION_TABLE = "cross_tx_relation_table_";
    //Transaction Relationship Record Table
    String TX_UNCONFIRM_RELATION_TABLE = "tx_unconfirm_relation_table_";
    //Transaction table
    String TX_UNCONFIRM_TABLE = "tx_UNCONFIRM_table_";
    //coinDatarecord
    String COINDATA_TABLE = "coin_data_table_";
    //Red and Yellow Card Record Form
    String PUNISH_TABLE = "punish_table_";

    String ROUND_TABLE = "round_table_";

    String ROUND_ITEM_TABLE = "round_item_table_";
    //accounttokenInformation table
    String ACCOUNT_TOKEN_TABLE = "account_token_table_";
    //Smart Contract Information Table
    String CONTRACT_TABLE = "contract_table_";
    //Smart Contract Transaction Record Table
    String CONTRACT_TX_TABLE = "contract_tx_table_";
    //Smart contractstokenTransfer Record Form
    String TOKEN_TRANSFER_TABLE = "token_transfer_table_";
    //Smart contract result recording
    String CONTRACT_RESULT_TABLE = "contract_result_table_";
    //Statistical table
    String STATISTICAL_TABLE = "statistical_table_";

    String CHAIN_STATISTICAL_TABLE = "chain_statistical_table";

    //token721Mint Information Table
    String TOKEN721_IDS_TABLE = "token721_ids_table_";
    //accounttoken721Information table
    String ACCOUNT_TOKEN721_TABLE = "account_token721_table_";
    //Smart contractstoken721Transfer Record Form
    String TOKEN721_TRANSFER_TABLE = "token721_transfer_table_";

    String LAST_DAY_REWARD_TABLE = "token721_transfer_table_";

    //token1155Mint Information Table
    String TOKEN1155_IDS_TABLE = "token1155_ids_table_";
    //accounttoken1155Information table
    String ACCOUNT_TOKEN1155_TABLE = "account_token1155_table_";
    //Smart contractstoken1155Transfer Record Form
    String TOKEN1155_TRANSFER_TABLE = "token1155_transfer_table_";

    //---------------------------------field(field)------------------------------
    //new_infoTable, latest statistical time points
    String LAST_STATISTICAL_TIME = "last_statistical_time";

    String TX_COUNT = "txCount";
    String ANNUALIZE_REWARD = "annualizedReward";
    String CONSENSUS_LOCKED = "consensusLocked";

    String LastDayRewardKey = "lastDayReward";

    //Number of transaction relationship table shards
    int TX_RELATION_SHARDING_COUNT = 128;
}
