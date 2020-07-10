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

package com.amplifyframework.video.resources.live;

import java.util.Collection;

/**
 * Live video behaviors include TODO: what? .
 */
public interface LiveBehavior {

    /**
     * Retrieve all configured live video resources.
     * @return A Set of live video resources.
     */
    Collection<LiveResource> liveResources();

    /**
     * Get a live resource by its identifier.
     * @param identifier String resource identifier.
     * @return A {@link LiveResource} with the given identifier.
     */
    LiveResource getLiveResource(String identifier);

}
