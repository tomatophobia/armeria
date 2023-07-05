/*
 * Copyright 2018 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.server.annotation.decorator;

import java.util.function.Function;

import com.linecorp.armeria.common.logging.LogWriter;
import com.linecorp.armeria.server.HttpService;
import com.linecorp.armeria.server.annotation.DecoratorFactoryFunction;
import com.linecorp.armeria.server.logging.LoggingService;

/**
 * A factory which creates a {@link LoggingService} decorator.
 */
public final class LoggingDecoratorFactoryFunction implements DecoratorFactoryFunction<LoggingDecorator> {

    /**
     * Creates a new decorator with the specified {@link LoggingDecorator}.
     */
    @Override
    public Function<? super HttpService, ? extends HttpService> newDecorator(LoggingDecorator parameter) {
        final float successSamplingRate =
                parameter.successSamplingRate() >= 0.0f ? parameter.successSamplingRate()
                                                        : parameter.samplingRate();
        final float failureSamplingRate =
                parameter.failureSamplingRate() >= 0.0f ? parameter.failureSamplingRate()
                                                        : parameter.samplingRate();
        return LoggingService.builder()
                             .logWriter(LogWriter.builder()
                                                 .requestLogLevel(parameter.requestLogLevel())
                                                 .successfulResponseLogLevel(
                                                         parameter.successfulResponseLogLevel())
                                                 .failureResponseLogLevel(parameter.failureResponseLogLevel())
                                                 .build())
                             .successSamplingRate(successSamplingRate)
                             .failureSamplingRate(failureSamplingRate)
                             .newDecorator();
    }
}
