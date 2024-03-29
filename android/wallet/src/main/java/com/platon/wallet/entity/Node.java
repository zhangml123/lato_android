package com.platon.wallet.entity;

import com.platon.wallet.utils.LogUtils;
import com.platon.wallet.db.entity.NodeEntity;


/**
 * @author matrixelement
 */
public class Node implements Cloneable, Nullable {

    public long id;
    /**
     * 节点地址
     */
    public String nodeAddress;
    /**
     * 是否是默认的节点
     */
    public boolean isDefaultNode;
    /**
     * 主网络
     */
    public boolean isMainNetworkNode;

    public boolean isFormatCorrect = false;

    public boolean isChecked = false;

    public String chainId;

    public String nodeName;

    public Node() {

    }

    private Node(Builder builder) {
        setId(builder.id);
        setNodeAddress(builder.nodeAddress);
        setDefaultNode(builder.isDefaultNode);
        setFormatCorrect(builder.isFormatCorrect);
        setChecked(builder.isChecked);
        setMainNetworkNode(builder.isMainNetworkNode);
        setChainId(builder.chainId);
        setNodeName(builder.nodeName);
    }

    public static Node createNullNode() {
        return NullNode.getInstance();
    }

    public NodeEntity createNodeInfo() {
        return new NodeEntity(id, nodeAddress, isDefaultNode, isChecked, isMainNetworkNode, chainId, nodeName);
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public boolean isDefaultNode() {
        return isDefaultNode;
    }

    public void setDefaultNode(boolean defaultNode) {
        isDefaultNode = defaultNode;
    }

    public boolean isFormatCorrect() {
        return isFormatCorrect;
    }

    public void setFormatCorrect(boolean formatCorrect) {
        isFormatCorrect = formatCorrect;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isMainNetworkNode() {
        return isMainNetworkNode;
    }

    public void setMainNetworkNode(boolean mainNetworkNode) {
        isMainNetworkNode = mainNetworkNode;
    }

    public String getChainId() {
        return chainId;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }
    public String getNodeName(){
        return nodeName;
    }
    public void setNodeName(String nodeName){
        this.nodeName = nodeName;
    }
    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof Node) {
            Node node = (Node) obj;
            return id == node.id;
        }
        return super.equals(obj);
    }

    @Override
    public Node clone() {
        Node nodeEntity = null;
        try {
            nodeEntity = (Node) super.clone();
        } catch (CloneNotSupportedException exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
        }
        return nodeEntity;
    }

    @Override
    public boolean isNull() {
        return false;
    }


    public static final class Builder {
        private long id;
        private String nodeAddress;
        private boolean isDefaultNode;
        private boolean isFormatCorrect;
        private boolean isChecked;
        private boolean isMainNetworkNode;
        private String chainId;
        private String nodeName;

        public Builder() {
            id = System.currentTimeMillis();
        }

        public Builder id(long val) {
            id = val;
            return this;
        }

        public Builder nodeAddress(String val) {
            nodeAddress = val;
            return this;
        }

        public Builder isDefaultNode(boolean val) {
            isDefaultNode = val;
            return this;
        }

        public Builder isFormatCorrect(boolean val) {
            isFormatCorrect = val;
            return this;
        }

        public Builder isChecked(boolean val) {
            isChecked = val;
            return this;
        }

        public Builder isMainNetworkNode(boolean val) {
            isMainNetworkNode = val;
            return this;
        }

        public Builder chainId(String val) {
            chainId = val;
            return this;
        }
        public Builder nodeName(String val){
            nodeName = val;
            return this;
        }

        public Node build() {
            return new Node(this);
        }
    }

    public String getRPCUrl() {
        return nodeAddress + "/rpc";
    }

}