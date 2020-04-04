package com.example.chat_project.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chat_project.Communication.local.client.services.ClientService;
import com.example.chat_project.Communication.local.jobs.Job;
import com.example.chat_project.Communication.local.jobs.JobHandler;
import com.example.chat_project.Communication.local.queues.OutputMessageQueueCollection;
import com.example.chat_project.Communication.local.resources.communication.commands.Greeting;
import com.example.chat_project.Communication.local.resources.communication.data.CommunicationData;
import com.example.chat_project.Communication.local.resources.communication.data.MailConfirmation;
import com.example.chat_project.Communication.local.resources.communication.packages.CommunicationPackage;
import com.example.chat_project.R;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import static com.example.chat_project.Communication.local.client.models.ClientConnectionInfo.NOT_ACCEPTATION_MESSAGE;

public class LyricsSlave extends AppCompatActivity {
    public TextView wifiStatus;
    public EditText mail;
    public Button sendConfirmation;
    public Button sendToOtherDevice;
    public WifiManager wifi;
    public String currentDeviceEmail;
    public static final String NO_WIFI_WARNING_MESSAGE = "WIFI NOT CONFIGURED !!";
    public static final String SERVER_LOST_MESSAGE = "SERVER LOST !!!";
    public static final String SERVER_FOUND_MESSAGE = "\n Some server found !";
    public AlertDialog myDialog;
    private ClientService clientService;
    private boolean accepted = false;
    private String serverIp;
    public static final String intentLostKey = "intentLostKey";
    public static final String intentFoundKey = "intentFoundKey";

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyrics_slave);
        wifiStatus = findViewById(R.id.wifiStatusTf);
        sendConfirmation = findViewById(R.id.sendConfrimation);
        sendToOtherDevice = findViewById(R.id.sendToOtherDevice);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mail = findViewById(R.id.mail);
        mail.setText("noyyn");
        setStatuses(true, true, false);
        clientService = new ClientService();
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(sercverLostReceiver, new IntentFilter(intentLostKey));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(sercverFoundReceiver, new IntentFilter(intentFoundKey));
        if (!checkWifiOnAndConnected()) {
            turnOnWifi();
        } else {
            clientService.startServerDiscovery(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiStateReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(wifiStateReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(sercverLostReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(sercverFoundReceiver);
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!checkWifiOnAndConnected()) {
            turnOnWifi();
        }
    }


    private BroadcastReceiver sercverFoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            wifiStatus.append(SERVER_FOUND_MESSAGE);
            if (accepted) {
                wifiStatus.append("\nTrying to connect");
                getPermisionForRoom();
            }
        }
    };
    private BroadcastReceiver sercverLostReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!TextUtils.isEmpty(currentDeviceEmail)) {
                mail.setText(currentDeviceEmail);
            }
            setStatuses(true, true, false);
            wifiStatus.setTextColor(Color.RED);
            wifiStatus.setText(SERVER_LOST_MESSAGE);
        }
    };


    private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int wifiStateExtra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN);

            switch (wifiStateExtra) {
                case WifiManager.WIFI_STATE_ENABLED:
                    registerServerGreetingJob();
                    hideMyDialogAlert();
                    setWifiStatusText();
                    setStatuses(true, true, false);
                    returnToChat();
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    wifiStatus.setTextColor(Color.RED);
                    wifiStatus.setText(NO_WIFI_WARNING_MESSAGE);
                    setStatuses(false, false, false);
                    turnOnWifi();
                    break;
            }
        }
    };

    private void hideMyDialogAlert() {
        if (myDialog != null && myDialog.isShowing()) {
            myDialog.cancel();
        }
    }

    private void setWifiStatusText() {
        if (getWifiName().contains("<unknown ssid>")) {
            wifiStatus.setTextColor(Color.RED);
        } else {
            wifiStatus.setTextColor(Color.BLACK);
        }
        wifiStatus.setText("SSID: " + getWifiName());
    }

    private void returnToChat() {
        if (!TextUtils.isEmpty(currentDeviceEmail) && accepted) {
            mail.setText(currentDeviceEmail);
            goToAcceptedState(serverIp);
        } else {
            clientService.startServerDiscovery(this);
            getPermisionForRoom();
        }
    }

    private void setStatuses(boolean sendConfirmationState, boolean mailTfStatus, boolean sendToOtherDeviceState) {
        sendConfirmation.setEnabled(sendConfirmationState);
        mail.setEnabled(mailTfStatus);
        sendToOtherDevice.setEnabled(sendToOtherDeviceState);
    }

    private void turnOnWifi() {
        if (myDialog != null && myDialog.isShowing()) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You need Wi-Fi connection for this app. \n \n WLACZ: \n Wi-Fi -> wlacz (switch)")
                .setTitle("Unable to connect")
                .setCancelable(false)
                .setPositiveButton("Settings",
                        (dialog, id) -> {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                            wifi.setWifiEnabled(true);
                        }
                )
                .setNegativeButton("Cancel",
                        (dialog, id) -> LyricsSlave.this.finish()
                );
        myDialog = builder.create();
        myDialog.show();
    }

    private void grantPermm() {

        try {
            if (ContextCompat.checkSelfPermission(LyricsSlave.this,
                    Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED) {
            } else {
                ActivityCompat.requestPermissions(LyricsSlave.this, new String[]{Manifest.permission.CHANGE_WIFI_STATE}, 101);
            }
        } catch (Exception xx) {
            xx.printStackTrace();
        }

    }


    private String getWifiName() {
        return wifi.getConnectionInfo().getSSID();
    }

    private boolean checkWifiOnAndConnected() {

        if (wifi.isWifiEnabled()) { // Wi-Fi adapter is ON

            WifiInfo wifiInfo = wifi.getConnectionInfo();

            if (wifiInfo.getNetworkId() == -1) {
                return false; // Not connected to an access point
            }
            return true; // Connected to an access point
        } else {

            return false; // Wi-Fi adapter is OFF
        }
    }

    private void registerServerGreetingJob() {
        final Context applicationContext = getApplicationContext();
        JobHandler.getInstance().registerJob(new Greeting(), new Job() {
            @Override
            public void execute(CommunicationData inputData) {
                if (inputData instanceof MailConfirmation) {
                    handleMailConfirmationEvent((MailConfirmation) inputData);
                }
            }
        });
    }

    private void handleMailConfirmationEvent(MailConfirmation inputData) {
        String title = inputData.getTitle();
        String lyrics = inputData.getLyrics();
        String ip = inputData.getDeviceIp();
        serverIp = inputData.getServerIp();
        if (title != null && lyrics != null && ip != null) {
            if (title.equals(currentDeviceEmail) && ip.equals(getIPAddress(true))) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (lyrics.equals(NOT_ACCEPTATION_MESSAGE)) {
                            accepted = false;
                            currentDeviceEmail = "";
                            goToNotAcceptedState();
                        } else {
                            accepted = true;
                            currentDeviceEmail = title;
                            goToAcceptedState(serverIp);
                        }
                    }
                });
            }
        }
    }

    private void goToNotAcceptedState() {
        setStatuses(true, true, false);
        wifiStatus.setTextColor(Color.RED);
        wifiStatus.setText(String.format("\n%s <-its your IP \n Email not approved from: %s", getIPAddress(true), serverIp));
    }

    private void goToAcceptedState(String serverIp) {
        setStatuses(false, true, true);
        wifiStatus.setTextColor(Color.BLACK);
        wifiStatus.setText(String.format("\n%s <-its your IP \n Email approved from: %s", getIPAddress(true), serverIp));
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            for (NetworkInterface intf : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        if (addr.getHostAddress().indexOf(':') < 0)
                            return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    public void sendEmailAcceptanceRequest(View view) {
        getPermisionForRoom();
    }

    private void getPermisionForRoom() {
        String mailToSend = mail.getText().toString();
        wifiStatus.setTextColor(Color.RED);
        wifiStatus.setText("Check your server name from Wi-Fi connection ! ");
        if (mailToSend.isEmpty()) {
            Toast.makeText(getApplicationContext(), "empty email !", Toast.LENGTH_SHORT).show();
            return;
        } else {
            currentDeviceEmail = mailToSend;
        }
        clientService.startServerDiscovery(this);
        CommunicationPackage communicationPackage = new CommunicationPackage();
        communicationPackage.setCommunicationCommand(new Greeting());
        MailConfirmation confirmation = new MailConfirmation();
        confirmation.setTitle(currentDeviceEmail);
        confirmation.setLyrics(getIPAddress(true));
        communicationPackage.setCommunicationData(confirmation);
        OutputMessageQueueCollection.getInstance().put(communicationPackage);
        OutputMessageQueueCollection.getInstance().put(communicationPackage);
    }

    public void sendTOtherDevice(View view) {
    }
}
