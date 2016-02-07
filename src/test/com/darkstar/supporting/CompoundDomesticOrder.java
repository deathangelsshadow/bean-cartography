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
@NamedClass(name = "Compound Domestic Order")
public class CompoundDomesticOrder {

    @NamedField(name = "people")
    private List<Person> peeps = null;
    @NamedField(name = "main address 2")
    private Address2 mainAddress = null;

    public CompoundDomesticOrder() {
        super();
        mainAddress = new Address2();
        peeps = new ArrayList<>();
    }

    public List<Person> getPeeps() {
        return peeps;
    }

    public void setPeeps(List<Person> peeps) {
        this.peeps = peeps;
    }

    public Address2 getMainAddress() {
        return mainAddress;
    }

    public void setMainAddress(Address2 mainAddress) {
        this.mainAddress = mainAddress;
    }

    @Override
    public String toString() {
        return "CompoundDomesticOrder{" +
                "peeps=" + peeps +
                ", mainAddress=" + mainAddress +
                '}';
    }
}

