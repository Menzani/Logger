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
