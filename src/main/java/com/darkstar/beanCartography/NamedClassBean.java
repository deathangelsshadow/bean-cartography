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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class encapsulates the data present on a Named Class annotation.  It captures all of the names
 * contained inside on its various fields.  This maps the hierarchy of the name structure.
 *
 * @author michael snavely
 */
public class NamedClassBean {

    private String name = null;
    private Class<?> clazz = null;
    private List<NamePointerBean> fields = null;
    private List<Object> instances = null;

    /**
     * Constructor
     */
    public NamedClassBean() {
        super();
        fields = new ArrayList<>();
        instances = new ArrayList<>();
    }

    /**
     * Constructor
     *
     * @param obj object to use
     */
    public NamedClassBean(Object obj) throws IllegalAccessException {
        this();
        instances.add(obj);
        clazz = obj.getClass();
        if (!NameUtils.hasBusinessName(clazz))
            throw new IllegalArgumentException();
        this.name = NameUtils.getBusinessName(obj.getClass());

        Map<String, List<Field>> classFieldMap = NameUtils.getFields(obj, true);

        // look for business field annotation...
        for (List<Field> classFields : classFieldMap.values())
            for (Field field : classFields)
                if (NameUtils.hasBusinessName(field))
                    fields.add(new NamePointerBean(obj, field));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NamedClassBean that = (NamedClassBean) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return !(clazz != null ? !clazz.equals(that.clazz) : that.clazz != null);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (clazz != null ? clazz.hashCode() : 0);
        return result;
    }

    public List<Object> getInstances() { return instances; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public List<NamePointerBean> getFields() {
        return fields;
    }

    @Override
    public String toString() {
        return "NamedClassBean{" +
                "name='" + name + '\'' +
                ", clazz=" + clazz +
                ", fields=" + fields +
                ", instances=" + instances +
                '}';
    }
}

