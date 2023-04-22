package com.rl.ff_face_detection_terload.database

import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import com.rl.ff_face_detection_terload.extensions.unZipToDirectory
import com.rl.ff_face_detection_terload.extensions.zipDirectory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel

class DataOperation {

    companion object {
        private const val TAG = "DataOperation"

        fun backupFaceData(backupDataNames: Array<String>, applicationContext: Context?): Int {
            try {
                val contextWrapper = ContextWrapper(applicationContext)
                val exportDir = File(contextWrapper.externalCacheDir, "face_data_backup")
                if (!exportDir.exists())
                    exportDir.mkdir()
                val workPath = applicationContext!!.filesDir.absolutePath
                for (backupDataName in backupDataNames) {
                    var backupFile: File
                    if (backupDataName.endsWith("/")) {
                        //Directory
                        backupFile = File(exportDir, "${backupDataName.substring(0, backupDataName.length - 1)}.zip")
                        zipDirectory("${workPath}/${backupDataName}", backupFile.absolutePath)
                        Log.d(TAG, "FaceData exported to " + backupFile.absolutePath)
                    } else {
                        //File
                        backupFile = File(exportDir, backupDataName)
                        if (copyFile(File("${workPath}/${backupDataName}"), backupFile) != 0) return -1
                    }
                    Log.d(TAG, "FaceData exported to " + backupFile.absolutePath)
                }
                return 0
            } catch (e: IOException) {
                Log.e(TAG, "Failed to export FaceData", e)
            }
            return -1
        }


        fun restoreFaceData(restoreDataNames: Array<String>, applicationContext: Context?): Int {
            try {
                val contextWrapper = ContextWrapper(applicationContext)
                val exportDir = File(contextWrapper.externalCacheDir, "face_data_backup")
                if (!exportDir.exists())
                    return 1
                val workPath = applicationContext!!.filesDir.absolutePath
                for (restoreDataName in restoreDataNames) {
                    var restoreFile: File
                    if (restoreDataName.endsWith("/")) {
                        //Directory
                        restoreFile = File(exportDir, "${restoreDataName.substring(0, restoreDataName.length - 1)}.zip")
                        unZipToDirectory(restoreFile.absolutePath, workPath)
                        Log.d(TAG, "FaceData import from " + restoreFile.absolutePath)
                    } else {
                        //File
                        restoreFile = File(exportDir, restoreDataName)
                        if (copyFile(restoreFile, File("${workPath}/${restoreDataName}")) != 0) return 1
                    }
                    Log.d(TAG, "FaceData import from " + restoreFile.absolutePath)
                }
                return 0
            } catch (e: IOException) {
                Log.e(TAG, "Failed to export FaceData", e)
            }
            return -1
        }

        fun backupDataBase(backupDatabaseNames: Array<String>, applicationContext: Context?): Int {
            try {
                val contextWrapper = ContextWrapper(applicationContext)
                val exportDir = File(contextWrapper.externalCacheDir, "database_backup")
                if (!exportDir.exists())
                    exportDir.mkdir()
                for (backupDatabaseName in backupDatabaseNames) {
                    val dbFile: File = applicationContext!!.getDatabasePath(backupDatabaseName)
                    val backupFile = File(exportDir, backupDatabaseName)
                    if (copyFile(dbFile, backupFile) != 0) return 1
                    Log.d(TAG, "Database exported to " + backupFile.absolutePath)
                }
                return 0
            } catch (e: IOException) {
                Log.e(TAG, "Failed to export database", e)
            }
            return -1
        }

        fun restoreDataBase(restoreDatabaseNames: Array<String>, applicationContext: Context?): Int {
            try {
                val contextWrapper = ContextWrapper(applicationContext)
                val importDir = File(contextWrapper.externalCacheDir, "database_backup")
                if (!importDir.exists()) return 1
                for (restoreDatabaseName in restoreDatabaseNames) {
                    val dbFile: File = applicationContext!!.getDatabasePath(restoreDatabaseName)
                    val backupFile = File(importDir, restoreDatabaseName)
                    if (copyFile(backupFile, dbFile) != 0) return 1
                    Log.d(TAG, "Database import from " + backupFile.absolutePath)
                }
                return 0
            } catch (e: IOException) {
                Log.d(TAG, "Failed to import database")
            }
            return -1
        }

        private fun copyFile(source: File, destination: File?): Int {
            if (!source.exists()) return -1
            val inputStream = FileInputStream(source)
            val outputStream = FileOutputStream(destination)
            val sourceChannel: FileChannel = inputStream.channel
            val destinationChannel: FileChannel = outputStream.channel
            sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel)
            sourceChannel.close()
            destinationChannel.close()
            inputStream.close()
            outputStream.close()
            return 0
        }
    }

}