package org.platon.lato.service.websocket;

public interface ConnectivityListener {
    void onConnected();
    void onConnecting();
    void onDisconnected();
    void onAuthenticationFailure();
}
