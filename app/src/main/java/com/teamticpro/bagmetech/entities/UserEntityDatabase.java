package com.teamticpro.bagmetech.entities;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {UserEntity.class}, version = 1)
@TypeConverters({DateRoomConverter.class})
public abstract class UserEntityDatabase extends RoomDatabase {

    public abstract UserEntityDAO getUserEntityDao();

    private static UserEntityDatabase db;

    public static UserEntityDatabase getInstance(Context context) {
        if (db == null) {
            db = buildDatabase(context);
        }
        return db;
    }

    private static UserEntityDatabase buildDatabase(Context context) {
        String db_name = "hnjkdhjqewuyiksahndj.db";
        return Room.databaseBuilder(context.getApplicationContext(),
                UserEntityDatabase.class,
                db_name).fallbackToDestructiveMigration().build();
    }

    public void cleanDatabase() {
        db = null;
    }
}
