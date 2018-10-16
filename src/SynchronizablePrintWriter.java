/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package it.menzani.logger;

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
