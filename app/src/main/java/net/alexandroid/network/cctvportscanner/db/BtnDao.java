package net.alexandroid.network.cctvportscanner.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface BtnDao {

    @Query("SELECT * FROM btn")
    LiveData<List<Btn>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Btn... btns);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Btn btn);

    @Delete
    void delete(Btn btn);
}
