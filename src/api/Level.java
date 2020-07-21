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
