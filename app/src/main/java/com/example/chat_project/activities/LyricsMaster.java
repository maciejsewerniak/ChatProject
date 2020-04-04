package com.example.chat_project.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.widget.TextView;

import com.example.chat_project.Communication.local.jobs.Job;
import com.example.chat_project.Communication.local.resources.communication.commands.CommunicationCommand;
import com.example.chat_project.Communication.local.resources.communication.commands.Greeting;
import com.example.chat_project.Communication.local.resources.communication.data.CommunicationData;
import com.example.chat_project.Communication.local.resources.communication.data.Lyrics;
import com.example.chat_project.Communication.local.resources.communication.data.MailConfirmation;
import com.example.chat_project.Communication.local.resources.communication.packages.CommunicationPackage;
import com.example.chat_project.Communication.local.server.models.ServerCommunicationModel;
import com.example.chat_project.Communication.local.server.services.ServerService;
import com.example.chat_project.R;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.chat_project.Communication.local.client.models.ClientConnectionInfo.ACCEPTATION_MESSAGE;
import static com.example.chat_project.Communication.local.client.models.ClientConnectionInfo.NOT_ACCEPTATION_MESSAGE;
import static com.example.chat_project.activities.LyricsSlave.getIPAddress;

public class LyricsMaster extends AppCompatActivity {

    public ServerService serverService;
    public TextView connectionStatus;
    public WifiManager wifi;
    private static List<Pair<String, String>> credentials;
    public AlertDialog myDialog;
    private ServerCommunicationModel serverCommunicationModel;
    private boolean serverRegistered = false;
    public static final String HOT_SPOT_ACTION_NAME = "android.net.wifi.WIFI_AP_STATE_CHANGED";

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.serverService = new ServerService();
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_lyrics_master);
        connectionStatus = findViewById(R.id.connectionStatusTf);
        credentials = new ArrayList<>();
        startConnection();
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }


    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter mIntentFilter = new IntentFilter(HOT_SPOT_ACTION_NAME);
        registerReceiver(connectionReceiver, mIntentFilter);
        IntentFilter mIntentFilterConn = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectionReceiver, mIntentFilterConn);
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

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(connectionReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private final BroadcastReceiver connectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean hotspotStatus = isHotSpotEnabled();
            boolean wifiStatus = checkWifiOnAndConnected();
            handleTextViewResult(hotspotStatus, wifiStatus);
        }
    };

    private void handleTextViewResult(boolean hotspotStatus, boolean wifiStatus) {
        String status = "Your IP: " + getIPAddress(true) + "\n HotspotName:" + getWifiName() + "\nHotSpot status : " + hotspotStatus + "\n Wifi status: " + wifiStatus;
//        if (wifiStatus || hotspotStatus) {
        if (hotspotStatus) {
            connectionStatus.setTextColor(Color.BLACK);
            if (myDialog != null && myDialog.isShowing()) {
                myDialog.cancel();
            }
            startConnection();

        } else {
            connectionStatus.setTextColor(Color.RED);
            turnOnHotSpot();
        }
        connectionStatus.setText(status);
    }

    private void disconnectAllDevices() {
        if (serverRegistered) {
            serverService.stopClientsListening(serverCommunicationModel);
            serverRegistered = false;
        }
    }

    public boolean isHotSpotEnabled() {
        Method[] wmMethods = wifi.getClass().getDeclaredMethods();
        for (Method method : wmMethods) {
            if (method.getName().equals("isWifiApEnabled")) {
                try {
                    return (boolean) method.invoke(wifi);
                } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    private boolean isMobileDataEnabled() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return activeNetwork != null && activeNetwork.isConnected();

    }

    protected void turnOnHotSpot() {

        if (myDialog != null && myDialog.isShowing()) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You need to enable Hot-Spot for this app. Please turn on Hot- Spot in Settings. \n \n WLACZ: \n -> Przenosny punkt Wi-Fi -> wlacz (switch) \n \n NAZWA: \n ->Skonfiguruj Hotspot -> Nazwa hotspotu (SSID) \n \n If you re-run Hot-Spot in Server mode you need to re run this section")
                .setTitle("Unable to connect")
                .setCancelable(false)
                .setPositiveButton("Settings",
                        (dialog, id) -> {
                            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_SETTINGS)) {
                                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_SETTINGS}, 121);
                            }
                            final Intent intent = new Intent(Intent.ACTION_MAIN, null);
                            intent.addCategory(Intent.CATEGORY_LAUNCHER);
                            final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.TetherSettings");
                            intent.setComponent(cn);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                )
                .setNegativeButton("Cancel",
                        (dialog, id) -> {
                            LyricsMaster.this.finish();
                        }
                );
        myDialog = builder.create();
        myDialog.show();
    }


    protected void turnOnMobileData() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You need internet connection for this app. Please turn on mobile network in Settings. \n \n WLACZ: \n -> Transfer danych (mobile data) -> wlacz (switch) \n LUB \n Roaming danych -> wlacz")
                .setTitle("Unable to connect")
                .setCancelable(false)
                .setPositiveButton("Settings",
                        (dialog, id) -> {
                            Intent intent = new Intent(android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS);
                            startActivity(intent);
                        }
                )
                .setNegativeButton("Cancel",
                        (dialog, id) -> LyricsMaster.this.finish()
                );
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void startConnection() {
        serverCommunicationModel = serverService.startCommunication(getApplicationContext());
        serverRegistered = true;
        registerClientGreetingJob();
        sendGreetingsToOtherDevices();
    }

//    private void sendAcceptanceMessageToRegisteredDevices() {
//        String ipAddress = getIPAddress(true);
//        for (Pair<String, String> registeredDevice : credentials) {
//            sendMessageToOtherDevices(registeredDevice.first, ACCEPTATION_MESSAGE, registeredDevice.second, ipAddress);
//        }
//    }

    private void registerClientGreetingJob() {
        serverService.getJobHandler().registerJob(new Greeting(), new Job() {
            @Override
            public void execute(CommunicationData inputData) {
                if (inputData instanceof MailConfirmation) {
                    String email = ((MailConfirmation) inputData).getTitle();
                    String ip = ((MailConfirmation) inputData).getLyrics();
                    Pair<String, String> receivedCredentials = new Pair<>(email, ip);

                    sendMessageToOtherDevices(email, ACCEPTATION_MESSAGE, ip, getIPAddress(true));  //nowy IP jest zawsze zaakceptowany

                    Optional<Pair<String, String>> credentialsToDelete = handleReceivedCredentials(receivedCredentials);    //zaaktualizowanie listy plus pobranie do unsubscirbe

                    credentialsToDelete.ifPresent(stringStringPair ->
                            sendMessageToOtherDevices(stringStringPair.first, NOT_ACCEPTATION_MESSAGE, stringStringPair.second, getIPAddress(true)));
                    runOnUiThread(new Runnable() {  // UI update
                        @Override
                        public void run() {
                            // Stuff that updates the UI
                            connectionStatus.setText(String.format("Your Ip: %s \n%s \nAccepted : \n%s", getIPAddress(true), getWifiName(), credentials.toString()));
                        }
                    });
                }
            }
        });
    }

    private String getWifiName() {
        return wifi.getConnectionInfo().getSSID();
    }

    private Optional<Pair<String, String>> handleReceivedCredentials(Pair<String, String> credentialToCheck) {
        if (credentials.isEmpty()) {
            credentials.add(credentialToCheck);
            return Optional.empty();        //pierwszy user
        }
        if (credentials.contains(credentialToCheck)) {
            return Optional.empty();        //ponowne zatwierdzenie kogos kto juz ma credentiale
        }

        for (int i = 0; i < credentials.size(); i++) {
            if (credentials.get(i).first.equals(credentialToCheck.first)) {             //ktos juz ma ten mail
                final Pair<String, String> credentialsToDelete = credentials.get(i); //pobieram do unsubscribe
                credentials.remove(i);          //wywalam
                credentials.add(i, credentialToCheck);      //dodaje nowy, dobry
                for (int k = i + 1; k < credentials.size(); k++) {
                    if (credentials.get(k).second.equals(credentialToCheck.second)) {
                        credentials.remove(k);                                          //usuniecie innyego maila jezeli byl dla tego IP
                    }
                }
                return Optional.of(credentialsToDelete);        //odsylam do unsubscribea
            }
            if (credentials.get(i).second.equals(credentialToCheck.second)) {   //te IP ma juz inny mail
                credentials.set(i, credentialToCheck);      //zamieniam mail
                return Optional.empty();        //nie potrzeba wysylac unsubscribe
            }
        }
        credentials.add(credentialToCheck); //nowe credentiale
        return Optional.empty();
    }

//    private boolean isIPBusy(Pair<String, String> credentialToCheck) {
//        boolean present = false;
//        for (Pair<String, String> cred : credentials) {
//            if (cred.second.equals(credentialToCheck.second)) {
//                present = true;
//                break;
//            }
//        }
//        return present;
//    }
//
//    private Optional<Pair<String, String>> getDeletedCredenialsName(Pair<String, String> credential) {
//        for (int i = 0; i < credentials.size(); i++) {
//            if (credentials.get(i) != null && credentials.get(i).first.equals(credential.first) && !credentials.get(i).second.equals(credential.second)) {
//                final Pair<String, String> credentialsToDelete = credentials.get(i);
//                credentials.remove(i);
//                credentials.add(i, credential);
//                return Optional.of(credentialsToDelete);
//            }
//        }
//        return Optional.empty();
//    }

    public void sendGreetingsToOtherDevices() {
        Lyrics lyrics = new Lyrics();
        lyrics.setTitle("welcome on Huawei P10 !");
        lyrics.setLyrics("hello");
        this.sendCommunicationPackageToOtherDevices(new Greeting(), lyrics);
    }

    public void sendMessageToOtherDevices(final String title, final String lyric,
                                          final String Ip, final String serverIp) {
        MailConfirmation confirmation = new MailConfirmation();
        confirmation.setTitle(title);
        confirmation.setLyrics(lyric);
        confirmation.setDeviceIp(Ip);
        confirmation.setServerIp(serverIp);
        this.sendCommunicationPackageToOtherDevices(new Greeting(), confirmation);
    }

    private void sendCommunicationPackageToOtherDevices(
            CommunicationCommand communicationCommand,
            CommunicationData communicationData
    ) {
        CommunicationPackage communicationPackage = this.serverService.getNewCommunicationPackage();
        communicationPackage.setCommunicationCommand(communicationCommand);
        communicationPackage.setCommunicationData(communicationData);
        this.serverService.getOutputMessageQueue().put(communicationPackage);
    }

}
