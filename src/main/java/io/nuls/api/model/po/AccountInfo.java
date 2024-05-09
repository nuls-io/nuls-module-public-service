package io.nuls.api.model.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.nuls.base.data.Address;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


public class AccountInfo {

    private String address;

    private String alias;

    private int type;

    private int txCount;

    private BigInteger totalOut;

    private BigInteger totalIn;

    private BigInteger consensusLock;

    private BigInteger timeLock;

    private BigInteger balance;

    private BigInteger totalBalance;

    private BigInteger totalReward;

    private BigInteger lastReward;

    private BigInteger lastDayReward = BigInteger.ZERO;

    private BigInteger todayReward = BigInteger.ZERO;

    private String symbol;

    private List<String> tokens;

    private List<String> token721s;

    private List<String> token1155s;

    //Is it a newly created account based on the latest block transaction, only for business use, without storing this field
    @JsonIgnore
    private boolean isNew;
    private String tag;

    public AccountInfo() {
    }

    public AccountInfo(String address) {
        this.address = address;
        Address address1 = new Address(address);
        this.type = address1.getAddressType();
        this.tokens = new ArrayList<>();
        this.token721s = new ArrayList<>();
        this.token1155s = new ArrayList<>();
        this.isNew = true;
        this.totalOut = BigInteger.ZERO;
        this.totalIn = BigInteger.ZERO;
        this.consensusLock = BigInteger.ZERO;
        this.timeLock = BigInteger.ZERO;
        this.balance = BigInteger.ZERO;
        this.totalBalance = BigInteger.ZERO;
        this.totalReward = BigInteger.ZERO;
        this.lastReward = BigInteger.ZERO;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTxCount() {
        return txCount;
    }

    public void setTxCount(int txCount) {
        this.txCount = txCount;
    }

    public BigInteger getTotalOut() {
        return totalOut;
    }

    public void setTotalOut(BigInteger totalOut) {
        this.totalOut = totalOut;
    }

    public BigInteger getTotalIn() {
        return totalIn;
    }

    public void setTotalIn(BigInteger totalIn) {
        this.totalIn = totalIn;
    }

    public BigInteger getConsensusLock() {
        return consensusLock;
    }

    public void setConsensusLock(BigInteger consensusLock) {
        this.consensusLock = consensusLock;
    }

    public BigInteger getTimeLock() {
        return timeLock;
    }

    public void setTimeLock(BigInteger timeLock) {
        this.timeLock = timeLock;
    }

    public BigInteger getBalance() {
        return balance;
    }

    public void setBalance(BigInteger balance) {
        this.balance = balance;
    }

    public BigInteger getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(BigInteger totalBalance) {
        this.totalBalance = totalBalance;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }

    public List<String> getToken721s() {
        return token721s;
    }

    public void setToken721s(List<String> token721s) {
        this.token721s = token721s;
    }

    public List<String> getToken1155s() {
        return token1155s;
    }

    public void setToken1155s(List<String> token1155s) {
        this.token1155s = token1155s;
    }

    @JsonIgnore
    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public BigInteger getTotalReward() {
        return totalReward;
    }

    public void setTotalReward(BigInteger totalReward) {
        this.totalReward = totalReward;
    }


    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigInteger getLastReward() {
        return lastReward;
    }

    public void setLastReward(BigInteger lastReward) {
        this.lastReward = lastReward;
    }

    public BigInteger getLastDayReward() {
        return lastDayReward;
    }

    public void setLastDayReward(BigInteger lastDayReward) {
        this.lastDayReward = lastDayReward;
    }

    public BigInteger getTodayReward() {
        return todayReward;
    }

    public void setTodayReward(BigInteger todayReward) {
        this.todayReward = todayReward;
    }

    public AccountInfo copy() {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.address = this.address;
        accountInfo.alias = this.alias;
        accountInfo.type = this.type;
        accountInfo.txCount = this.txCount;
        accountInfo.totalOut = new BigInteger(this.totalOut.toString());
        accountInfo.totalIn = new BigInteger(this.totalIn.toString());
        accountInfo.consensusLock = new BigInteger(this.consensusLock.toString());
        accountInfo.timeLock = new BigInteger(this.timeLock.toString());
        accountInfo.balance = new BigInteger(this.balance.toString());
        accountInfo.totalBalance = new BigInteger(this.totalBalance.toString());
        accountInfo.totalReward = new BigInteger(this.totalReward.toString());
        accountInfo.lastReward = new BigInteger(this.lastReward.toString());
        accountInfo.todayReward = new BigInteger(this.todayReward.toString());
        accountInfo.lastDayReward = new BigInteger(this.lastDayReward.toString());
        if (this.tokens == null) {
            this.tokens = new ArrayList<>();
        }
        accountInfo.tokens = new ArrayList<>(this.tokens);
        if (this.token721s == null) {
            this.token721s = new ArrayList<>();
        }
        accountInfo.token721s = new ArrayList<>(this.token721s);
        if (this.token1155s == null) {
            this.token1155s = new ArrayList<>();
        }
        accountInfo.token1155s = new ArrayList<>(this.token1155s);
        accountInfo.tag = this.getTag();
        return accountInfo;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}
