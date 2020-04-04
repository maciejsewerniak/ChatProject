package com.example.chat_project.Communication.local.server.models;

import android.net.nsd.NsdManager;

public class ServerCommunicationModel {

    private NsdManager nsdManager;
    private NsdManager.RegistrationListener serverRegistrationListener;

    public NsdManager getNsdManager() {
        return this.nsdManager;
    }

    public ServerCommunicationModel setNsdManager(NsdManager nsdManager) {
        this.nsdManager = nsdManager;
        return this;
    }

    public NsdManager.RegistrationListener getServerRegistrationListener() {
        return this.serverRegistrationListener;
    }

    public ServerCommunicationModel setServerRegistrationListener(NsdManager.RegistrationListener serverRegistrationListener) {
        this.serverRegistrationListener = serverRegistrationListener;
        return this;
    }
}
