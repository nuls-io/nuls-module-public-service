# NULS2.0 Public Service Module

This is NULS2.0 provides external data query function RPC Interface. 

By default, this module is not started. The mongoDB database needs to be installed when starting this module. After the module is started, it will automatically parse the underlying block information, convert the block data into queryable business data, and store it in the mongoDB database.
For related interfaces provided by nuls-public-service, please see the [NULS2.0-Public-RPC](https://docs.nuls.io/) interface document for details.


## Module Running Environment

- JDK: 11
- IDE: IntelliJ IDEA 2018.3.3 (Community Edition)
- MAVEN: 3.3.9


## Module Configuration Instructions

### public-service module external rpc port number
rpcPort=18003
### mongoDB database url address 
databaseUrl=127.0.0.1
### mongoDB database port number
databasePort=27017
### Maximum number of connection pools
maxAliveConnect=20
### Maximum waiting time for connection
maxWaitTime=120000
### Connection timeout
connectTimeOut=30000


## Contribute to This Module
Click Star and Fork to start contributing improvements to this module.
Hope more contributors can submit improvement suggestions and bug reports here.
Issues: https://github.com/nuls-io/nuls-module-public-service/issues



# Welcome to NULS! #

NULS — Making It Easier To Innovate

## Introduction

NULS is a blockchain infrastructure that provides customizable services and is also a global open-source community blockchain project. NULS adopts micro-services to achieve a highly modular underlying architecture, using smart contracts and cross-chain technologies, combined with the ability of ChainBox to quickly build chains, reduce development costs, and accelerate blockchain business application landing.

## Contribute to NULS
We are committed to making blockchain technology simpler and our slogan is "NULS Making It Easier to Innovate".

Get to know NULS developers
https://nuls.io/developer

You are welcome to contribute to NULS! We sincerely invite developers with rich experience in the blockchain field to join the NULS technology community.
https://nuls.io/community

Documentation：https://docs.nuls.io

NULS Brand Assets: https://nuls.io/brand-assets



## License

NULS is released under the [MIT](http://opensource.org/licenses/MIT) license.
Modules added in the future may be release under different license, will specified in the module library path.

## Community

- Website: https://nuls.io
- Twitter: https://twitter.com/nuls
- Discord:https://discord.gg/aRCwbj47WN
- Telegram: https://t.me/Nulsio)
- Medium: https://nuls.medium.com
- Forum: https://forum.nuls.io
- GitHub: https://github.com/nuls-io

#### 
