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

/**
 * Types of stream keys.
 */
public enum StreamKeyType {
    /**
     * Primary.
     */
    PRIMARY("primary"),
    /**
     * DASH.
     */
    BACKUP("backup");

    private String key;

    /**
     * Constructor for the IngressType.
     * @param key JSON field name
     */
    StreamKeyType(String key) {
        this.key = key;
    }

    /**
     * Get a key representation.
     * @return Key as a String.
     */
    public String key() {
        return key;
    }

    /**
     * Look up a StreamKeyType by its key name.
     * @param key String representation of a key type
     * @return The corresponding stream key type
     */
    public static StreamKeyType fromKey(String key) {
        for (final StreamKeyType streamKeyType : values()) {
            if (streamKeyType.key().equals(key)) {
                return streamKeyType;
            }
        }

        throw new IllegalArgumentException("No such key type: " + key);
    }

    /**
     * Look up a {@link StreamKeyType} by its String name.
     * @param name String representation of a stream key type
     * @return The corresponding key type
     */
    public static StreamKeyType from(String name) {
        for (final StreamKeyType streamKeyType : values()) {
            if (streamKeyType.name().equals(name)) {
                return streamKeyType;
            }
        }

        throw new IllegalArgumentException("No such key type: " + name);
    }
}
