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

import java.util.Collection;

/**
 * On-demand behaviors include TODO: what? .
 */
public interface OnDemandBehavior {

    /**
     * Retrieve all configured on-demand video resources.
     * @return A Set of on-demand video resources.
     */
    Collection<OnDemandResource> onDemandResources();

    /**
     * Get an on-demand resource by its identifier.
     * @param identifier String resource identifier.
     * @return A {@link OnDemandResource} with the given identifier.
     */
    OnDemandResource getOnDemandResource(String identifier);

}
