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

package com.amplifyframework.extended.video.resources;

/**
 * Types of video resources.
 */
public enum VideoResourceType {
    /**
     * Live streaming video resource.
     */
    LIVE,

    /**
     * On-demand video resource.
     */
    ON_DEMAND;

    /**
     * Look up a VideoResourceType by its String name.
     * @param name String representation of a resource type
     * @return The corresponding video resource type
     */
    public static VideoResourceType from(String name) {
        for (final VideoResourceType resourceType : values()) {
            if (resourceType.name().equals(name)) {
                return resourceType;
            }
        }

        throw new IllegalArgumentException("No such video resource type: " + name);
    }
}
