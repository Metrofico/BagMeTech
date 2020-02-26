package com.teamticpro.bagmetech.entities;

import androidx.room.TypeConverter;

import java.util.Date;

class DateRoomConverter {
    @TypeConverter
    public static Date toDate(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long toLong(Date value) {
        return value == null ? null : value.getTime();
    }
}
