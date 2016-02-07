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

import com.darkstar.supporting.Person;
import org.junit.Test;

import java.lang.annotation.Annotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author michael snavely
 */
public class NamedClassAnnotationTest {

    @Test
    public void test() {
        Person person = new Person.Builder()
                .firstName("jim")
                .lastName("smith")
                .phoneNumber("(402)555-1212")
                .build();

        assertNotNull(person);
        assertEquals("jim", person.getFirstName());
        assertEquals("smith", person.getLastName());
        assertEquals("(402)555-1212", person.getPhoneNumber());

        assertTrue(person.getClass().isAnnotationPresent(NamedClass.class));

        // retrieve the class annotations... (should only be one)...
        Annotation[] annotations = person.getClass().getAnnotationsByType(NamedClass.class);
        assertNotNull(annotations);
        assertEquals(1, annotations.length);

        System.out.println(((NamedClass) annotations[0]).name());
        assertEquals("Person", ((NamedClass) annotations[0]).name());
    }
}

