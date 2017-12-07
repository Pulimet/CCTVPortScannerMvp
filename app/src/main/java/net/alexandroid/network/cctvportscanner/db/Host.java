package net.alexandroid.network.cctvportscanner.db;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Host {
    @PrimaryKey(autoGenerate = true)
    private int uid;

    private String host;

    public Host() {
    }

    public Host(String pQuery) {
        host = pQuery;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int pUid) {
        uid = pUid;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String pHost) {
        host = pHost;
    }
}
