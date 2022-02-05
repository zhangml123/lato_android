package com.platon.wallet.db.entity;

import com.platon.wallet.entity.Asset;
import com.platon.wallet.entity.Message;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class MessageEntity extends RealmObject {
    @PrimaryKey
    private String id;
    private String type;
    private String msg;
    private long createTime;
    private long updateTime;
    private boolean read;
    public MessageEntity(){
    }
    private MessageEntity(Builder builder){
        setId(builder.id);
        setType(builder.type);
        setMsg(builder.msg);
        setCreateTime(builder.createTime);
        setUpdateTime(builder.updateTime);
        setRead(builder.read);
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
        public MessageEntity build() {
            return new MessageEntity(this);
        }
    }

    public Message buildMessage(){
        return new Message.Builder()
                .id(id)
                .type(type)
                .msg(msg)
                .createTime(createTime)
                .updateTime(updateTime)
                .read(read)
                .build();
    }
}
