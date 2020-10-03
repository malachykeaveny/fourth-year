package db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import models.Article

@Database(
    entities = [Article::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {

    abstract fun getArticleDao(): ArticleDao

    companion object {
        // set it to volatile so other threads can immediately see
        // when another thread changes this instance
        @Volatile
        private var instance: ArticleDatabase? = null
        // synchronise setting that instance to make sure there's only 1
        // instance of the db at once
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "article_db.db"
            ).build()
    }
}