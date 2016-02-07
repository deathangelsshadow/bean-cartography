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
package com.darkstar.beanCartography.utils;

import com.darkstar.supporting.Order;
import com.darkstar.supporting.Person;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.GregorianCalendar;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

/**
 * @author michael snavely
 */
public class NameUtilsTest {

    @Test
    public void hasBusinessNameClassTest() {
        try {
            NameUtils.hasBusinessName((Class<?>)null);
            assertTrue(false);
        } catch (NullPointerException e) {
            assertThat(e.getMessage(), equalTo("Class cannot be null"));
        }

        assertTrue(NameUtils.hasBusinessName(Person.class));
    }

    @Test
    public void hasBusinessNameFieldTest() throws NoSuchFieldException {
        try {
            NameUtils.hasBusinessName((Field)null);
            assertTrue(false);
        } catch (NullPointerException e) {
            assertThat(e.getMessage(), equalTo("Field cannot be null"));
        }

        assertTrue(NameUtils.hasBusinessName(Person.class.getDeclaredField("firstName")));
    }

    @Test
    public void getBusinessNameClassTest() {
        try {
            NameUtils.getBusinessName((Class<?>)null);
            assertTrue(false);
        } catch (NullPointerException e) {
            assertThat(e.getMessage(), equalTo("Class cannot be null"));
        }

        assertThat(NameUtils.getBusinessName(Person.class), equalTo("Person"));
    }

    @Test
    public void getBusinessNameFieldTest() throws NoSuchFieldException {
        try {
            NameUtils.getBusinessName((Field)null);
            assertTrue(false);
        } catch (NullPointerException e) {
            assertThat(e.getMessage(), equalTo("Field cannot be null"));
        }

        assertThat(NameUtils.getBusinessName(Person.class.getDeclaredField("firstName")), equalTo("FirstName"));
    }

    @Test
    public void hasBusinessCompositesTest() {
        try {
            NameUtils.getBusinessName((Class<?>)null);
            assertTrue(false);
        } catch (NullPointerException e) {
            assertThat(e.getMessage(), equalTo("Class cannot be null"));
        }

        assertTrue(NameUtils.hasBusinessComposites(Order.class));
        assertFalse(NameUtils.hasBusinessComposites(GregorianCalendar.class));
    }

    @Test
    public void getBusinessCompositesTest() {
        try {
            NameUtils.getBusinessComposites((Class<?>)null);
            assertTrue(false);
        } catch (NullPointerException e) {
            assertThat(e.getMessage(), equalTo("Class cannot be null"));
        }

        try {
            NameUtils.getBusinessComposites(GregorianCalendar.class);
            assertTrue(false);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), equalTo("not a business composite"));
        }

        assertThat(NameUtils.getBusinessComposites(Order.class), equalTo(new String[] {"Person", "Item"}));
    }

    @Test
    public void hasFieldBusinessNamesTest() {
        try {
            NameUtils.hasFieldBusinessNames(null);
            assertTrue(false);
        } catch (NullPointerException e) {
            assertThat(e.getMessage(), equalTo("Object cannot be null"));
        }

        assertFalse(NameUtils.hasFieldBusinessNames(new GregorianCalendar()));
        assertTrue(NameUtils.hasFieldBusinessNames(new Order()));
    }
}

