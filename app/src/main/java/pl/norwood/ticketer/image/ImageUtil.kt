package pl.norwood.ticketer.image

import android.content.Context

public fun saveImageToInternalStorage(context: Context, uri: android.net.Uri): String {
    val inputStream = context.contentResolver.openInputStream(uri)
    val fileName = "guest_${System.currentTimeMillis()}.jpg"
    val file = java.io.File(context.filesDir, fileName)

    inputStream?.use { input ->
        file.outputStream().use { output ->
            input.copyTo(output)
        }
    }
    return file.absolutePath
}
