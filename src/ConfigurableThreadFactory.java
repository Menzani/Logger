/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
