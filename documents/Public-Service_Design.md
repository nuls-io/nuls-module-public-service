# public-serviceModule Design Document

[TOC]

## Overall Overview

### Module Overview

#### Why do we need to havepublic-servicemodule

During the operation of blockchain projects, the data generated on the chain will be broadcasted to each other, and each node will also store the data. However, these data cannot be visually displayed to users, and it is also troublesome for users to query relevant data.public-serviceIt refers to providing users with a way to query on chain data and related statistical information through a browser or web wallet.

#### public-serviceWhat to do

Analyze the blocks that the node wallet has synchronized to, and store the data in a database that can provide relationship queries and statistics.

Provide external query blocks、transaction、account、Consensus information、Contract information、Statistical information and other interfaces.

#### public-servicePositioning in the system

public-serviceIt belongs to the auxiliary module and is not the underlying core module, so it will not run by default after the wallet is started.

workingpublic-serviceThe front-end server needs to install the database first, and the default implementation ismongoDBdatabase

## functional design

### Functional architecture diagram

![](/img/public-service-functions.png)



### Interface Description

**io.nuls.api.analysis**

Responsible for calling the underlying module interface and parsing the data returned by the interface

WalletRpcHandler：public-serviceCall other modulesRPCInterface processing class

AnalysisHandler: public-serviceParsing underlying block data processing classes

**io.nuls.api.db**

providepublic-serviceThe interface and implementation of database addition, deletion, modification, and query

**io.nuls.api.model**

public-serviceThe data structure, including the persistence layer、dtolayer

**io.nuls.api.rpc**

External provisionrpcInterface, querying blocks、transaction、Account information, etc

**io.nuls.api.service**

public-serviceThe main business interface for synchronizing and rolling back blocks

SyncService: sync block

RollbackService：Rolling back blocks

**io.nuls.api.task**

public-serviceTimed tasks, including synchronized block tasks、Statistical tasks, etc

SyncBlockTask：Timed tasks for synchronizing blocks

## moduleRPCinterface

reference[NULS2.0-APIInterface documentation](./account.md)

 

## JavaUnique design

### JAVABrief description of implementation details

**io.nuls.api.cache.ApiCache**

Common data on the cache chain, including chain information、Account information、Consensus information、Statistical information, etc.

**io.nuls.api.task.SyncBlockTask**

Call the underlying block module interface to retrieve the next block. After successful block continuity verification, store the data in themongoDBContinue to obtain information for the next block；If the blockhashContinuity verification failed, roll back the latest stored block until continuity verification passes.

If the next block cannot be obtained, it indicates that the currentpublic-serviceIf the resolution has reached the latest height, then every time10Second, retrieve the latest height block and perform parsing and storage.

**io.nuls.api.service.SyncService**

The main interface for synchronizing blocks, Firstly, we need to process the statistics of block rewards, then process data related to various businesses based on different transactions, process information related to rounds, and finally store the parsed data in the database.

