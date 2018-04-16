package hiveconnect.com.superwifidirect.Bean;

import android.net.wifi.p2p.WifiP2pDevice;

public class WifiP2pDeviceInReceiving {

    private WifiP2pDevice wifiP2pDevice;
    private int receivingProgress;

    public WifiP2pDeviceInReceiving(WifiP2pDevice wifiP2pDevice, int receivingProgress) {
        this.wifiP2pDevice = wifiP2pDevice;
        this.receivingProgress = receivingProgress;
    }

    public WifiP2pDevice getWifiP2pDevice() {
        return wifiP2pDevice;
    }

    public void setWifiP2pDevice(WifiP2pDevice wifiP2pDevice) {
        this.wifiP2pDevice = wifiP2pDevice;
    }

    public int getReceivingProgress() {
        return receivingProgress;
    }

    public void setReceivingProgress(int receivingProgress) {
        this.receivingProgress = receivingProgress;
    }
}
