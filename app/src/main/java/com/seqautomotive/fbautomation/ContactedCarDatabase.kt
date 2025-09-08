package com.seqautomotive.fbautomation

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "contacted_cars")
data class ContactedCar(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val carModel: String,
    val sellerName: String,
    val contactedAt: Long = System.currentTimeMillis()
)

@Dao
interface ContactedCarDao {
    @Query("SELECT * FROM contacted_cars ORDER BY contactedAt DESC")
    fun getAllContactedCars(): Flow<List<ContactedCar>>
    
    @Insert
    suspend fun insertContactedCar(car: ContactedCar)
    
    @Query("DELETE FROM contacted_cars WHERE id = :id")
    suspend fun deleteContactedCar(id: Long)
}

@Database(
    entities = [ContactedCar::class],
    version = 1,
    exportSchema = false
)
abstract class ContactedCarDatabase : RoomDatabase() {
    abstract fun contactedCarDao(): ContactedCarDao
}