package com.platon.wallet.db.entity;

import android.text.TextUtils;

import com.platon.wallet.entity.Node;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author matrixelement
 */
public class NodeEntity extends RealmObject {

    @PrimaryKey
    private long id;

    private String nodeName;
    /**
     * 节点地址
     */
    private String nodeAddress;
    /**
     * 是否是默认的节点
     */
    private boolean isDefaultNode;
    /**
     * 主网络
     */
    private boolean isMainNetworkNode;

    private boolean isChecked;

    private String chainId;

    public Node createNode() {
        return new Node.Builder()
                .id(id)
                .nodeAddress(nodeAddress)
                .isDefaultNode(isDefaultNode)
                .isChecked(isChecked)
                .chainId(chainId)
                .nodeName(nodeName)
                .build();
    }

    public NodeEntity() {
    }

    public NodeEntity(String nodeAddress, boolean isDefaultNode, boolean isChecked) {
        this.id = System.currentTimeMillis();
        this.nodeAddress = nodeAddress;
        this.isDefaultNode = isDefaultNode;
        this.isChecked = isChecked;
    }

    public NodeEntity(long id, String nodeAddress, boolean isDefaultNode, boolean isChecked, boolean isMainNetworkNode,String chainId, String nodeName) {
        this.id = id;
        this.nodeAddress = nodeAddress;
        this.isDefaultNode = isDefaultNode;
        this.isChecked = isChecked;
        this.isMainNetworkNode = isMainNetworkNode;
        this.chainId = chainId;
        this.nodeName = nodeName;
    }

    public Node buildNodeEntity() {
        return TextUtils.isEmpty(nodeAddress) ? Node.createNullNode() : new Node.Builder()
                .id(id)
                .nodeAddress(nodeAddress)
                .isDefaultNode(isDefaultNode)
                .isChecked(isChecked)
                .isMainNetworkNode(isMainNetworkNode)
                .chainId(chainId)
                .nodeName(nodeName)
                .build();
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
    public String toString() {
        return "NodeEntity{" +
                "id=" + id +
                ", nodeAddress='" + nodeAddress + '\'' +
                ", isDefaultNode=" + isDefaultNode +
                ", isMainNetworkNode=" + isMainNetworkNode +
                ", isChecked=" + isChecked +
                ", chainId='" + chainId + '\'' +
                '}';
    }
}