# NULS2.0——APIInterface documentation

## brief introduction

each NULS2.0A set of optional nodes is provided API Interface, used to obtain visual blockchain data from nodes, making the development of blockchain applications very convenient. Interface passed through [JSON-RPC](http://wiki.geekdream.com/Specification/json-rpc_2.0.html) Provided in a certain way, with underlying usage HTTPProtocol for communication.

To initiate a provision RPC The nodes of the service need to follow the following steps：

- Get Wallet

Method 1：Download can provideRPCFull node wallet for services（http://Fill in after official launch Download address link）

Method 2：synchronizationhttps://github.com/nuls-io/nuls-v2upperNULS2.0projectmasterBranch source code, execute the following command to manually package the entire node wallet：

```
./package -a public-service
./package
```

- Node servers need to be installedmongoDBdatabase
- modifymodule.ncfFiles,[public-service]The relevant configurations are as follows：

```
[public-service]
#databaseurladdress
databaseUrl=127.0.0.1
#Database port number
databasePort=27017
```

After completing the configuration, start the node program, and the client will parse the synchronized blocks and store them in themongoDBMedium.

## Listening port

The default port is18003, modifiablemodule.ncfFiles,[public-service]The relevant configurations are as follows：

```
[public-service]
#public-serviceModule ExternalrpcPort number
rpcPort=18003
```

## Interface Description

### Character set encoding

UTF-8

### Remote Call Protocol

JSON-RPC

```
{
	"jsonrpc":"2.0",
	"method":"getChainInfo",		//Interface Name
	"params":[],					//All interface parameters have been passed in an array manner, and the order of parameters cannot be changed
	"id":1234
}
```

### Interface return format

```
Normal return
//example
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {
         "networkHeight": 4936,
         "localHeight": 4936
     }
}
Abnormal return
//example
{
     "jsonrpc": "2.0",
     "id": 1234,
     "error": {
          "code": 1000,
          "message": "Parameters is wrong!",
          "data": "Incorrect number of parameters"
     }
}
```

### Token conversion

When it comes to interfaces with tokens, the number of tokens in parameters and returns is uniformly converted to avoid losing decimal precisionBigIntegerFormat.NULSThe decimal precision of the main network is8Bit, therefore the interface layer is uniformly shifted to the right8Bit100,000,000 = 1NULS.

### Return Data Definition

#### Transaction type(txType)

```
    int COIN_BASE = 1;						//coinBasereward
    int TRANSFER = 2;						//Transfer
 	int ACCOUNT_ALIAS = 3;					//Set account alias
   	int REGISTER_AGENT = 4;					//Create a new consensus node
   	int DEPOSIT = 5;						//Entrusting participation in consensus
    int CANCEL_DEPOSIT = 6;					//Cancel delegation
    int YELLOW_PUNISH = 7;					//Yellow card punishment
    int RED_PUNISH = 8;						//Red card punishment
    int STOP_AGENT = 9;						//Unregister consensus node
    int CROSS_CHAIN = 10;					//Cross chain transfer
	int REGISTER_CHAIN_AND_ASSET = 11;		//Registration Chain
    int DESTROY_CHAIN_AND_ASSET = 12;		//Destruction chain
    int ADD_ASSET_TO_CHAIN = 13;			//Add new assets on the chain
   	int REMOVE_ASSET_FROM_CHAIN = 14;		//Cancellation of assets
    int CREATE_CONTRACT = 15;				//Create a smart contract
    int CALL_CONTRACT = 16;					//Calling smart contracts
    int DELETE_CONTRACT = 17;				//Delete smart contract
    int CONTRACT_TRANSFER = 18;				//Internal transfer of contract
    int CONTRACT_RETURN_GAS = 19;			//Contract execution fee refund
    int CONTRACT_CREATE_AGENT = 20;			//Contract creation consensus node
	int CONTRACT_DEPOSIT = 21;				//Contract Entrustment Participation Consensus
 	int CONTRACT_CANCEL_DEPOSIT = 22;		//Contract cancellation commission
 	int CONTRACT_STOP_AGENT = 23;			//Contract Cancellation Consensus Node
```



#### Asset information(assetInfo)

```
assetInfo：{
    "key": "100-1",						//string	Primary key
    "chainId": 100,						//int		The chain of assetsid 		
    "assetId": 1,						//int		assetid
    "symbol": "NULS",					//string	Asset symbols
    "decimals":8						//int		Asset support decimal places
    "initCoins": 100000000000000,		//bigInt	Initial amount of assets
    "address": "tNULSeBaMoodYW7A……",	//string	Address of asset creator			
    "status": 1							//int		Status, 0：logout	1：Enable
}


```

#### Block header information(blockHeaderInfo)

```
blockHeaderInfo: {
    "hash": "c31d198b6fb5a……",					//string	blockhash
    "height": 304,								//long		block height
    "preHash": "d7596990d508……",				//string	Previous blockhash
    "merkleHash": "85c661b36aa3fdc……",			//string	Merkelhash
    "createTime": 1559725301,					//long		Creation time
    "agentHash": null,							//string	Out of block nodeshash
    "agentId": "8CPcA7kaXSHbWb3GHP7……",			//string	Out of block nodesid
    "packingAddress": "8CPcA7kaXSH……",			//string	The block packaging address of the outbound node
    "agentAlias": null,							//string	Proxy alias for block node
    "txCount": 1,								//int		Number of block packaging transactions
    "roundIndex": 155972530,					//long		Block output round
    "totalFee": 0,								//bigInt	Packaged transaction fees
    "reward": 0,								//bigInt	Reward for block output
    "size": 235,								//long		block size
    "packingIndexOfRound": 1,					//int		The order of block production in this round
    "scriptSign": "210e2ab7a219bca2a……",		//string	Block signature
    "txHashList": [								//[string]	Transactions corresponding to block packaged transactionshashaggregate
        "85c661b36aa3fdc93b9bc27bb8fdf1……"
    ],
    "roundStartTime": 1559725291,				//long		The starting block output time of this round
    "agentVersion": 1,							//int		The protocol version number of the block node
    "seedPacked": true							//boolean	Is the current block a seed node packaging
}
```

#### Transaction information(txInfo)

```
txInfo: {
    "hash": "0020b15e564……",				//string	transactionhash
    "type": 2,								//int 		Transaction type(txType)
    "height": -1,							//long		Confirm the block height of the transaction,-1Indicates that it has not been confirmed yet
    "size": 228,							//int		Transaction size
    "createTime": 1552300674920,			//long		Creation time
    "remark": "transfer test",				//string	Remarks
    "txData": null,							//object	Trading business objects, distinguished by transaction type,
    													Please refer to the following data definition for details
    "txDataHex": null,						//string	Business objects16Serialized string in hexadecimal format
    "txDataList": null,						//[object]	Collection of transaction business objects, distinguished by transaction type
    "fee": { 								//bigInt	Handling fees
        "chainId": 100,						//Service fee chainid
        "assetId": 1,						//Handling fee assetsid
        "symbol": "ATOM",					//Handling fee asset symbol
        "value": 100000						//Handling fee amount
    },
    "coinFroms": [
    {
        "address": "5MR_2CbSSboa……",			//string	Transfer address
        "chainId": 12345,						//int		Chain of transferring out assetsid
        "assetsId": 1,							//int		Transfer of assetsid
        "amount": 1870000000000,				//bigInt	Transfer amount	
        "locked": 0,							//long		Lock time
        "nonce": "ffffffff"						//string	Latest transfer of assetsnoncevalue
        "symbol":"nuls"							//string	Asset symbols
    }
    ],
    "coinTos": [
    {
        "address": "5MR_2CbSSboa……",			//string	Receiving address
        "chainId": 12345,						//int		Chain for receiving assetsid
        "assetsId": 1,							//int		Receiving assetsid
        "amount": 1870000000000,				//bigInt	Received amount	
        "locked": 0,							//long		Lock time
        "symbol":"nuls"							//string	Asset symbols
    }
    ],
    "value": 1860000000000						//bigInt	The amount of asset changes involved in the transaction
}
```

#### Account information(accountInfo)

```
accountInfo: {
    "address": "5MR_2ChNj……",					//string	Account address
    "alias": null,								//string	Account Aliases
    "type": 1,									//int		Account type 
                                                //1：Normal address	2：Contract address	3：Multiple signed addresses
    "txCount": 8,								//int		Number of transactions
    "totalOut": 0,								//bigInt	Total expenditure
    "totalIn": 1000000000000000,				//bigInt	Total revenue
    "consensusLock": 0,							//bigInt	The default asset consensus lock in this chain
    "timeLock": 0,								//bigInt	The default asset time lock in this chain
    "balance": 1000000000000000,				//bigInt	The default available balance of assets in this chain
    "totalBalance": 1000000000000000,			//bigInt	The default total assets of this chain
    "totalReward": 0,							//bigInt	Consensus total reward
    "tokens": []								//[string]	Ownednrc20Asset Symbol List
}
```

#### Asset information(accountLedgerInfo)

```
accountLedgerInfo: {
    "address": "tNULSeBaMrbMRiFAUeeAt……",			//string	Account address
    "chainId": 2,									//int		The chain of assetsid
    "assetId": 1,									//int		Assetsid
    "symbol": "NULS",								//string	Symbols for assets
    "totalBalance": 1000000000000000,				//bigInt	Total assets
    "balance": 1000000000000000,					//bigInt	Available balance
    "timeLock": 0,									//bigInt	Time lock
    "consensusLock": 0								//bigInt	Consensus locking
}
```

#### Consensus node information(consensusInfo)

```
 {
     "txHash": "0020c734c7ec……",				//string	Create transactions for consensus nodeshash
     "agentId": "e4ae68a2",						//string	nodeid
     "agentAddress": "5MR_2CfWGwnfh……",			//string	Create a proxy account address for the node
     "packingAddress": "5MR_2CeXYdnth……",		//string	The account address where the node is responsible for packaging blocks
     "rewardAddress": "5MR_2CeXYdnt……",			//string	The account address where the node obtains consensus rewards
     "agentAlias": null,						//string	Node's proxy address alias
     "deposit": 2000000000000,					//bigInt	Deposit for proxy nodes when creating nodes
     "commissionRate": 10,						//int		The commission ratio charged by the node, in units%
     "createTime": 1552300674920,				//long		The creation time of the node
     "status": 0,								//int		Node status 
     											//0:Pending consensus, 1:In consensus, 2:Unregistered
     "totalDeposit": 20000000000000,			//bigInt	Total amount of entrusted participation consensus
     "depositCount": 0,							//int		Number of Commissions
     "creditValue": 0,							//double	Credit value Value[-1,1]
     "totalPackingCount": 3966,					//int		The total number of blocks packaged by nodes
     "lostRate": 0,								//double	Block loss rate
     "lastRewardHeight": 8000,					//long		The height of the block where the reward was obtained for the last block output
     "deleteHash": null,						//string	Unregister node transactionshash
     "blockHeight": 67,							//long		Block height when creating nodes
     "deleteHeight": 0,							//long		Block height when logging out nodes
     "totalReward": 1256976254880,				//bigInt	Total consensus reward				                                                       totalReward=commissionReward+agentReward
     "commissionReward": 1256976254880,			//bigInt	Commission consensus reward
     "agentReward": 0,							//bigInt	Node obtains rewards
     "roundPackingTime": 0,						//long		The time when the current round node is responsible for packaging blocks
     "version": 1,								//int		Protocol version number of the node
     "type": 1,									//int		1:Normal node,2:Developer node,3:Ambassador node
 }
```

#### Commission consensus information(depositInfo)

```
depositInfo:{
    "txHash": "0020dd1b606191068566c……",			//string	Transactions with delegated consensushash
    "amount": 20000000000000,						//bigint	Entrusted amount		
    "agentHash": "0020c734c7ecf447……",				//string	Transaction of consensus nodes entrustedhash
    "address": "5MR_2CfWGwnfhPcdnho……",				//string	Principal's account address
    "createTime": 1552292357109,					//long		Entrustment time
    "blockHeight": 69,								//long		Block height at the time of delegation
    "deleteHeight": 0,								//long		Block height when canceling delegation
    "type": 0										//int		0:entrust, 1:Cancel delegation
    "fee": { 										//bigInt	Commission fees for entrusted transactions
        "chainId": 100,								//Service fee chainid
        "assetId": 1,								//Handling fee assetsid
        "symbol": "ATOM",							//Handling fee asset symbol
        "value": 100000								//Handling fee amount
    },
}
```



## Interface List

### Chain related interfaces[chain]

#### Query information on this chain

request：

```
{
    "jsonrpc":"2.0",
    "method":"getChainInfo",
    "params":[],
    "id":1234
}
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {
          "chainId": 100,								//This chain'sid
          "chainName": "nuls",							//Chain Name
          "defaultAsset": {assetInfo},					//Default asset information for this chain
          "assets": [									//All asset information sets in this chain
               {assetInfo}
          ],
          "seeds": [									//Consensus seed node address of the chain
               "8CPcA7kaXSHbWb3GHP7bd5hRLFu8RZv57rY9w"	
          ],
          "inflationCoins": 500000000000000,			//The number of inflation tokens for default assets in this chain/year
          "status": 1									//state：0 Cancellation,1Enable
     }
}
```

#### Query the general information after the chain runs

request：

```
{
    "jsonrpc":"2.0",
    "method":"getInfo",
    "params":[chainId],
    "id":1234
}
//Parameter Description
chainId: int									//This chain'sid
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {
          "networkHeight": 278,							//The latest block height in the network
          "localHeight": 278							//The height of synchronized blocks at the current node
          "defaultAsset": {								//Default asset information for this chain
               "symbol": "NULS",						//Asset symbols
               "chainId": 2,							//Asset ChainID
               "assetId": 1,							//assetID
               "decimals": 8							//Supports decimal places
          },
          "agentAsset": {								//Asset information used for consensus participation in this chain
               "symbol": "NULS",
               "chainId": 2,
               "assetId": 1,
               "decimals": 8
          },
          "isRunCrossChain": true,						//Does it support cross chain
          "isRunSmartContract": true					//Whether to enable smart contracts
     }
}
```

#### Query other registered cross chain chain information

request：

```
{
    "jsonrpc":"2.0",
    "method":"getOtherChainList",
    "params":[chainId],
    "id":1234
}
//Parameter Description
chainId: int									//This chain'sid
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": [
          {
               "chainName": "nuls2",					//Chain Name
               "chainId": 2								//chainid
          }
     ]
}
```

### Block related interfaces[block]

#### Query the latest block header

request：

```
{
    "jsonrpc":"2.0",
    "method":"getBestBlockHeader",
    "params":[chainId],
    "id":1234
}
//Parameter Description
chainId: int									//Chain basedid
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {blockHeaderInfo}						//Block header information
}
```

#### Query block headers based on height

request：

```
{
    "jsonrpc":"2.0",
    "method":"getHeaderByHeight",
    "params":[chainId, blockHeight],
    "id":1234
}
//Parameter Description
chainId: int									 //Chain basedid
blockHeight：long								//block height
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {blockHeaderInfo}						//Block header information
}
```

#### Based on blockshashQuery block header

request：

```
{
    "jsonrpc":"2.0",
    "method":"getHeaderByHash",
    "params":[chainId, blockHash],
    "id":1234
}
chainId: int									 //Chain basedid
blockHash：string								//blockhash
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {blockHeaderInfo}						//Block header information
}
```

#### Query complete blocks based on height

request：

```
{
    "jsonrpc":"2.0",
    "method":"getBlockByHeight",
    "params":[chainId, blockHeight],
    "id":1234
}
//Parameter Description
chainId: int									 //Chain basedid
blockHeight：long								//block height
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {
     	"header":{blockHeaderInfo},						//Block header information
     	"txList":[										//Packaged transaction information
     		{txInfo}
     	]
     }						
}
```

#### Based on blockshashQuery complete blocks

request：

```
{
    "jsonrpc":"2.0",
    "method":"getBlockByHash",
    "params":[chainId, blockHash],
    "id":1234
}
//Parameter Description
chainId: int									 //Chain basedid
blockHash：string								//blockhash
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {
     	"header":{blockHeaderInfo},						//Block header information
     	"txList":[										//Packaged transaction information
     		{txInfo}
     	]
     }						
}
```

#### Query block header list

request：

```
{
    "jsonrpc":"2.0",
    "method":"getBlockHeaderList",
    "params":[chainId,pageNumber,pageSize, isHidden, packedAddress],
    "id":1234
}
//Parameter Description
chainId: int									//Chain basedid
pageNumber:int									//Page number
pageSize:int									//Display the number of entries per page, with a value of[1-1000]
isHidden:boolean								//Do you want to hide blocks with only consensus reward transactions 
packedAddress:string							//Filter based on block packaging address, not mandatory
```

return:

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {
          "pageNumber": 1,
          "pageSize": 10,
          "totalCount": 7,
          "list": [
               {blockHeaderInfo}
          ]
     }
}
```

### Account related interfaces[account]

#### Query account details

request：

```
{
    "jsonrpc":"2.0",
    "method":"getAccount",
    "params":[chainId,address],
    "id":1234
}
//Parameter Description
chainId: int									//Chain basedid
address: string									//Account address
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {accountInfo}					//Account information
}
```

#### Query account details based on alias

request：

```
{
    "jsonrpc":"2.0",
    "method":"getAccountByAlias",
    "params":[chainId,alias],
    "id":1234
}
//Parameter Description
chainId: int									//Chain basedid
alias: string									//Account Aliases
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {accountInfo}					//Account information
}
```

#### Query the ranking of coin holding accounts

request：

```
{
    "jsonrpc":"2.0",
    "method":"getAssetRanking",
    "params":[chainId,assetChainId,assetId,pageNumber,pageSize],
    "id":1234
}
//Parameter Description
chainId: int									//Chain basedid
assetChainId: int								//Asset Chainid
assetId: int									//assetid
pageNumber:int									//Page number
pageSize:int									//Display the number of entries per page, with a value of[1-1000]
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {
          "pageNumber": 1,
          "pageSize": 10,
          "totalCount": 1,
          "list": [
            {
                "address": "NULSd6HhGcgkvEjzGU6Zmx2cxonjKQXA26Cth",		//Account address
                "alias": null,											//Account Aliases
                "type": 3,												//Address type,1:Normal address,2:Contract address,3:Multiple signed addresses
                "totalBalance": 3029296137980,							//Total balance
                "locked": 0,											//Lock in amount
                "proportion": "0.159%",									//Total amount proportion
                "decimal": 8											//Decimal places of assets
            }
               ……
          ]
     }
}
```

#### Query the asset list of the account's main chain

request：

```
{
    "jsonrpc":"2.0",
    "method":"getAccountLedgerList",
    "params":[chainId,address],
    "id":1234
}
//Parameter Description
chainId: int									//Chain basedid
address: string									//Account address
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": [
          {accountLedgerInfo}
     ]
}
```

#### Query account cross chain asset list

request：

```
{
    "jsonrpc":"2.0",
    "method":"getAccountCrossLedgerList",
    "params":[chainId,address],
    "id":1234
}
//Parameter Description
chainId: int									//Chain basedid
address: string									//Account address
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": [
          {accountLedgerInfo}
     ]
}
```

#### Query the balance of individual assets in the account

request：

```
{
    "jsonrpc":"2.0",
    "method":"getAccountBalance",
    "params":[chainId,assetChainId,assetId,address],
    "id":1234
}
//Parameter Description
chainId: int									//This chain'sid
assetChainId: int								//The chain of assetsid
assetId: int									//Assetsid
address: string									//Account address
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {
          "totalBalance": 1000000000000000,					//bigInt	Total assets
          "balance": 1000000000000000,						//bigInt	Available balance
          "timeLock": 0,									//bigInt	Time lock amount
          "consensusLock": 0,								//bigInt	Consensus locking amount
          "freeze": 0,										//bigInt	Transaction unconfirmed amount
          "nonce": "0000000000000000",						//string	Assetsnoncevalue
          "nonceType": 1									//int		nonceHas the value been confirmed
          													// 0:Unconfirmed, 1:Confirmed
     }
}
```

#### Query account locked amount list

request：

```
{
    "jsonrpc":"2.0",
    "method":"getAccountFreezes",
    "params":[chainId,pageNumber,pageSize,address],
    "id":1234
}
//Parameter Description
chainId: int									//Chain basedid
pageNumber:int									//Page number
pageSize:int									//Display the number of entries per page, with a value of[1-1000]
address: string									//Account address
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {
          "txHash":"d3ks2x9bAl38bfsl……" 		//transactionhash
          "type":1								//Lock type 
          										//1：Time lock, 2:Height lock, 3:Consensus locking
          "time":1552300674920					//Generation time
          "lockedValue":155650000000			//Locked value
          "amount":100000000					//Lock in amount
          "reason":"Consensus rewards"					 //Reason for locking
     }
}
```

#### Check if aliases are available

request：

```
{
    "jsonrpc":"2.0",
    "method":"isAliasUsable",
    "params":[chainId,alias],
    "id":1234
}
//Parameter Description
chainId: int									//Chain basedid
alias:string									//alias
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {
          "value": true							//boolean	true: available, false: Not available
     }
}
```

#### Query the address prefix of each chain

request：

```
{
    "jsonrpc":"2.0",
    "method":"getAllAddressPrefix",
    "params":[],
    "id":1234
}
```

return：

```
{
    "jsonrpc": "2.0",
    "id": "1234",
    "result": [
        {
            "chainId": 1,						//chainID
            "addressPrefix": "NULS"				//Address prefix
        },
        {
            "chainId": 9,
            "addressPrefix": "NERVE"
        }
    ]
}
```



### Transaction related interfaces[transaction]

#### Query transaction details

request：

```
{
    "jsonrpc":"2.0",
    "method":"getTx",
    "params":[chainId,txHash],
    "id":1234
}
//Parameter Description
chainId: int									//Chain basedid
txHash: string									//transactionhash	
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {txInfo}
}
```

#### Query transaction list

request：

```
{
    "jsonrpc":"2.0",
    "method":"getTxList",
    "params":[chainId,pageNumber,pageSize,txType,isHidden,startTime,endTime],                       
    "id":1234
}
//Parameter Description
chainId: int									//Chain basedid
pageNumber:int									//Page number
pageSize:int									//Display the number of entries per page, with a value of[1-1000]
txType:int										//Transaction type(txType),type=0When querying all transactions
isHidden:boolean    							//Whether to hide consensus reward transactions, default is not hidden, this parameter can only betype=0Time effective
startTime:long									//Block start time(unit：second), default to0
endTime:long									//End time of block(unit：second), default to0
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {
          "pageNumber": 1,
          "pageSize": 10,
          "totalCount": 1,
          "list": [
               {
                    "txHash": "a8611112f2b35385ee84f85……",		//transactionhash
                    "address": "tNULSeBaMrbMRiFA……",			//Account address
                    "type": 1,									//Transaction type
                    "createTime": 1531152,						//Transaction time, in seconds
                    "height": 0,								//The block height determined by the transaction being packaged
                    "chainId": 2,								//The chain of assetsid
                    "assetId": 1,								//assetid
                    "symbol": "NULS",							//Asset symbols
                    "values": 1000000000000000,					//Transaction amount
                    "fee": { 									//bigInt	Handling fees
                        "chainId": 100,							//Service fee chainid
                        "assetId": 1,							//Handling fee assetsid
                        "symbol": "ATOM",						//Handling fee asset symbol
                        "value": 100000							//Handling fee amount
                    },
                    "balance": 1000000000000000,				//The balance of the account after the transaction
                    "transferType": 1,							// -1:Transfer out, 1:Transfer in
                    "status": 1									//Transaction status 0:Unconfirmed,1:Confirmed
               }
          ]
     }
}
```

#### Query block packaged transactions

request：

```
{
    "jsonrpc":"2.0",
    "method":"getBlockTxList",
    "params":[chainId,blockHeight,txType], 
    "id":1234
}
//Parameter Description
chainId: int									//Chain basedid
blockHeight:long								//block height
txType:int										//Transaction type(txType),type=0When querying all transactions
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {
          "pageNumber": 1,
          "pageSize": 10,
          "totalCount": 1,
          "list": [
               {
                    "txHash": "a8611112f2b35385ee84f85……",		//transactionhash
                    "address": "tNULSeBaMrbMRiFA……",			//Account address
                    "type": 1,									//Transaction type
                    "createTime": 1531152,						//Transaction time, in seconds
                    "height": 0,								//The block height determined by the transaction being packaged
                    "chainId": 2,								//The chain of assetsid
                    "assetId": 1,								//assetid
                    "symbol": "NULS",							//Asset symbols
                    "values": 1000000000000000,					//Transaction amount
                    "fee": { 									//bigInt	Handling fees
                        "chainId": 100,							//Service fee chainid
                        "assetId": 1,							//Handling fee assetsid
                        "symbol": "ATOM",						//Handling fee asset symbol
                        "value": 100000							//Handling fee amount
                    },
                    "balance": 1000000000000000,				//The balance of the account after the transaction
                    "transferType": 1,							// -1:Transfer out, 1:Transfer in
                    "status": 1									//Transaction status 0:Unconfirmed,1:Confirmed
               }
          ]
     }
}
```

#### Query the transaction list of the account

request：

```
{
    "jsonrpc":"2.0",
    "method":"getAccountTxs",
    "params":[chainId,pageNumber,pageSize,address,txType,startHeight, endHeight,assetChainId, assetId],                       
    "id":1234
}
//Parameter Description
chainId: int									//Chain basedid
pageNumber:int									//Page number
pageSize:int									//Display the number of entries per page, with a value of[1-1000]
address: string									//Account address
txType:int										//Transaction type(txType),type=0When querying all transactions
startHeight:long                                //The starting height of the block for packaging transactions, default to-1,Unrestricted
endHeight:long                                  //The block cutoff height for transactions, defaulting to-1, unrestricted
assetChainId:int                                //assetchainId, default to0
assetId:int                                     //assetID, default to0
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {
          "pageNumber": 1,
          "pageSize": 10,
          "totalCount": 1,
          "list": [
               {
                    "txHash": "a8611112f2b35385ee84f85……",		//transactionhash
                    "address": "tNULSeBaMrbMRiFA……",			//Account address
                    "type": 1,									//Transaction type
                    "createTime": 1531152,						//Transaction time, in seconds
                    "height": 0,								//The block height determined by the transaction being packaged
                    "chainId": 2,								//The chain of assetsid
                    "assetId": 1,								//assetid
                    "symbol": "NULS",							//Asset symbols
                    "values": 1000000000000000,					//Transaction amount
                    "fee": { 									//bigInt	Handling fees
                        "chainId": 100,							//Service fee chainid
                        "assetId": 1,							//Handling fee assetsid
                        "symbol": "ATOM",						//Handling fee asset symbol
                        "value": 100000							//Handling fee amount
                    },
                    "balance": 1000000000000000,				//The balance of the account after the transaction
                    "transferType": 1,							// -1:Transfer out, 1:Transfer in
                    "status": 1									//Transaction status 0:Unconfirmed,1:Confirmed
               }
          ]
     }
}
```

#### Verify whether offline assembly transactions are legal

request：

```
{
    "jsonrpc":"2.0",
    "method":"validateTx",
    "params":[chainId, txHex], 
    "id":1234
}
//Parameter Description
chainId: int									//Chain basedid
txHex: string									//Assembled transaction serialized16Hexadecimal Strings
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {
          "value": "46b90763901898c0c250bd749……"				//transactionhash
     }
}
```

#### Broadcast offline assembly transactions

request：

```
{
    "jsonrpc":"2.0",
    "method":"broadcastTx",
    "params":[chainId, txHex], 
    "id":1234
}
//Parameter Description
chainId: int									//Chain basedid
txHex: string									//Assembled transaction serialized16Hexadecimal Strings
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {
          "value": true							//trueBroadcast successful,falseBroadcast failed
     }
}
```

#### Broadcast offline assembly transactions(Not verifying contracts)

request：

```
{
    "jsonrpc":"2.0",
    "method":"broadcastTxWithNoContractValidation",
    "params":[chainId, txHex], 
    "id":1234
}
//Parameter Description
chainId: int									//Chain basedid
txHex: string									//Assembled transaction serialized16Hexadecimal Strings
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {
          "value": true							//trueBroadcast successful,falseBroadcast failed
     }
}
```

### Consensus related interfaces[consensus]

#### Query the list of delegated consensus nodes

request：

```
{
    "jsonrpc":"2.0",
    "method":"getConsensusNodes",
    "params":[chainId,pageNumber,pageSize,type],
    "id":1234
}
//Parameter Description
chainId: int									//Chain basedid
pageNumber:int									//Page number
pageSize:int									//Display the number of entries per page, with a value of[1-1000]
type:int										//Node type
												//0:All nodes,1:Normal node,2:Developer node,3:Ambassador node
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {
          "pageNumber": 1,
          "pageSize": 10,
          "totalCount": 1,
          "list": [
               {conesnsusInfo}
          ]
     }
}
```

#### Query the list of all delegated consensus nodes（Including Exited、Or being penalized with a red card）

request：

```
{
    "jsonrpc":"2.0",
    "method":"getAllConsensusNodes",
    "params":[chainId,pageNumber,pageSize],
    "id":1234
}
//Parameter Description
chainId: int									//Chain basedid
pageNumber:int									//Page number
pageSize:int									//Display the number of entries per page, with a value of[1-1000]
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {
          "pageNumber": 1,
          "pageSize": 10,
          "totalCount": 1,
          "list": [
               {conesnsusInfo}
          ]
     }
}
```

#### Query the consensus node list entrusted by the account

request：

```
{
    "jsonrpc":"2.0",
    "method":"getAccountConsensus",
    "params":[chainId,pageNumber,pageSize, address],
    "id":1234
}
//Parameter Description
chainId: int									//Chain basedid
pageNumber:int									//Page number
pageSize:int									//Display the number of entries per page, with a value of[1-1000]
address:string									//Account address
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {
          "pageNumber": 1,
          "pageSize": 10,
          "totalCount": 1,
          "list": [
           		{conesnsusInfo}
          ]
     }
}
```

#### Query consensus node details

request：

```
{
    "jsonrpc":"2.0",
    "method":"getConsensusNode",
    "params":[chainId,txHash],
    "id":1234
}
//Parameter Description
chainId: int									//Chain basedid
txHash:string									//Transactions during node creationhash
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {conesnsusInfo}
}
```

#### Query the consensus node details created by the account

request：

```
{
    "jsonrpc":"2.0",
    "method":"getAccountConsensusNode",
    "params":[chainId,address],
    "id":1234
}
//Parameter Description
chainId: int									//Chain basedid
address:string									//Account address
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {conesnsusInfo}
}
```

#### Query list information in node delegation

request：

```
{
    "jsonrpc":"2.0",
    "method":"getConsensusDeposit",
    "params":[chainId,pageNumber,pageSize,txHash],
    "id":1234
}
//Parameter Description
chainId: int									//Chain basedid
pageNumber:int									//Page number
pageSize:int									//Display the number of entries per page, with a value of[1-1000]
txHash:string									//Transactions during node creationhash
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {
          "pageNumber": 1,
          "pageSize": 10,
          "totalCount": 1,
          "list": [
           		{depositInfo}
          ]
     }
}
```

#### Query node history delegation list

request：

```
{
    "jsonrpc":"2.0",
    "method":"getAllConsensusDeposit",
    "params":[chainId,pageNumber,pageSize,txHash,type],
    "id":1234
}
//Parameter Description
chainId: int									//Chain basedid
pageNumber:int									//Page number
pageSize:int									//Display the number of entries per page, with a value of[1-1000]
txHash:string									//Transactions during node creationhash
type:int										//0:Join the delegation,1:Exit the commission,2:All  
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {
          "pageNumber": 1,
          "pageSize": 10,
          "totalCount": 1,
          "list": [
           		{depositInfo}
          ]
     }
}
```

#### Query the delegation list of the account

request：

```
{
    "jsonrpc":"2.0",
    "method":"getAccountDeposit",
    "params":[chainId,pageNumber,pageSize,address,agentHash],
    "id":1234
}
//Parameter Description
chainId: int									//Chain basedid
pageNumber:int									//Page number
pageSize:int									//Display the number of entries per page, with a value of[1-1000]
address:string									//Account address	
txHash:string									//Transactions during node creationhash,When empty, query all delegates in the account
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {
          "pageNumber": 1,
          "pageSize": 10,
          "totalCount": 1,
          "list": [
           		{depositInfo}
          ]
     }
}
```

#### Query the total entrusted amount of the account

request：

```
{
    "jsonrpc":"2.0",
    "method":"getAccountDepositValue",
    "params":[chainId,address,agentHash],
    "id":1234
}
//Parameter Description
chainId: int									//Chain basedid
address:string									//Account address	
txHash:string									//Transactions during node creationhash,When empty, query all delegates in the account
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": 10000000000						//Total amount entrusted
}
```

#### Query consensus penalty list

request：

```
{
    "jsonrpc":"2.0",
    "method":"getPunishList",
    "params":[chainId,pageNumber,pageSize,0,agentAddress],
    "id":1234
}
//Parameter Description
chainId: int									//Chain basedid
pageNumber:int									//Page number
pageSize:int									//Display the number of entries per page, with a value of[1-1000]
type:int							 			//Punishment type  0:Query All,1:Yellow card,2:Red card
agentAddress:string								//Proxy account address for consensus nodes	
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {
          "pageNumber": 1,
          "pageSize": 10,
          "totalCount": 1,
          "list": [
           		{
           			"txHash":				//string	Punish tradinghash
           			"type":					//int		Punishment type 1:Yellow card,2:Red card
           			"address":				//string	Punish the proxy account address of consensus nodes
           			"time":					//long		Punishment time
           			"blockHeight":			//long		Block height for penalty transactions
           			"roundIndex":			//long		Rotation of blocks		
           			"packageIndex":			//long		Packaged serial number
           			"reason":				//string	Reason for punishment
           		}
          ]
     }
}
```

#### Query round list

request：

```
{
    "jsonrpc":"2.0",
    "method":"getRoundList",
    "params":[chainId,pageNumber,pageSize],
    "id":1234
}
//Parameter Description
chainId: int									//Chain basedid
pageNumber:int									//Page number
pageSize:int									//Display the number of entries per page, with a value of[1-1000]
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {
          "pageNumber": 1,
          "pageSize": 1,
          "totalCount": 4036,
          "list": [
               {
                    "index": 155233203,				//long	Consensus round
                    "startTime": 1552371670001,		//long	Starting time of the current round
                    "memberCount": 2,				//int	The current number of block output nodes
                    "endTime": 1552371690001,		//long	End time of current round
                    "redCardCount": 0,				//int	The number of red cards penalized in this round
                    "yellowCardCount": 0,			//int	Number of yellow cards issued in this round
                    "producedBlockCount": 1,		//int	The total number of blocks produced in this round
                    "startHeight": 8000,			//long	Starting height of this round
                    "endHeight": 0,					//long	End height of this round	
                    "lostRate": 0					//double Block loss rate
               }
          ]
     }
}
```



### Smart contract related interfaces[contract]

#### Query contract details

request：

```
{
    "jsonrpc":"2.0",
    "method":"getContract",
    "params":[chainId, contractAddress],
    "id":1234
}
chainId: int									//Chain basedid
contractAddress:string							//Smart contract address
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {
          "contractAddress": "tNULSeBaNC46Z66DgU……",		//string	Contract address
          "creater": "tNULSeBaMvEtDfvZuu……",				//string	Contract Creator Address
          "createTxHash": "00209d28833258b192493……",		//string	Create transactions for contractshash
          "blockHeight": 15,								//long		Block height for creating contracts
          "success": true,									//boolean	Was it successfully created
          "balance": 0,										//bigInt	ContractualNULSbalance
          "errorMsg": null,									//string	Error message for failed creation
          "status": 0,										//int		Contract status
          									-1:Execution failed,0:Unauthenticated,1:Under review,2:Through verification,3:Removed
          "certificationTime": 0,							//long		Certification time
          "createTime": 1553336525059,						//long		Contract creation time
          "remark": "create contract test",					//string	Remarks
          "txCount": 2,										//int		Contract related transactions
          "deleteHash": null,								//string	Delete transactions for contractshash
          "methods": [										//[object]	Functions included in the contract
               {
                    "name": "name",							//string	Interface Name
                    "returnType": "String",					//string	return type
                    "params": []							//[object]	Interface parameters
               }
          ],
          "nrc20": true,									//boolean	Is itnrc20contract
          "tokenName": "KQB",								//string	tokenname		
          "symbol": "KQB",									//string	tokensymbol
          "decimals": 2,									//string	Decimal places
          "totalSupply": "1000000000000",					//bigInt	total
          "transferCount": 2,								//int		tokenNumber of transfers
          "owners": [										//[string]	tokenholder
               "tNULSeBaMvEtDfvZuukDf2mVyfGo3DdiN8KLRG",
               "tNULSeBaMnrs6JKrCy6TQdzYJZkMZJDng7QAsD"
          ]
     }
}
```

#### Query Contract List

request：

```
{
    "jsonrpc":"2.0",
    "method":"getContractList",
    "params":[chainId,pageNumber,pageSize,onlyNrc20,isHidden],
    "id":1234
}
//Parameter Description
chainId: int									//Chain basedid
pageNumber:int									//Page number
pageSize:int									//Display the number of entries per page, with a value of[1-1000]
tokenType:int 								    //contracttokentype  0: wrongtoken, 1: NRC20, 2: NRC721
isHidden: boolean 								//Whether to hidetokenType contract
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {
          "pageNumber": 1,
          "pageSize": 10,
          "totalCount": 1,
          "list": [
           		{
           			"contractAddress":				//string	Contract address
           			"remark":						//string	Remarks
           			"txCount":						//int		Number of transactions related to smart contracts
           			"status":						//int		Contract status
           									-1:Execution failed,0:Unauthenticated,1:Under review,2:Through verification,3:Removed
           			"createTime":					//long		Creation time
           			"balance":						//bigInt	Contract surplusNULSbalance
           			"tokenName":					//string	tokenname
           		    "symbol": "KQB",				//string	tokensymbol
                    "decimals": 2,					//string	Decimal places
        			"totalSupply": "1000000000000", //bigInt	total,
        			"tokenType":1                   //int       tokentype, 0: wrongtoken, 1: NRC20, 2: NRC721
           		}
          ]
     }
}
```

#### Query contract related transaction list

request：

```
{
    "jsonrpc":"2.0",
    "method":"getContractTxList",
    "params":[chainId,pageNumber,pageSize,txType,contractAddress],
    "id":1234
}
//Parameter Description
chainId: int									//Chain basedid
pageNumber:int									//Page number
pageSize:int									//Display the number of entries per page, with a value of[1-1000]
txType:int										//Transaction type Default to0, query all transactions
contractAddress:string							//Contract address
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {
          "pageNumber": 1,
          "pageSize": 10,
          "totalCount": 3,
          "list": [
               {
                    "contractAddress": "tNULSeBaN32a2h……",		//string Contract address
                    "txHash": "0020658e3edc61196e73be0……		//string transactionhash
                    "blockHeight": 12,							//long	 Transaction confirmation block height
                    "time": 1553336503846,						//long 	 Transaction generation time
                    "type": 20									//int    Transaction type
                    "fee": "5100000"							//bigint Transaction fees
               }
          ]
     }
}
```

#### querynrc20Contract transfer record list

request：

```
{
    "jsonrpc":"2.0",
    "method":"getContractTokens",
    "params":[chainId,pageNumber,pageSize,contractAddress],
    "id":1234
}
//Parameter Description
chainId: int									//Chain basedid
pageNumber:int									//Page number
pageSize:int									//Display the number of entries per page, with a value of[1-1000]
contractAddress:string							//Contract address
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {
          "pageNumber": 1,
          "pageSize": 10,
          "totalCount": 3,
          "list": [
              {
                 "address": "tNULSeBaMvEt……",				//string	Account address
                 "tokenName": "KQB",						//string	Transfertokenname
                 "tokenSymbol": "KQB",						//string	Transfertokensymbol
                 "contractAddress": "tNULSeBaNC46Z……",		//string	Contract address
                 "balance": 999900000000,					//bigint	Balance after transfer
                 "decimals": 2								//int		Exact decimal places
              }
          ]
     }
}
```

#### Query accountnrc20Transfer Record List

request：

```
{
    "jsonrpc":"2.0",
    "method":"getTokenTransfers",
    "params":[chainId,pageNumber,pageSize,address,contractAddress],
    "id":1234
}
//Parameter Description
chainId: int									//Chain basedid
pageNumber:int									//Page number
pageSize:int									//Display the number of entries per page, with a value of[1-1000]
address:string									//Account address
contractAddress:string							//Contract address
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": {
          "pageNumber": 1,
          "pageSize": 10,
          "totalCount": 1,
          "list": [
               {
                    "txHash": "002016f5a811b939535……",		//string	transactionhash
                    "height": 19,							//long		Transaction packaging confirmation block height
                    "contractAddress": "tNULSeBaNC……",		//string	Contract address
                    "name": "KQB",							//string	tokenname
                    "symbol": "KQB",						//string	tokensymbol
                    "decimals": 2,							//int		Exact decimal places
                    "fromAddress": "tNULSeBaMvE……",			//string	Transfer address
                    "toAddress": "tNULSeBaMnrs6……",			//string	Receiving address
                    "value": "100000000",					//bigInt	Transfer amount
                    "time": 1553336574791,					//long		Transaction time
                    "fromBalance": "999900000000",			//bigInt	Transferor balance
                    "toBalance": "100000000"				//bigInt	Recipient balance
               }
          ]
     }
}
```

### Statistical related interfaces[statistical]

#### Transaction quantity statistics

request：

```
{
"jsonrpc":"2.0",
"method":"getTxStatistical",
"params":[chainId,type],
"id":1234
}
//Parameter Description
chainId: int								//Chain basedid
type: int							 		//0:recently14day, 1:Last week, 2:Last month, 3:Last year
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": [
          {
               "key": "2018-6",						//string	Statistical cycle
               "value": 265234						//long		Statistical quantity
          },
          {
               "key": "2018-7",
               "value": 425327
          }
     ]
}
```

#### Count the number of consensus nodes

request：

```
{
    "jsonrpc":"2.0",
    "method":"getConsensusNodeCount",
    "params":[chainId],
    "id":1234
}
//Parameter Description
chainId: int								//Chain basedid
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result":{
          "consensusCount":78,						//int	Number of consensus nodes
          "seedsCount":5,							//int	Number of seed nodes
          "totalCount":83							//int	Total quantity
     }
}
```

#### Consensus reward statistics

request：

```
{
    "jsonrpc":"2.0",
    "method":"getConsensusStatistical",
    "params":[chainId,type],
    "id":1234
}
//Parameter Description
chainId: int								//Chain basedid
type: int							 		//0:14Oh my god,1:Zhou,2：Month,3：Year,4：whole
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": [
          {
               "key": "6/5",							//string	Statistical cycle
               "value": 556572872229264					//bigInt	Total amount of consensus rewards
          },
          {
               "key": "6/6",
               "value": 608939272229264
          },
          {
               "key": "6/7",
               "value": 628717072229264
          },
          {
               "key": "6/8",
               "value": 632738172229264
          },
          {
               "key": "6/9",
               "value": 629865972229264
          },
          {
               "key": "6/10",
               "value": 671865972229264
          }
     ]
}
```

#### Annual reward rate statistics

request：

```
{
    "jsonrpc":"2.0",
    "method":"getAnnulizedRewardStatistical",
    "params":[chainId,type],
    "id":1234
}
//Parameter Description
chainId: int								//Chain basedid
type: int							 		//0:14Oh my god,1:Zhou,2：Month,3：Year,4：whole
```

return：

```
{
     "jsonrpc": "2.0",
     "id": 1234,
     "result": [
          {
               "key": "5/29",						//string	Statistical cycle
               "value": 116.17						//dobule	Annualized income%
          },
          {
               "key": "5/30",
               "value": 121.61
          },
          {
               "key": "5/31",
               "value": 106.16
          },
          {
               "key": "6/1",
               "value": 112.27
          },
          {
               "key": "6/2",
               "value": 112.27
          }
     ]
}     
```

