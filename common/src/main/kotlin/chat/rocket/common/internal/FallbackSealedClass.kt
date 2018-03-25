package chat.rocket.common.internal

/*
 * Copyright 2016 Serj Lotutovici
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.squareup.moshi.Moshi

/**
 * Indicates that the annotated sealed class has a fallback value. The fallback must be set via
 * [.name] and must have a constructor String field [.fieldName]. If no class with the provided name is declared in the
 * annotated sealed class type an [assertion error][AssertionError] will be thrown.
 *
 *
 * To leverage from [FallbackSealedClass] [FallbackSealedClassJsonAdapter.ADAPTER_FACTORY] must be added to
 * your [moshi instance][Moshi]:
 *
 *
 * <pre>`
 * Moshi moshi = new Moshi.Builder()
 * .add(FallbackEnum.ADAPTER_FACTORY)
 * .build();
 * `</pre>
 *
 * Declaration example:
 * <pre>`
 * @FallbackSealedClass(name = "Custom", fieldName = "rawType")
 * sealed class RoomType {
 *   @Json(name = "c") class Public : RoomType()
 *   @Json(name = "d") class OneToOne: RoomType()
 *   class Custom(val rawType: String) : RoomType()
 * }
 * `</pre>
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class FallbackSealedClass(val name: String, val fieldName:  String)
