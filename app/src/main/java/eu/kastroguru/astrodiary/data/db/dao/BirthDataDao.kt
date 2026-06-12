package eu.kastroguru.astrodiary.data.db.dao

import androidx.room.*
import eu.kastroguru.astrodiary.data.db.entity.BirthDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BirthDataDao {

    @Query("SELECT * FROM birth_data ORDER BY createdAt DESC")
    fun getAll(): Flow<List<BirthDataEntity>>

    @Query("SELECT * FROM birth_data WHERE name LIKE '%' || :query || '%' OR city LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun search(query: String): Flow<List<BirthDataEntity>>

    @Query("SELECT * FROM birth_data WHERE sunS = :signId ORDER BY createdAt DESC")
    fun getBySunSign(signId: Int): Flow<List<BirthDataEntity>>

    @Query("SELECT * FROM birth_data WHERE moonS = :signId ORDER BY createdAt DESC")
    fun getByMoonSign(signId: Int): Flow<List<BirthDataEntity>>

    @Query("SELECT * FROM birth_data WHERE year = :year ORDER BY month, day")
    fun getByYear(year: Int): Flow<List<BirthDataEntity>>

    @Query("SELECT * FROM birth_data WHERE id = :id")
    suspend fun getById(id: Long): BirthDataEntity?

    @Insert
    suspend fun insert(entity: BirthDataEntity): Long

    @Update
    suspend fun update(entity: BirthDataEntity)

    @Delete
    suspend fun delete(entity: BirthDataEntity)

    @Query("DELETE FROM birth_data WHERE id = :id")
    suspend fun deleteById(id: Long)
}
