package com.platon.wallet.network;

import java.io.Serializable;

/**
 * @author ziv
 */

public enum NetState implements Serializable {

	CONNECTED, NOTCONNECTED, PING_SUCCESS, PING_FAILED;
}
