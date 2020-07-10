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
 * Various live streaming protocols.
 */
public enum EgressType {
    /**
     * HLS.
     */
    HLS("hls"),
    /**
     * DASH.
     */
    DASH("dash"),
    /**
     * MSS.
     */
    MSS("mss"),
    /**
     * CMAF.
     */
    CMAF("cmaf"),
    /**
     * MEDIASTORE.
     */
    MEDIASTORE("mediastore");

    private String key;

    /**
     * Constructor for the EgressType.
     * @param key JSON field name
     */
    EgressType(String key) {
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
     * Look up a EgressType by its key name.
     * @param key String representation of a protocol type
     * @return The corresponding egress type
     */
    public static EgressType fromKey(String key) {
        for (final EgressType egressType : values()) {
            if (egressType.key().equals(key)) {
                return egressType;
            }
        }

        throw new IllegalArgumentException("No such egress type: " + key);
    }

    /**
     * Look up a EgressType by its String name.
     * @param name String representation of a protocol type
     * @return The corresponding egress type
     */
    public static EgressType from(String name) {
        for (final EgressType egressType : values()) {
            if (egressType.name().equals(name)) {
                return egressType;
            }
        }

        throw new IllegalArgumentException("No such egress type: " + name);
    }
}
