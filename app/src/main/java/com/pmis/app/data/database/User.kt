package com.pmis.app.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pmis.app.screens.InternFormState

@Entity(tableName = "users")
@TypeConverters(Converters::class)
data class User(
    @PrimaryKey
    val googleId: String,
    val email: String,
    val displayName: String? = null,
    val profilePictureUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis(),
    val formResponses: String? = null // JSON string of InternFormState
)

class Converters {
    private val gson = Gson()
    
    @TypeConverter
    fun fromInternFormState(formState: InternFormState?): String? {
        return if (formState == null) null else gson.toJson(formState)
    }
    
    @TypeConverter
    fun toInternFormState(formStateJson: String?): InternFormState? {
        return if (formStateJson == null) null else {
            try {
                gson.fromJson(formStateJson, InternFormState::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
    
    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        return gson.toJson(list)
    }
    
    @TypeConverter
    fun toStringList(listString: String?): List<String>? {
        return if (listString == null) null else {
            val listType = object : TypeToken<List<String>>() {}.type
            gson.fromJson(listString, listType)
        }
    }
}

