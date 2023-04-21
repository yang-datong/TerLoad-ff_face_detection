package com.rl.ff_face_detection_terload.database

data class UserStatusAndCheckTime(
        val status: Int = 0, //0=已考勤，1=未考勤
        val checkin_time: Long = 0,
        val checkout_time: Long = 0
)