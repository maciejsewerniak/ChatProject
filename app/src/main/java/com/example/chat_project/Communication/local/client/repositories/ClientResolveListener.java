package com.example.chat_project.Communication.local.client.repositories;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;

import com.example.chat_project.Communication.local.client.models.ClientConnectionInfo;
import com.example.chat_project.Communication.local.threads.ListenerThread;
import com.example.chat_project.Communication.local.threads.TransmitterThread;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ClientResolveListener implements NsdManager.ResolveListener {

    ClientResolveListener() {

    }

    @Override
    public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
        // Called when the resolve fails. Use the error code to debug.
        System.out.println("Listening  Resolve failed " + errorCode);
        System.out.println("Listening serivce = " + serviceInfo);
    }

    @Override
    public void onServiceResolved(NsdServiceInfo serviceInfo) {
        System.out.println("Listening . " + serviceInfo.getServiceName());

        if (serviceInfo.getServiceName().equals(ClientConnectionInfo.SERVICE_NAME)) {
            System.out.println("Listening port " + serviceInfo);
            return;
        }

        // Obtain port and IP

        int hostPort = serviceInfo.getPort();
        InetAddress hostAddress = serviceInfo.getHost();
        System.out.println("Listening port " + hostPort);
        System.out.println("Listening ip " + hostAddress);
        try {
            Socket socket = this.createSocketOutOfNsdServiceInfo(serviceInfo);
            TransmitterThread transmitterThread = new TransmitterThread(socket);
            new Thread(transmitterThread).start();
            ListenerThread listenerThread = new ListenerThread(socket);
            new Thread(listenerThread).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Socket createSocketOutOfNsdServiceInfo(NsdServiceInfo nsdServiceInfo) throws IOException {
        return new Socket(nsdServiceInfo.getHost(), nsdServiceInfo.getPort());
    }
}
