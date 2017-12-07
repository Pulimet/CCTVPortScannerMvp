package net.alexandroid.network.cctvportscanner.scan;


public interface ScanResult {
    void onResult(String host, int port, int state);
}
