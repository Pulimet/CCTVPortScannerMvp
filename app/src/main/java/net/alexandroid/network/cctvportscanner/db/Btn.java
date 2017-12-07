package net.alexandroid.network.cctvportscanner.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Btn {
    @PrimaryKey(autoGenerate = true)
    private int uid;

    private String title;

    private String ports;

    public Btn(String title, String ports) {
        this.title = title;
        this.ports = ports;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int pUid) {
        uid = pUid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String pTitle) {
        title = pTitle;
    }

    public String getPorts() {
        return ports;
    }

    public void setPorts(String pPorts) {
        ports = pPorts;
    }
}
