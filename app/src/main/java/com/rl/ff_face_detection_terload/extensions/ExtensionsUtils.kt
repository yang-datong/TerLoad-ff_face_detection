package com.rl.ff_face_detection_terload.extensions

import android.content.Context
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream


fun String.isValidUserName() = this.matches(Regex("^[a-zA-Z]\\w{2,15}$"))
fun String.isValidPassword() = this.matches(Regex("^.{3,20}$"))


fun zipDirectory(directoryPath: String, zipPath: String) {
    val directory = File(directoryPath)
    val zipFile = File(zipPath)

    ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { zipOut ->
        zip(directory, directory.name, zipOut)
    }
}

private fun zip(fileToZip: File, fileName: String, zipOut: ZipOutputStream) {
    if (fileToZip.isHidden) {
        return
    }
    if (fileToZip.isDirectory) {
        if (fileName.endsWith("/")) {
            zipOut.putNextEntry(ZipEntry(fileName))
            zipOut.closeEntry()
        } else {
            zipOut.putNextEntry(ZipEntry("$fileName/"))
            zipOut.closeEntry()
        }
        val children = fileToZip.listFiles()
        for (childFile in children) {
            zip(childFile, fileName + "/" + childFile.name, zipOut)
        }
        return
    }
    FileInputStream(fileToZip).use { fi ->
        BufferedInputStream(fi).use { origin ->
            val entry = ZipEntry(fileName)
            zipOut.putNextEntry(entry)
            origin.copyTo(zipOut, 1024)
        }
    }
}


fun unZipToDirectory(zipFilePath: String, destDirectoryPath: String) {
    val destDir = File(destDirectoryPath)
    if (!destDir.exists()) {
        destDir.mkdir()
    }

    ZipInputStream(BufferedInputStream(FileInputStream(zipFilePath))).use { zipIn ->
        var entry: ZipEntry? = zipIn.nextEntry
        while (entry != null) {
            val filePath = destDirectoryPath + File.separator + entry.name
            if (!entry.isDirectory) {
                extractFile(zipIn, filePath)
            } else {
                val dir = File(filePath)
                dir.mkdir()
            }
            zipIn.closeEntry()
            entry = zipIn.nextEntry
        }
    }
}

private fun extractFile(zipIn: ZipInputStream, filePath: String) {
    BufferedOutputStream(FileOutputStream(filePath)).use { bos ->
        val bytesIn = ByteArray(1024)
        var read = 0
        while (zipIn.read(bytesIn).also { read = it } != -1) {
            bos.write(bytesIn, 0, read)
        }
    }
}