package com.peihou.willgood2.custom;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildPosition(view) != 0) {
            outRect.left = space;
            outRect.right = 0;
            outRect.bottom = 0;
            outRect.top = 0;
        }
    }
}
