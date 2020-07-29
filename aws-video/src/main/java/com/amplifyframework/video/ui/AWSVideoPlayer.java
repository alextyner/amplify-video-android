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
import android.widget.VideoView;
import androidx.annotation.NonNull;

import com.amplifyframework.analytics.AnalyticsCategory;
import com.amplifyframework.extended.video.resources.VideoResource;
import com.amplifyframework.extended.video.ui.VideoPlayer;

import java.util.Objects;
import java.util.Optional;

/**
 * A video player for Amplify Video.
 */
public abstract class AWSVideoPlayer extends VideoPlayer {

    private VideoView videoView;

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
     * Get the underlying {@link VideoResource} managed by this player.
     * @return An Amplify {@link VideoResource}.
     */
    public abstract VideoResource getVideoResource();

    /**
     * {@inheritDoc}
     */
    @Override
    public long getDuration() {
        return getVideoView().getDuration();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getCurrentPosition() {
        return getVideoView().getCurrentPosition();
    }

}
