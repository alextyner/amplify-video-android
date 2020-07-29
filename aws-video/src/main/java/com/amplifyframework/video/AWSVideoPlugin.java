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

package com.amplifyframework.video;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amplifyframework.core.category.CategoryType;
import com.amplifyframework.extended.video.VideoException;
import com.amplifyframework.extended.video.VideoPlugin;
import com.amplifyframework.extended.video.resources.live.LiveResource;
import com.amplifyframework.extended.video.resources.ondemand.OnDemandResource;
import com.amplifyframework.video.config.AWSVideoPluginConfiguration;
import com.amplifyframework.video.config.AWSVideoPluginConfigurationReader;

import org.json.JSONObject;

import java.util.Collection;
import java.util.Collections;

/**
 * An AWS implementation of the {@link VideoPlugin}.
 */
public class AWSVideoPlugin extends VideoPlugin<Void> {

    private static final String AWS_VIDEO_PLUGIN_KEY = "awsVideoPlugin";

    private AWSVideoPluginConfiguration configuration;

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public String getPluginKey() {
        return AWS_VIDEO_PLUGIN_KEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(JSONObject pluginConfiguration, @NonNull Context context) throws VideoException {
        this.configuration = AWSVideoPluginConfigurationReader.readFrom(pluginConfiguration);
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Void getEscapeHatch() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Uri getEgressFor(String resourceName) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<LiveResource> liveResources() {
        return Collections.unmodifiableCollection(configuration.liveResources().values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<OnDemandResource> onDemandResources() {
        return Collections.unmodifiableCollection(configuration.onDemandResources().values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveResource getLiveResource(String identifier) {
        return configuration.liveResources().get(identifier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnDemandResource getOnDemandResource(String identifier) {
        return configuration.onDemandResources().get(identifier);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public CategoryType getCategoryType() {
        // TODO: change this arbitrary decision
        return CategoryType.API;
    }
}
