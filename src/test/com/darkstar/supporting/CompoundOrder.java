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

import java.util.ArrayList;
import java.util.List;

/**
 * @author michael snavely
 */
@NamedClass(name = "Compound Order")
public class CompoundOrder {

    @NamedField(name = "people")
    private List<Person> internationalPeople = null;
    @NamedField(name = "main address")
    private Address mainAddress = null;

    public CompoundOrder() {
        super();
        internationalPeople = new ArrayList<>();
    }

    public List<Person> getInternationalPeople() {
        return internationalPeople;
    }

    public void setInternationalPeople(List<Person> internationalPeople) {
        this.internationalPeople = internationalPeople;
    }

    public Address getMainAddress() {
        return mainAddress;
    }

    public void setMainAddress(Address mainAddress) {
        this.mainAddress = mainAddress;
    }

    @Override
    public String toString() {
        return "CompoundOrder{" +
                "internationalPeople=" + internationalPeople +
                ", mainAddress=" + mainAddress +
                '}';
    }
}

