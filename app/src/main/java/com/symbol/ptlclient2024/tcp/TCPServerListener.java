package com.symbol.ptlclient2024.tcp;

import java.util.ArrayList;

public interface TCPServerListener {

    public void onTCPMessageServerRecieved(ArrayList<String> messages);

    public void onInfoEventOccured(String pTyp, String message);

}
