package com.likhit.userlist.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.likhit.userlist.data.remote.model.Address

class Converters {
    @TypeConverter
    fun fromAddressJson(value: String): Address{
        return Gson().fromJson(value, Address::class.java)
    }

    @TypeConverter
    fun toAddressJson(address: Address): String{
        return Gson().toJson(address)
    }
}