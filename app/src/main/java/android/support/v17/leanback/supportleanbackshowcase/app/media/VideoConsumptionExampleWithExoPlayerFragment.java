/*
 * Copyright (C) 2016 The Android Open Source Project
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

package android.support.v17.leanback.supportleanbackshowcase.app.media;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v17.leanback.app.VideoFragment;
import android.support.v17.leanback.app.VideoFragmentGlueHost;
import android.support.v17.leanback.media.PlaybackGlue;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.util.Log;


public class VideoConsumptionExampleWithExoPlayerFragment
        extends VideoFragment {

    public static final String TAG = "VideoWithExoPlayer";
    private VideoMediaPlayerGlue<ExoPlayerAdapter> mMediaPlayerGlue;
    final VideoFragmentGlueHost mHost = new VideoFragmentGlueHost(this);
    private int mCurrentSourceIndex;
    private String[] mSourcePathArray;

    static void playWhenReady(PlaybackGlue glue) {
        if (glue.isPrepared()) {
            glue.play();
        } else {
            glue.addPlayerCallback(new PlaybackGlue.PlayerCallback() {
                @Override
                public void onPreparedStateChanged(PlaybackGlue glue) {
                    if (glue.isPrepared()) {
                        glue.removePlayerCallback(this);
                        glue.play();
                    }
                }
            });
        }
    }

    AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener
            = state -> {
    };

    @Override
    protected void onError(int errorCode, CharSequence errorMessage) {
        Log.d(TAG, "" + errorMessage);
        Log.d(TAG, "current source:" + mSourcePathArray[mCurrentSourceIndex]);
        if (mCurrentSourceIndex < mSourcePathArray.length - 1) {
            mCurrentSourceIndex++;
            mMediaPlayerGlue.getPlayerAdapter()
                    .setDataSource(Uri.parse(mSourcePathArray[mCurrentSourceIndex]));
            PlaybackSeekDiskDataProvider.setDemoSeekProvider(mMediaPlayerGlue);
            playWhenReady(mMediaPlayerGlue);
        } else {
            getActivity().finish();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ExoPlayerAdapter playerAdapter = new ExoPlayerAdapter(getActivity());
        playerAdapter.setAudioStreamType(AudioManager.USE_DEFAULT_STREAM_TYPE);
        mMediaPlayerGlue = new VideoMediaPlayerGlue(getActivity(), playerAdapter);
        mMediaPlayerGlue.setHost(mHost);
        AudioManager audioManager = (AudioManager) getActivity()
                .getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN)
                != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.w(TAG, "video player cannot obtain audio focus!");
        }
        mMediaPlayerGlue.setMode(PlaybackControlsRow.RepeatAction.NONE);
        TvMetaData intentMetaData = getActivity().getIntent().getParcelableExtra(
                VideoExampleActivity.TAG);
        if (intentMetaData != null) {
            mMediaPlayerGlue.setTitle(intentMetaData.getMediaTitle());
            mMediaPlayerGlue.setSubtitle(intentMetaData.getMediaArtistName());
            mSourcePathArray = intentMetaData.getMediaSourcePath();
            //mMediaPlayerGlue.getPlayerAdapter().setDataSource(intentMetaData.getMediaSourcePath());
            mMediaPlayerGlue.getPlayerAdapter().setDataSource(Uri.parse(mSourcePathArray[0]));
        } else {
            Log.w(TAG, "the metadata is null");
        }
        PlaybackSeekDiskDataProvider.setDemoSeekProvider(mMediaPlayerGlue);
        playWhenReady(mMediaPlayerGlue);
        setBackgroundType(BG_LIGHT);
    }

    @Override
    public void onPause() {
        if (mMediaPlayerGlue != null) {
            mMediaPlayerGlue.pause();
        }
        super.onPause();
    }

}
