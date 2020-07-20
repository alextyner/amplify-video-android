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

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.VideoView;

import androidx.annotation.NonNull;

import com.amplifyframework.analytics.AnalyticsEvent;
import com.amplifyframework.video.resources.live.EgressType;
import com.amplifyframework.video.resources.live.LiveResource;

import java.util.Objects;

/**
 * Manages a video player for a live AWS resource.
 */
public class AWSLiveVideoPlayer extends AWSVideoPlayer {

    private LiveResource liveResource;

    /**
     * Create a new {@link AWSVideoPlayer} composed of a {@link VideoView}.
     *
     * @param videoView The primary {@link VideoView} used by the player.
     */
    public AWSLiveVideoPlayer(@NonNull VideoView videoView) {
        super(videoView);
    }

    /**
     * Configure this video player for a live streaming resource.
     * @param liveResource the {@link LiveResource} to source from.
     */
    public void attach(LiveResource liveResource) {
        this.liveResource = Objects.requireNonNull(liveResource);
        configureListeners();
        connect(liveResource);

        getAnalyticsCategory().ifPresent(analytics -> {
            AnalyticsEvent loadStart = AnalyticsEvent.builder()
                    .name("LiveStreamLoadStart")
                    .addProperty("LiveStreamIdentifier", getVideoResource().getIdentifier())
                    .build();
            analytics.recordEvent(loadStart);
        });
    }

    private void connect(LiveResource liveResource) {
        // Try all of the egress types until one isn't null.
        for (EgressType type : EgressType.values()) {
            String egressPoint = liveResource.getEgressPoint(type);
            if (egressPoint != null) {
                setSourceURI(Uri.parse(egressPoint));
                break;
            }
        }
        getVideoView().requestFocus();
        getVideoView().start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveResource getVideoResource() {
        return liveResource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureListeners() {
        super.configureListeners();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getVideoView().setOnInfoListener(new LiveInfoListener());
        }
        getVideoView().setOnErrorListener(new LiveErrorListener());
        getVideoView().setOnPreparedListener(new LivePreparedListener());
    }

    private class LivePreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            getAnalyticsCategory().ifPresent(analytics -> {
                AnalyticsEvent prepared = AnalyticsEvent.builder()
                        .name("LiveStreamPrepared")
                        .addProperty("LiveStreamIdentifier", getVideoResource().getIdentifier())
                        .build();
                analytics.recordEvent(prepared);
            });
        }
    }

    private class LiveErrorListener implements MediaPlayer.OnErrorListener {
        private static final int RETRY_DELAY = 5000;
        private Handler handler = new Handler();
        private Runnable doTryReconnect = () -> {
            Log.d("AMPAPP", "Attemting to connect to the video stream.");
            connect(getVideoResource());
            Log.d("AMPAPP", "Waiting and trying again...");
        };

        @Override
        public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
            switch (extra) {
                case MediaPlayer.MEDIA_ERROR_IO:

                    getAnalyticsCategory().ifPresent(analytics -> {
                        AnalyticsEvent disconnect = AnalyticsEvent.builder()
                                .name("LiveStreamIOError")
                                .addProperty("LiveStreamIdentifier", getVideoResource().getIdentifier())
                                .addProperty("CurrentPlaybackPosition", getVideoView().getCurrentPosition())
                                .build();
                        analytics.recordEvent(disconnect);
                    });

                    // Trigger 1 connection re-try event. If it fails, an error will propogate right back
                    // to this point, triggering another re-try event.
                    handler.postDelayed(doTryReconnect, RETRY_DELAY);

                    return true;
                default:
                    return false;
            }
        }
    }

    private class LiveInfoListener implements MediaPlayer.OnInfoListener {
        private static final int RETRY_DELAY = 5000;
        private Handler handler = new Handler();
        private Runnable doTryReconnect = () -> {
            Log.d("AMPAPP", "Attempting to reconnect to the video stream.");
            connect(getVideoResource());
            Log.d("AMPAPP", "Waiting and trying again...");
        };

        @Override
        public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    Log.d("AMPAPP", "Buffering started...");

                    getAnalyticsCategory().ifPresent(analytics -> {
                        AnalyticsEvent bufferStart = AnalyticsEvent.builder()
                                .name("LiveStreamBufferStart")
                                .addProperty("LiveStreamIdentifier", getVideoResource().getIdentifier())
                                .addProperty("CurrentPlaybackPosition", getVideoView().getCurrentPosition())
                                .build();
                        analytics.recordEvent(bufferStart);
                    });

                    // Buffering triggers 1 connection re-try event after RETRY_DELAY seconds. If the connection
                    // fails, LiveErrorListener.onError will be called, and another re-try event will be triggered.
                    handler.postDelayed(doTryReconnect, RETRY_DELAY);

                    return true;
                case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    Log.d("AMPAPP", "Buffering has stopped, won't try to reconnect.");

                    getAnalyticsCategory().ifPresent(analytics -> {
                        AnalyticsEvent bufferStop = AnalyticsEvent.builder()
                                .name("LiveStreamBufferStop")
                                .addProperty("LiveStreamIdentifier", getVideoResource().getIdentifier())
                                .addProperty("CurrentPlaybackPosition", getVideoView().getCurrentPosition())
                                .build();
                        analytics.recordEvent(bufferStop);
                    });

                    // If the player signals that buffering has stopped, don't try to reconnect.
                    handler.removeCallbacks(doTryReconnect);

                    return true;
                default:
                    return false;
            }
        }
    }
}
