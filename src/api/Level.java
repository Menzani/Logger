/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package it.menzani.logger.api;

public interface Level {
    int getVerbosity();

    String getMarker();

    boolean isError();

    default Verbosity compareTo(Level that) {
        switch (Integer.compare(this.getVerbosity(), that.getVerbosity())) {
            case -1:
                return Verbosity.LOWER;
            case 0:
                return Verbosity.EQUAL;
            case 1:
                return Verbosity.GREATER;
            default:
                throw new AssertionError();
        }
    }

    enum Verbosity {
        LOWER(-1),
        EQUAL(0),
        GREATER(1);

        private final int id;

        Verbosity(int id) {
            this.id = id;
        }
    }

    final class Comparator implements java.util.Comparator<Level> {
        @Override
        public int compare(Level o1, Level o2) {
            return o1.compareTo(o2).id;
        }
    }
}
