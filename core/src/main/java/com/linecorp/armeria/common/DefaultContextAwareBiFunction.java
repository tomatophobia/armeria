/*
 * Copyright 2023 LINE Corporation
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

package com.linecorp.armeria.common;

import static java.util.Objects.requireNonNull;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.linecorp.armeria.common.annotation.Nullable;
import com.linecorp.armeria.common.util.SafeCloseable;

final class DefaultContextAwareBiFunction<T, U, R> implements ContextAwareBiFunction<T, U, R> {

    private final RequestContext context;
    private final BiFunction<T, U, R> function;
    @Nullable
    private final Function<Throwable, R> exceptionHandler;

    DefaultContextAwareBiFunction(RequestContext context, BiFunction<T, U, R> function) {
        this.context = requireNonNull(context, "context");
        this.function = requireNonNull(function, "function");
        exceptionHandler = null;
    }

    DefaultContextAwareBiFunction(RequestContext context, BiFunction<T, U, R> function,
                                  Function<Throwable, R> exceptionHandler) {
        this.context = requireNonNull(context, "context");
        this.function = requireNonNull(function, "function");
        this.exceptionHandler = requireNonNull(exceptionHandler, "exceptionHandler");
    }

    @Override
    public RequestContext context() {
        return context;
    }

    @Override
    public BiFunction<T, U, R> withoutContext() {
        return function;
    }

    @Override
    public R apply(T t, U u) {
        try (SafeCloseable ignored = context.push()) {
            return function.apply(t, u);
        } catch (Throwable cause) {
            if (exceptionHandler == null) {
                throw cause;
            }
            return exceptionHandler.apply(cause);
        }
    }
}
