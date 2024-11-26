/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.server.metrics;

import org.apache.kafka.common.Uuid;
import org.apache.kafka.common.requests.PushTelemetryRequest;
import org.apache.kafka.server.telemetry.ClientTelemetryPayload;

import java.nio.ByteBuffer;

/**
 * Implements the {@code ClientTelemetryPayload} interface for the metrics payload sent by the client.
 */
public class DefaultClientTelemetryPayload implements ClientTelemetryPayload {

    final private Uuid clientInstanceId;
    final private boolean isClientTerminating;
    final private String metricsContentType;
    final private ByteBuffer metricsData;

    DefaultClientTelemetryPayload(PushTelemetryRequest request) {
        this.clientInstanceId = request.data().clientInstanceId();
        this.isClientTerminating = request.data().terminating();
        this.metricsContentType = request.metricsContentType();
        this.metricsData = request.metricsData();
    }

    @Override
    public Uuid clientInstanceId() {
        return this.clientInstanceId;
    }

    @Override
    public boolean isTerminating() {
        return isClientTerminating;
    }

    @Override
    public String contentType() {
        return metricsContentType;
    }

    @Override
    public ByteBuffer data() {
        return metricsData;
    }
}
