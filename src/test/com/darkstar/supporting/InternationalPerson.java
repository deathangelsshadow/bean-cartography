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
@NamedClass(name = "InternationalPerson")
public class InternationalPerson {

    @NamedField(name = "FirstName")
    private String firstName = null;

    @NamedField(name = "LastName")
    private String lastName = null;

    @NamedField(name = "ResidenceCountry")
    private String countryOfResidence = null;

    @NamedField(name = "Properties")
    private List<InternationalAddress> residences = new ArrayList<>();

    @NamedField(name = "Aliases")
    private List<String> aliases = new ArrayList<>();

    public InternationalPerson() {
        super();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCountryOfResidence() {
        return countryOfResidence;
    }

    public void setCountryOfResidence(String countryOfResidence) {
        this.countryOfResidence = countryOfResidence;
    }

    public List<InternationalAddress> getResidences() {
        return residences;
    }

    public void setResidences(List<InternationalAddress> residences) {
        this.residences = residences;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    @Override
    public String toString() {
        return "InternationalPerson{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", countryOfResidence='" + countryOfResidence + '\'' +
                ", residences=" + residences +
                ", aliases=" + aliases +
                '}';
    }
}

