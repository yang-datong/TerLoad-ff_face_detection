package com.example.myapplication.extensions



   fun String.isValidUserName() = this.matches(Regex("^[a-zA-Z]\\w{2,15}$"))
   fun String.isValidPassword() = this.matches(Regex("^.{3,20}$"))
