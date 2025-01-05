package com.symbol.ptlclient2024.tcp;

//LISTENER NIE JE/NEBUDE NIC INE AKO INTERFACE (objekt), KTORY IMPLEMENTUJE HANDLERE NA OBSLUHY PRISLUSNYCH UDALOSTI SUVISIACICH SNAZVOM LISTENERA
//A VOLANIE HANDLERU SA REALIZUJE PRI DANEJ UDLALOSTI MIMO TRIEDU, KTORA HO IMPLEMNTUJE (MIMO LISTENERA)
public interface TCPClientListener {
    public void onTCPMessageRecieved(String message);
    public void onTCPConnectionStatusChanged(boolean isConnectedNow);
}
