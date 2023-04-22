package com.rl.ff_face_detection_terload.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
        @PrimaryKey(autoGenerate = true) val id: Int? = null,
        @ColumnInfo(name = "username") val username: String,
        @ColumnInfo(name = "password") var password: String,
        @ColumnInfo(name = "name") val name: String? = null,
        @ColumnInfo(name = "email") val email: String? = null,
        @ColumnInfo(name = "phone") val phone: String? = null,
        @ColumnInfo(name = "avatar") val avatar: String? = null,
        @ColumnInfo(name = "status") val status: Int = 0, //0=未考勤，1=开始考勤，2=结束考勤
        @ColumnInfo(name = "checkin_time") val checkin_time: Long = 0,
        @ColumnInfo(name = "checkout_time") val checkout_time: Long = 0,
        @ColumnInfo(name = "create_time") val create_time: Long = 0 //最近登录时间
) {
    override fun toString(): String {
        return "User(id=$id, " +
                "name='$name', " +
                "username=$username, " +
                "password=$password, " +
                "status=$status, " +
                "checkin_time=$checkin_time, " +
                "checkout_time=$checkout_time, " +
                "create_time=$create_time)"
    }
}
