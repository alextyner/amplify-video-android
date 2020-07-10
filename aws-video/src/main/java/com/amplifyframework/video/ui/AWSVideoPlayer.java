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

import android.net.Uri;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;
import androidx.annotation.NonNull;

import com.amplifyframework.video.event.PauseEvent;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * A video player for Amplify Video.
 */
public class AWSVideoPlayer extends VideoPlayer {

    private VideoView videoView;
    private MediaController mediaController;

    // Event callbacks
    private BiConsumer<View, PauseEvent> onPause;

    /**
     * Create a new {@link AWSVideoPlayer} composed of a {@link VideoView}.
     * @param videoView The primary {@link VideoView} used by the player.
     */
    public AWSVideoPlayer(@NonNull VideoView videoView) {
        attach(videoView);
    }

    private void attach(@NonNull VideoView videoView) {
        this.videoView = Objects.requireNonNull(videoView);
    }

    /**
     * Set a callback for when the player is paused.
     * @param callback Consumer called on a pause event.
     */
    public void onPause(BiConsumer<View, PauseEvent> callback) {
        onPause = Objects.requireNonNull(callback);
    }

    /**
     * Set the URI to use as the video source.
     * @param uri A valid {@link Uri}.
     */
    public void setSourceURI(Uri uri) {
        videoView.setVideoURI(uri);
    }

    /**
     * Access the underlying {@link VideoView}.
     * @return The {@link VideoView} managed by this player.
     */
    public VideoView getVideoView() {
        return videoView;
    }

    /**
     * Pause video playback.
     */
    public void pause() {
        videoView.pause();
        onPause.accept(videoView, new PauseEvent());
    }

    /**
     * Add controls to this player.
     */
    public void addControls() {
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
    }

}
