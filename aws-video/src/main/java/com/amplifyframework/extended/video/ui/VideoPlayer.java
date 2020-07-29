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

package com.amplifyframework.extended.video.ui;

import android.view.MotionEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * A video player for Amplify Video.
 */
public abstract class VideoPlayer {

    private Set<Listener> listeners;
    private State state;

    /**
     * Constructor for {@link VideoPlayer}.
     */
    public VideoPlayer() {
        listeners = new HashSet<>();
    }

    /**
     * Add a new {@link Listener} to handle player events.
     * @param listener a player event listener
     */
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    /**
     * Remove a {@link Listener}.
     * @param listener a listener you've added before
     * @return true if the listener was removed
     */
    public boolean removeListener(Listener listener) {
        return listeners.remove(listener);
    }

    /**
     * Get all of the listeners attached to this player.
     * @return Iterable of attached listeners
     */
    protected Iterable<Listener> getListeners() {
        return listeners;
    }

    /**
     * Get the current playback position of the player.
     * @return current playback position as a long
     */
    public abstract long getCurrentPosition();

    /**
     * Get the total duration of the clip.
     * @return total duration of the clip being played; not useful for live streams
     */
    public abstract long getDuration();

    /**
     * Listener for {@link VideoPlayer} events.
     */
    public abstract static class Listener {

        /**
         * Called when the state of the {@link VideoPlayer} changes.
         * @param newState the new state of the player
         * @throws IllegalStateException if an unhandled {@link State} is provided
         */
        public abstract void onStateChange(State newState);

        /**
         * Called when the player is {@link State#PREPARING}.
         * @param totalDuration the total clip duration
         */
        public abstract void onPreparing(long totalDuration);

        /**
         * Called when the player is {@link State#READY}.
         */
        public abstract void onReady();

        /**
         * Called when the player starts playing.
         * @param currentPosition current playback position
         */
        public abstract void onPlay(long currentPosition);

        /**
         * Called when the player is paused.
         * @param currentPosition current playback position
         */
        public abstract void onPause(long currentPosition);

        /**
         * Called when playback ends.
         * @param totalDuration the total clip duration
         */
        public abstract void onEnd(long totalDuration);

        /**
         * Called when playback seeks to a new position.
         * @param oldPosition the old playback position
         * @param newPosition the new playback position
         */
        public abstract void onSeek(long oldPosition, int newPosition);

        /**
         * Called when buffering starts.
         * @param currentPosition current playback position
         */
        public abstract void onBufferingStart(long currentPosition);

        /**
         * Called when buffering is completed.
         * @param currentPosition current playback position
         */
        public abstract void onBufferingComplete(long currentPosition);

        /**
         * Called when the player is touched.
         * @param event touch event
         */
        public abstract void onTouch(MotionEvent event);
    }

    /**
     * Possible states in which the {@link VideoPlayer} can exist.
     */
    public enum State {
        /**
         * Idle.
         */
        IDLE,
        /**
         * Preparing.
         */
        PREPARING,
        /**
         * Buffering.
         */
        BUFFERING,
        /**
         * Ready.
         */
        READY,
        /**
         * Playing.
         */
        PLAYING,
        /**
         * Ended.
         */
        ENDED
    }

//    public static abstract class Factory {
//        public abstract VideoPlayer create();
//    }

}
