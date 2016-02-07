/*
 ******************************************************************************
 *  Copyright 2016 Michael Snavely
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************
 */
package com.darkstar.beanCartography.utils;

import com.darkstar.beanCartography.annotations.NamedClass;
import com.darkstar.beanCartography.annotations.NamedClassComposite;
import com.darkstar.beanCartography.annotations.NamedField;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.mutable.MutableBoolean;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * This class contains general utility methods for use by this project.
 *
 * @author michael snavely
 */
public class NameUtils {
    private static Set<Class<?>> immutableClasses = null;
    static {
        immutableClasses = new HashSet<>();

        immutableClasses.add(boolean.class);
        immutableClasses.add(Boolean.class);
        immutableClasses.add(char.class);
        immutableClasses.add(Character.class);
        immutableClasses.add(byte.class);
        immutableClasses.add(Byte.class);
        immutableClasses.add(short.class);
        immutableClasses.add(Short.class);
        immutableClasses.add(int.class);
        immutableClasses.add(Integer.class);
        immutableClasses.add(long.class);
        immutableClasses.add(Long.class);
        immutableClasses.add(float.class);
        immutableClasses.add(Float.class);
        immutableClasses.add(double.class);
        immutableClasses.add(Double.class);
        immutableClasses.add(void.class);
        immutableClasses.add(Void.class);

        immutableClasses.add(BigDecimal.class);
        immutableClasses.add(BigInteger.class);
        immutableClasses.add(Number.class);
        immutableClasses.add(String.class);
        immutableClasses.add(java.sql.Date.class);
        immutableClasses.add(Time.class);
        immutableClasses.add(Timestamp.class);
    }

    /**
     * For the purposes of this project, 'immutable' types will be those types that we do not want to
     * delve down into.  We want to treat them as if they were leaf nodes.
     *
     * @param clazz class to check for immutability
     * @return <code>true</code> if immutable
     */
    public static boolean isImmutable(Class<?> clazz) {
        return clazz == null || clazz.isEnum() || immutableClasses.contains(clazz);
    }

    /**
     * @param clazz class to check
     * @return <code>true</code> if the class has a name annotation on it
     */
    public static boolean hasBusinessName(Class<?> clazz) {
        Preconditions.checkNotNull(clazz, "Class cannot be null");
        return clazz.isAnnotationPresent(NamedClass.class);
    }

    /**
     * @param field Field to check
     * @return <code>true</code> if the Field object has a name annotation on it
     */
    public static boolean hasBusinessName(Field field) {
        Preconditions.checkNotNull(field, "Field cannot be null");
        return field.isAnnotationPresent(NamedField.class);
    }

    /**
     * @param clazz class to check
     * @return the name associated to the class
     */
    public static String getBusinessName(Class<?> clazz) {
        Preconditions.checkNotNull(clazz, "Class cannot be null");
        if (hasBusinessName(clazz))
            return clazz.getAnnotation(NamedClass.class).name();
        return null;
    }

    /**
     * @param field field to check
     * @return the name associated to the field
     */
    public static String getBusinessName(Field field) {
        Preconditions.checkNotNull(field, "Field cannot be null");
        if (hasBusinessName(field))
            return field.getAnnotation(NamedField.class).name();
        return null;
    }

    /**
     * @param clazz class to check
     * @return <code>true</code> if the class has a composite annotation
     */
    public static boolean hasBusinessComposites(Class<?> clazz) {
        Preconditions.checkNotNull(clazz, "Class cannot be null");
        return clazz.isAnnotationPresent(NamedClassComposite.class);
    }

    /**
     * @param clazz class to check
     * @return array of string names associated to the composite
     */
    public static String[] getBusinessComposites(Class<?> clazz) {
        Preconditions.checkNotNull(clazz, "Class cannot be null");
        if (!hasBusinessComposites(clazz))
            throw new IllegalArgumentException("not a business composite");
        return clazz.getAnnotation(NamedClassComposite.class).names();
    }

    /**
     * @param o object instanct to check
     * @return <code>true</code> if any fields have names associated with them
     */
    public static boolean hasFieldBusinessNames(Object o) {
        Preconditions.checkNotNull(o, "Object cannot be null");

        // look for business field annotation...
        Map<String, List<Field>> classFieldMap = getFields(o, true);
        final MutableBoolean hasBusinessName = new MutableBoolean(false);
        classFieldMap.values().stream()
            .forEach(fieldList -> {
                Optional<Field> fieldOpt = fieldList.stream().filter(NameUtils::hasBusinessName).findFirst();
                fieldOpt.ifPresent(field -> hasBusinessName.setValue(field != null));
            });
        return hasBusinessName.booleanValue();
    }

    /**
     * Return all types of fields declared by the passed source object's class.
     * If getParentFields is false then only return those fields declared by
     * the object's class passed.  Otherwise, return all fields up the parent
     * hierarchy.
     *
     * @param source object that is the source of the lookup.
     * @param includeSuperClasses if true follow the inheritance hierarchy.
     * @return A map containing a list of field objects for each class encountered.
     */
    public static <T> Map<String, List<Field>> getFields(T source, boolean includeSuperClasses) {
        Preconditions.checkNotNull(source, "source cannot be null!");
        Map<String, List<Field>> resultMap = new HashMap<>();
        Class<?> clazz                     = source.getClass();
        List<Field> resultList;

        do {
            resultList = new ArrayList<>();
            getClassFields(clazz, resultList, false);
            resultMap.put(clazz.getCanonicalName(), resultList);
            clazz = clazz.getSuperclass();
        } while (clazz != null && includeSuperClasses);

        return resultMap;
    }

    /**
     * Return public, private, protected, etc. fields declared by the passed
     * class in the list provided.
     *
     * @param clazz class to inspect
     * @param list field list to add newly discovered fields
     * @param includeSuperClasses <code>true</code> will cause superclasses to be included in the field collection
     */
    public static void getClassFields(Class<?> clazz, List<Field> list, boolean includeSuperClasses) {
        Preconditions.checkNotNull(clazz, "Class cannot be null!");
        Preconditions.checkNotNull(list, "List cannot be null!");
        Field[] fields = getClassFields(clazz, includeSuperClasses);
        list.addAll(Arrays.asList(fields));
    }

    /**
     * Return public, private, protected, etc. fields declared by the passed
     * class in a field array.
     *
     * @param clazz class to inspect
     * @param includeSuperClasses <code>true</code> will cause superclasses to be included in the field collection
     * @return array of Fields found
     */
    public static Field[] getClassFields(Class<?> clazz, boolean includeSuperClasses) {
        Preconditions.checkNotNull(clazz, "Class cannot be null!");
        List<Field> allFields = new ArrayList<>();
        do {
            allFields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        } while (includeSuperClasses && (clazz = clazz.getSuperclass()) != null);

        Field[] fields = new Field[allFields.size()];
        return allFields.toArray(fields);
    }
}

