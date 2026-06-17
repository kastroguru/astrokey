package eu.kastroguru.astrodiary.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import eu.kastroguru.astrodiary.data.db.dao.BirthDataDao
import eu.kastroguru.astrodiary.data.db.dao.HistoryEventDao
import eu.kastroguru.astrodiary.data.db.entity.BirthDataEntity
import eu.kastroguru.astrodiary.data.db.entity.HistoryEventEntity

@Database(
    entities = [BirthDataEntity::class, HistoryEventEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun birthDataDao(): BirthDataDao
    abstract fun historyEventDao(): HistoryEventDao

    companion object {
        /** v2: events can be linked to a natal chart. Adds nullable history_events.personId. */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE history_events ADD COLUMN personId INTEGER")
            }
        }

        /** v3: events can carry a user-picked image. Adds nullable history_events.imagePath. */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE history_events ADD COLUMN imagePath TEXT")
            }
        }
    }
}
