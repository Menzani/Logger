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

package eu.menzani.logger.api;

import eu.menzani.logger.Cloneable;

public interface Logger extends Named, Toggleable, Cloneable<Logger> {
    void trace(LazyMessage lazyMessage);

    void debug(LazyMessage lazyMessage);

    void fine(LazyMessage lazyMessage);

    void info(LazyMessage lazyMessage);

    void header(LazyMessage lazyMessage);

    void warn(LazyMessage lazyMessage);

    void fail(LazyMessage lazyMessage);

    void throwable(Throwable throwable, LazyMessage lazyMessage);

    void throwable(Level level, Throwable throwable, LazyMessage lazyMessage);

    void fatal(LazyMessage lazyMessage);

    void log(Level level, LazyMessage lazyMessage);


    void trace(String parameterizedMessage, Object... arguments);

    void debug(String parameterizedMessage, Object... arguments);

    void fine(String parameterizedMessage, Object... arguments);

    void info(String parameterizedMessage, Object... arguments);

    void header(String parameterizedMessage, Object... arguments);

    void warn(String parameterizedMessage, Object... arguments);

    void fail(String parameterizedMessage, Object... arguments);

    void throwable(Throwable throwable, String parameterizedMessage, Object... arguments);

    void throwable(Level level, Throwable throwable, String parameterizedMessage, Object... arguments);

    void fatal(String parameterizedMessage, Object... arguments);

    void log(Level level, String parameterizedMessage, Object... arguments);


    void trace(Object message);

    void debug(Object message);

    void fine(Object message);

    void info(Object message);

    void header(Object message);

    void warn(Object message);

    void fail(Object message);

    void throwable(Throwable throwable, Object message);

    void throwable(Level level, Throwable throwable, Object message);

    void fatal(Object message);

    void log(Level level, Object message);
}
