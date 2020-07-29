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

package com.amplifyframework.video.config;

import com.amplifyframework.extended.video.resources.live.LiveResource;
import com.amplifyframework.extended.video.resources.ondemand.OnDemandResource;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for the video plugin.
 */
public final class AWSVideoPluginConfiguration {

    private Map<String, LiveResource> liveResources;
    private Map<String, OnDemandResource> onDemandResources;

    private AWSVideoPluginConfiguration() {
        this.liveResources = new HashMap<>();
        this.onDemandResources = new HashMap<>();
    }

    /**
     * Get all of the live video resources.
     * @return A Set of configured video resources.
     */
    public Map<String, LiveResource> liveResources() {
        return liveResources;
    }

    /**
     * Get all of the on-demand video resources.
     * @return A Set of configured video resources.
     */
    public Map<String, OnDemandResource> onDemandResources() {
        return onDemandResources;
    }

    /**
     * Builder for {@link AWSVideoPluginConfiguration}.
     * @return A new {@link AWSVideoPluginConfiguration.Builder}.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for {@link AWSVideoPluginConfiguration}.
     */
    public static class Builder {
        private AWSVideoPluginConfiguration toBuild;

        /**
         * Constructor.
         */
        public Builder() {
            toBuild = new AWSVideoPluginConfiguration();
        }

        /**
         * Add a {@link LiveResource}.
         * @param resource A {@link LiveResource}.
         * @return The {@link Builder}.
         */
        public Builder addLiveResource(LiveResource resource) {
            toBuild.liveResources.put(resource.getIdentifier(), resource);
            return this;
        }

        /**
         * Add an {@link OnDemandResource}.
         * @param resource An {@link OnDemandResource}.
         * @return The {@link Builder}.
         */
        public Builder addOnDemandResource(OnDemandResource resource) {
            toBuild.onDemandResources.put(resource.getIdentifier(), resource);
            return this;
        }

        /**
         * Build the {@link AWSVideoPluginConfiguration}.
         * @return The {@link AWSVideoPluginConfiguration}.
         */
        public AWSVideoPluginConfiguration build() {
            return toBuild;
        }
    }
}
