/*
 * Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amplifyframework.video.ui;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.MediaController;
import android.widget.VideoView;
import androidx.annotation.NonNull;

import com.amplifyframework.analytics.AnalyticsCategory;
import com.amplifyframework.analytics.AnalyticsEvent;
import com.amplifyframework.video.resources.VideoResource;
import com.amplifyframework.video.resources.VideoResourceType;

import java.util.Objects;
import java.util.Optional;

/**
 * A video player for Amplify Video.
 */
public abstract class AWSVideoPlayer extends VideoPlayer {

    private VideoView videoView;
    private MediaController mediaController;

    // Analytics.
    private Optional<AnalyticsCategory> analyticsCategory = Optional.empty();
    private boolean autoplay = true;

    /**
     * Create a new {@link AWSVideoPlayer} composed of a {@link VideoView}.
     * @param videoView The primary {@link VideoView} used by the player.
     */
    protected AWSVideoPlayer(@NonNull VideoView videoView) {
        setVideoView(videoView);
    }

    private void setVideoView(@NonNull VideoView videoView) {
        this.videoView = Objects.requireNonNull(videoView);
    }

    /**
     * Record player actions to an Amplify Analytics provider.
     * @param amplifyAnalytics {@link AnalyticsCategory} for AWS PinPoint Analytics, etc.
     */
    public void addAnalytics(AnalyticsCategory amplifyAnalytics) {
        this.analyticsCategory = Optional.of(amplifyAnalytics);
    }

    /**
     * Use the Amplify Analytics category.
     * @return An {@link AnalyticsCategory} or null.
     */
    protected Optional<AnalyticsCategory> getAnalyticsCategory() {
        return analyticsCategory;
    }

    /**
     * Set the URI to use as the video source.
     * @param uri A valid {@link Uri}.
     */
    public void setSourceURI(Uri uri) {
        videoView.setVideoURI(uri);
        if (autoplay) {
            videoView.setOnPreparedListener(player -> player.start());
        }
    }

    /**
     * Access the underlying {@link VideoView}.
     * @return The {@link VideoView} managed by this player.
     */
    public VideoView getVideoView() {
        return videoView;
    }

    /**
     * Access the underlying {@link MediaController}.
     * @return The {@link MediaController} used by the {@link VideoView}. May be null.
     */
    public MediaController getMediaController() {
        return mediaController;
    }

    /**
     * Pause video playback.
     */
    public void pause() {
        videoView.pause();

        Log.d("AWSVIDEO", "Paused.");

        analyticsCategory.ifPresent(analytics -> {
            AnalyticsEvent pause = AnalyticsEvent.builder()
                    .name("VideoPause")
                    .addProperty("StreamType", getVideoResource().getType().toString())
                    .addProperty("CurrentPlaybackPosition", getVideoView().getCurrentPosition())
                    .addProperty("StreamLength", getVideoResource().getType().equals(VideoResourceType.LIVE) ? 0
                            : getVideoView().getDuration())
                    .build();
            analytics.recordEvent(pause);
        });
    }

    /**
     * Start video playback.
     */
    public void start() {
        videoView.start();

        Log.d("AWSVIDEO", "Playing.");

        analyticsCategory.ifPresent(analytics -> {
            AnalyticsEvent start = AnalyticsEvent.builder()
                    .name("VideoStart")
                    .addProperty("StreamType", getVideoResource().getType().toString())
                    .addProperty("CurrentPlaybackPosition", getVideoView().getCurrentPosition())
                    .addProperty("StreamLength", getVideoResource().getType().equals(VideoResourceType.LIVE) ? 0 :
                            getVideoView().getDuration())
                    .build();
            analytics.recordEvent(start);
        });
    }

    /**
     * Add controls to this player.
     */
    public void addControls() {
        // TODO: controls don't seem to show up when added this way
        MediaController mediaController = new MediaController(videoView.getContext());
        setMediaController(mediaController);
    }

    /**
     * Remove controls from this player.
     */
    public void removeControls() {
        setMediaController(null);
    }

    private void setMediaController(MediaController mediaController) {
        this.mediaController = mediaController;
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
    }

    /**
     * Configure any action listeners.
     */
    @SuppressLint("ClickableViewAccessibility")
    protected void configureListeners() {
        videoView.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (videoView.isPlaying()) {
                    pause();
                } else {
                    start();
                }
                return true;
            }
            return false;
        });
    }

    /**
     * Get the underlying {@link VideoResource} managed by this player.
     * @return An Amplify {@link VideoResource}.
     */
    public abstract VideoResource getVideoResource();

}
