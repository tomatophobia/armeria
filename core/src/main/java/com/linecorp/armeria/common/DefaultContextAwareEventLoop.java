/*
 * Copyright 2020 LINE Corporation
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

import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import com.google.common.base.MoreObjects;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ProgressivePromise;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ScheduledFuture;

final class DefaultContextAwareEventLoop
        extends AbstractContextAwareExecutorService<EventLoop> implements ContextAwareEventLoop {

    private final RequestContext context;

    DefaultContextAwareEventLoop(RequestContext context, EventLoop eventLoop) {
        super(eventLoop);
        this.context = context;
    }

    @Override
    public RequestContext context() {
        return context;
    }

    @Override
    @Nonnull
    public RequestContext contextOrNull() {
        return context;
    }

    @Override
    public EventLoop next() {
        return this;
    }

    @Override
    public EventLoopGroup parent() {
        return withoutContext().parent();
    }

    @Override
    public boolean inEventLoop() {
        return withoutContext().inEventLoop();
    }

    @Override
    public boolean inEventLoop(Thread thread) {
        return withoutContext().inEventLoop(thread);
    }

    @Override
    public <V> Promise<V> newPromise() {
        return new ContextAwarePromise<>(context, withoutContext().newPromise());
    }

    @Override
    public <V> ProgressivePromise<V> newProgressivePromise() {
        return new ContextAwareProgressivePromise<>(context, withoutContext().newProgressivePromise());
    }

    @Override
    public <V> Future<V> newSucceededFuture(V result) {
        return new ContextAwareFuture<>(context, withoutContext().newSucceededFuture(result));
    }

    @Override
    public <V> Future<V> newFailedFuture(Throwable cause) {
        return new ContextAwareFuture<>(context, withoutContext().newFailedFuture(cause));
    }

    @Override
    public boolean isShuttingDown() {
        return withoutContext().isShuttingDown();
    }

    @Override
    public Future<?> shutdownGracefully() {
        return withoutContext().shutdownGracefully();
    }

    @Override
    public Future<?> shutdownGracefully(long quietPeriod, long timeout,
                                        TimeUnit unit) {
        return withoutContext().shutdownGracefully(quietPeriod, timeout, unit);
    }

    @Override
    public Future<?> terminationFuture() {
        return withoutContext().terminationFuture();
    }

    @Override
    public Iterator<EventExecutor> iterator() {
        return withoutContext().iterator();
    }

    @Override
    public Future<?> submit(Runnable task) {
        return (Future<?>) super.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return (Future<T>) super.submit(task, result);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return (Future<T>) super.submit(task);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return withoutContext().schedule(context.makeContextAware(command), delay, unit);
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return withoutContext().schedule(context.makeContextAware(callable), delay, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(
            Runnable command, long initialDelay, long period, TimeUnit unit) {
        return withoutContext().scheduleAtFixedRate(context.makeContextAware(command), initialDelay, period,
                                                    unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(
            Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return withoutContext().scheduleWithFixedDelay(context.makeContextAware(command), initialDelay, delay,
                                                       unit);
    }

    @Override
    public ChannelFuture register(Channel channel) {
        return withoutContext().register(channel);
    }

    @Override
    public ChannelFuture register(ChannelPromise channelPromise) {
        return withoutContext().register(channelPromise);
    }

    @Override
    public ChannelFuture register(Channel channel,
                                  ChannelPromise channelPromise) {
        return withoutContext().register(channel, channelPromise);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("context", context)
                          .add("eventLoop", withoutContext())
                          .toString();
    }
}
