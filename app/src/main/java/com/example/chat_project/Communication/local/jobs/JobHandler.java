package com.example.chat_project.Communication.local.jobs;


import com.example.chat_project.Communication.local.queues.InputMessageQueue;
import com.example.chat_project.Communication.local.resources.communication.commands.CommandName;
import com.example.chat_project.Communication.local.resources.communication.packages.CommunicationPackage;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

public class JobHandler {
    private static JobHandler instance = null;
    private final HashMap<String, Job> jobQueue;
    private final Runnable worker;
    private final BlockingQueue<CommunicationPackage> messageQueue;
    private final Thread threadInstance;

    private JobHandler(BlockingQueue<CommunicationPackage> inputMessageQueue) {
        this.jobQueue = new HashMap<>();
        this.messageQueue = inputMessageQueue;
        this.worker = new Runnable() {
            @Override
            public void run() {
                try {
                    while (!Thread.interrupted()) {
                        CommunicationPackage communicationPackage = messageQueue.take();
                        Job job = findJob(communicationPackage.getCommunicationCommand());
                        if (job != null) {
                            job.execute(communicationPackage.getCommunicationData());
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        this.threadInstance = new Thread(this.worker);
        this.threadInstance.start();
    }

    public static JobHandler getInstance() {
        if (instance == null) {
            instance = new JobHandler(InputMessageQueue.getInstance());
            return instance;
        } else {
            return instance;
        }
    }

    public void registerJob(CommandName command, Job job) {
        synchronized (this.jobQueue) {
            this.jobQueue.put(command.getCommandName(), job);
        }
    }

    public void unregisterJob(CommandName command) {
        synchronized (this.jobQueue) {
            this.jobQueue.remove(command.getCommandName());
        }
    }

    private Job findJob(CommandName command) {
        return this.jobQueue.get(command.getCommandName());
    }

    private HashMap getJobsMap() {
        return this.jobQueue;
    }
}
