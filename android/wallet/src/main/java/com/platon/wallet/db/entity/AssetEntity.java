package com.platon.wallet.db.entity;
import com.platon.wallet.entity.Asset;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
public class AssetEntity extends RealmObject {
    @PrimaryKey
    private String id;
    private String walletId;
    private int assetType;
    private long createTime;
    private long updateTime;
    private String contractAddress;
    private String binary;
    private String name;
    private String symbol;
    private String balance;
    private boolean isHome;
    public AssetEntity() {
    }
    private AssetEntity(Builder builder) {
        setId(builder.id);
        setWalletId(builder.walletId);
        setAssetType(builder.assetType);
        setCreateTime(builder.createTime);
        setUpdateTime(builder.updateTime);
        setContractAddress(builder.contractAddress);
        setBinary(builder.binary);
        setName(builder.name);
        setSymbol(builder.symbol);
        setBalance(builder.balance);
        setHome(builder.isHome);
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getWalletId() {
        return walletId;
    }
    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }
    public void setAssetType(int assetType) {
        this.assetType = assetType;
    }
    public int getAssetType() {
        return assetType;
    }
    public long getCreateTime() {
        return createTime;
    }
    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
    public long getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
    public String getContractAddress() {
        return contractAddress;
    }
    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }
    public String getBinary() {
        return binary;
    }
    public void setBinary(String binary) {
        this.binary = binary;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    public void setBalance(String balance) {
        this.balance = balance;
    }
    public String getBalance() {
        return this.balance;
    }
    public void setHome(boolean isHome){
        this.isHome = isHome;
    }
    public boolean isHome(){
        return this.isHome;
    }
    public static final class Builder {
        private String id;
        private String walletId;
        private int assetType;
        private long createTime;
        private long updateTime;
        private String contractAddress;
        private String binary;
        private String name;
        private String symbol;
        private String balance;
        private boolean isHome;
        public Builder id(String val) {
            id = val;
            return this;
        }
        public Builder walletId(String val) {
            walletId = val;
            return this;
        }
        public Builder assetType(int val) {
            assetType = val;
            return this;
        }
        public Builder createTime(long val) {
            createTime = val;
            return this;
        }
        public Builder updateTime(long val) {
            updateTime = val;
            return this;
        }
        public Builder contractAddress(String val) {
            contractAddress = val;
            return this;
        }
        public Builder binary(String val) {
            binary = val;
            return this;
        }
        public Builder name(String val) {
            name = val;
            return this;
        }
        public Builder symbol(String val) {
            symbol = val;
            return this;
        }
        public Builder balance(String val) {
            balance = val;
            return this;
        }
        public Builder setHome(boolean val){
            isHome = val;
            return this;
        }
        public AssetEntity build() {
            return new AssetEntity(this);
        }

    }
    public Asset buildAsset() {
        return new Asset.Builder()
                .id(id)
                .walletId(walletId)
                .assetType(assetType)
                .createTime(createTime)
                .updateTime(updateTime)
                .contractAddress(contractAddress)
                .binary(binary)
                .name(name)
                .symbol(symbol)
                .balance(balance)
                .setHome(isHome)
                .build();
    }
}
