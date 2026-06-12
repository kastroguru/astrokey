package eu.kastroguru.astrodiary.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import eu.kastroguru.astrodiary.data.db.dao.BirthDataDao
import eu.kastroguru.astrodiary.data.db.dao.HistoryEventDao
import eu.kastroguru.astrodiary.data.db.entity.BirthDataEntity
import eu.kastroguru.astrodiary.data.db.entity.HistoryEventEntity

@Database(
    entities = [BirthDataEntity::class, HistoryEventEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun birthDataDao(): BirthDataDao
    abstract fun historyEventDao(): HistoryEventDao
}
