package net.openhft.chronicle.core.util;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public enum GenericReflection {
    ;

    /**
     * Obtain the return types for method on this type
     *
     * @param type to scan
     * @return set of types
     */
    public static Set<Type> getMethodReturnTypes(Type type) {
        Set<Type> types = new LinkedHashSet<>();
        if (type instanceof Class || type instanceof ParameterizedType) {
            for (Method method : erase(type).getMethods()) {
                types.add(getReturnType(method, type));
            }
            return types;
        }
        throw new UnsupportedOperationException();
    }

    /**
     * Obtain the return type of a method as defined by a class or interface
     *
     * @param method to lookup
     * @param type   to look for the definition.
     * @return the return type
     */
    public static Type getReturnType(Method method, Type type) {
        final Type genericReturnType = method.getGenericReturnType();
        if (genericReturnType instanceof Class)
            return genericReturnType;
        final Class<?> declaringClass = method.getDeclaringClass();
        final Optional<? extends Type> extendsType = Stream.of(
                        Stream.of(getGenericSuperclass(type)), Stream.of(getGenericInterfaces(type)))
                .flatMap(s -> s)
                .filter(t -> declaringClass.equals(erase(t)))
                .findFirst();
        final Type[] typeParameters = declaringClass.getTypeParameters();
        if (extendsType.isPresent() && extendsType.get() instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType) extendsType.get();
            final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            for (int i = 0; i < typeParameters.length; i++)
                if (typeParameters[i].equals(genericReturnType))
                    return actualTypeArguments[i];
        }
        return method.getGenericReturnType();
    }

    static Type[] getGenericInterfaces(Type forClass) {
        if (forClass instanceof Class)
            return ((Class) forClass).getGenericInterfaces();
        if (forClass instanceof ParameterizedType) {
            return new Type[]{forClass};
        }
        throw new UnsupportedOperationException();
    }

    static Type getGenericSuperclass(Type forClass) {
        if (forClass instanceof Class)
            return ((Class) forClass).getGenericSuperclass();
        if (forClass instanceof ParameterizedType) {
            return null;
        }
        throw new UnsupportedOperationException();
    }

    /**
     * Obatin the raw type if available.
     *
     * @param type to erase
     * @return raw class
     */
    public static Class<?> erase(Type type) {
        return type instanceof ParameterizedType
                ? erase(((ParameterizedType) type).getRawType())
                : (Class) type;
    }
}
