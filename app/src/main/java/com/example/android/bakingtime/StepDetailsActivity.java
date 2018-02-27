package com.example.android.bakingtime;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bakingtime.data.RecipesDataContract;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, ExoPlayer.EventListener{
    @BindView(R.id.description) TextView mDescriptionTextView;
    @BindView(R.id.placeholder) TextView mNoVideoMessage;
    @BindView(R.id.player_view) SimpleExoPlayerView mPlayerView;
    private SimpleExoPlayer mExoPlayer;
    @BindView(R.id.move_to_previous_button) ImageButton mPreviousButton;
    @BindView(R.id.move_to_next_button) ImageButton mNextButton;
    private int stepId;
    private static final int STEP_INFO_LOADER_ID = 989;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private int firstId;
    private int lastId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_details);
        ButterKnife.bind(this);
        Intent intentThatStartedThisActivity = getIntent();
        if(intentThatStartedThisActivity.hasExtra(RecipeDetailsActivity.ID_TAG)){
            stepId = intentThatStartedThisActivity.getIntExtra(RecipeDetailsActivity.ID_TAG,0);
            firstId = intentThatStartedThisActivity.getIntExtra(RecipeDetailsActivity.START_ID_TAG,0);
            lastId = intentThatStartedThisActivity.getIntExtra(RecipeDetailsActivity.END_ID_TAG,0);
            if(stepId == firstId){mPreviousButton.setVisibility(View.GONE);}
            if(stepId == lastId){mNextButton.setVisibility(View.GONE);}
        }
        getLoaderManager().initLoader(STEP_INFO_LOADER_ID,null,this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mExoPlayer != null) {
            mMediaSession.setActive(false);
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    public void moveToNextStep(View view){
        if(stepId < lastId) {
            Intent restartThisActivityIntent = new Intent(this, StepDetailsActivity.class);
            restartThisActivityIntent.putExtra(RecipeDetailsActivity.ID_TAG, stepId + 1);
            restartThisActivityIntent.putExtra(RecipeDetailsActivity.START_ID_TAG, firstId);
            restartThisActivityIntent.putExtra(RecipeDetailsActivity.END_ID_TAG, lastId);
            startActivity(restartThisActivityIntent);
        }
    }

    public void moveToPreviousStep(View view){
        if(stepId > firstId) {
            Intent restartThisActivityIntent = new Intent(this, StepDetailsActivity.class);
            restartThisActivityIntent.putExtra(RecipeDetailsActivity.ID_TAG, stepId - 1);
            restartThisActivityIntent.putExtra(RecipeDetailsActivity.START_ID_TAG, firstId);
            restartThisActivityIntent.putExtra(RecipeDetailsActivity.END_ID_TAG, lastId);
            startActivity(restartThisActivityIntent);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        switch (loaderId){
            case STEP_INFO_LOADER_ID:
                Uri stepsUri = RecipesDataContract.StepEntry.STEPS_CONTENT_URI;
                String selection = "_id=?";
                String id = String.valueOf(stepId);
                String[] selectionArgs = {id};
                return new CursorLoader(this,stepsUri,RecipeDetailsActivity.STEPS_PROJECTION,selection,selectionArgs,null);
            default:
                throw new RuntimeException("Loader not implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor != null) {
            DatabaseUtils.dumpCursor(cursor);
            cursor.moveToFirst();
            String description = cursor.getString(3);
            String videoUrl = cursor.getString(4);
            mDescriptionTextView.setText(description);
            if(videoUrl == null || videoUrl.isEmpty() || videoUrl.equalsIgnoreCase("null")) {
                mPlayerView.setVisibility(View.GONE);
                mNoVideoMessage.setVisibility(View.VISIBLE);
            }else{
                mNoVideoMessage.setVisibility(View.GONE);
                mPlayerView.setVisibility(View.VISIBLE);
                Uri videoUri = Uri.parse(videoUrl);
                initializeMediaSession();
                initializePlayer(videoUri);
            }
        }else{
            Log.d(StepDetailsActivity.class.getSimpleName(),"cursor is empty");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    private void initializePlayer(Uri uri){
        Log.d(StepDetailsActivity.class.getSimpleName(),uri.toString());
        if(mExoPlayer == null){
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(this,trackSelector,loadControl);
            mPlayerView.setPlayer(mExoPlayer);
            mExoPlayer.addListener(this);
            String userAgent = Util.getUserAgent(this,"Baking Time");
            MediaSource mediaSource = new ExtractorMediaSource(uri, new DefaultDataSourceFactory(this,userAgent),new DefaultExtractorsFactory(),null,null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    private void initializeMediaSession(){
        mMediaSession = new MediaSessionCompat(this,StepDetailsActivity.class.getSimpleName());
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setMediaButtonReceiver(null);
        mStateBuilder = new PlaybackStateCompat.Builder().setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE
                | PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
        mMediaSession.setCallback(new mySessionCallback());
        mMediaSession.setActive(true);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {}

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {}

    @Override
    public void onLoadingChanged(boolean isLoading) {}

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if((playbackState == ExoPlayer.STATE_READY) && playWhenReady){
            Log.d(StepDetailsActivity.class.getSimpleName(),"playing");
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,mExoPlayer.getCurrentPosition(),1F);
        }else if(playbackState==ExoPlayer.STATE_READY){
            Log.d(StepDetailsActivity.class.getSimpleName(),"paused");
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,mExoPlayer.getCurrentPosition(),1F);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {}

    @Override
    public void onPositionDiscontinuity() {}

    private class mySessionCallback extends MediaSessionCompat.Callback{
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

    public static class MediaReceiver extends BroadcastReceiver{
        public MediaReceiver(){}
        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession,intent);
        }
    }

}
