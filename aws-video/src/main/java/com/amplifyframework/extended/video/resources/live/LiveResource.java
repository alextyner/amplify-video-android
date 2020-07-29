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

package com.amplifyframework.extended.video.resources.live;

import androidx.annotation.NonNull;

import com.amplifyframework.extended.video.resources.VideoResource;
import com.amplifyframework.extended.video.resources.VideoResourceType;

import java.util.Map;
import java.util.Objects;

/**
 * A live video resource.
 */
public class LiveResource extends VideoResource {

    private Map<EgressType, String> egress;

    /**
     * Constructor for the VideoResource.
     * @param identifier A resource identifier.
     * @param egress One or more egress protocol URIs.
     */
    public LiveResource(@NonNull String identifier, Map<EgressType, String> egress) {
        super(identifier);
        this.egress = Objects.requireNonNull(egress);
    }

    /**
     * Get a URI for streaming from this resource.
     * @param type Type of egress protocol.
     * @return An egress point as a String (should be a valid URI).
     */
    public String getEgressPoint(EgressType type) {
        return egress.get(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VideoResourceType getType() {
        return VideoResourceType.LIVE;
    }
}
