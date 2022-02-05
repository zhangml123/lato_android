package com.platon.wallet.engine;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.platon.wallet.BuildConfig;
import com.platon.wallet.app.Constants;
import com.platon.wallet.db.entity.NodeEntity;
import com.platon.wallet.db.entity.WalletEntity;
import com.platon.wallet.db.sqlite.NodeDao;
import com.platon.wallet.db.sqlite.WalletDao;
import com.platon.wallet.utils.PreferenceTool;
import com.platon.wallet.entity.Node;
import com.platon.wallet.event.EventPublisher;
import com.platon.wallet.utils.RxUtils;
//import com.platon.framework.app.Constants;
//import com.platon.framework.utils.PreferenceTool;
import org.reactivestreams.Publisher;

import rx.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * @author matrixelement
 */
public class NodeManager {

    private Node curNode;
    private NodeService nodeService;
    private NodeManager() {
    }

    private static class InstanceHolder {
        private static volatile NodeManager INSTANCE = new NodeManager();
    }

    public static NodeManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public Node getCurNode() {
        return curNode;
    }
/*
    public String getCurNodeAddress() {
        return curNode == null || TextUtils.isEmpty(curNode.getNodeAddress()) ? PreferenceTool.getString(Constants.Preference.KEY_CURRENT_NODE_ADDRESS) : curNode.getNodeAddress();
    }*/

    public void setCurNode(Node curNode) {
        this.curNode = curNode;
    }

    @SuppressLint("CheckResult")
    public void init() {
        System.out.println("nodeManagert init");
        nodeService = new NodeService();
        Flowable
                .fromIterable(buildDefaultNodeList())
                .map(new Function<Node, Node>() {
                    @Override
                    public Node apply(Node nodeEntity) throws Exception {
                        System.out.println("nodeManagert nodeEntity ="+nodeEntity );
                        return getInsertNode(nodeEntity).blockingGet();
                    }
                })
                .filter(new Predicate<Node>() {
                    @Override
                    public boolean test(Node nodeEntity) throws Exception {
                        return !nodeEntity.isNull();
                    }
                })
                .toList()
                .map(new Function<List<Node>, Boolean>() {
                    @Override
                    public Boolean apply(List<Node> nodeEntities) throws Exception {
                        System.out.println("nodeManagert nodeEntities ="+nodeEntities );
                        return nodeService.insertNode(nodeEntities).blockingGet();
                    }
                })
                .map(new Function<Boolean, Node>() {
                    @Override
                    public Node apply(Boolean aBoolean) throws Exception {
                        return getCheckedNode().blockingGet();
                    }
                })
                .filter(new Predicate<Node>() {
                    @Override
                    public boolean test(Node nodeEntity) throws Exception {
                        return !nodeEntity.isNull();
                    }
                })
                .toSingle()
                //.compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new Consumer<Node>() {
                    @Override
                    public void accept(Node nodeEntity) throws Exception {
                        switchNode(nodeEntity);
                        EventPublisher.getInstance().sendNodeChangedEvent(nodeEntity);
                        AppConfigManager.getInstance().init();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    public String getChainId() {
        if (getCurNode() == null || TextUtils.isEmpty(getCurNode().getChainId())) {
            System.out.println("getChainId 1=" +BuildConfig.RELEASE_TYPE );
            if (BuildConfig.RELEASE_TYPE.equals("server.typeC")) {
                //return BuildConfig.ID_TEST_CHAIN;
                return BuildConfig.ID_DEVELOP_CHAIN;
            } else if (BuildConfig.RELEASE_TYPE.equals("server.typeOC")) {
                return BuildConfig.ID_TEST_CHAIN;
            } else if (BuildConfig.RELEASE_TYPE.equals("server.typeTX")) {
                return BuildConfig.ID_TEST_MAIN_CHAIN;
            } else if (BuildConfig.RELEASE_TYPE.equals("server.typeT")) {
                return BuildConfig.ID_PLATON_TESTNET_CHAIN;
            }else {
                return BuildConfig.ID_TEST_NET;
            }
        } else {
            System.out.println("getChainId 2=" +  getCurNode().getChainId() );
            return getCurNode().getChainId();
        }
    }

    public void switchNode(Node nodeEntity) {
        setCurNode(nodeEntity);
        PreferenceTool.putString(Constants.Preference.KEY_CURRENT_NODE_ADDRESS, nodeEntity.getNodeAddress());
        Web3jManager.getInstance().init(nodeEntity.getRPCUrl());
    }
    public List<Boolean> switchNode(long nodeId) {
        NodeEntity nodeEntity1 = NodeDao.getNode(nodeId);
        setCurNode(nodeEntity1.buildNodeEntity());
        return Flowable
                .fromCallable(new Callable<List<NodeEntity>>() {
                    @Override
                    public List<NodeEntity> call() throws Exception {
                        return NodeDao.getNodeList();
                    }
                }).flatMap(new Function<List<NodeEntity>, Publisher<Boolean>>() {
                    @Override
                    public Publisher<Boolean> apply( List<NodeEntity> nodeEntities) throws Exception {
                        //updateNode(nodeEntity1.buildNodeEntity(),true);
                        NodeDao.updateNode(nodeId, true);
                        return Flowable.range(0, nodeEntities.size()).map(new Function<Integer, Boolean>() {
                            @Override
                            public Boolean apply(Integer integer) throws Exception {
                                if(nodeEntities.get(integer).getId() != nodeId){
                                    System.out.println("switchNode   = "+nodeEntities.get(integer).getId());

                                    NodeDao.updateNode(nodeEntities.get(integer).getId(), false);

                                    System.out.println("wallet_create  currentNodeId22222 = "+ NodeManager.getInstance().getCheckedNode().blockingGet().getId());


                                }
                                return true;
                            }
                        });
                    }
                })
                .toList()
                .blockingGet();
    }
    public Single<List<Node>> getNodeList() {
        return nodeService.getNodeList();
    }

    public Single<Node> getCheckedNode() {
        return nodeService.getNode(true);
    }

    public Single<Boolean> insertNodeList(List<Node> nodeEntityList) {
        return nodeService.insertNode(nodeEntityList);
    }

    public Single<Boolean> deleteNode(Node nodeEntity) {
        return nodeService.deleteNode(nodeEntity.getId());
    }

    public Single<Boolean> updateNode(Node nodeEntity, boolean isChecked) {
        return nodeService.updateNode(nodeEntity.getId(), isChecked);
    }


    private List<Node> buildDefaultNodeList() {
        List<Node> nodeInfoEntityList = new ArrayList<>();
        /*if (BuildConfig.RELEASE_TYPE.equals("server.typeX")) {//测试网络(贝莱世界)
            nodeInfoEntityList.add(new Node.Builder()
                    .id(UUID.randomUUID().hashCode())
                    .nodeAddress(BuildConfig.URL_TEST_NET)
                    .isDefaultNode(true)
                    .isChecked(true)
                    .chainId(BuildConfig.ID_TEST_NET)
                    .build());
        } else if (BuildConfig.RELEASE_TYPE.equals("server.typeOC")) {//公网测试网络
            nodeInfoEntityList.add(new Node.Builder()
                    .id(UUID.randomUUID().hashCode())
                    .nodeAddress(BuildConfig.URL_TEST_OUTER_SERVER)
                    .isDefaultNode(true)
                    .isChecked(true)
                    .chainId(BuildConfig.ID_TEST_CHAIN)
                    .build());
        } else if (BuildConfig.RELEASE_TYPE.equals("server.typeTX")) {//平行网
            nodeInfoEntityList.add(new Node.Builder()
                    .id(UUID.randomUUID().hashCode())
                    .nodeAddress(BuildConfig.URL_TEST_MAIN_SERVER)
                    .isDefaultNode(true)
                    .isChecked(true)
                    .chainId(BuildConfig.ID_TEST_MAIN_CHAIN)
                    .build());
        } else if (BuildConfig.RELEASE_TYPE.equals("server.typeA")) {
            nodeInfoEntityList.add(new Node.Builder()
                    .id(UUID.randomUUID().hashCode())
                    .nodeAddress(BuildConfig.URL_ALAYA_SERVER)
                    .isDefaultNode(true)
                    .isChecked(true)
                    .chainId(BuildConfig.ID_ALAYA_CHAIN)
                    .build());
        }else if (BuildConfig.RELEASE_TYPE.equals("server.typeT")) {
            nodeInfoEntityList.add(new Node.Builder()
                    .id(UUID.randomUUID().hashCode())
                    .nodeAddress(BuildConfig.URL_PLATON_TESTNET_SERVER)
                    .isDefaultNode(true)
                    .isChecked(true)
                    .chainId(BuildConfig.ID_PLATON_TESTNET_CHAIN)
                    .build());
        }*/
        nodeInfoEntityList.add(new Node.Builder()
                .id(UUID.randomUUID().hashCode())
                .nodeAddress(BuildConfig.URL_PLATON_SERVER)
                .isDefaultNode(true)
                .isChecked(false)
                .chainId(BuildConfig.ID_PLATON_CHAIN)
                .nodeName("PlatON Main Net")
                .build());
        nodeInfoEntityList.add(new Node.Builder()
                .id(UUID.randomUUID().hashCode())
                .nodeAddress(BuildConfig.URL_PLATON_TESTNET_SERVER)
                .isDefaultNode(true)
                .isChecked(true)
                .chainId(BuildConfig.ID_PLATON_TESTNET_CHAIN)
                .nodeName("PlatON Test Net")
                .build());
       /* nodeInfoEntityList.add(new Node.Builder()
                .id(UUID.randomUUID().hashCode())
                .nodeAddress(BuildConfig.URL_ALAYA_SERVER)
                .isDefaultNode(true)
                .isChecked(false)
                .chainId(BuildConfig.ID_ALAYA_CHAIN)
                .nodeName("Alaya Test Net")
                .build());*/

        return nodeInfoEntityList;
    }


    private Single<Node> getInsertNode(final Node nodeEntity) {
        return Single.create(new SingleOnSubscribe<Node>() {
            @Override
            public void subscribe(SingleEmitter<Node> emitter) throws Exception {
                List<Node> nodeEntityList = nodeService.getNode(nodeEntity.getNodeAddress()).blockingGet();
                if (nodeEntityList == null || nodeEntityList.isEmpty()) {
                    emitter.onSuccess(nodeEntity);
                } else {
                    emitter.onSuccess(Node.createNullNode());
                }
            }
        });
    }

}