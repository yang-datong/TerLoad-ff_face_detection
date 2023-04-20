package com.rl.ff_face_detection_terload.database

import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import java.io.*
import java.nio.channels.FileChannel

class DataBaseOperation {

    companion object {
        private const val TAG = "DataBaseOperation"

        fun backupData(backupDatabaseNames: Array<String>, applicationContext: Context?): Int {
            try {
                val contextWrapper = ContextWrapper(applicationContext)
                val exportDir = File(contextWrapper.dataDir, "database_backup")
                if (!exportDir.exists())
                    exportDir.mkdir()
                for (backupDatabaseName in backupDatabaseNames) {
                    val dbFile: File = applicationContext!!.getDatabasePath(backupDatabaseName)
                    val backupFile = File(exportDir, backupDatabaseName)
                    copyFile(dbFile, backupFile)
                    Log.d(TAG, "Database exported to " + backupFile.absolutePath)
                }
                return 0
            } catch (e: IOException) {
                Log.e(TAG, "Failed to export database", e)
            }
            return -1
        }


        fun restoreData(restoreDatabaseNames: Array<String>, applicationContext: Context?): Int {
            try {
                val contextWrapper = ContextWrapper(applicationContext)
                val importDir = File(contextWrapper.dataDir, "database_backup")
                if (!importDir.exists())
                    return 1
                for (restoreDatabaseName in restoreDatabaseNames) {
                    val dbFile: File = applicationContext!!.getDatabasePath(restoreDatabaseName)
                    val backupFile = File(importDir, restoreDatabaseName)
                    copyFile(backupFile, dbFile)
                    Log.d(TAG, "Database import from " + backupFile.absolutePath)
                }
                return 0
            } catch (e: IOException) {
                Log.e(TAG, "Failed to import database", e)
            }
            return -1
        }

        @Throws(IOException::class)
        private fun copyFile(source: File?, destination: File?) {
            val inputStream = FileInputStream(source)
            val outputStream = FileOutputStream(destination)
            val sourceChannel: FileChannel = inputStream.channel
            val destinationChannel: FileChannel = outputStream.channel
            sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel)
            sourceChannel.close()
            destinationChannel.close()
            inputStream.close()
            outputStream.close()
        }
    }

}