/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package eu.menzani.logger;

import java.lang.annotation.*;

/**
 * When applied to a type in a method declaration, indicates that its value may be {@code null}.<br>
 * It can be used to inform the caller that the {@code null} value has some special meaning and is expected in certain cases.
 * <p/>
 * <b>Use of this annotation implies that all non-annotated types do not accept the {@code null} value.</b>
 * To perform runtime checks in those cases, consider using the static methods found in our {@link Objects} class.
 * <p/>
 * This annotation should be applied to types in method parameters only.
 * If a return value may be {@code null}, wrap it using {@link java.util.Optional} instead.
 */
@Documented
@Target(ElementType.TYPE_USE)
@Retention(RetentionPolicy.SOURCE)
public @interface Nullable {
}
