/*
 * Copyright 2020 Francesco Menzani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.menzani.logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public final class ConfigurableThreadFactory implements ThreadFactory {
    public static ThreadFactory daemon() {
        return new ConfigurableThreadFactory(null, true);
    }

    public static ThreadFactory daemon(String name) {
        return new ConfigurableThreadFactory(Objects.objectNotNull(name, "name"), true);
    }

    public static ThreadFactory named(String name) {
        return new ConfigurableThreadFactory(Objects.objectNotNull(name, "name"), false);
    }

    private final ThreadFactory defaultFactory = Executors.defaultThreadFactory();
    private final String name;
    private final boolean daemon;

    private ConfigurableThreadFactory(String name, boolean daemon) {
        this.name = name;
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = defaultFactory.newThread(r);
        if (name != null) thread.setName(name);
        if (daemon) thread.setDaemon(true);
        return thread;
    }
}
