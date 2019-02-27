package android.support.v17.leanback.supportleanbackshowcase.app.smarttv;

/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import android.app.Activity;
import android.os.Bundle;
import android.support.v17.leanback.supportleanbackshowcase.R;

/**
 * TODO: Javadoc
 */
public class SmartTVMainActivity extends Activity {
    private final static String TAG = "SmartTVMainActivity";
    //private LinearLayout mDebugRootView;
    //private TextView mDbugTextView;
    //private SmartTVMainFragment mPlayerFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiy_smarttv);
        //mDebugRootView = findViewById(R.id.controls_root);
        //mDbugTextView = findViewById(R.id.debug_text_view);
        //mPlayerFragment = (SmartTVMainFragment)
        //getFragmentManager().findFragmentById(R.id.smartTvFragment);


    }
}
