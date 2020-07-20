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

package com.amplifyframework.video.resources.ondemand;

/**
 * Types of output for VOD streaming.
 */
public enum OutputType {
    /**
     * An output URL; typically the full resource name, but not a valid URI.
     * May be at S3 or CloudFront depending on the environment.
     */
    BASE_URL("output");

    private String key;

    /**
     * Constructor for the OutputType.
     * @param key JSON field name
     */
    OutputType(String key) {
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
     * Look up an OutputType by its key name.
     * @param key String representation of an output type
     * @return The corresponding ingress type
     */
    public static OutputType fromKey(String key) {
        for (final OutputType outputType : values()) {
            if (outputType.key().equals(key)) {
                return outputType;
            }
        }

        throw new IllegalArgumentException("No such output type: " + key);
    }

    /**
     * Look up a OutputType by its String name.
     * @param name String representation of an output type
     * @return The corresponding output type
     */
    public static OutputType from(String name) {
        for (final OutputType outputType : values()) {
            if (outputType.name().equals(name)) {
                return outputType;
            }
        }

        throw new IllegalArgumentException("No such output type: " + name);
    }
}
