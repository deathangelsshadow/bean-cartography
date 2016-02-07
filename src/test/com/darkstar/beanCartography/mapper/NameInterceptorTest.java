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
import com.darkstar.beanCartography.NameInterceptor;
import com.darkstar.beanCartography.NamePointerBean;
import com.darkstar.supporting.*;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;


/**
 * @author michael snavely
 */
public class NameInterceptorTest {

    @Test
    public void test() {
        Address address = new Address();
        address.setCity("Bellevue");
        address.setLine1("123 Main St");
        address.setLine2("attn: accounts payable");
        address.setState("NE");
        address.setZip("686147");

        Customer customer = new Customer.Builder().firstName("Joe").lastName("Schmoe").phoneNumber("4025551212").build();
        customer.setResidences(address);

        NameInterceptor intercepter = new NameInterceptor();
        assertThat(intercepter, notNullValue());

        intercepter.intercept(address);
        intercepter.intercept(customer);

        Map<String, List<NamedClassBean>> results = intercepter.getNameToBusinessClassMap();
        assertThat(results, notNullValue());
        assertThat(results.size(), equalTo(2));

        for (Map.Entry<String, List<NamedClassBean>> entry : results.entrySet()) {
            System.out.println("Entry Name: "+entry.getKey());
            for (NamedClassBean bean : entry.getValue()) {
                System.out.println("Business Class Bean Contents:");
                for (NamedClassBean bcb : entry.getValue()) {
                    for (NamePointerBean bnpb : bcb.getFields())
                        System.out.println(bnpb);
                }
            }
        }
    }
}
