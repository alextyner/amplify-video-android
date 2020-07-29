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

import com.amplifyframework.extended.video.VideoException;
import com.amplifyframework.extended.video.resources.VideoResourceType;
import com.amplifyframework.extended.video.resources.live.EgressType;
import com.amplifyframework.extended.video.resources.live.LiveResource;
import com.amplifyframework.extended.video.resources.ondemand.InputType;
import com.amplifyframework.extended.video.resources.ondemand.OnDemandResource;
import com.amplifyframework.extended.video.resources.ondemand.OutputType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Reads video plugin configuration from JSON.
 */
public final class AWSVideoPluginConfigurationReader {

    private AWSVideoPluginConfigurationReader() { }

    /**
     * Parses JSON video configuration producing an {@link AWSVideoPluginConfiguration} object.
     * @param configurationJson JSON configuration.
     * @return A configuration object.
     * @throws VideoException If the JSON object is null or malformed
     */
    public static AWSVideoPluginConfiguration readFrom(JSONObject configurationJson) throws VideoException {

        if (configurationJson == null) {
            throw new VideoException(
                    "Null configuration JSON provided to AWS Video plugin.",
                    "Check that the content of the configuration file hasn't " +
                            "been deleted."
            );
        }

        return parseConfigurationJson(configurationJson);
    }

    private static AWSVideoPluginConfiguration parseConfigurationJson(JSONObject configurationJson)
            throws VideoException {
        try {
            AWSVideoPluginConfiguration.Builder config = AWSVideoPluginConfiguration.builder();

            Iterator<String> iter = configurationJson.keys();
            while (iter.hasNext()) {
                String identifier = iter.next();
                JSONObject videoResource = configurationJson.getJSONObject(identifier);
                VideoResourceType type = VideoResourceType.from(videoResource.getString("type"));
                switch (type) {
                    case LIVE:
                        JSONObject egress = videoResource.getJSONObject("egress");
                        Map<EgressType, String> egressPoints = readEgressAsMap(egress);

                        config.addLiveResource(new LiveResource(identifier, egressPoints));
                        break;
                    case ON_DEMAND:
                        String input = videoResource.getString("input");
                        Map<InputType, String> inputMethods = new HashMap<>();
                        inputMethods.put(InputType.S3_BUCKET_NAME, input);

                        Map<OutputType, String> outputMethods = new HashMap<>();
                        String outputS3 = videoResource.getString("output");
                        outputMethods.put(OutputType.S3_BUCKET_NAME, outputS3);

                        String outputUrl = videoResource.getString("outputUrl");
                        if (outputUrl != null) { // Not always present
                            outputMethods.put(OutputType.BASE_URL, outputUrl);
                        }

                        config.addOnDemandResource(new OnDemandResource(identifier, inputMethods, outputMethods));
                        break;
                    default:
                        throw new VideoException("Invalid video resource type.", "Consider " +
                                "re-generating the Amplify Video config file.");
                }
            }
            return config.build();

        } catch (JSONException exception) {
            throw new VideoException(
                    "Failed to parse configuration JSON for AWS Video Plugin",
                    exception,
                    "Check the config file to make sure it hasn't been " +
                            "wrongly modified."
            );
        }
    }

    private static Map<EgressType, String> readEgressAsMap(JSONObject jsonObject) throws JSONException {
        Map<EgressType, String> map = new HashMap<>();
        Iterator<String> iter = jsonObject.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            String value = jsonObject.getString(key);
            map.put(EgressType.fromKey(key), value);
        }
        return map;
    }

}
