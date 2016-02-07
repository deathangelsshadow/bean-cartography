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

/**
 * @author michael snavely
 */
@NamedClass(name = "InternationalPerson")
public class InternationalPersonArr {

    @NamedField(name = "FirstName")
    private String firstName   = null;

    @NamedField(name = "LastName")
    private String lastName    = null;

    @NamedField(name = "PhoneNumber")
    private String phoneNumber = null;

    @NamedField(name = "Properties")
    private InternationalAddress[] residences = null;

    /**
     * Builder
     */
    public static class Builder {
        private String firstName   = null;
        private String lastName    = null;
        private String phoneNumber = null;

        public Builder() {
            super();
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public InternationalPersonArr build() {
            return new InternationalPersonArr(this);
        }
    }

    /**
     * Person constructor from builder
     *
     * @param builder
     */
    private InternationalPersonArr(Builder builder) {
        super();
        this.firstName   = builder.firstName;
        this.lastName    = builder.lastName;
        this.phoneNumber = builder.phoneNumber;
    }

    /**
     * Person default constructor (required for name mapper.
     */
    public InternationalPersonArr() {
        super();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public InternationalAddress[] getResidences() {
        return residences;
    }

    public void setResidences(InternationalAddress[] residences) {
        this.residences = residences;
    }
}

