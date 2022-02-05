package com.platon.wallet.entity;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import com.platon.wallet.db.entity.MessageEntity;

public class Message implements Parcelable {
    private String id;
    private String type;
    private String msg;
    private long createTime;
    private long updateTime;
    private boolean read;
    public Message(Builder builder){
        setId(builder.id);
        setType(builder.type);
        setMsg(builder.msg);
        setCreateTime(builder.createTime);
        setUpdateTime(builder.updateTime);
        setRead(builder.read);
    }
    protected Message(Parcel in) {
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(id);
        dest.writeString(type);
        dest.writeLong(createTime);
        dest.writeLong(updateTime);
        dest.writeBoolean(read);
    }



    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
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

    public boolean getRead() {
        return read;
    }
    public void setRead(boolean read) {
        this.read = read;
    }



    public static final class Builder {
        private String id;
        private String type;
        private String msg;
        private long createTime;
        private long updateTime;
        private boolean read;
        public Builder id(String val) {
            id = val;
            return this;
        }
        public Builder type(String val){
            type = val;
            return this;
        }
        public Builder msg(String val){
            msg = val;
            return this;
        }
        public Builder createTime(long val){
            createTime = val;
            return this;
        }
        public Builder updateTime(long val){
            updateTime = val;
            return this;
        }
        public Builder read(boolean val){
            read = val;
            return this;
        }
        public Message build() {
            return new Message(this);
        }
    }
}
