package com.platon.wallet.entity;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import com.platon.wallet.db.entity.AssetEntity;

public class Asset implements Parcelable, Comparable<Asset>, Nullable, Cloneable {
    public final static int ASSET_LAT = 1;
    public final static int ASSET_CONTRACT = 2;
    protected String id;
    protected String walletId;
    protected int assetType;
    protected long createTime;
    protected long updateTime;
    protected String contractAddress;
    protected String binary;
    protected String name;
    protected String symbol;
    protected String balance;
    protected boolean isHome;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected Asset(Parcel in) {
        id = in.readString();
        walletId = in.readString();
        assetType = in.readInt();
        createTime = in.readLong();
        updateTime = in.readLong();
        contractAddress = in.readString();
        binary = in.readString();
        name = in.readString();
        symbol = in.readString();
        balance = in.readString();
        isHome = in.readBoolean();
    }
    public String getId() {
        return id;
    }
    public void sedId(String id) {
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
        return isHome;
    }
    public Asset(Builder builder) {
        this.id = builder.id;
        this.walletId = builder.walletId;
        this.assetType = builder.assetType;
        this.createTime = builder.createTime;
        this.updateTime = builder.updateTime;
        this.contractAddress = builder.contractAddress;
        this.binary = builder.binary;
        this.name = builder.name;
        this.symbol = builder.symbol;
        this.balance = builder.balance;
        this.isHome = builder.isHome;
    }
    public AssetEntity buildAssetInfoEntity() {
        return new AssetEntity.Builder()
                .id(getId())
                .walletId(getWalletId())
                .assetType(getAssetType())
                .createTime(getCreateTime())
                .updateTime(getUpdateTime())
                .contractAddress(getContractAddress())
                .binary(getBinary())
                .name(getName())
                .symbol(getSymbol())
                .balance(getBalance())
                .setHome(isHome())
                .build();
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
        protected String balance;
        protected boolean isHome;
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
        public Asset build() {
            return new Asset(this);
        }

    }
    public static final Creator<Asset> CREATOR = new Creator<Asset>() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public Asset createFromParcel(Parcel in) {
            return new Asset(in);
        }

        @Override
        public Asset[] newArray(int size) {
            return new Asset[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public int compareTo(Asset asset) {
        return 0;
    }
}
