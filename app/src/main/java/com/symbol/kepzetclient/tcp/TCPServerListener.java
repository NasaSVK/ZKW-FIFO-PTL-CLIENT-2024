package com.symbol.kepzetclient.tcp;

import java.util.ArrayList;

public interface TCPServerListener {

    public void onTCPMessageServerRecieved(ArrayList<String> messages);

    public void onInfoEventOccured(String message);

}
