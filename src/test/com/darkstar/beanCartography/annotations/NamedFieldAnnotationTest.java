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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author michael snavely
 */
public class NamedFieldAnnotationTest {

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

        List<Field> annotatedFields = new ArrayList<>();
        Field[] fields = person.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(NamedField.class))
                annotatedFields.add(field);
        }

        List<String> annotationValidation = new ArrayList<>();
        annotationValidation.add("FirstName");
        annotationValidation.add("LastName");
        annotationValidation.add("PhoneNumber");
        annotationValidation.add("Properties");

        assertEquals(4, annotatedFields.size());
        for (Field field : annotatedFields) {
            System.out.println(field.getDeclaredAnnotation(NamedField.class).name());
            assertTrue(annotationValidation.contains(field.getDeclaredAnnotation(NamedField.class).name()));
            annotationValidation.remove(field.getDeclaredAnnotation(NamedField.class).name());
        }
        assertTrue(annotationValidation.isEmpty());
    }
}

