package io.nuls.api.cache;

import io.nuls.api.model.po.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ApiCache {

    private ChainInfo chainInfo;

    private ChainConfigInfo configInfo;

    private CoinContextInfo coinContextInfo;

    private BlockHeaderInfo bestHeader;

    private CurrentRound currentRound;

    private Map<String, AccountInfo> accountMap = new ConcurrentHashMap<>();

    private Map<String, AccountLedgerInfo> ledgerMap = new ConcurrentHashMap<>();

    private Map<String, AgentInfo> agentMap = new ConcurrentHashMap<>();

    private Map<String, AliasInfo> aliasMap = new ConcurrentHashMap<>();

    private Map<String, Nrc20Info> nrc20InfoMap = new ConcurrentHashMap<>();

    private Map<String, Nrc721Info> nrc721InfoMap = new ConcurrentHashMap<>();

    private Map<String, Nrc1155Info> nrc1155InfoMap = new ConcurrentHashMap<>();

    public ApiCache() {
        currentRound = new CurrentRound();
    }

    public void addAccountInfo(AccountInfo accountInfo) {
        accountMap.put(accountInfo.getAddress(), accountInfo);
    }

    public AccountInfo getAccountInfo(String address) {
        return accountMap.get(address);
    }

    public AccountLedgerInfo getAccountLedgerInfo(String key) {
        return ledgerMap.get(key);
    }

    public void addAccountLedgerInfo(AccountLedgerInfo ledgerInfo) {
        ledgerMap.put(ledgerInfo.getKey(), ledgerInfo);
    }

    public List<Nrc20Info> getNrc20InfoList() {
        return nrc20InfoMap.values().stream().collect(Collectors.toList());
    }

    public List<Nrc721Info> getNrc721InfoList() {
        return nrc721InfoMap.values().stream().collect(Collectors.toList());
    }

    public List<Nrc1155Info> getNrc1155InfoList() {
        return nrc1155InfoMap.values().stream().collect(Collectors.toList());
    }

    public void addNrc20Info(Nrc20Info nrc20Info) {
        nrc20InfoMap.put(nrc20Info.getContractAddress(), nrc20Info);
    }

    public void addNrc721Info(Nrc721Info nrc721Info) {
        nrc721InfoMap.put(nrc721Info.getContractAddress(), nrc721Info);
    }

    public void addNrc1155Info(Nrc1155Info nrc1155Info) {
        nrc1155InfoMap.put(nrc1155Info.getContractAddress(), nrc1155Info);
    }

    public Nrc20Info getNrc20Info(String contract) {
        return nrc20InfoMap.get(contract);
    }

    public Nrc721Info getNrc721Info(String contract) {
        return nrc721InfoMap.get(contract);
    }

    public Nrc1155Info getNrc1155Info(String contract) {
        return nrc1155InfoMap.get(contract);
    }

    public void addAgentInfo(AgentInfo agentInfo) {
        agentMap.put(agentInfo.getTxHash(), agentInfo);
    }

    public AgentInfo getAgentInfo(String agentHash) {
        return agentMap.get(agentHash);
    }

    public void addAlias(AliasInfo aliasInfo) {
        aliasMap.put(aliasInfo.getAddress(), aliasInfo);
        aliasMap.put(aliasInfo.getAlias(), aliasInfo);
    }

    public AliasInfo getAlias(String key) {
        return aliasMap.get(key);
    }


    public ChainInfo getChainInfo() {
        return chainInfo;
    }

    public void setChainInfo(ChainInfo chainInfo) {
        this.chainInfo = chainInfo;
    }


    public BlockHeaderInfo getBestHeader() {
        return bestHeader;
    }

    public void setBestHeader(BlockHeaderInfo bestHeader) {
        this.bestHeader = bestHeader;
    }

    public CurrentRound getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(CurrentRound currentRound) {
        this.currentRound = currentRound;
    }

    public Map<String, AccountInfo> getAccountMap() {
        return accountMap;
    }

    public void setAccountMap(Map<String, AccountInfo> accountMap) {
        this.accountMap = accountMap;
    }

    public Map<String, AccountLedgerInfo> getLedgerMap() {
        return ledgerMap;
    }

    public void setLedgerMap(Map<String, AccountLedgerInfo> ledgerMap) {
        this.ledgerMap = ledgerMap;
    }

    public Map<String, AgentInfo> getAgentMap() {
        return agentMap;
    }

    public void setAgentMap(Map<String, AgentInfo> agentMap) {
        this.agentMap = agentMap;
    }

    public Map<String, AliasInfo> getAliasMap() {
        return aliasMap;
    }

    public void setAliasMap(Map<String, AliasInfo> aliasMap) {
        this.aliasMap = aliasMap;
    }

    public CoinContextInfo getCoinContextInfo() {
        return coinContextInfo;
    }

    public void setCoinContextInfo(CoinContextInfo coinContextInfo) {
        this.coinContextInfo = coinContextInfo;
    }

    public ChainConfigInfo getConfigInfo() {
        return configInfo;
    }

    public void setConfigInfo(ChainConfigInfo configInfo) {
        this.configInfo = configInfo;
    }

}
