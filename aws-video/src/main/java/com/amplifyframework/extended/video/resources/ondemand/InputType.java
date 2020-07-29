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

package com.amplifyframework.extended.video.resources.ondemand;

/**
 * Types of input for VOD streaming.
 */
public enum InputType {
    /**
     * An input S3 bucket name.
     */
    S3_BUCKET_NAME("input");

    private String key;

    /**
     * Constructor for the InputType.
     * @param key JSON field name
     */
    InputType(String key) {
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
     * Look up an InputType by its key name.
     * @param key String representation of an input type
     * @return The corresponding input type
     */
    public static InputType fromKey(String key) {
        for (final InputType inputType : values()) {
            if (inputType.key().equals(key)) {
                return inputType;
            }
        }

        throw new IllegalArgumentException("No such input type: " + key);
    }

    /**
     * Look up a InputType by its String name.
     * @param name String representation of an input type
     * @return The corresponding input type
     */
    public static InputType from(String name) {
        for (final InputType inputType : values()) {
            if (inputType.name().equals(name)) {
                return inputType;
            }
        }

        throw new IllegalArgumentException("No such input type: " + name);
    }
}
