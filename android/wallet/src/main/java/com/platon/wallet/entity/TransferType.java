package com.platon.wallet.entity;

import androidx.annotation.IntDef;

@IntDef({
        TransferType.SEND,
        TransferType.RECEIVE,
        TransferType.TRANSFER
})
public @interface TransferType {

    /**
     * 发送
     */
    int SEND = 0;
    /**
     * 接收
     */
    int RECEIVE = 1;
    /**
     * 转账
     */
    int TRANSFER = 2;
}
