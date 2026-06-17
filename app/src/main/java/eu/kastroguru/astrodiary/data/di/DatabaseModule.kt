package eu.kastroguru.astrodiary.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import eu.kastroguru.astrodiary.data.db.AppDatabase
import eu.kastroguru.astrodiary.data.db.dao.BirthDataDao
import eu.kastroguru.astrodiary.data.db.dao.HistoryEventDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "astro_diary.db"
        ).addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3).build()
    }

    @Provides
    fun provideBirthDataDao(db: AppDatabase): BirthDataDao = db.birthDataDao()

    @Provides
    fun provideHistoryEventDao(db: AppDatabase): HistoryEventDao = db.historyEventDao()
}
