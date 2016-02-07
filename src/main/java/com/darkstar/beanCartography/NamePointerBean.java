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
import com.google.common.base.Preconditions;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 * This class encapsulates the information for a field that has the named field annotation on it.
 * (i.e. homeAddress would refer to an Address class.)  The name pointer may refer to a business class, a
 * terminal (i.e. primitive) type, a collection, or a map.  In short, this class describes the field that points to a
 * named class.
 *
 * @author michael snavely
 */
public class NamePointerBean {

    public static enum NAME_TYPE {BUSINESS_CLASS, ARRAY, COLLECTION, MAP, TERMINAL};

    private String name           = null;
    private NAME_TYPE type        = null;
    private Object instance       = null; // points to the field contents
    private Field field           = null;

    private Object fieldContainer = null;

    /**
     * Constructor
     */
    public NamePointerBean() {
        super();
    }

    /**
     * Constructor
     *
     * @param fieldContainer object containing the field
     * @param field field to use
     * @throws IllegalAccessException
     */
    public NamePointerBean(Object fieldContainer, Field field) throws IllegalAccessException {
        super();
        Preconditions.checkNotNull(fieldContainer, "fieldContainer cannot be null");
        Preconditions.checkNotNull(field, "field cannot be null");
        Preconditions.checkArgument(NameUtils.hasBusinessName(field), "passed field does not have a name associated with it! "+field.getName());

        this.fieldContainer = fieldContainer;
        this.field = field;
        this.name = NameUtils.getBusinessName(field);
        field.setAccessible(true);
        this.instance = field.get(fieldContainer);

        if (instance != null && instance.getClass().isArray())
            type = NAME_TYPE.ARRAY;
        else if (instance instanceof Collection<?>)
            type = NAME_TYPE.COLLECTION;
        else if (instance instanceof Map<?, ?>)
            type = NAME_TYPE.MAP;
        else if (NameUtils.isImmutable(field.getType()))
            type = NAME_TYPE.TERMINAL;
        else
            type = NAME_TYPE.BUSINESS_CLASS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NamePointerBean that = (NamePointerBean) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NAME_TYPE getType() {
        return type;
    }

    public void setType(NAME_TYPE type) {
        this.type = type;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
        try {
            field.set(fieldContainer, instance);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public Field getField() {return field;}

    @Override
    public String toString() {
        return "NamePointerBean{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", instance=" + instance +
                ", field=" + field +
                '}';
    }
}

