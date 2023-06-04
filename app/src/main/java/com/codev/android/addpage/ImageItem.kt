package com.codev.android.addpage

import android.net.Uri

data class ImageItem(
    val imageUri: Uri,
    val imageCopyPath: String,
    var imageUrl: String = "",
)
