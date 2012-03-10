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

import com.misocast.widget.GridViewSpecial;
import com.misocast.widget.SimpleDrawAdapter;
import com.misocast.widget.SimpleListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class FooterGridviewSampleActivity extends Activity {
    private ArrayList<UserData> mAllImages;

    private SimpleDrawAdapter adapter;
    private GridViewSpecial mGvs;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        adapter = new SimpleDrawAdapter(this, R.drawable.ic_missing_thumbnail_picture);
        mGvs = (GridViewSpecial) findViewById(R.id.grid);
        mGvs.setListener(new SimpleListener() {
            @Override
            public void onImageClicked(int index) {
                Toast.makeText(FooterGridviewSampleActivity.this,
                        "onImageClicked(index=" + index + ")", Toast.LENGTH_SHORT).show();
            }});

        // add footers
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
}