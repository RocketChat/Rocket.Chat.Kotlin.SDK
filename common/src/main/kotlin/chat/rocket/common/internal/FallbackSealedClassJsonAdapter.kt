package chat.rocket.common.internal

/*
 * Copyright 2018 Lucio Maciel, Rocket.Chat
 * Copyright 2016 Serj Lotutovici
 * Copyright (C) 2014 Square, Inc.
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

import com.squareup.moshi.*

import java.io.IOException
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.util.LinkedHashMap

/**
 * [JsonAdapter] that fallbacks to a default class type declared in the sealed class annotated
 * with [FallbackSealedClass].
 *
 *
 */
class FallbackSealedClassJsonAdapter<T>(private val classType: Class<T>,
                                        fallback: String,
                                        fieldName: String) : JsonAdapter<T>() {
    private val fallbackConstant: Class<out T>
    private val fallbackConstructor: Constructor<out T>
    private val nameConstantMap: Map<String, Class<out T>>
    private val nameStrings: Array<String?>
    private val fallbackConstructorField: Field

    init {

        try {
            var fallbackConstantIndex = -1
            val classes = classType.classes
            val nameMap = LinkedHashMap<String, Class<out T>>()
            nameStrings = arrayOfNulls(classes.size)

            for (index in classes.indices) {
                val clazz = classes[index]
                val annotation = clazz.getAnnotation(Json::class.java)
                val name = annotation?.name ?: clazz.simpleName
                nameMap[name] = clazz as Class<out T>
                nameStrings[index] = name

                if (fallback == clazz.simpleName) {
                    fallbackConstantIndex = index
                }
            }

            if (fallbackConstantIndex != -1) {
                fallbackConstant = classes[fallbackConstantIndex] as Class<out T>
                fallbackConstructor = fallbackConstant.getConstructor(String::class.java)
                fallbackConstructorField = fallbackConstant.getDeclaredField(fieldName)
            } else {
                throw NoSuchFieldException("Filed \"$fallback\" is not declared.")
            }

            nameConstantMap = nameMap.toMap()
        } catch (e: NoSuchFieldException) {
            throw AssertionError("Missing field in " + classType.name, e)
        } catch (e: NoSuchMethodException) {
            throw AssertionError("Missing constructor with \"String\" parameter")
        } catch (e: SecurityException) {
            throw AssertionError("Invalid permission for constructor")
        }
    }

    @Throws(IOException::class)
    override fun fromJson(reader: JsonReader): T? {
        val name = reader.nextString()
        val constant = nameConstantMap[name]
        return constant?.newInstance() ?: fallbackConstructor.newInstance(name)
    }

    @Throws(IOException::class)
    override fun toJson(writer: JsonWriter, value: T?) {
        value?.let {
            if (fallbackConstant.isInstance(value)) {
                val accessible = fallbackConstructorField.isAccessible
                fallbackConstructorField.isAccessible = true
                writer.value(fallbackConstructorField.get(value) as String)
                fallbackConstructorField.isAccessible = accessible
                return
            }
            for (entry in nameConstantMap) {
                if (entry.value.isInstance(value)) {
                    writer.value(entry.key)
                    return
                }
            }
        }
    }

    override fun toString(): String {
        return "JsonAdapter(" + classType.name + ").fallbackClass(" + fallbackConstant + ")"
    }


    companion object {

        /**
         * Builds an adapter that can process sealed classes annotated with [FallbackSealedClass].
         */
        val ADAPTER_FACTORY: JsonAdapter.Factory = JsonAdapter.Factory { type, annotations, moshi ->
            if (!annotations.isEmpty()) return@Factory null

            val rawType = Types.getRawType(type)
            val annotation = rawType.getAnnotation(FallbackSealedClass::class.java) ?: return@Factory null


            return@Factory FallbackSealedClassJsonAdapter(rawType, annotation.name, annotation.fieldName)
                    .nullSafe()

        }
    }
}