package com.example.chat_project.Communication.local.server.services;

import android.app.Activity;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.chat_project.Communication.local.jobs.JobHandler;
import com.example.chat_project.Communication.local.queues.InputMessageQueue;
import com.example.chat_project.Communication.local.queues.OutputMessageQueueCollection;
import com.example.chat_project.Communication.local.queues.PuttableQueue;
import com.example.chat_project.Communication.local.resources.communication.packages.CommunicationPackage;
import com.example.chat_project.Communication.local.server.models.ServerCommunicationModel;
import com.example.chat_project.Communication.local.server.repositories.ServerRegistrationListener;
import com.example.chat_project.Communication.local.server.threads.ServerThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.BlockingQueue;

public class ServerService {
    private String SERVICE_NAME = "MyServer";
    private String SERVICE_TYPE = "_http._tcp.";

    public ServerCommunicationModel startCommunication(Context context) {
        try {
            return this.startClientsListening(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CommunicationPackage getNewCommunicationPackage() {
        return new CommunicationPackage();
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

    public JobHandler getJobHandler() {
        return JobHandler.getInstance();
    }

    /**
     * Stop listening for clients and unregister actual clients
     *
     * @param serverCommunicationModel Server communication model received during clients listening start
     */
    public void stopClientsListening(ServerCommunicationModel serverCommunicationModel) {
        if (serverCommunicationModel != null) {
            NsdManager nsdManager = serverCommunicationModel.getNsdManager();
            nsdManager.unregisterService(serverCommunicationModel.getServerRegistrationListener());
        }
    }

    /**
     * Starts listening to clients to allow them to connect to the base devide (server).
     */
    private ServerCommunicationModel startClientsListening(Context context) throws IOException {
        try {
            ServerSocket serverSocket;
            serverSocket = this.getAllocatedServerSocket();
            ServerThread serverThread = new ServerThread(serverSocket);

            ServerRegistrationListener serverRegistrationListener = new ServerRegistrationListener(serverThread);
            ServerCommunicationModel serverCommunicationModel = new ServerCommunicationModel()
                    .setNsdManager((NsdManager) context.getSystemService(Activity.NSD_SERVICE))
                    .setServerRegistrationListener(serverRegistrationListener);

            serverCommunicationModel
                    .getNsdManager()
                    .registerService(
                            getNsdServiceInfo(serverSocket),
                            NsdManager.PROTOCOL_DNS_SD,
                            serverRegistrationListener
                    );
            return serverCommunicationModel;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Get Nsd service info.
     *
     * @param serverSocket Server socket object to gather set port number
     * @return NsdServiceInfo
     */
    @NonNull
    private NsdServiceInfo getNsdServiceInfo(ServerSocket serverSocket) {
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName(SERVICE_NAME);
        serviceInfo.setServiceType(SERVICE_TYPE);
        serviceInfo.setPort(serverSocket.getLocalPort());
        return serviceInfo;
    }

    /**
     * Get server socket which will be alocated during this method execution.
     *
     * @return ServerSocket if everything is fine
     * @throws IOException If application is not able to allocate server socket.
     */
    @NonNull
    private ServerSocket getAllocatedServerSocket() throws IOException {
        ServerSocket serverSocket = null;
        int portStartRange = 7000;
        int portEndRange = 10000;
        try {
            serverSocket = new ServerSocket(0);
        } catch (IOException ioe) {
            for (int i = portStartRange; i < portEndRange; i++)
                try {
                    serverSocket = new ServerSocket(i);
                    break;
                } catch (IOException ioe1) {
                    Log.e("MyError", String.format("Unable to open server in port (%d)", i));
                }
        }
        if (serverSocket == null) {
            throw new IOException(String.format("Unable to open server in port range (%d - %d)", portStartRange, portEndRange));
        }
        return serverSocket;
    }
}
