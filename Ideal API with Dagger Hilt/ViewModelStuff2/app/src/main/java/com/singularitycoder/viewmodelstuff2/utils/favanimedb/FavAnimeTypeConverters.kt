package com.singularitycoder.viewmodelstuff2.utils.favanimedb

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.singularitycoder.viewmodelstuff2.anime.model.Titles
import java.lang.reflect.Type

// Type converters must not contain any arguments in the constructor
// Classes that are used as TypeConverters must have no-argument public constructors.
// Use a ProvidedTypeConverter annotation if you need to take control over creating an instance of a TypeConverter.

class AnimeGeneresListConverter {
    private val type: Type = object : TypeToken<List<String>?>() {}.type
//    @Inject lateinit var gson: Gson   // Not injecting
    val gson: Gson = Gson()

    @TypeConverter
    fun listToString(list: List<String>?): String? {
        list ?: return null
        return gson.toJson(list, type)
    }

    @TypeConverter
    fun stringToList(string: String?): List<String>? {
        string ?: return null
        return gson.fromJson(string, type)
    }
}

class AnimeTitleConverter {
    val gson: Gson = Gson()

    @TypeConverter
    fun objectToString(obj: Titles?): String? {
        obj ?: return null
        return gson.toJson(obj)
    }

    @TypeConverter
    fun stringToObject(string: String?): Titles? {
        string ?: return null
        return gson.fromJson(string, Titles::class.java)
    }
}