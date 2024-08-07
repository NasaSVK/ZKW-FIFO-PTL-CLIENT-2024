package com.symbol.kepzetclient.tcp;

public interface TCPListener {
    public void onTCPMessageRecieved(String message);
    public void onTCPConnectionStatusChanged(boolean isConnectedNow);
}
