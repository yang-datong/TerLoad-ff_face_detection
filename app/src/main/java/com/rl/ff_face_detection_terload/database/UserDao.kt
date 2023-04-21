package com.rl.ff_face_detection_terload.database

import androidx.room.*

@Dao
interface UserDao {

    @Query("SELECT * FROM users ORDER BY ID DESC")
    suspend fun getAllUser(): MutableList<User>

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?

//    @Query("SELECT status FROM users WHERE username = :username")
//    suspend fun getStatusByUsername(username: String): Int? //还需要后续逻辑判断，使用getStatusAndCheckTimeByUsername

    @Query("SELECT status, checkin_time,checkout_time FROM users WHERE username = :username")
    suspend fun getStatusAndCheckTimeByUsername(username: String): UserStatusAndCheckTime?


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(user: User): Long


    @Update
    suspend fun updateUser(user: User): Int

    @Query("UPDATE users SET status = :status WHERE username = :username")
    suspend fun updateStatusByUsername(username: String, status: Int): Int
    //TODO 用户考勤的数据需要上传到服务器与管理员端进行同步展示

    @Query("UPDATE users SET status = :status, checkin_time = :checkinTime WHERE username = :username")
    suspend fun updateStatusAndCheckinTimeByUsername(username: String, status: Int, checkinTime: Long): Int

    @Query("UPDATE users SET status = :status, checkout_time = :checkoutTime WHERE username = :username")
    suspend fun updateStatusAndCheckoutTimeByUsername(username: String, status: Int, checkoutTime: Long): Int

    @Query("UPDATE users SET checkin_time = :checkin_time WHERE username = :username")
    suspend fun updateCheckinTimeByUsername(username: String, checkin_time: Long): Int


    @Delete
    suspend fun deleteUser(user: User)
}
