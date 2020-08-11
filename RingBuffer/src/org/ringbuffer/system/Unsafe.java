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

package org.ringbuffer.system;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * To avoid using reflection, consider adding the VM option: {@code --add-opens java.base/jdk.internal.misc=org.ringbuffer}
 */
public class Unsafe {
    public static final Module JAVA_BASE_MODULE = Class.class.getModule();

    public static final jdk.internal.misc.Unsafe UNSAFE;

    public static final long ARRAY_BYTE_BASE_OFFSET;
    public static final long ARRAY_CHAR_BASE_OFFSET;
    public static final long ARRAY_SHORT_BASE_OFFSET;
    public static final long ARRAY_INT_BASE_OFFSET;
    public static final long ARRAY_LONG_BASE_OFFSET;
    public static final long ARRAY_FLOAT_BASE_OFFSET;
    public static final long ARRAY_DOUBLE_BASE_OFFSET;
    public static final long ARRAY_BOOLEAN_BASE_OFFSET;
    public static final long ARRAY_OBJECT_BASE_OFFSET;

    public static final long ARRAY_BYTE_INDEX_SCALE;
    public static final long ARRAY_CHAR_INDEX_SCALE;
    public static final long ARRAY_SHORT_INDEX_SCALE;
    public static final long ARRAY_INT_INDEX_SCALE;
    public static final long ARRAY_LONG_INDEX_SCALE;
    public static final long ARRAY_FLOAT_INDEX_SCALE;
    public static final long ARRAY_DOUBLE_INDEX_SCALE;
    public static final long ARRAY_BOOLEAN_INDEX_SCALE;
    public static final long ARRAY_OBJECT_INDEX_SCALE;

    private static final long OVERRIDE;

    static {
        final Module from = JAVA_BASE_MODULE;
        final Module to = Unsafe.class.getModule();
        final String packageName = "jdk.internal.misc";
        try {
            if (!from.isOpen(packageName, to)) {
                Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
                field.setAccessible(true);
                sun.misc.Unsafe unsafe = (sun.misc.Unsafe) field.get(null);

                // Code is duplicated so that we do not have to use reflection in the other case.
                final Class<?> clazz = Module.class;
                Method implAddOpens = clazz.getDeclaredMethod("implAddOpens", String.class, clazz);

                long OVERRIDE;
                if (Version.current() == Version.JAVA_11) {
                    OVERRIDE = unsafe.objectFieldOffset(AccessibleObject.class.getDeclaredField("override"));
                } else if (Platform.current().is32Bit()) {
                    OVERRIDE = 8L;
                } else {
                    long offset = unsafe.objectFieldOffset(Platform.OopsCompressed.class.getDeclaredField("i"));
                    if (offset == 8L) {
                        assert Platform.current().is32Bit();
                        OVERRIDE = -1L;
                    } else if (offset == 12L) {
                        OVERRIDE = 12L;
                    } else if (offset == 16L) {
                        OVERRIDE = 16L;
                    } else {
                        throw new AssertionError();
                    }
                }
                unsafe.putBoolean(implAddOpens, OVERRIDE, true);

                implAddOpens.invoke(from, packageName, to);
            }
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
        UNSAFE = jdk.internal.misc.Unsafe.getUnsafe();

        ARRAY_BYTE_BASE_OFFSET = jdk.internal.misc.Unsafe.ARRAY_BYTE_BASE_OFFSET;
        ARRAY_CHAR_BASE_OFFSET = jdk.internal.misc.Unsafe.ARRAY_CHAR_BASE_OFFSET;
        ARRAY_SHORT_BASE_OFFSET = jdk.internal.misc.Unsafe.ARRAY_SHORT_BASE_OFFSET;
        ARRAY_INT_BASE_OFFSET = jdk.internal.misc.Unsafe.ARRAY_INT_BASE_OFFSET;
        ARRAY_LONG_BASE_OFFSET = jdk.internal.misc.Unsafe.ARRAY_LONG_BASE_OFFSET;
        ARRAY_FLOAT_BASE_OFFSET = jdk.internal.misc.Unsafe.ARRAY_FLOAT_BASE_OFFSET;
        ARRAY_DOUBLE_BASE_OFFSET = jdk.internal.misc.Unsafe.ARRAY_DOUBLE_BASE_OFFSET;
        ARRAY_BOOLEAN_BASE_OFFSET = jdk.internal.misc.Unsafe.ARRAY_BOOLEAN_BASE_OFFSET;
        ARRAY_OBJECT_BASE_OFFSET = jdk.internal.misc.Unsafe.ARRAY_OBJECT_BASE_OFFSET;

        ARRAY_BYTE_INDEX_SCALE = jdk.internal.misc.Unsafe.ARRAY_BYTE_INDEX_SCALE;
        ARRAY_CHAR_INDEX_SCALE = jdk.internal.misc.Unsafe.ARRAY_CHAR_INDEX_SCALE;
        ARRAY_SHORT_INDEX_SCALE = jdk.internal.misc.Unsafe.ARRAY_SHORT_INDEX_SCALE;
        ARRAY_INT_INDEX_SCALE = jdk.internal.misc.Unsafe.ARRAY_INT_INDEX_SCALE;
        ARRAY_LONG_INDEX_SCALE = jdk.internal.misc.Unsafe.ARRAY_LONG_INDEX_SCALE;
        ARRAY_FLOAT_INDEX_SCALE = jdk.internal.misc.Unsafe.ARRAY_FLOAT_INDEX_SCALE;
        ARRAY_DOUBLE_INDEX_SCALE = jdk.internal.misc.Unsafe.ARRAY_DOUBLE_INDEX_SCALE;
        ARRAY_BOOLEAN_INDEX_SCALE = jdk.internal.misc.Unsafe.ARRAY_BOOLEAN_INDEX_SCALE;
        ARRAY_OBJECT_INDEX_SCALE = jdk.internal.misc.Unsafe.ARRAY_OBJECT_INDEX_SCALE;

        if (Version.current() == Version.JAVA_11) {
            OVERRIDE = objectFieldOffset(AccessibleObject.class, "override");
        } else if (Platform.current().is32Bit()) {
            OVERRIDE = 8L;
        } else if (Platform.areOopsCompressed()) {
            OVERRIDE = 12L;
        } else {
            OVERRIDE = 16L;
        }
    }

    public static long objectFieldOffset(Class<?> clazz, String fieldName) {
        try {
            return UNSAFE.objectFieldOffset(clazz.getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void setAccessible(AccessibleObject accessibleObject) {
        UNSAFE.putBoolean(accessibleObject, OVERRIDE, true);
    }

    public static void addOpens(Module from, Module to, String packageName) {
        if (!from.isOpen(packageName, to)) {
            try {
                RequiringReflection.implAddOpens.invoke(from, packageName, to);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class RequiringReflection {
        static final Method implAddOpens;

        static {
            final Class<?> clazz = Module.class;
            try {
                implAddOpens = clazz.getDeclaredMethod("implAddOpens", String.class, clazz);
            } catch (NoSuchMethodException e) {
                throw new ExceptionInInitializerError(e);
            }
            setAccessible(implAddOpens);
        }
    }
}