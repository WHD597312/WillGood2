package com.peihou.willgood2.custom;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Administrator on 2017/3/13 0013.
 * E-Mail：543441727@qq.com
 */

public abstract class OnRecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private GestureDetectorCompat mGestureDetector;
    private RecyclerView recyclerView;
    private Context context;

    protected static final float FLIP_DISTANCE = 50;
    SharedPreferences preferences;

    public OnRecyclerItemClickListener(RecyclerView recyclerView, Context context) {
        this.recyclerView = recyclerView;
        this.context=context;
        preferences=context.getSharedPreferences("direction",Context.MODE_PRIVATE);
        mGestureDetector = new GestureDetectorCompat(recyclerView.getContext(), new ItemTouchHelperGestureListener());
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    private class ItemTouchHelperGestureListener extends GestureDetector.SimpleOnGestureListener {


        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (child != null) {
                RecyclerView.ViewHolder vh = recyclerView.getChildViewHolder(child);
                onItemClick(vh);
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (child != null) {
                RecyclerView.ViewHolder vh = recyclerView.getChildViewHolder(child);
                onItemLongClick(vh);
            }
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > FLIP_DISTANCE) {

                SharedPreferences.Editor editor=preferences.edit();
                editor.putString("direction","left").commit();

                Log.i("TAG", "<--- left, left, go go go");
                return true;
            }
            if (e2.getX() - e1.getX() > FLIP_DISTANCE) {
                Log.i("TAG", "right, right, go go go --->");  //忽然觉得这个log好智障...
                SharedPreferences.Editor editor=preferences.edit();
                editor.putString("direction","right").commit();
                return true;
            }
            if (e1.getY() - e2.getY() > FLIP_DISTANCE) {
                Log.i("TAG", "向上滑...");
                return true;
            }
            if (e2.getY() - e1.getY() > FLIP_DISTANCE) {
                Log.i("TAG", "向下滑...");
                return true;
            }

            Log.d("TAG", e2.getX() + " " + e2.getY());

            return false;
        }
    }

    public abstract void onItemClick(RecyclerView.ViewHolder vh);

    public abstract void onItemLongClick(RecyclerView.ViewHolder vh);
}