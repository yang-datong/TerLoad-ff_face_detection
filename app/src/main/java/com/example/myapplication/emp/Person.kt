package com.example.myapplication.emp

import org.litepal.crud.LitePalSupport

/**
 * @author 杨景
 * @description:
 * @date :2021/1/4 21:20
 */
data class Person(val id:Int , val username: String ) : LitePalSupport()