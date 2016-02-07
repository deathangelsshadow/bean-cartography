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
package com.darkstar.beanCartography.mapper;

import com.darkstar.beanCartography.NamedClassBean;
import com.darkstar.beanCartography.NamePointerBean;
import com.darkstar.beanCartography.utils.Pair;
import com.darkstar.supporting.*;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertTrue;

/**
 * @author michael snavely
 */
public class NamedClassBeanTest {

    @Test
    public void testBusinessClass() throws IllegalAccessException {
        Address address = new Address();
        address.setCity("Bellevue");
        address.setLine1("123 Main St");
        address.setLine2("attn: accounts payable");
        address.setState("NE");
        address.setZip("686147");

        NamedClassBean bb = null;
        bb = new NamedClassBean(address);
        assertThat(bb, notNullValue());

        assertThat(bb.getName(), equalTo("Address"));
        assertThat(bb.getClazz().getCanonicalName(), equalTo(address.getClass().getCanonicalName()));
        assertThat(bb.getFields().size(), equalTo(5));

        for (NamePointerBean nameBean : bb.getFields()) {
            System.out.println("\n"+nameBean);
            assertThat(nameBean.getName(), notNullValue());
            assertThat(nameBean.getType().toString(), equalTo("TERMINAL"));
            assertThat(nameBean.getInstance().getClass(), equalTo(String.class));
        }
    }

    @Test
    public void testNonBusinessClass() throws IllegalAccessException {
        Pair<String, String> pair = new Pair<>("testKey", "testValue");

        NamedClassBean bb = null;
        try {
            bb = new NamedClassBean(pair);
            assertTrue(false);

        } catch (IllegalArgumentException e) {}
    }

    @Test
    public void testMixedBusinessClass() throws IllegalAccessException {
        Address2 address = new Address2();
        address.setCity("Bellevue");
        address.setLine1("123 Main St");
        address.setLine2("attn: accounts payable");
        address.setState("NE");
        address.setZip("686147");

        NamedClassBean bb = null;
        bb = new NamedClassBean(address);
        assertThat(bb, notNullValue());

        assertThat(bb.getName(), equalTo("Address2"));
        assertThat(bb.getClazz().getCanonicalName(), equalTo(address.getClass().getCanonicalName()));
        assertThat(bb.getFields().size(), equalTo(3));

        for (NamePointerBean nameBean : bb.getFields()) {
            System.out.println("\n"+nameBean);
            assertThat(nameBean.getName(), notNullValue());
            assertThat(nameBean.getType().toString(), equalTo("TERMINAL"));
            assertThat(nameBean.getInstance().getClass(), equalTo(String.class));
        }
    }
}

