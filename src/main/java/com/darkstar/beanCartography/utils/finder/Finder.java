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
package com.darkstar.beanCartography.utils.finder;

import com.darkstar.beanCartography.utils.NameUtils;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.mutable.MutableObject;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * This class will scan an object and its contents passing each through a filter.  If the Filter determines that
 * the object needs further processing then that processing will be deferred to the Interceptor mapped to that Filter.
 *
 * @author michael snavely
 */
public class Finder {

    /**
     * This BeanContext class will retrieve and find the field values contained by a source object.
     *
     * @author michael snavely
     */
    private static class BeanContext {
        private Object source = null;
        private List<Object> fieldValueList = null;
        private Iterator<Object> fieldIt = null;

        /**
         * Constructor
         *
         * @param source the object that will be inspected
         */
        public BeanContext(Object source) {
            super();
            Preconditions.checkNotNull(source, "Parameter source must not be null!");
            this.source = source;
            init();
        }

        /**
         * This method will initialize the field values list from the contents of the source object.
         */
        private void init() {
            fieldValueList = new ArrayList<>();
            MutableObject clazz = new MutableObject(source.getClass());
            List<Field> fields;

            // retrieve and add the field values of the source object as well as his ancestors...
            do {
                fields = Arrays.asList(((Class<?>) clazz.getValue()).getDeclaredFields());
                fields.stream()
                        .forEach(field -> {
                            field.setAccessible(true);
                            try {
                                fieldValueList.add(field.get(((Class<?>) clazz.getValue()).cast(source)));
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        });
                clazz.setValue(((Class<?>) clazz.getValue()).getSuperclass());
            } while (clazz.getValue() != null);
        }

        /**
         * @return the source object
         */
        public Object getSource() {
            return source;
        }

        /**
         * Use this method to reset the field iterator to the beginning.
         */
        public void resetFieldIt() {
            fieldIt = null;
        }

        /**
         * @return <code>true</code> if the iterator has another field value
         */
        public boolean hasNextFieldValue() { return getFieldValueIt().hasNext(); }

        /**
         * @return the value of the next field
         */
        public Object nextFieldValue() {
            return hasNextFieldValue() ? getFieldValueIt().next() : null;
        }

        /**
         * @return the field values iterator preserving its current position
         */
        private Iterator<Object> getFieldValueIt() {
            if (fieldIt == null)
                fieldIt = fieldValueList.iterator();
            return fieldIt;
        }

        /*
         * equals and hashcode are based off of the source object...
         */

        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;
            if (this == obj)
                return true;
            if (!(obj instanceof BeanContext))
                return false;
            BeanContext other = (BeanContext) obj;
            return source.equals(other.source);
        }

        @Override
        public int hashCode() {
            return (source == null) ? super.hashCode() : source.hashCode();
        }
    }

    private Map<Filter, Interceptor> filtersInterceptors = null;

    private boolean processArrays      = true;
    private boolean processMaps        = true;
    private boolean processCollections = true;

    /**
     * Constructor
     */
    public Finder() {
        super();
        filtersInterceptors = new HashMap<>();
    }

    /**
     * Constructor
     *
     * @param processArrays <code>true</code> if arrays should be included in the mapping process
     * @param processMaps <code>true</code> if maps should be included in the mapping process
     * @param processCollections <code>true</code> if collections should be included in the mapping process
     */
    public Finder(boolean processArrays, boolean processMaps, boolean processCollections) {
        this();
        this.processArrays      = processArrays;
        this.processMaps        = processMaps;
        this.processCollections = processCollections;
    }

    /**
     * Add a filter and its corresponding interceptor.
     *
     * @param f filter
     * @param i interceptor
     */
    public void addFilterIntecepter(Filter f, Interceptor i) {
        Preconditions.checkNotNull(f, "Filter cannot be null!");
        Preconditions.checkNotNull(i, "Interceptor cannot be null!");
        filtersInterceptors.put(f, i);
    }

    /**
     * Search through all contained objects.  Those matching a filter will have
     * the corresponding interceptor executed.
     *
     * @param target object to search
     */
    public void find(Object target) {
        if (target == null)
            return;
        Deque<BeanContext> stack = new LinkedList<>();
        Set<BeanContext> visited = new LinkedHashSet<>();
        stack.push(new BeanContext(target));

        while(!stack.isEmpty())
            visit(stack, visited);
    }

    /**
     * Process the bean context stack.
     *
     * @param stack stack of objects left to search
     * @param visited set of objects already searched
     */
    protected void visit(Deque<BeanContext> stack, Set<BeanContext> visited) {
        BeanContext target = stack.pop();
        if (target == null)
            return;

        if (visited.contains(target))
            return;
        visited.add(target);

        // process this object and check the filters.  if passed filter then run interceptors...
        filtersInterceptors.entrySet().stream()
                .filter(entry -> entry.getKey().accept(target.getSource()))
                .forEach(entry -> entry.getValue().intercept(target.getSource()));

        // process this object's contained objects (i.e. see what we need to add to the stack)...
        if (NameUtils.isImmutable(target.getSource().getClass()))
            return;
        Object fieldValue = null;
        try {
            while(target.hasNextFieldValue()) {
                fieldValue = target.nextFieldValue();

                // skip nulls...
                if (fieldValue == null)
                    continue;

                // add pojo or container or whatever this is...
                if (!visited.contains(fieldValue) && !stack.contains(fieldValue))
                    stack.add(new BeanContext(fieldValue));

                // arrays...
                if (fieldValue.getClass().isArray()) {
                    if (!processArrays)
                        continue;
                    final Object arrayFieldValue = fieldValue;
                    IntStream.range(0, Array.getLength(arrayFieldValue))
                            .forEach(i -> {
                                Object element = Array.get(arrayFieldValue, i);
                                if (element != null && !visited.contains(element) && !stack.contains(element))
                                    stack.add(new BeanContext(element));
                            });

                // collections...
                } else if (fieldValue instanceof Collection<?>) {
                    if (!processCollections)
                        continue;
                    ((Collection<?>)fieldValue).stream()
                            .filter(element -> element != null && !visited.contains(element) && !stack.contains(element))
                            .forEach(element -> stack.add(new BeanContext(element)));

                // maps...
                } else if (fieldValue instanceof Map<?, ?>) {
                    if (!processMaps)
                        continue;
                    ((Map<?,?>)fieldValue).entrySet().stream()
                            .forEach(entry -> {
                                if (entry.getKey() != null && !visited.contains(entry.getKey()) && !stack.contains(entry.getKey()))
                                    stack.add(new BeanContext(entry.getKey()));
                                if (entry.getValue() != null && !visited.contains(entry.getValue()) && !stack.contains(entry.getValue()))
                                    stack.add(new BeanContext(entry.getValue()));
                            });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

