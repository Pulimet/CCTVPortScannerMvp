package net.alexandroid.network.cctvportscanner.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface HostDao {

    @Query("SELECT * FROM host")
    LiveData<List<Host>> getAll();

    @Query("SELECT * FROM host WHERE host = :host")
    Host getHost(String host);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Host host);

    @Query("DELETE FROM host WHERE host = :host")
    int delete(final String host);

    @Delete
    void delete(Host host);
}
