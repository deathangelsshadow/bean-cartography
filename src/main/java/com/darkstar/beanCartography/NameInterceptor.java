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

import com.darkstar.beanCartography.utils.finder.Interceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is Intercept processing for classes that contain named fields.
 *
 * @author michael snavely
 */
public class NameInterceptor implements Interceptor {

    // keeps the business name hierarchy...
    private Map<String, List<NamedClassBean>> nameToBusinessClassMap  = null;

    /**
     * Constructor
     */
    public NameInterceptor() {
        super();
        nameToBusinessClassMap = new HashMap<>();
    }

    /**
     * Collect all named classes under their name.
     *
     * @param o the object to be processed
     */
    @Override
    public void intercept(Object o) {
        try {
            NamedClassBean bean = new NamedClassBean(o);
            List<NamedClassBean> beans = nameToBusinessClassMap.get(bean.getName());
            if (beans == null) {
                beans = new ArrayList<>();
                nameToBusinessClassMap.put(bean.getName(), beans);
            }
            beans.add(bean);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the map of the name to bean list
     */
    public Map<String, List<NamedClassBean>> getNameToBusinessClassMap() {
        return nameToBusinessClassMap;
    }
}

