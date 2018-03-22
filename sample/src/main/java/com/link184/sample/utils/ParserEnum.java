package com.link184.sample.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by eugeniu on 1/6/17.
 */

public class ParserEnum {

    public static <E extends Enum<E>, S> E parse(Class<E> enumType, S source) {
        for (E e : enumType.getEnumConstants()) {
            if (e.name().toLowerCase().equals(source)) {
                return e;
            }
        }
        return null;
    }

    public static <E extends Enum<E>> E parse(Class<E> enumType, long source) {
        for (E e : enumType.getEnumConstants()) {
            if (e.ordinal() == source) {
                return e;
            }
        }
        return null;
    }

    public static <E extends Enum<E>> E parse(Class<E> enumType, Map<String, Enum> sources) {
        for (E e : enumType.getEnumConstants()) {
            if (checkEnums(e, sources)) {
                return e;
            }
        }
        return null;
    }

    public static <E extends Enum<E>> E parse(Class<E> enumType, String source, String methodName) {
        try {
            Method method = enumType.getDeclaredMethod(methodName);
            for (E e : enumType.getEnumConstants()) {
                if (((String) method.invoke(e)).toLowerCase().equalsIgnoreCase(source)) {
                    return e;
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <E extends Enum<E>> E parse(Class<E> enumType, int source, String methodName) {
        try {
            Method method = enumType.getDeclaredMethod(methodName);
            for (E e : enumType.getEnumConstants()) {
                if ((int) method.invoke(e) == source) {
                    return e;
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static boolean checkEnums(Enum firstEnum, Map<String, Enum> sources) {
        boolean result = true;
        for (Map.Entry<String, Enum> entry: sources.entrySet()) {
            try {
                Method method = firstEnum.getClass().getDeclaredMethod(entry.getKey());
                result &= method.invoke(firstEnum).equals(entry.getValue());
            } catch (NullPointerException e) {
                e.printStackTrace();
                result = false;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                result = false;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                result = false;
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                result = false;
            }
        }
        return result;
    }
}
