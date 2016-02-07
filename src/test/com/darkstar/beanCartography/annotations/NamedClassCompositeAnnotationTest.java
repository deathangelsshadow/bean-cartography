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

import com.darkstar.supporting.Order;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author michael snavely
 */
public class NamedClassCompositeAnnotationTest {

    @Test
    public void test() {
        Order order = new Order();

        assertTrue(order.getClass().isAnnotationPresent(NamedClassComposite.class));
        NamedClassComposite bcc = order.getClass().getAnnotation(NamedClassComposite.class);
        assertNotNull(bcc);
        String[] names = bcc.names();
        assertNotNull(names);

        List<String> namesValidation = new ArrayList<>();
        namesValidation.add("Person");
        namesValidation.add("Item");

        for (String name : names) {
            System.out.println(name);
            assertTrue(namesValidation.contains(name));
            namesValidation.remove(name);
        }

        assertTrue(namesValidation.isEmpty());
    }
}

