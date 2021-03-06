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
package com.darkstar.supporting;

import com.darkstar.beanCartography.annotations.NamedClass;
import com.darkstar.beanCartography.annotations.NamedField;

import java.util.HashMap;
import java.util.Map;

/**
 * @author michael snavely
 */
@NamedClass(name = "PhoneToCustomerRepository")
public class PhoneToCustomerRepository {

    @NamedField(name = "PhoneToPersonMap")
    private Map<String, Customer> map = new HashMap<>();

    public PhoneToCustomerRepository() {
        super();
    }

    public Map<String, Customer> getMap() {
        return map;
    }

    public void setMap(Map<String, Customer> map) {
        this.map = map;
    }

    @Override
    public String toString() {
        return "PhoneToCustomerRepository{" +
                "map=" + map +
                '}';
    }
}

