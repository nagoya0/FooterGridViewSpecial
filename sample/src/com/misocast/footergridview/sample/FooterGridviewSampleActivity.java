/*
 * Copyright (C) 2007 The Android Open Source Project
 * Copyright (C) 2012 Nagoya0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.misocast.footergridview.sample;

import java.util.ArrayList;

import com.misocast.widget.GridItem;
import com.misocast.widget.GridViewSpecial;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class FooterGridviewSampleActivity extends Activity implements
        GridViewSpecial.Listener {
    private ArrayList<UserData> mAllImages;

    private GridViewSpecialAdapter adapter;
    private GridViewSpecial mGvs;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        adapter = new GridViewSpecialAdapter();
        mGvs = (GridViewSpecial) findViewById(R.id.grid);
        mGvs.setListener(this);

        LayoutInflater inflator = getLayoutInflater();
        View footer1 = inflator.inflate(R.layout.footer1, null);
        mGvs.addView(footer1);
        View footer2 = inflator.inflate(R.layout.footer2, null);
        Button button = (Button) footer2;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FooterGridviewSampleActivity.this,
                        "footer clicked.", Toast.LENGTH_SHORT).show();
            }});
        mGvs.addView(footer2);

        makeImageList();

        registerForContextMenu(mGvs);
    }

    private void makeImageList() {
        mAllImages = new ArrayList<UserData>();
        for(int i = 0; i < 30; i++) {
            mAllImages.add(new UserData(this));
        }
    }

    public void refresh() {
        mGvs.stop(false);
        mGvs.setImageList(mAllImages);
        mGvs.setDrawAdapter(adapter);
        mGvs.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGvs.stop(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        switch(v.getId()) {
            case R.id.grid:
                int currentSelection = mGvs.getCurrentSelection();
                if(currentSelection > GridViewSpecial.INDEX_NONE) {
                    menu.setHeaderTitle("ContextMenu Test");
                    menu.add("menu");
                }
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int currentSelection = mGvs.getCurrentSelection();
        Toast.makeText(FooterGridviewSampleActivity.this,
                "onContextItemSelected(index=" + currentSelection + ")",
                Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onImageClicked(int index) {
        Toast.makeText(FooterGridviewSampleActivity.this,
                "onImageClicked(index=" + index + ")", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onImageTapped(int index) {
        onImageClicked(index);
    }

    @Override
    public void onLayoutComplete(boolean changed) {
    }

    @Override
    public void onScroll(float scrollPosition) {
    }

    class GridViewSpecialAdapter implements GridViewSpecial.DrawAdapter {
        // mSrcRect and mDstRect are only used in drawImage, but we put them as
        // instance variables to reduce the memory allocation overhead because
        // drawImage() is called a lot.
        private final Rect mSrcRect = new Rect();
        private final Rect mDstRect = new Rect();

        private final Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);

        @Override
        public void drawImage(Canvas canvas, GridItem image,
                Bitmap b, int xPos, int yPos, int w, int h) {
            if (b != null) {
                // if the image is close to the target size then crop,
                // otherwise scale both the bitmap and the view should be
                // square but I suppose that could change in the future.

                int bw = b.getWidth();
                int bh = b.getHeight();

                int deltaW = bw - w;
                int deltaH = bh - h;

                if (deltaW >= 0 && deltaW < 10 &&
                    deltaH >= 0 && deltaH < 10) {
                    int halfDeltaW = deltaW / 2;
                    int halfDeltaH = deltaH / 2;
                    mSrcRect.set(0 + halfDeltaW, 0 + halfDeltaH,
                            bw - halfDeltaW, bh - halfDeltaH);
                    mDstRect.set(xPos, yPos, xPos + w, yPos + h);
                    canvas.drawBitmap(b, mSrcRect, mDstRect, null);
                } else {
                    mSrcRect.set(0, 0, bw, bh);
                    mDstRect.set(xPos, yPos, xPos + w, yPos + h);
                    canvas.drawBitmap(b, mSrcRect, mDstRect, mPaint);
                }
            } else {
                // If the thumbnail cannot be drawn, put up an error icon
                // instead
                Bitmap error = getErrorBitmap(image);
                int width = error.getWidth();
                int height = error.getHeight();
                mSrcRect.set(0, 0, width, height);
                int left = (w - width) / 2 + xPos;
                int top = (w - height) / 2 + yPos;
                mDstRect.set(left, top, left + width, top + height);
                canvas.drawBitmap(error, mSrcRect, mDstRect, null);
            }
        }

        @Override
        public void drawDecoration(Canvas canvas, GridItem image, int xPos,
                int yPos, int w, int h) {
        }

        @Override
        public boolean needsDecoration() {
            return false;
        }

        private Bitmap mMissingImageThumbnailBitmap;

        public Bitmap getErrorBitmap(GridItem image) {
            if (mMissingImageThumbnailBitmap == null) {
                mMissingImageThumbnailBitmap = BitmapFactory.decodeResource(
                        getResources(),
                        R.drawable.ic_missing_thumbnail_picture);
            }
            return mMissingImageThumbnailBitmap;
        }
    }
}