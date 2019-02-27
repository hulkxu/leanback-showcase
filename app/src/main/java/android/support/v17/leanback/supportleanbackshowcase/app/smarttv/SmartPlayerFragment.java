package android.support.v17.leanback.supportleanbackshowcase.app.smarttv;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.VideoFragment;
import android.support.v17.leanback.app.VideoFragmentGlueHost;
import android.support.v17.leanback.media.PlaybackGlue;
import android.support.v17.leanback.supportleanbackshowcase.R;
import android.support.v17.leanback.supportleanbackshowcase.app.media.ExoPlayerAdapter;
import android.support.v17.leanback.supportleanbackshowcase.app.media.PlaybackSeekDiskDataProvider;
import android.support.v17.leanback.supportleanbackshowcase.app.media.TvMetaData;
import android.support.v17.leanback.supportleanbackshowcase.app.media.VideoExampleActivity;
import android.support.v17.leanback.supportleanbackshowcase.app.media.VideoMediaPlayerGlue;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SmartPlayerFragment extends VideoFragment {
    public static final String TAG = "VideoWithExoPlayer";
    private VideoMediaPlayerGlue<ExoPlayerAdapter> mMediaPlayerGlue;
    final VideoFragmentGlueHost mHost = new VideoFragmentGlueHost(this);
    private int mCurrentSourceIndex;
    private String[] mSourcePathArray;
    private TextView mDebugTextView;
    private LinearLayout mLinearLayout;

    private void playWhenReady(PlaybackGlue glue) {
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
        showDebugTextView();
    }

    public void showDebugTextView() {
        mLinearLayout.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
            disableDebugTextView();
        }, 5000);
    }

    private void disableDebugTextView() {
        mLinearLayout.setVisibility(View.INVISIBLE);
    }

    AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener
            = state -> {
    };

    @Override
    protected void onError(int errorCode, CharSequence errorMessage) {
        Log.d(TAG, "" + errorMessage);
        Log.d(TAG, "current source:" + mSourcePathArray[mCurrentSourceIndex]);
        Toast.makeText(getActivity()
                , "play " + mSourcePathArray[mCurrentSourceIndex] + " fail!"
                , Toast.LENGTH_LONG).show();
        nextVideoSource(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDebugTextView = getActivity().findViewById(R.id.debug_text_view);
        mLinearLayout = getActivity().findViewById(R.id.debug_layout_out);
        ExoPlayerAdapter playerAdapter = new ExoPlayerAdapter(getActivity());
        playerAdapter.setDebugTextView(mDebugTextView);
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

    public void nextVideoSource(boolean isFinish) {
        Log.d(TAG, "nextVideoSource");
        if (mCurrentSourceIndex < mSourcePathArray.length - 1) {
            mCurrentSourceIndex++;
            mMediaPlayerGlue.getPlayerAdapter().reset();
            mMediaPlayerGlue.getPlayerAdapter()
                    .setDataSource(Uri.parse(mSourcePathArray[mCurrentSourceIndex]));
            PlaybackSeekDiskDataProvider.setDemoSeekProvider(mMediaPlayerGlue);
            playWhenReady(mMediaPlayerGlue);
        } else {
            if (isFinish) {
                getActivity().finish();
            }
        }
    }
}
