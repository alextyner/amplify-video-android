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

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.VideoView;

import com.amplifyframework.analytics.AnalyticsEvent;
import com.amplifyframework.extended.video.ui.VideoPlayer;

/**
 * View for AWS live video playback.
 */
public class AWSLiveVideoView extends VideoView {

    private AWSLiveVideoPlayer player;

    /**
     * Constructor for {@link AWSLiveVideoView}.
     * @param context application context
     * @param attrs attribute set
     */
    public AWSLiveVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Constructor for {@link AWSLiveVideoView}.
     * @param context application context
     * @param attrs attribute set
     * @param defStyleAttr attribute def
     */
    public AWSLiveVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        player = new AWSLiveVideoPlayer(this);
        player.addListener(new VideoPlayer.Listener() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void onStateChange(VideoPlayer.State newState) {
                Log.d("AWSLivePlayer", "State changed to: " + newState.toString());
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onPreparing(long totalDuration) {
                Log.i("AWSLivePlayer", "Preparing.");
                getPlayer().getAnalyticsCategory().ifPresent(analytics -> {
                    AnalyticsEvent preparing = AnalyticsEvent.builder()
                            .name("LiveStreamReady")
                            .addProperty("LiveStreamIdentifier", getPlayer().getVideoResource().getIdentifier())
                            .addProperty("TotalDuration", (double) totalDuration)
                            .build();
                    analytics.recordEvent(preparing);
                });
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onReady() {
                Log.i("AWSLivePlayer", "Ready.");
                getPlayer().getAnalyticsCategory().ifPresent(analytics -> {
                    AnalyticsEvent ready = AnalyticsEvent.builder()
                            .name("LiveStreamReady")
                            .addProperty("LiveStreamIdentifier", getPlayer().getVideoResource().getIdentifier())
                            .build();
                    analytics.recordEvent(ready);
                });
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onSeek(long oldPosition, int newPosition) {
                Log.i("AWSLivePlayer", "Seek from " + oldPosition + " to " + newPosition + ".");
                getPlayer().getAnalyticsCategory().ifPresent(analytics -> {
                    AnalyticsEvent seek = AnalyticsEvent.builder()
                            .name("LiveStreamSeek")
                            .addProperty("LiveStreamIdentifier", getPlayer().getVideoResource().getIdentifier())
                            .addProperty("OldPosition", (double) oldPosition)
                            .addProperty("NewPosition", (double) newPosition)
                            .build();
                    analytics.recordEvent(seek);
                });
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onPlay(long currentPosition) {
                Log.i("AWSLivePlayer", "Play.");
                getPlayer().getAnalyticsCategory().ifPresent(analytics -> {
                    AnalyticsEvent play = AnalyticsEvent.builder()
                            .name("LiveStreamPlay")
                            .addProperty("LiveStreamIdentifier", getPlayer().getVideoResource().getIdentifier())
                            .addProperty("CurrentPosition", (double) currentPosition)
                            .build();
                    analytics.recordEvent(play);
                });
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onPause(long currentPosition) {
                Log.i("AWSLivePlayer", "Pause.");
                getPlayer().getAnalyticsCategory().ifPresent(analytics -> {
                    AnalyticsEvent pause = AnalyticsEvent.builder()
                            .name("LiveStreamPause")
                            .addProperty("LiveStreamIdentifier", getPlayer().getVideoResource().getIdentifier())
                            .addProperty("CurrentPosition", (double) currentPosition)
                            .build();
                    analytics.recordEvent(pause);
                });
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onBufferingStart(long currentPosition) {
                Log.i("AWSLivePlayer", "Buffer start.");
                getPlayer().getAnalyticsCategory().ifPresent(analytics -> {
                    AnalyticsEvent bufferStart = AnalyticsEvent.builder()
                            .name("LiveStreamBufferingStart")
                            .addProperty("LiveStreamIdentifier", getPlayer().getVideoResource().getIdentifier())
                            .addProperty("CurrentPosition", (double) currentPosition)
                            .build();
                    analytics.recordEvent(bufferStart);
                });
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onBufferingComplete(long currentPosition) {
                Log.i("AWSLivePlayer", "Buffer complete.");
                getPlayer().getAnalyticsCategory().ifPresent(analytics -> {
                    AnalyticsEvent bufferComplete = AnalyticsEvent.builder()
                            .name("LiveStreamBufferingComplete")
                            .addProperty("LiveStreamIdentifier", getPlayer().getVideoResource().getIdentifier())
                            .addProperty("CurrentPosition", (double) currentPosition)
                            .build();
                    analytics.recordEvent(bufferComplete);
                });
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onEnd(long totalDuration) {
                Log.i("AWSLivePlayer", "End.");
                getPlayer().getAnalyticsCategory().ifPresent(analytics -> {
                    AnalyticsEvent end = AnalyticsEvent.builder()
                            .name("LiveStreamEnd")
                            .addProperty("LiveStreamIdentifier", getPlayer().getVideoResource().getIdentifier())
                            .addProperty("TotalDuration", (double) totalDuration)
                            .build();
                    analytics.recordEvent(end);
                });
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onTouch(MotionEvent event) {
                Log.d("AWSLivePlayer", "Touched!");
            }
        });
    }

    /**
     * Constructor for {@link AWSLiveVideoView}.
     * @param context application context
     */
    public AWSLiveVideoView(Context context) {
        this(context, null);
    }

    /**
     * Get the underlying video player.
     * @return a player
     */
    public AWSLiveVideoPlayer getPlayer() {
        return player;
    }

    @Override
    public void seekTo(int newPosition) {
        player.onSeek(getCurrentPosition(), newPosition);
        super.seekTo(newPosition);
    }

}
