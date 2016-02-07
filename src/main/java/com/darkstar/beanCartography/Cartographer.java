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
package com.darkstar.beanCartography;

import com.darkstar.beanCartography.utils.NameUtils;
import com.darkstar.beanCartography.utils.finder.Filter;
import com.darkstar.beanCartography.utils.finder.Finder;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 *  @author michael snavely
 */
public class Cartographer {

    private boolean copyCollections = false;

    // business field name to field formatter map...
    private final Map<String, FieldFormatter> fieldFormatters = new HashMap<>();

    /**
     * Constructor
     */
    public Cartographer() {
        super();
    }

    /**
     * Constructor
     *
     * @param copyCollections set to <code>true</code> if collections, maps, and arrays should be copied
     */
    public Cartographer(boolean copyCollections) {
        super();
        this.copyCollections = copyCollections;
    }

    /**
     * @param businessFieldName name to associate to the formatter
     * @param f formatter to use
     */
    public void addFieldFormatter(String businessFieldName, FieldFormatter f) {fieldFormatters.put(businessFieldName, f);}

    /**
     * @param map containing names to formatter mappings
     */
    public void addFieldFormatter(Map<String, FieldFormatter> map) {
        fieldFormatters.putAll(map);
    }

    /**
     * This is the main processing method.
     *
     * @param sourceObj object containing named field values to copy
     * @param targetObj object that will serve as the target of the copy
     */
    private void process(Object sourceObj, Object targetObj) {
        if (sourceObj == null)
            throw new IllegalArgumentException("sourceObj cannot be null");
        if (targetObj == null)
            throw new IllegalArgumentException("targetObj cannot be null");

        Finder walker = new Finder();

        // map the source object...
        Filter businessNameFilter = new NameFilter();
        NameInterceptor intercepter = new NameInterceptor();
        walker.addFilterIntecepter(businessNameFilter, intercepter);

        walker.find(sourceObj);
        Map<String, List<NamedClassBean>> sourceMap = intercepter.getNameToBusinessClassMap();

        walker = new Finder(false, false, false);
        // map the target object...
        businessNameFilter = new NameFilter();
        intercepter = new NameInterceptor();
        walker.addFilterIntecepter(businessNameFilter, intercepter);

        walker.find(targetObj);
        Map<String, List<NamedClassBean>> targetMap = intercepter.getNameToBusinessClassMap();

        /*
         * now attend to the business of matching and moving field data from source to target one field at a time...
         */

        // let's drive the process off of the map of the target object...
        String targetBusinessName = null;
        List<NamedClassBean> targetClassBeanList = null;
        for (Map.Entry<String, List<NamedClassBean>> entry : targetMap.entrySet()) {
            targetBusinessName = entry.getKey();
            targetClassBeanList = entry.getValue();

            boolean foundClassMatch = false;
            for (NamedClassBean targetBean : targetClassBeanList) {
                foundClassMatch = false;

                // process matching class name
                // we have a matching business CLASS name among the business class names
                // which means we have a matching business class type... (should i support the same business name on different types? --i dont think so)
                if (sourceMap.containsKey(targetBusinessName)) {
                    foundClassMatch = processBusinessClassName(targetBusinessName, targetBean, sourceMap);
                }

                // process composite classes
                else if (NameUtils.hasBusinessComposites(targetBean.getClazz())) {
                    // copy all fields from composites (if found)...
                    Set<String> processedFieldNames = processComposites(targetBean, sourceMap);

                    // remove processed field names if they are terminal types...
                    Iterator<NamePointerBean> it = targetBean.getFields().iterator();
                    NamePointerBean bnpb = null;
                    while (it.hasNext()) {
                        bnpb = it.next();
                        if (!NamePointerBean.NAME_TYPE.TERMINAL.equals(bnpb.getType()))
                            continue;
                        if (processedFieldNames.contains(bnpb.getName()))
                            it.remove();
                    }

                    // process the remaining fields...
                    for (NamePointerBean targetNameBean : targetBean.getFields()) {
                        processField(targetNameBean.getName(), targetBean, sourceMap);
                    }
                    foundClassMatch = true;
                }

                // check for the business CLASS name in the contained fields for all of the business classes...
                // this should end up being a class...
                else {
                    foundClassMatch = processField(targetBusinessName, targetBean, sourceMap);
                }

                // if we get here we have no matching business class names anywhere.  What we need to do now
                // is search through his contained business FIELDs for a match on business class or contained field...
                if (!foundClassMatch) {
                    for (NamePointerBean targetNameBean : targetBean.getFields()) {
                        if (!processBusinessClassName(targetNameBean.getName(), targetBean, sourceMap))
                            processField(targetNameBean.getName(), targetBean, sourceMap);
                    }
                }
            }
        }
    }

    /**
     * Use this mehtod to copy source named fields to target named fields.
     *
     * @param sourceObj object to use as the source
     * @param targetObj object to use as the target
     * @throws IllegalAccessException
     */
    public void mapObject(Object sourceObj, Object targetObj) throws IllegalAccessException {
        if (sourceObj == null)
            throw new IllegalArgumentException("sourceObj cannot be null");
        if (targetObj == null)
            throw new IllegalArgumentException("targetObj cannot be null");

        process(sourceObj, targetObj);
    }

    /**
     * This method will process the classes that have the composite annotation at the class level.  All composites
     * listed will be used to populate this class.
     *
     * @param targetBean bean containing target data
     * @param sourceMap map of named class beans
     * @return set of named fields that have been processed
     */
    private Set<String> processComposites(NamedClassBean targetBean, Map<String, List<NamedClassBean>> sourceMap) {
        if (targetBean == null)
            throw new IllegalArgumentException("cannot be null");
        String[] compositeNames = NameUtils.getBusinessComposites(targetBean.getClazz());
        Set<String> processedFieldNames = new HashSet<>();
        if (compositeNames == null || compositeNames.length == 0)
            return processedFieldNames;

        for (String businessName : compositeNames) {
            // we have a matching business name among the business class names
            // which means we have a matching business class type... (should i support the same business name on different types? --i dont think so)
            if (sourceMap.containsKey(businessName)) {
                List<NamedClassBean> sourceClassBeanList = sourceMap.get(businessName);

                // find the source bean having the same business name that we are looking for and copy the fields...
                for (NamedClassBean bcb : sourceClassBeanList) {
                    if (bcb.getName().equals(businessName)) {
                        NamedClassBean sourceBean = bcb;

                        // copy the matching class fields...

                        // get the container instances...
                        List<Object> targetInstances = targetBean.getInstances(); // HOW DO I PICK AN INSTANCE AGAIN?????  let's use the first one for now
                        Object targetInstance = targetInstances.get(0);

                        // get the business fields...
                        List<NamePointerBean> sourceFields = sourceBean.getFields();
                        List<NamePointerBean> targetFields = targetBean.getFields();

                        processedFieldNames.addAll(copyMatchingFields(sourceFields, targetFields, targetInstance, sourceMap));
                        break;
                    }
                }
            }
        }
        return processedFieldNames;
    }

    /**
     * If we are searching for a business class name then the only place that can be found will be in the business name
     * map!  All class level business names will exist as keys in this map.
     *
     * @param targetBusinessName target name
     * @param targetBean target bean
     * @param sourceMap map of source name beans
     * @return true if the business class name was found and the field contents were copied
     */
    private boolean processBusinessClassName(String targetBusinessName, NamedClassBean targetBean, Map<String, List<NamedClassBean>> sourceMap) {
        // we have a matching business name among the business class names
        // which means we have a matching business class type... (should i support the same business name on different types? --i dont think so)
        if (sourceMap.containsKey(targetBusinessName)) {
            List<NamedClassBean> sourceClassBeanList = sourceMap.get(targetBusinessName);

            // if the source contains the same bean as the target then copy the fields...
            if (sourceClassBeanList.contains(targetBean)) {
                NamedClassBean sourceBean = sourceClassBeanList.get(sourceClassBeanList.indexOf(targetBean));

                // copy the matching class fields...

                // get the container instances...
                List<Object> targetInstances = targetBean.getInstances(); // HOW DO I PICK AN INSTANCE AGAIN?????  let's use the first one for now
                Object targetInstance = targetInstances.get(0);

                // get the business fields...
                List<NamePointerBean> sourceFields = sourceBean.getFields();
                List<NamePointerBean> targetFields = targetBean.getFields();

                copyMatchingFields(sourceFields, targetFields, targetInstance, sourceMap);
            }
            return true;
        }
        return false;
    }

    /**
     * Copy source business field value to target field as long as their business names match and they are terminal
     * types!
     *
     * @param sourceFields list of source name beans
     * @param targetFields list of target fields
     * @param targetInstance target instance
     * @return List of business field names that were copied
     */
    private List<String> copyMatchingFields(List<NamePointerBean> sourceFields, List<NamePointerBean> targetFields, Object targetInstance, Map<String, List<NamedClassBean>> sourceMap) {
        List<String> copiedFields = new ArrayList<>();

        sourceFields.stream()
                .filter(targetFields::contains)
                .forEach(sourceField -> {
                    copyFieldContents(targetInstance, targetFields.get(targetFields.indexOf(sourceField)), sourceField);
                    copiedFields.add(sourceField.getName());
                });
        return copiedFields;
    }

    /**
     * This method will look through all of the source fields for a match on target business name.  If found that field
     * will be copied to the target provided it is a terminal field and not another complex object.
     *
     * @param targetBusinessName target name
     * @param targetBean target bean
     * @param sourceMap map of source names
     * @return <code>true</code> if match found
     */
    private boolean processField(String targetBusinessName, NamedClassBean targetBean, Map<String, List<NamedClassBean>> sourceMap) {
        List<Object> targetInstances = targetBean.getInstances(); // HOW DO I PICK AN INSTANCE AGAIN?????  let's use the first one for now
        Object instance = targetInstances.get(0);
        return processField(targetBusinessName, targetBean.getFields(), instance, sourceMap);
    }

    /**
     * This method will look through all of the source fields for a match on target business name.  If found that field
     * will be copied to the target provided it is a terminal field and not another complex object.
     *
     * @param targetBusinessName target name
     * @param targetFields target field list
     * @param targetInstance target instance
     * @param sourceMap map of source names
     * @return <code>true</code> if match found
     */
    private boolean processField(String targetBusinessName, List<NamePointerBean> targetFields, Object targetInstance, Map<String, List<NamedClassBean>> sourceMap) {
        boolean foundClassMatch = false;
        for (Map.Entry<String, List<NamedClassBean>> sourceMapEntry : sourceMap.entrySet()) {
            for (NamedClassBean sourceClassBean : sourceMapEntry.getValue()) {
                for (NamePointerBean sourceNameBean : sourceClassBean.getFields()) {
                    if (targetBusinessName.equals(sourceNameBean.getName())) {
                        // copy the field data...
                        copyFieldContents(targetInstance, targetFields.get(targetFields.indexOf(sourceNameBean)), sourceNameBean);
                        foundClassMatch = true;
                        break;
                    }
                }
                if (foundClassMatch)
                    break;
            }
            if (foundClassMatch)
                break;
        }
        return foundClassMatch;
    }

    /**
     * Create a bean instance from it's class.
     *
     * @param namePointer name pointer bean containing an instance
     */
    private static void createInstanceFromClass(NamePointerBean namePointer) {
        if (namePointer != null && namePointer.getInstance() == null) {
            try {
                namePointer.setInstance(namePointer.getClass().newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Copy data from one array to another.
     *
     * @param targetNameBean target bean
     * @param sourceNameBean source name bean
     */
    private void copyArray(NamePointerBean targetNameBean, NamePointerBean sourceNameBean) {
        // is this feature enabled?
        if (!copyCollections)
            return;
        // if the source bean has an instance associated with it (otherwise we ignore it)...
        if (sourceNameBean.getInstance() != null) {
            // if target bean array object is null create one and set it back into the target bean...
            if (targetNameBean.getInstance() == null) {
                targetNameBean.setInstance(Array.newInstance(targetNameBean.getField().getType().getComponentType(), Array.getLength(sourceNameBean.getInstance())));
            }

            final Class<?> targetElementClass;
            targetElementClass = targetNameBean.getInstance().getClass().getComponentType();

            // if the target bean array instance exists...
            if (targetNameBean.getInstance() != null) {
                Object sourceArray = sourceNameBean.getInstance();
                Object targetArray = targetNameBean.getInstance();

                final boolean isTerminal = NameUtils.isImmutable(targetElementClass);
                // for each element in the source collection create a new element for the target collection
                // populating it by recursively calling mapObject.  Once the new instance has been mapped,
                // add him to the target collection...
                Object sourceElement = null;
                for (int i = 0; i < Array.getLength(sourceArray); i++) {
                    try {
                        sourceElement = Array.get(sourceArray, i);
                        Object targetElement;
                        if (sourceElement == null || isTerminal)
                            targetElement = sourceElement;
                        else {
                            targetElement = targetElementClass.newInstance();
                            process(sourceElement, targetElementClass.cast(targetElement));
                        }
                        Array.set(targetArray, i, targetElement);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Copy source collection to a target collection.
     *
     * @param targetNameBean target name bean
     * @param sourceNameBean source name bean
     */
    private void copyCollection(NamePointerBean targetNameBean, NamePointerBean sourceNameBean) {
        // is this feature enabled?
        if (!copyCollections)
            return;
        // if the source bean has an instance associated with it (otherwise we ignore it)...
        if (sourceNameBean.getInstance() != null) {
            // if target bean collection object is null create one and set it back into the target bean...
            createInstanceFromClass(targetNameBean);

            final Class<?> targetElementClass;
            try {
                targetElementClass = Class.forName(((ParameterizedType) targetNameBean.getField().getGenericType()).getActualTypeArguments()[0].getTypeName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }
            // if the target bean collection instance exists...
            if (targetNameBean.getInstance() != null) {
                Collection<?> sourceCollection = (Collection<?>) sourceNameBean.getInstance();
                Collection<?> targetCollection = (Collection<?>) targetNameBean.getInstance();

                final boolean isTerminal = NameUtils.isImmutable(targetElementClass);
                // for each element in the source collection create a new element for the target collection
                // populating it by recursively calling mapObject.  Once the new instance has been mapped,
                // add him to the target collection...
                sourceCollection.stream()
                        .forEach(sourceElement -> {
                            try {
                                Object targetElement;
                                if (sourceElement == null || isTerminal)
                                    targetElement = sourceElement;
                                else {
                                    targetElement = targetElementClass.newInstance();
                                    process(sourceElement, targetElement);
                                }
                                Method add = null;
                                add = targetCollection.getClass().getDeclaredMethod("add", Object.class);
                                add.invoke(targetCollection, targetElement);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
            }
        }
    }

    /**
     * Copy source map to target map
     *
     * @param targetNameBean target name bean
     * @param sourceNameBean source name bean
     */
    private void copyMap(NamePointerBean targetNameBean, NamePointerBean sourceNameBean) {
        // is this feature enabled?
        if (!copyCollections)
            return;
        // if the source bean has an instance associated with it (otherwise we ignore it)...
        if (sourceNameBean.getInstance() != null) {
            // if target bean map object is null create one and set it back into the target bean...
            createInstanceFromClass(targetNameBean);

            final Class<?> targetKeyClass;
            final Class<?> targetValueClass;
            try {
                targetKeyClass = Class.forName(((ParameterizedType) targetNameBean.getField().getGenericType()).getActualTypeArguments()[0].getTypeName());
                targetValueClass = Class.forName(((ParameterizedType) targetNameBean.getField().getGenericType()).getActualTypeArguments()[1].getTypeName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }
            Map<?, ?> sourceMap = (Map<?, ?>) sourceNameBean.getInstance();
            Map<?, ?> targetMap = (Map<?, ?>) targetNameBean.getInstance();

            boolean isKeyTerminal = NameUtils.isImmutable(targetKeyClass);
            boolean isValueTerminal = NameUtils.isImmutable(targetValueClass);
            // for each element in the source collection create a new element for the target collection
            // populating it by recursively calling mapObject.  Once the new instance has been mapped,
            // add him to the target collection...
            sourceMap.entrySet().stream().forEach(entry -> {
                try {
                    Object targetKey;
                    if (isKeyTerminal)
                        targetKey = entry.getKey();
                    else {
                        targetKey = targetKeyClass.newInstance();
                        process(entry.getKey(), targetKey);
                    }

                    Object targetValue;
                    if (isValueTerminal)
                        targetValue = entry.getValue();
                    else {
                        if (entry.getValue() != null) {
                            targetValue = targetValueClass.newInstance();
                            process(entry.getValue(), targetValue);
                        } else
                            targetValue = null;
                    }

                    Method put = targetMap.getClass().getDeclaredMethod("put", Object.class, Object.class);
                    put.invoke(targetMap, targetKey, targetValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Copy source field value to target field value but ONLY FOR TERMINAL TYPES!  Class types will eventually get processed.
     * <p>
     * If the source field is a collection then recursively call the process again with the target object.  If the target
     * collection does not have an element one will be created.
     *
     * @param targetInstance target object instance
     * @param targetNameBean target name bean
     * @param sourceNameBean source name bean
     */
    private void copyFieldContents(Object targetInstance, NamePointerBean targetNameBean, NamePointerBean sourceNameBean) {
        // source field is a COLLECTION...
        if (NamePointerBean.NAME_TYPE.COLLECTION.equals(sourceNameBean.getType())) {
            copyCollection(targetNameBean, sourceNameBean);
        }
        // process MAPS...
        else if (NamePointerBean.NAME_TYPE.MAP.equals(sourceNameBean.getType())) {
            copyMap(targetNameBean, sourceNameBean);
        }
        // process ARRAYS...
        else if (NamePointerBean.NAME_TYPE.ARRAY.equals(sourceNameBean.getType())) {
            copyArray(targetNameBean, sourceNameBean);
        }
        // process TERMINAL fields...
        else if (NamePointerBean.NAME_TYPE.TERMINAL.equals(sourceNameBean.getType()) &&
                NamePointerBean.NAME_TYPE.TERMINAL.equals(targetNameBean.getType())) {
            try {
                sourceNameBean.getField().setAccessible(true);
                targetNameBean.getField().setAccessible(true);

                // we have a field formatter so use it...
                if (fieldFormatters.containsKey(targetNameBean.getName()))
                    targetNameBean.getField().set(targetInstance, fieldFormatters.get(targetNameBean.getName()).format(sourceNameBean.getInstance()));
                else
                    targetNameBean.getField().set(targetInstance, sourceNameBean.getInstance());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

