package com.example.chat_project.Communication.local.server.repositories;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;

public class ServerRegistrationListener implements NsdManager.RegistrationListener {

    private Runnable runnableToStartDuringRegister;
    private Thread actualThread;

    public ServerRegistrationListener(Runnable runnableToStartDuringRegister) {

        this.runnableToStartDuringRegister = runnableToStartDuringRegister;
    }

    @Override
    public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
        String serviceName = NsdServiceInfo.getServiceName();
        System.out.println("Listening Registered name : " + serviceName);
        this.actualThread = new Thread(this.runnableToStartDuringRegister);
        this.actualThread.start();
    }

    @Override
    public void onRegistrationFailed(NsdServiceInfo serviceInfo,
                                     int errorCode) {
        System.out.println("Listening Registered failed : " + errorCode);
    }

    @Override
    public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
        // Service has been unregistered. This only happens when you
        // call
        // NsdManager.unregisterService() and pass in this listener.

        if (actualThread != null) {
            this.actualThread.interrupt();
        }
        System.out.println("Listening Service Unregistered : " + serviceInfo.getServiceName());
    }

    @Override
    public void onUnregistrationFailed(NsdServiceInfo serviceInfo,
                                       int errorCode) {
        // Unregistration failed. Put debugging code here to determine
        // why.
        System.out.println("Listening Unregistration failed : " + errorCode);
    }
}
