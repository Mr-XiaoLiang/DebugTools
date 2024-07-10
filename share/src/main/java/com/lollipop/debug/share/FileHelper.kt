package com.lollipop.debug.share

import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.io.OutputStream

object FileHelper {

    fun from(input: () -> InputStream): FileWriter {
        return FileWriter().from(input)
    }

    fun fromString(content: String): FileWriter {
        return FileWriter().fromString(content)
    }

    fun fromFile(file: File): FileWriter {
        return FileWriter().fromFile(file)
    }

    fun from(provider: () -> Any): ListWriter {
        return ListWriter().from(provider)
    }

    fun fromList(list: List<Any>): ListWriter {
        return ListWriter().fromList(list)
    }

    private class OutDelegate {
        var output: () -> OutputStream = { ByteArrayOutputStream() }
            private set

        fun to(output: () -> OutputStream) {
            this.output = output
        }

        fun toFile(file: File) {
            to {
                if (file.exists()) {
                    file.delete()
                }
                file.parentFile?.let {
                    if (!it.exists()) {
                        it.mkdirs()
                    }
                }
                file.outputStream()
            }
        }

        fun toString(outputSteam: ByteArrayOutputStream) {
            to { outputSteam }
        }

        fun toMediaStore(
            context: Context,
            displayName: String,
            mimeType: String
        ) {
            to {
                val contentResolver = context.contentResolver
                val contentValues = android.content.ContentValues()
                contentValues.put(
                    MediaStore.Images.Media.DISPLAY_NAME,
                    displayName
                )
                contentValues.put(
                    MediaStore.Images.Media.MIME_TYPE,
                    mimeType
                )
                contentValues.put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS
                )
                val volumeName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.VOLUME_EXTERNAL
                } else {
                    "external"
                }
                val uri = contentResolver.insert(
                    MediaStore.Files.getContentUri(volumeName),
                    contentValues
                )
                contentResolver.openOutputStream(uri!!)!!
            }
        }
    }

    class FileWriter {

        private var input: () -> InputStream = { ByteArrayInputStream(ByteArray(0)) }
        private val outDelegate = OutDelegate()

        fun to(output: () -> OutputStream): FileWriter {
            this.outDelegate.to(output)
            return this
        }

        fun toFile(file: File): FileWriter {
            outDelegate.toFile(file)
            return this
        }

        fun toString(outputSteam: ByteArrayOutputStream): FileWriter {
            outDelegate.toString(outputSteam)
            return this
        }

        fun toMediaStore(
            context: Context,
            displayName: String,
            mimeType: String = "text/plain"
        ): FileWriter {
            outDelegate.toMediaStore(context, displayName, mimeType)
            return this
        }

        fun from(input: () -> InputStream): FileWriter {
            this.input = input
            return this
        }

        fun fromString(content: String): FileWriter {
            return from { ByteArrayInputStream(content.toByteArray(Charsets.UTF_8)) }
        }

        fun fromFile(file: File): FileWriter {
            return from { file.inputStream() }
        }

        private fun writeAndClose(input: InputStream, output: OutputStream) {
            try {
                val buffer = ByteArray(8192)
                while (true) {
                    val read = input.read(buffer)
                    if (read <= 0) {
                        break
                    }
                    output.write(buffer, 0, read)
                }
            } finally {
                try {
                    input.close()
                } catch (_: Throwable) {
                }
                try {
                    output.close()
                } catch (_: Throwable) {
                }
            }
        }

        private fun write(input: () -> InputStream, output: () -> OutputStream): Result<Unit> {
            try {
                writeAndClose(input(), output())
                return Result.success(Unit)
            } catch (e: Throwable) {
                return Result.failure(e)
            }
        }

        fun write(): Result<Unit> {
            return write(input, outDelegate.output)
        }

    }

    class ListWriter {

        data object ItemEnd

        private var itemProvider: () -> Any = { ItemEnd }
        private var byteProvider: (Any) -> ByteArray = { it.toString().toByteArray(Charsets.UTF_8) }
        private val outDelegate = OutDelegate()

        fun to(output: () -> OutputStream): ListWriter {
            this.outDelegate.to(output)
            return this
        }

        fun toFile(file: File): ListWriter {
            outDelegate.toFile(file)
            return this
        }

        fun toString(outputSteam: ByteArrayOutputStream): ListWriter {
            outDelegate.toString(outputSteam)
            return this
        }

        fun toMediaStore(
            context: Context,
            displayName: String,
            mimeType: String = "text/plain"
        ): ListWriter {
            outDelegate.toMediaStore(context, displayName, mimeType)
            return this
        }

        fun from(provider: () -> Any): ListWriter {
            this.itemProvider = provider
            return this
        }

        fun fromList(list: List<Any>): ListWriter {
            var index = 0
            val tempList = ArrayList<Any>()
            tempList.addAll(list)
            return from {
                val item = if (index >= tempList.size) {
                    ItemEnd
                } else {
                    tempList[index]
                }
                index++
                return@from item
            }
        }

        fun map(provider: (Any) -> ByteArray): ListWriter {
            this.byteProvider = provider
            return this
        }

        fun mapString(provider: (Any) -> String): ListWriter {
            return map { provider(it).toByteArray(Charsets.UTF_8) }
        }

        private fun writeList(
            list: () -> Any,
            map: (Any) -> ByteArray,
            output: OutputStream
        ) {
            try {
                while (true) {
                    val item = list()
                    if (item == ItemEnd) {
                        break
                    }
                    output.write(map(item))
                }
                output.flush()
            } finally {
                try {
                    output.close()
                } catch (_: Throwable) {
                }
            }
        }

        private fun writeList(
            list: () -> Any,
            map: (Any) -> ByteArray,
            output: () -> OutputStream
        ): Result<Unit> {
            try {
                writeList(list, map, output())
                return Result.success(Unit)
            } catch (e: Throwable) {
                return Result.failure(e)
            }
        }

        fun write(): Result<Unit> {
            return writeList(itemProvider, byteProvider, outDelegate.output)
        }

    }

}