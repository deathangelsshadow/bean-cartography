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

import com.darkstar.beanCartography.NamePointerBean;
import com.darkstar.supporting.*;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

/**
 * @author michael snavely
 */
public class NamePointerBeanTest {

    @Test
    public void test() throws NoSuchFieldException, IllegalAccessException {
        Address address = new Address();
        address.setCity("Bellevue");
        address.setLine1("123 Main St");
        address.setLine2(null);
        address.setState("NE");
        address.setZip("686147");

        Field f = address.getClass().getDeclaredField("city");
        f.setAccessible(true);
        NamePointerBean bnpb = new NamePointerBean(address, f);
        assertThat(bnpb, notNullValue());
        assertThat(bnpb.getName(), equalTo("City"));
        assertThat(bnpb.getType().toString(), equalTo("TERMINAL"));
        assertThat(bnpb.getInstance(), equalTo("Bellevue"));

        f = address.getClass().getDeclaredField("line2");
        f.setAccessible(true);
        bnpb = new NamePointerBean(address, f);
        assertThat(bnpb, notNullValue());
        assertThat(bnpb.getName(), equalTo("LineTwo"));
        assertThat(bnpb.getType().toString(), equalTo("TERMINAL"));
        assertThat(bnpb.getInstance(), nullValue());
    }
}

