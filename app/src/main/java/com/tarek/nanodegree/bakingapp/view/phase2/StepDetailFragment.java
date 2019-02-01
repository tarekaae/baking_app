package com.tarek.nanodegree.bakingapp.view.phase2;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.tarek.nanodegree.bakingapp.R;
import com.tarek.nanodegree.bakingapp.model.pojo.Recipe;
import com.tarek.nanodegree.bakingapp.model.pojo.Step;
import com.tarek.nanodegree.bakingapp.view.phase2.dummy.DummyContent;

import java.text.Normalizer;

/**
 * A fragment representing a single Step detail screen.
 * This fragment is either contained in a {@link StepListActivity}
 * in two-pane mode (on tablets) or a {@link StepDetailActivity}
 * on handsets.
 */
public class StepDetailFragment extends Fragment {

    private Recipe recipeDetails = null;
    private SimpleExoPlayerView mSimpleExoPlayerView;
    private SimpleExoPlayer mSimpleExoPlayer;
    private long playerPosition = -1;
    private String recipeDetailsAsString = "";
    int stepPosition = -1;
    //
    String stepDesc;
    String imagePath;
    String videoPath;

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_ITEM_ID_INT = "int_postition";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StepDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments().containsKey(ARG_ITEM_ID)) {

            getArguments().getString(ARG_ITEM_ID);
            recipeDetailsAsString = getArguments().getString(ARG_ITEM_ID);
            stepPosition = getArguments().getInt(ARG_ITEM_ID_INT);
            recipeDetails = new Gson().fromJson(recipeDetailsAsString, Recipe.class);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle("");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.step_detail, container, false);

        if (savedInstanceState != null) {
            playerPosition = savedInstanceState.getLong("ContentPosition");
            videoPath = savedInstanceState.getString("videoPath");
        }


        Step currentStep = null;
        if (stepPosition != -1 && !recipeDetailsAsString.equals("")) {
            recipeDetails = new Gson().fromJson(recipeDetailsAsString, Recipe.class);
            currentStep = recipeDetails.getSteps().get(stepPosition);
        }

        TextView textViewDesc = (TextView) rootView.findViewById(R.id.step_text);
        ImageView thumb = (ImageView) rootView.findViewById(R.id.step_image);


        mSimpleExoPlayerView = rootView.findViewById(R.id.player_view);
        mSimpleExoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(), R.drawable.ic_cake_gray_220_180dp));

        stepDesc = currentStep.getDescription();
        imagePath = currentStep.getThumbnailURL();
        videoPath = currentStep.getVideoURL();

        textViewDesc.setText(removeUnwantedChars(stepDesc));

        if (imagePath != null && !"".equals(imagePath)) {
            thumb.setVisibility(View.VISIBLE);

            Picasso.with(getActivity()).load(imagePath).fit().into(thumb);
        } else {
            thumb.setVisibility(View.GONE);
        }

        if (videoPath != null && !"".equals(videoPath)) {
            mSimpleExoPlayerView.setVisibility(View.VISIBLE);

            initializePlayer();
        } else {
            mSimpleExoPlayerView.setVisibility(View.GONE);

        }

        return rootView;
    }

    private void initializePlayer() {

        if (videoPath != null && !"".equals(videoPath)) {
            Uri mediaUri = Uri.parse(videoPath);


            if (mSimpleExoPlayer == null) {
                TrackSelector trackSelector = new DefaultTrackSelector();
                LoadControl loadControl = new DefaultLoadControl();
                mSimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
                mSimpleExoPlayerView.setPlayer(mSimpleExoPlayer);

                String userAgent = Util.getUserAgent(getActivity(), "BackingApp");
                MediaSource mediaSource = new ExtractorMediaSource(mediaUri,
                        new DefaultDataSourceFactory(getActivity(), userAgent),
                        new DefaultExtractorsFactory(), null, null);
                mSimpleExoPlayer.prepare(mediaSource);
                mSimpleExoPlayer.setPlayWhenReady(true);

                if (playerPosition != -1) {
                    mSimpleExoPlayer.seekTo(playerPosition);
                }
            }
        }
    }

    private void releasePlayer() {
        savePlayerState();
        if (mSimpleExoPlayer != null) {
            mSimpleExoPlayer.stop();
            mSimpleExoPlayer.release();
            playerPosition = mSimpleExoPlayer.getContentPosition();
            mSimpleExoPlayer = null;
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initializePlayer();
    }


    public String removeUnwantedChars(String src) {
        return Normalizer
                .normalize(src, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }

    private void savePlayerState() {
        if (mSimpleExoPlayer != null) {
            playerPosition = mSimpleExoPlayer.getContentPosition();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        savePlayerState();
        outState.putLong("ContentPosition", playerPosition);
        if (videoPath != null && !"".equals(videoPath)) {
            outState.putString("videoPath", videoPath);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (mSimpleExoPlayer != null) {
                releasePlayer();
            }
//            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }
}
