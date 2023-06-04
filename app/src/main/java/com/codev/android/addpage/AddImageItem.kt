package com.codev.android.addpage

import android.net.Uri
import java.io.File

data class AddImageItem(
    val imageUri: Uri,
    val imageFile: File
)
