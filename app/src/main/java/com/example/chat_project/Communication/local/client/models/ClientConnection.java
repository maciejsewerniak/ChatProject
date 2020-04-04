package com.example.chat_project.Communication.local.client.models;

import android.net.nsd.NsdManager;
import com.example.chat_project.Communication.local.client.repositories.ClientDiscoveryListener;

public class ClientConnection {
    private NsdManager nsdManager;
    private ClientDiscoveryListener discoveryListener;

    public NsdManager getNsdManager() {
        return nsdManager;
    }

    public void setNsdManager(NsdManager nsdManager) {
        this.nsdManager = nsdManager;
    }

    public ClientDiscoveryListener getDiscoveryListener() {
        return discoveryListener;
    }

    public void setDiscoveryListener(ClientDiscoveryListener discoveryListener) {
        this.discoveryListener = discoveryListener;
    }
}
