package eu.kastroguru.astrodiary.data.db.dao

import androidx.room.*
import eu.kastroguru.astrodiary.data.db.entity.HistoryEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryEventDao {

    @Query("SELECT * FROM history_events ORDER BY year DESC, month DESC, day DESC")
    fun getAll(): Flow<List<HistoryEventEntity>>

    @Query("SELECT * FROM history_events WHERE id = :id")
    suspend fun getById(id: Long): HistoryEventEntity?

    @Query("SELECT * FROM history_events WHERE tags LIKE '%' || :tag || '%' ORDER BY year DESC, month DESC, day DESC")
    fun getByTag(tag: String): Flow<List<HistoryEventEntity>>

    @Query("SELECT * FROM history_events WHERE name LIKE '%' || :q || '%' OR description LIKE '%' || :q || '%' ORDER BY year DESC, month DESC, day DESC")
    fun search(q: String): Flow<List<HistoryEventEntity>>

    @Query("SELECT * FROM history_events WHERE sunS = :signId ORDER BY year DESC, month DESC, day DESC")
    fun getBySunSign(signId: Int): Flow<List<HistoryEventEntity>>

    @Query("SELECT * FROM history_events WHERE moonS = :signId ORDER BY year DESC, month DESC, day DESC")
    fun getByMoonSign(signId: Int): Flow<List<HistoryEventEntity>>

    @Query("SELECT * FROM history_events WHERE isGlobal = 1 ORDER BY year DESC, month DESC, day DESC")
    fun getGlobalOnly(): Flow<List<HistoryEventEntity>>

    @Query("SELECT * FROM history_events WHERE year = :year ORDER BY month DESC, day DESC")
    fun getByYear(year: Int): Flow<List<HistoryEventEntity>>

    @Query("SELECT * FROM history_events WHERE personId = :personId ORDER BY year DESC, month DESC, day DESC")
    fun getByPerson(personId: Long): Flow<List<HistoryEventEntity>>

    @Query("SELECT DISTINCT tags FROM history_events WHERE tags != ''")
    suspend fun getAllTagStrings(): List<String>

    @Query("SELECT * FROM history_events WHERE tags LIKE '%' || :tag || '%'")
    suspend fun getCandidatesByTag(tag: String): List<HistoryEventEntity>

    @Query("SELECT DISTINCT year FROM history_events ORDER BY year DESC")
    suspend fun getAllYears(): List<Int>

    @Insert
    suspend fun insert(entity: HistoryEventEntity): Long

    @Update
    suspend fun update(entity: HistoryEventEntity)

    @Delete
    suspend fun delete(entity: HistoryEventEntity)

    @Query("DELETE FROM history_events WHERE id = :id")
    suspend fun deleteById(id: Long)
}
