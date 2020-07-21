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

import java.io.*;

/**
 * A {@link PrintWriter} exposing its monitor to allow for multiple calls to its methods to be synchronized.
 * Use the object returned by {@link #monitor()} for this purpose.
 *
 * <pre>
 * try (SynchronizablePrintWriter writer = new SynchronizablePrintWriter(...)) {
 *     synchronized (writer.monitor()) {
 *         writer.print("Hello,");
 *         writer.println(" world!");
 *     }
 * }
 * </pre>
 */
public final class SynchronizablePrintWriter extends PrintWriter {
    public SynchronizablePrintWriter(Writer out) {
        super(out);
    }

    public SynchronizablePrintWriter(Writer out, boolean autoFlush) {
        super(out, autoFlush);
    }

    public SynchronizablePrintWriter(OutputStream out) {
        super(out);
    }

    public SynchronizablePrintWriter(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
    }

    public SynchronizablePrintWriter(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    public SynchronizablePrintWriter(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(fileName, csn);
    }

    public SynchronizablePrintWriter(File file) throws FileNotFoundException {
        super(file);
    }

    public SynchronizablePrintWriter(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(file, csn);
    }

    public Object monitor() {
        return lock;
    }
}
