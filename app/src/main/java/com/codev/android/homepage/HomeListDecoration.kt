package com.codev.android.homepage

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HomeListDecoration(var px: Int): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.right = px
    }


}