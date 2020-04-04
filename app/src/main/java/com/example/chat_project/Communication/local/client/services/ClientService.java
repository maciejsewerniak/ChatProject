package com.example.chat_project.Communication.local.client.services;

import android.app.Activity;
import android.net.nsd.NsdManager;
import android.os.StrictMode;

import com.example.chat_project.Communication.local.client.models.ClientConnection;
import com.example.chat_project.Communication.local.client.models.ClientConnectionInfo;
import com.example.chat_project.Communication.local.client.repositories.ClientDiscoveryListener;
import com.example.chat_project.Communication.local.queues.InputMessageQueue;
import com.example.chat_project.Communication.local.queues.OutputMessageQueueCollection;
import com.example.chat_project.Communication.local.queues.PuttableQueue;
import com.example.chat_project.Communication.local.resources.communication.packages.CommunicationPackage;

import java.util.concurrent.BlockingQueue;


public class ClientService {
    private boolean registered = false;

    /**
     * Start server discovery.
     *
     * @param activity Activity used for gathering NSD_service
     * @return Client connection class object
     */
    public ClientConnection startServerDiscovery(Activity activity) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        NsdManager nsdManager = (NsdManager) activity.getSystemService(Activity.NSD_SERVICE);
        ClientDiscoveryListener discoveryListener = new ClientDiscoveryListener(nsdManager);
        nsdManager.discoverServices(ClientConnectionInfo.SERVICE_TYPE,
                NsdManager.PROTOCOL_DNS_SD, discoveryListener);
        ClientConnection clientConnection = new ClientConnection();
        clientConnection.setNsdManager(nsdManager);
        clientConnection.setDiscoveryListener(discoveryListener);
        registered = true;
        return clientConnection;
    }

    /**
     * Stop discovery of Servers
     *
     * @param clientConnection Object which keeps client connection
     */
    public void stopServerDiscovery(ClientConnection clientConnection) {
        if (registered) {
            NsdManager nsdManager = clientConnection.getNsdManager();
            ClientDiscoveryListener discoveryListener = clientConnection.getDiscoveryListener();
            nsdManager.stopServiceDiscovery(discoveryListener);
            registered = false;
        }
    }

    /**
     * Get queue which is used for output communication.
     *
     * @return Instance of output message queue
     */
    public PuttableQueue getOutputMessageQueue() {
        return OutputMessageQueueCollection.getInstance();
    }

    /**
     * Get queue which is used for input communication.
     *
     * @return Instance of input queue
     */
    public BlockingQueue<CommunicationPackage> getInputMessageQueue() {
        return InputMessageQueue.getInstance();
    }
}
