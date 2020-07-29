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

package com.amplifyframework.extended.video;

import android.net.Uri;

import com.amplifyframework.extended.video.resources.live.LiveBehavior;
import com.amplifyframework.extended.video.resources.ondemand.OnDemandBehavior;

/**
 * Video category behaviors include live and on-demand operations.
 * TODO: operations like what?
 */
public interface VideoCategoryBehavior extends LiveBehavior, OnDemandBehavior {

    /**
     * Get the stream egress point for a named resource.
     * @param resourceName Name of the stream resource
     * @return URI pointing to the stream egress point
     */
    Uri getEgressFor(String resourceName);

}
