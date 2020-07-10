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
public enum IngressType {
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
    IngressType(String key) {
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
     * Look up a IngressType by its key name.
     * @param key String representation of a protocol type
     * @return The corresponding ingress type
     */
    public static IngressType fromKey(String key) {
        for (final IngressType ingressType : values()) {
            if (ingressType.key().equals(key)) {
                return ingressType;
            }
        }

        throw new IllegalArgumentException("No such ingress type: " + key);
    }

    /**
     * Look up a IngressType by its String name.
     * @param name String representation of a protocol type
     * @return The corresponding ingress type
     */
    public static IngressType from(String name) {
        for (final IngressType ingressType : values()) {
            if (ingressType.name().equals(name)) {
                return ingressType;
            }
        }

        throw new IllegalArgumentException("No such ingress type: " + name);
    }
}
