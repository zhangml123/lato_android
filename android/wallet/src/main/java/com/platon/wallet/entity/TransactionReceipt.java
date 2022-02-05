package com.platon.wallet.entity;

public class TransactionReceipt {
    public TransactionReceipt() {
    }
    public int status;
    public String hash;
    public String totalReward;
    public String blockNumber;
    public String timestamp;
    public TransactionReceipt(int status, String hash) {
        this.status = status;
        this.hash = hash;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getTotalReward() {
        return totalReward;
    }

    public void setTotalReward(String totalReward) {
        this.totalReward = totalReward;
    }

    public String getBlockNumber() {
        return blockNumber;
    }
    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }
    public String getTimestamp(){
        return timestamp;
    }
    public void setTimestamp(String timestamp){
        this.timestamp = timestamp;
    }
}
