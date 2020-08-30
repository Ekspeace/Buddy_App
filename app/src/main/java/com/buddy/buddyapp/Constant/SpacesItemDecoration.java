package com.buddy.buddyapp.Constant;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public final class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;


    public SpacesItemDecoration(int space)
    {
        this.space = space;
    }


    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = outRect.left = outRect.right = outRect.top;


    }
}
