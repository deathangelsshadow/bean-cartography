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
package com.darkstar.beanCartography.annotations;

import com.darkstar.supporting.Address;
import com.darkstar.supporting.Person;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author michael snavely
 */
public class BusinessCollectionAnnotationTest {

    @Test
    public void test() throws NoSuchFieldException, IllegalAccessException {
        Person person = new Person.Builder().firstName("joe").lastName("schmoe").phoneNumber("402-555-1212").build();
        assertNotNull(person);

        Address addr = new Address();
        assertNotNull(addr);
        person.getResidences().add(addr);

        NamedClass bc = person.getClass().getAnnotation(NamedClass.class);
        assertNotNull(bc);
        assertEquals("Person", bc.name());

        Field coll = person.getClass().getDeclaredField("residences");
        assertNotNull(coll);

        assertTrue(coll.isAnnotationPresent(NamedField.class));
        NamedField bf = coll.getAnnotation(NamedField.class);
        assertNotNull(bf);
        assertEquals("Properties", bf.name());

        coll.setAccessible(true);
        List<Address> addrs = (List<Address>) coll.get(person);
        assertNotNull(addrs);
        assertTrue(addrs.size() > 0);

        for (Address a : addrs) {
            assertEquals("Address", a.getClass().getAnnotation(NamedClass.class).name());
        }
    }
}

