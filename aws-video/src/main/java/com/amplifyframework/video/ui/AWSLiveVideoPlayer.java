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
        configureFor(liveResource);
    }

    private void configureFor(LiveResource liveResource) {
        // TODO: try all of the egress types until one isn't null
        setSourceURI(Uri.parse(liveResource.getEgressPoint(EgressType.MEDIASTORE)));
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
            getVideoView().setOnInfoListener(new LiveDisconnectHandler(new Handler(), this, getVideoResource()));
        }
    }

    private static class LiveDisconnectHandler implements MediaPlayer.OnInfoListener {

        private static final int RETRY_DELAY = 5000;

        private Handler handler;
        private boolean disconnected;
        private Runnable task;

        LiveDisconnectHandler(Handler handler, AWSLiveVideoPlayer player, LiveResource resource) {
            this.handler = handler;
            this.task = new Reconnect(this, handler, player, resource);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    disconnected = true;
                    Log.d("AMPERR", "Stream has started buffering...");
                    handler.postDelayed(task, RETRY_DELAY);
                    return true;
                case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    disconnected = false;
                    Log.d("AMPERR", "Stream has stopped buffering!");
                    return true;
                default:
                    return false;
            }
        }

        private class Reconnect implements Runnable {

            private LiveDisconnectHandler listener;
            private Handler handler;
            private AWSLiveVideoPlayer player;
            private LiveResource resource;

            Reconnect(LiveDisconnectHandler listener, Handler handler, AWSLiveVideoPlayer player,
                      LiveResource resource) {
                this.listener = listener;
                this.handler = handler;
                this.player = player;
                this.resource = resource;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void run() {
                if (listener.disconnected) {
                    player.attach(resource);
                    Log.d("AMPAPP", "Trying to reattach the video resource...");
                    Log.d("AMPAPP", "Waiting 5s and trying again.");
                    handler.postDelayed(this, RETRY_DELAY);
                }
            }
        }
    }
}
