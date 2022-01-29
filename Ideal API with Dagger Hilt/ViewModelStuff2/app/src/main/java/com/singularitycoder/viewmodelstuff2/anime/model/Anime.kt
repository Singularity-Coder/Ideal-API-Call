package com.singularitycoder.viewmodelstuff2.anime.model

import android.os.Parcelable
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.singularitycoder.viewmodelstuff2.utils.TABLE_ANIME_DATA
import com.singularitycoder.viewmodelstuff2.utils.TABLE_DESCRIPTIONS
import com.singularitycoder.viewmodelstuff2.utils.network.Skip

// https://www.json2kotlin.com/
// For things to be testable they have to as loosely coupled as possible. So dont introduce context stuff in models.
// A class annotated with @Entity must always have a primary key

// @Relation - Relationships btw tables - So Anime and Genre are 2 tables
// 1. One-to-One - One Anime falls under One Genre
// 2. One-to-Many - One Anime falls under Many Genres
// 3. Many-to-Many - Many Anime fall under One Genre

data class AnimeList(
    @SerializedName("status_code") val statusCode: Int = -1,
    val message: String = "",
    var data: AnimeListData = AnimeListData(),
    val version: Int = -1
) {
    constructor() : this(statusCode = -1, message = "", data = AnimeListData(), version = -1)
}

data class AnimeListData(
    @SerializedName("current_page") val currentPage: Int = -1,
    val count: Int = -1,
    var documents: List<AnimeData> = emptyList(),
    @SerializedName("last_page") val lastPage: Int = -1
)

data class Anime(
    @SerializedName("status_code") val statusCode: Int = -1,
    val message: String = "",
    var data: AnimeData = AnimeData(),
    val version: Int = -1
)

@kotlinx.parcelize.Parcelize
@Entity(tableName = TABLE_ANIME_DATA)
data class AnimeData(
    @PrimaryKey @ColumnInfo(name = "aniListId", index = true) @SerializedName("anilist_id") var aniListId: Long,
    // serialize true if we want to send it, deserialize true if we want to receive it. We can ignore this field to begin with. Just for show. This is now a local field. Problem with @Transient is that it ignores serialization and deserialization and we cant do just one of them and it also excludes from Room. so less control. So go with an exclusion strategy
    /*@Transient*/
    /*@Expose(serialize = false, deserialize = true)*/
    @Ignore @Skip(serialize = false, deserialize = true) @SerializedName("mal_id") val malId: Long,
    var format: Int,
    var status: Int,
    @Embedded(prefix = "title_") var titles: Titles, // Embedding with a prefix in order to give a unique column name. We can also assign a unique column name in both the data classes but we have to assign it to every field manually. Inestead an embeded prefix is a more easy way to assign a unique prefix to the column name of a table
    @Embedded(prefix = "desc_") var descriptions: Descriptions, // What Embedded does is attach all the columns of the Descriptions table and appends them to TABLE_ANIME_DATA instead of creating a new table
    @SerializedName("start_date") var startDate: String,
    @SerializedName("end_date") var endDate: String,
    @SerializedName("season_period") var seasonPeriod: Int,
    @SerializedName("season_year") var seasonYear: Int,
    @SerializedName("episodes_count") var episodesCount: Int,
    @SerializedName("episode_duration") var episodeDuration: Int,
    @SerializedName("trailer_url") var trailerUrl: String,
    @SerializedName("cover_image") var coverImage: String,
    @SerializedName("banner_image") var bannerImage: String,
    var genres: List<String>,   // This must have a type converter
    var score: Int,
    var id: Int,
    @Skip @ColumnInfo(name = "myFavReason", defaultValue = "") var myFavReason: String = "",
    @Skip @ColumnInfo(name = "myFavReasonDate", defaultValue = "") var myFavReasonDate: String = "",
    @Skip @ColumnInfo(name = "isFavourite", defaultValue = "") var isFav: Boolean = false
) : Parcelable {

    constructor() : this(
        aniListId = 0,
        malId = 0,
        format = 0,
        status = 0,
        titles = Titles(),
        descriptions = Descriptions(),
        startDate = "",
        endDate = "",
        seasonPeriod = 0,
        seasonYear = 0,
        episodesCount = 0,
        episodeDuration = 0,
        trailerUrl = "",
        coverImage = "",
        bannerImage = "",
        genres = emptyList(),
        score = 0,
        id = 0
    )

    override fun equals(other: Any?): Boolean = aniListId == (other as? AnimeData)?.aniListId

    override fun toString(): String = GsonBuilder().setPrettyPrinting().create().toJson(this)

    override fun hashCode(): Int = aniListId.hashCode()
}

// When you embedd this, it becomes part of the parent table which in this case is TABLE_ANIME_DATA
@kotlinx.parcelize.Parcelize
data class Titles(
    var en: String? = "",
    var jp: String? = "",
    var it: String? = ""
) : Parcelable {
    constructor() : this(en = "", jp = "", it = "")
}

// Descriptions is in a one to one relationship with Anime. Each anime has a description obj
@kotlinx.parcelize.Parcelize
@Entity(
    tableName = TABLE_DESCRIPTIONS,
/*    foreignKeys = [
        ForeignKey(
            entity = AnimeData::class,
            parentColumns = ["aniListId"],
            childColumns = ["descId"],
            onDelete = CASCADE,
            onUpdate = CASCADE // CASCADE means each action impacts both the child and the parent tables
        )
    ],
    indices = [
        Index(value = ["descId"])
    ]*/
)
data class Descriptions(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "descId") var descId: Long = -1,   // either add index = true in the ColumnInfo or in the @Entity annotation. Also avoid autoGenerate primary key as much as possible. What if int overflows? Since it belongs to Anime Table use aniListId as primary key
    @ColumnInfo(defaultValue = "") var en: String? = "",
    @ColumnInfo(defaultValue = "") var it: String? = ""
) : Parcelable {
    constructor() : this(descId = -1, en = "", it = "")
}

enum class AnimeFormats
enum class AnimeStatus
enum class AnimeSeason
