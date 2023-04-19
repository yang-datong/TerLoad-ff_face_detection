package com.rl.ff_face_detection_terload.database

import androidx.room.*

@Dao
interface UserDao {

    @Query("SELECT * FROM users ORDER BY ID DESC")
    fun getAllUser():MutableList<User>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(user: User):Long

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?

    @Delete
    suspend fun deleteUser(user: User)

    @Update
    suspend fun updateUser(user: User):Int
}
