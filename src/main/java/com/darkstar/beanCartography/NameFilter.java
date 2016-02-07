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

/**
 * This class will accept only those classes that are annotated at the class or field level with business names.
 *
 * @author michael snavely
 */
public class NameFilter implements Filter {

    /**
     * Constructor
     */
    public NameFilter() {
        super();
    }

    /**
     * @param o object to check
     * @return <code>true</code> if the object has the specified annotation
     */
    @Override
    public boolean accept(Object o) {
        if (o == null)
            return false;

        return (NameUtils.hasBusinessName(o.getClass())       ||
                NameUtils.hasBusinessComposites(o.getClass()) ||
                NameUtils.hasFieldBusinessNames(o));
    }
}

