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

import com.amplifyframework.video.VideoException;
import com.amplifyframework.video.resources.VideoResourceType;
import com.amplifyframework.video.resources.live.EgressType;
import com.amplifyframework.video.resources.live.IngressType;
import com.amplifyframework.video.resources.live.LiveResource;
import com.amplifyframework.video.resources.live.StreamKeyType;
import com.amplifyframework.video.resources.ondemand.InputType;
import com.amplifyframework.video.resources.ondemand.OnDemandResource;
import com.amplifyframework.video.resources.ondemand.OutputType;

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
                        JSONObject ingress = videoResource.getJSONObject("ingress");
                        Map<IngressType, String> ingressPoints = readIngressAsMap(ingress);

                        JSONObject keys = videoResource.getJSONObject("keys");
                        Map<StreamKeyType, String> streamKeys = readStreamKeysAsMap(keys);

                        JSONObject egress = videoResource.getJSONObject("egress");
                        Map<EgressType, String> egressPoints = readEgressAsMap(egress);

                        config.addLiveResource(new LiveResource(identifier, ingressPoints, streamKeys, egressPoints));
                        break;
                    case ON_DEMAND:
                        String input = videoResource.getString("input");
                        Map<InputType, String> inputMethods = new HashMap<>();
                        inputMethods.put(InputType.S3_BUCKET_NAME, input);

                        String output = videoResource.getString("output");
                        Map<OutputType, String> outputMethods = new HashMap<>();
                        outputMethods.put(OutputType.BASE_URL, output);

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

    private static Map<IngressType, String> readIngressAsMap(JSONObject jsonObject) throws JSONException {
        Map<IngressType, String> map = new HashMap<>();
        Iterator<String> iter = jsonObject.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            String value = jsonObject.getString(key);
            map.put(IngressType.fromKey(key), value);
        }
        return map;
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

    private static Map<StreamKeyType, String> readStreamKeysAsMap(JSONObject jsonObject) throws JSONException {
        Map<StreamKeyType, String> map = new HashMap<>();
        Iterator<String> iter = jsonObject.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            String value = jsonObject.getString(key);
            map.put(StreamKeyType.fromKey(key), value);
        }
        return map;
    }

}
