package com.symbol.kepzetclient.tcp;

public interface TCPClientListener {
    public void onTCPMessageRecieved(String message);
    public void onTCPConnectionStatusChanged(boolean isConnectedNow);
}
