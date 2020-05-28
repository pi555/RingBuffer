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

package org.ringbuffer.classcopy;

import java.lang.reflect.InvocationTargetException;

class Constructor<T> implements Invokable<T> {
    private final java.lang.reflect.Constructor<T> constructor;

    Constructor(java.lang.reflect.Constructor<T> constructor) {
        this.constructor = constructor;
    }

    @Override
    public T call(Object... arguments) {
        try {
            return constructor.newInstance(arguments);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new AssertionError();
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        }
    }
}
