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

import com.darkstar.beanCartography.Cartographer;
import com.darkstar.supporting.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertTrue;

/**
 * Since the target (i.e. new) object map drives this process, if there are null objects in the target they will
 * be skipped -- even if the source has them.  Conversely, if the target has an object that the source does not (null)
 * then no fields will be changed on the target.
 *
 * @author michael snavely
 */
public class CartographerTest {

    @Test
    public void testFieldFormattersWithDifferentObjectsOrderForm() throws IllegalAccessException {
        Customer customer = new Customer.Builder().firstName("Joe").lastName("Blow").phoneNumber("4025551212").build();
        Address addr = new Address();
        addr.setLine1("123 Main Street");
        addr.setLine2("Attn: Payroll");
        addr.setCity("Bellevue");
        addr.setState("NE");
        addr.setZip("68147");
        customer.setResidences(addr);
        OrderForm orderForm = new OrderForm();
        Cartographer mapper = new Cartographer();

        mapper.addFieldFormatter("PhoneNumber", obj -> {
            StringBuilder buff = new StringBuilder(obj.toString());
            if (buff.length() == 10) {
                buff.insert(0, '(');
                buff.insert(4, ')');
                buff.insert(8, '-');
            }
            return buff.toString();
        });

        mapper.addFieldFormatter("LineOne", obj -> {
            StringBuilder buff = new StringBuilder(obj.toString());
            int i = buff.indexOf(" Street");
            if (i > -1)
                buff.replace(i, i+7, " St");
            return buff.toString();
        });

        assertThat(customer, notNullValue());
        assertThat(customer.getResidences(), notNullValue());
        assertThat(mapper, notNullValue());

        mapper.mapObject(customer, orderForm);

        assertThat("Joe", equalTo(orderForm.getFirstName()));
        assertThat("Blow", equalTo(orderForm.getLastName()));
        assertThat("(402)555-1212", equalTo(orderForm.getPhoneNumber()));

        assertThat("123 Main St", equalTo(orderForm.getLine1()));
        assertThat("Attn: Payroll", equalTo(orderForm.getLine2()));
        assertThat("Bellevue", equalTo(orderForm.getCity()));
        assertThat("NE", equalTo(orderForm.getState()));
        assertThat("68147", equalTo(orderForm.getZip()));
    }

    @Test
    public void mixedComplexTest() throws Exception {
        CompoundOrder order1 = new CompoundOrder();
        Person iperson = new Person.Builder().firstName("John").lastName("Doe").phoneNumber("4025551212").build();

        List<Address> iaddrs = new ArrayList<>();
        Address iaddr = new Address();
        iaddr.setState("IA");
        iaddr.setLine2("");
        iaddr.setLine1("456 second St");
        iaddrs.add(iaddr);
        iaddr = new Address();
        iaddr.setState("KY");
        iaddr.setLine2("");
        iaddr.setLine1("789 third St");
        iaddrs.add(iaddr);
        iperson.setResidences(iaddrs);
        order1.getInternationalPeople().add(iperson);

        Address addr = new Address();
        addr.setCity("Bellevue");
        addr.setLine1("123 Main St");
        addr.setLine2("");
        addr.setState("NE");
        addr.setZip("68147");
        order1.setMainAddress(addr);

        CompoundDomesticOrder order2 = new CompoundDomesticOrder();

        Cartographer mapper = new Cartographer(true);
        mapper.mapObject(order1, order2);

        assertThat(order2, notNullValue());
        assertThat(order2.getMainAddress(), notNullValue());
        assertThat(order2.getMainAddress().getState(), equalTo("NE"));
        assertThat(order2.getMainAddress().getLine1(), equalTo("123 Main St"));
        assertThat(order2.getMainAddress().getZip(), equalTo("68147"));

        assertThat(order2.getPeeps(), notNullValue());
        assertThat(order2.getPeeps().size(), equalTo(1));
        assertThat(order2.getPeeps().get(0).getFirstName(), equalTo("John"));
        assertThat(order2.getPeeps().get(0).getLastName(), equalTo("Doe"));
        assertThat(order2.getPeeps().get(0).getPhoneNumber(), equalTo("4025551212"));

        assertThat(order2.getPeeps().get(0).getResidences(), notNullValue());
        assertThat(order2.getPeeps().get(0).getResidences().size(), equalTo(2));
        assertThat(order2.getPeeps().get(0).getResidences().get(0).getState(), equalTo("IA"));
        assertThat(order2.getPeeps().get(0).getResidences().get(0).getLine1(), equalTo("456 second St"));
        assertThat(order2.getPeeps().get(0).getResidences().get(0).getLine2(), equalTo(""));
        assertThat(order2.getPeeps().get(0).getResidences().get(1).getState(), equalTo("KY"));
        assertThat(order2.getPeeps().get(0).getResidences().get(1).getLine1(), equalTo("789 third St"));
        assertThat(order2.getPeeps().get(0).getResidences().get(1).getLine2(), equalTo(""));
    }

    @Test
    public void arrayTestHeterogenius() throws IllegalAccessException {
        PersonArr person = new PersonArr.Builder()
                .phoneNumber("402-555-1212")
                .firstName("Joe")
                .lastName("Schmoe")
                .build();

        Address addr1 = new Address();
        addr1.setLine1("123 Main Street");
        addr1.setLine2("");
        addr1.setCity("Ralston");
        addr1.setState("NE");
        addr1.setZip("68127");

        Address addr2 = new Address();
        addr2.setLine1("456 First Street");
        addr2.setLine2("");
        addr2.setCity("Omaha");
        addr2.setState("NE");
        addr2.setZip("68154");

        Address[] addrs = new Address[2];
        addrs[0] = addr1;
        addrs[1] = addr2;
        person.setResidences(addrs);

        InternationalPersonArr iPerson = new InternationalPersonArr();

        Cartographer mapper = new Cartographer(true);
        mapper.mapObject(person, iPerson);

        assertThat(iPerson, notNullValue());
        assertThat(iPerson.getFirstName(), equalTo("Joe"));
        assertThat(iPerson.getLastName(), equalTo("Schmoe"));
        assertThat(iPerson.getResidences(), notNullValue());
        assertThat(iPerson.getResidences().length, equalTo(2));

        assertThat(iPerson.getResidences()[0].getLine1(), equalTo("123 Main Street"));
        assertThat(iPerson.getResidences()[0].getLine2(), equalTo(""));
        assertThat(iPerson.getResidences()[0].getCity(), equalTo("Ralston"));
        assertThat(iPerson.getResidences()[0].getState(), equalTo("NE"));
        assertThat(iPerson.getResidences()[0].getPostalCode(), equalTo("68127"));
        assertThat(iPerson.getResidences()[0].getCountry(), nullValue());
        assertThat(iPerson.getResidences()[0].getCountryCode(), nullValue());

        assertThat(iPerson.getResidences()[1].getLine1(), equalTo("456 First Street"));
        assertThat(iPerson.getResidences()[1].getLine2(), equalTo(""));
        assertThat(iPerson.getResidences()[1].getCity(), equalTo("Omaha"));
        assertThat(iPerson.getResidences()[1].getState(), equalTo("NE"));
        assertThat(iPerson.getResidences()[1].getPostalCode(), equalTo("68154"));
        assertThat(iPerson.getResidences()[1].getCountry(), nullValue());
        assertThat(iPerson.getResidences()[1].getCountryCode(), nullValue());
    }

    @Test
    public void arrayTestHomogenius() throws IllegalAccessException {
        PersonArr person = new PersonArr.Builder()
                .phoneNumber("402-555-1212")
                .firstName("Joe")
                .lastName("Schmoe")
                .build();

        Address addr1 = new Address();
        addr1.setLine1("123 Main Street");
        addr1.setLine2("");
        addr1.setCity("Ralston");
        addr1.setState("NE");
        addr1.setZip("68127");

        Address addr2 = new Address();
        addr2.setLine1("456 First Street");
        addr2.setLine2("");
        addr2.setCity("Omaha");
        addr2.setState("NE");
        addr2.setZip("68154");

        Address[] res = new Address[2];
        res[0] = addr1;
        res[1] = addr2;
        person.setResidences(res);

        PersonArr person2 = new PersonArr.Builder().build();

        Cartographer mapper = new Cartographer(true);
        mapper.mapObject(person, person2);

        assertThat(person2, notNullValue());
        assertThat(person2.getFirstName(), equalTo("Joe"));
        assertThat(person2.getLastName(), equalTo("Schmoe"));
        assertThat(person2.getPhoneNumber(), equalTo("402-555-1212"));

        assertThat(person2.getResidences(), notNullValue());
        assertThat(person2.getResidences().length, equalTo(2));

        assertThat(person2.getResidences()[0].getLine1(), equalTo("123 Main Street"));
        assertThat(person2.getResidences()[0].getLine2(), equalTo(""));
        assertThat(person2.getResidences()[0].getCity(), equalTo("Ralston"));
        assertThat(person2.getResidences()[0].getState(), equalTo("NE"));
        assertThat(person2.getResidences()[0].getZip(), equalTo("68127"));

        assertThat(person2.getResidences()[1].getLine1(), equalTo("456 First Street"));
        assertThat(person2.getResidences()[1].getLine2(), equalTo(""));
        assertThat(person2.getResidences()[1].getCity(), equalTo("Omaha"));
        assertThat(person2.getResidences()[1].getState(), equalTo("NE"));
        assertThat(person2.getResidences()[1].getZip(), equalTo("68154"));
    }

    @Test
    public void arrayTestTerminal() throws IllegalAccessException {
        AreaZipCodes repo1 = new AreaZipCodes();
        repo1.setZipCodes(new int[] {68147, 68130, 68127, 68144, 68154});

        AreaZipCodes repo2 = new AreaZipCodes();

        Cartographer mapper = new Cartographer(true);
        mapper.mapObject(repo1, repo2);

        assertThat(repo2, notNullValue());
        assertThat(repo2.getZipCodes(), notNullValue());
        assertThat(repo2.getZipCodes().length, equalTo(5));

        assertThat(repo2.getZipCodes()[0], equalTo(68147));
        assertThat(repo2.getZipCodes()[1], equalTo(68130));
        assertThat(repo2.getZipCodes()[2], equalTo(68127));
        assertThat(repo2.getZipCodes()[3], equalTo(68144));
        assertThat(repo2.getZipCodes()[4], equalTo(68154));
    }

    /**
     * The source and targets must at least match on general 'type'.  i.e. both must be maps, collections, or
     * arrays.
     *
     * The business names for the map or collection filds also must be the same although their contained objects
     * don't have to match.  How else would i be able to match contained elements?
     *
     * @throws IllegalAccessException
     */
    @Test
    public void mapTestHeterogenius() throws IllegalAccessException {
        PhoneToCustomerRepository repo1 = new PhoneToCustomerRepository();
        repo1.getMap().put("Amy", null);
        repo1.getMap().put("Joe", new Customer.Builder().firstName("Joe").phoneNumber("895-9752").build());
        repo1.getMap().put("Kim", new Customer.Builder().firstName("Kim").phoneNumber("397-7596").build());
        repo1.getMap().put("Jenny", new Customer.Builder().firstName("Jenny").phoneNumber("867-5309").build());
        repo1.getMap().put("Jeff", new Customer.Builder().firstName("Jeff").phoneNumber("333-6505").build());

        PhoneToPersonRepository repo2 = new PhoneToPersonRepository();

        Cartographer mapper = new Cartographer(true);
        mapper.mapObject(repo1, repo2);

        assertThat(repo2, notNullValue());
        assertThat(repo2.getMap(), notNullValue());
        assertThat(repo2.getMap().size(), equalTo(5));

        assertThat(repo2.getMap().get("Amy"), nullValue());
        assertThat(repo2.getMap().get("Joe").getFirstName(), equalTo("Joe"));
        assertThat(repo2.getMap().get("Joe").getPhoneNumber(), equalTo("895-9752"));
        assertThat(repo2.getMap().get("Kim").getFirstName(), equalTo("Kim"));
        assertThat(repo2.getMap().get("Kim").getPhoneNumber(), equalTo("397-7596"));
        assertThat(repo2.getMap().get("Jenny").getFirstName(), equalTo("Jenny"));
        assertThat(repo2.getMap().get("Jenny").getPhoneNumber(), equalTo("867-5309"));
        assertThat(repo2.getMap().get("Jeff").getFirstName(), equalTo("Jeff"));
        assertThat(repo2.getMap().get("Jeff").getPhoneNumber(), equalTo("333-6505"));
    }

    @Test
    public void mapTestHomogenius() throws IllegalAccessException {
        PhoneToPersonRepository repo1 = new PhoneToPersonRepository();
        repo1.getMap().put("Amy", null);
        repo1.getMap().put("Joe", new Person.Builder().firstName("Joe").phoneNumber("895-9752").build());
        repo1.getMap().put("Kim", new Person.Builder().firstName("Kim").phoneNumber("397-7596").build());
        repo1.getMap().put("Jenny", new Person.Builder().firstName("Jenny").phoneNumber("867-5309").build());
        repo1.getMap().put("Jeff", new Person.Builder().firstName("Jeff").phoneNumber("333-6505").build());

        PhoneToPersonRepository repo2 = new PhoneToPersonRepository();

        Cartographer mapper = new Cartographer(true);
        mapper.mapObject(repo1, repo2);

        assertThat(repo2, notNullValue());
        assertThat(repo2.getMap(), notNullValue());
        assertThat(repo2.getMap().size(), equalTo(5));

        assertThat(repo2.getMap().get("Amy"), nullValue());
        assertThat(repo2.getMap().get("Joe").getFirstName(), equalTo("Joe"));
        assertThat(repo2.getMap().get("Joe").getPhoneNumber(), equalTo("895-9752"));
        assertThat(repo2.getMap().get("Kim").getFirstName(), equalTo("Kim"));
        assertThat(repo2.getMap().get("Kim").getPhoneNumber(), equalTo("397-7596"));
        assertThat(repo2.getMap().get("Jenny").getFirstName(), equalTo("Jenny"));
        assertThat(repo2.getMap().get("Jenny").getPhoneNumber(), equalTo("867-5309"));
        assertThat(repo2.getMap().get("Jeff").getFirstName(), equalTo("Jeff"));
        assertThat(repo2.getMap().get("Jeff").getPhoneNumber(), equalTo("333-6505"));
    }

    @Test
    public void mapTestTerminal() throws IllegalAccessException {
        NameToPhoneRepository repo1 = new NameToPhoneRepository();
        repo1.getMap().put("Amy",null);
        repo1.getMap().put("Joe","895-9752");
        repo1.getMap().put("Kim","397-7596");
        repo1.getMap().put("Jenny","867-5309");
        repo1.getMap().put("Jeff","333-6505");

        NameToPhoneRepository repo2 = new NameToPhoneRepository();

        Cartographer mapper = new Cartographer(true);
        mapper.mapObject(repo1, repo2);

        assertThat(repo2, notNullValue());
        assertThat(repo2.getMap(), notNullValue());
        assertThat(repo2.getMap().size(), equalTo(5));

        assertThat(repo2.getMap().get("Amy"), nullValue());
        assertThat(repo2.getMap().get("Joe"), equalTo("895-9752"));
        assertThat(repo2.getMap().get("Kim"), equalTo("397-7596"));
        assertThat(repo2.getMap().get("Jenny"), equalTo("867-5309"));
        assertThat(repo2.getMap().get("Jeff"), equalTo("333-6505"));
    }

    @Test
    public void collectionTestTerminal() throws IllegalAccessException {
        InternationalPerson iPerson1 = new InternationalPerson();
        iPerson1.setFirstName("Joe");
        iPerson1.setLastName("Schmoe");
        iPerson1.setAliases(Arrays.asList("123", "456", null));

        InternationalPerson iPerson2 = new InternationalPerson();

        Cartographer mapper = new Cartographer(true);
        mapper.mapObject(iPerson1, iPerson2);

        assertThat(iPerson2, notNullValue());
        assertThat(iPerson2.getFirstName(), equalTo("Joe"));
        assertThat(iPerson2.getLastName(), equalTo("Schmoe"));
        assertThat(iPerson2.getAliases(), notNullValue());
        assertThat(iPerson2.getAliases().size(), equalTo(3));
        assertThat(iPerson2.getAliases().get(0), equalTo("123"));
        assertThat(iPerson2.getAliases().get(1), equalTo("456"));
        assertThat(iPerson2.getAliases().get(2), nullValue());
    }

    @Test
    public void collectionTestHeterogenius() throws IllegalAccessException {
        Person person = new Person.Builder()
                .phoneNumber("402-555-1212")
                .firstName("Joe")
                .lastName("Schmoe")
                .build();

        Address addr1 = new Address();
        addr1.setLine1("123 Main Street");
        addr1.setLine2("");
        addr1.setCity("Ralston");
        addr1.setState("NE");
        addr1.setZip("68127");

        Address addr2 = new Address();
        addr2.setLine1("456 First Street");
        addr2.setLine2("");
        addr2.setCity("Omaha");
        addr2.setState("NE");
        addr2.setZip("68154");

        person.getResidences().add(addr1);
        person.getResidences().add(addr2);

        InternationalPerson iPerson = new InternationalPerson();

        Cartographer mapper = new Cartographer(true);
        mapper.mapObject(person, iPerson);

        assertThat(iPerson, notNullValue());
        assertThat(iPerson.getFirstName(), equalTo("Joe"));
        assertThat(iPerson.getLastName(), equalTo("Schmoe"));
        assertThat(iPerson.getResidences(), notNullValue());
        assertThat(iPerson.getResidences().size(), equalTo(2));

        assertThat(iPerson.getResidences().get(0).getLine1(), equalTo("123 Main Street"));
        assertThat(iPerson.getResidences().get(0).getLine2(), equalTo(""));
        assertThat(iPerson.getResidences().get(0).getCity(), equalTo("Ralston"));
        assertThat(iPerson.getResidences().get(0).getState(), equalTo("NE"));
        assertThat(iPerson.getResidences().get(0).getPostalCode(), equalTo("68127"));
        assertThat(iPerson.getResidences().get(0).getCountry(), nullValue());
        assertThat(iPerson.getResidences().get(0).getCountryCode(), nullValue());

        assertThat(iPerson.getResidences().get(1).getLine1(), equalTo("456 First Street"));
        assertThat(iPerson.getResidences().get(1).getLine2(), equalTo(""));
        assertThat(iPerson.getResidences().get(1).getCity(), equalTo("Omaha"));
        assertThat(iPerson.getResidences().get(1).getState(), equalTo("NE"));
        assertThat(iPerson.getResidences().get(1).getPostalCode(), equalTo("68154"));
        assertThat(iPerson.getResidences().get(1).getCountry(), nullValue());
        assertThat(iPerson.getResidences().get(1).getCountryCode(), nullValue());
    }

    @Test
    public void collectionTestHomogenius() throws IllegalAccessException {
        Person person = new Person.Builder()
                .phoneNumber("402-555-1212")
                .firstName("Joe")
                .lastName("Schmoe")
                .build();

        Address addr1 = new Address();
        addr1.setLine1("123 Main Street");
        addr1.setLine2("");
        addr1.setCity("Ralston");
        addr1.setState("NE");
        addr1.setZip("68127");

        Address addr2 = new Address();
        addr2.setLine1("456 First Street");
        addr2.setLine2("");
        addr2.setCity("Omaha");
        addr2.setState("NE");
        addr2.setZip("68154");

        person.getResidences().add(addr1);
        person.getResidences().add(addr2);

        Person person2 = new Person.Builder().build();

        Cartographer mapper = new Cartographer(true);
        mapper.mapObject(person, person2);

        assertThat(person2, notNullValue());
        assertThat(person2.getFirstName(), equalTo("Joe"));
        assertThat(person2.getLastName(), equalTo("Schmoe"));
        assertThat(person2.getPhoneNumber(), equalTo("402-555-1212"));

        assertThat(person2.getResidences(), notNullValue());
        assertThat(person2.getResidences().size(), equalTo(2));

        assertThat(person2.getResidences().get(0).getLine1(), equalTo("123 Main Street"));
        assertThat(person2.getResidences().get(0).getLine2(), equalTo(""));
        assertThat(person2.getResidences().get(0).getCity(), equalTo("Ralston"));
        assertThat(person2.getResidences().get(0).getState(), equalTo("NE"));
        assertThat(person2.getResidences().get(0).getZip(), equalTo("68127"));

        assertThat(person2.getResidences().get(1).getLine1(), equalTo("456 First Street"));
        assertThat(person2.getResidences().get(1).getLine2(), equalTo(""));
        assertThat(person2.getResidences().get(1).getCity(), equalTo("Omaha"));
        assertThat(person2.getResidences().get(1).getState(), equalTo("NE"));
        assertThat(person2.getResidences().get(1).getZip(), equalTo("68154"));
    }

    @Test
    public void testNestedComposites() throws IllegalAccessException {
        Address addr = new Address();
        addr.setLine1("123 Main Street");
        addr.setLine2("");
        addr.setCity("Ralston");
        addr.setState("NE");
        addr.setZip("68127");

        Customer customer = new Customer.Builder().firstName("Tina").lastName("Fey").build();
        customer.setResidences(addr);

        Person person = new Person.Builder().firstName("Joe").lastName("Blow").phoneNumber("5551212").build();

        Item item = new Item.Builder().description("this is a test item").sku("12345").build();

        Order order = new Order();
        order.setBillingAddr(new Address());

        Cartographer mapper = new Cartographer();

        assertThat(person, notNullValue());
        assertThat(item, notNullValue());
        assertThat(order, notNullValue());
        assertThat(mapper, notNullValue());

        List<Object> list = new ArrayList<>();
        list.add(customer);
        list.add(person);
        list.add(item);

        mapper.mapObject(list, order);

        System.out.println(order);
        assertThat(order.getCustomerFirstName(), equalTo("Joe"));
        assertThat(order.getCustomerLastName(), equalTo("Blow"));
        assertThat(order.getSku(), equalTo("12345"));
        assertThat(order.getSkuDescription(), equalTo("this is a test item"));
        assertThat(order.getShipToLine1(), equalTo("123 Main Street"));

        assertThat(order.getBillingAddr().getLine1(), equalTo("123 Main Street"));
        assertThat(order.getBillingAddr().getLine2(), equalTo(""));
        assertThat(order.getBillingAddr().getCity(), equalTo("Ralston"));
        assertThat(order.getBillingAddr().getState(), equalTo("NE"));
        assertThat(order.getBillingAddr().getZip(), equalTo("68127"));
        assertTrue(addr != order.getBillingAddr());
    }

    /**
     * How composites work **COMPOSITES ONLY APPLY TO THE SAME CLASS THEY ARE DEFINED IN**:
     * -if there is a match on class level business name that will be used.
     * -if a composite class is named and it exists in source that will be used.
     * -for all terminal fields left the sources will be scanned for matching field level business names.
     *  if a match is found the first match is used.
     *
     * @throws IllegalAccessException
     */
    @Test
    public void testComposite() throws IllegalAccessException {
        Address addr = new Address();
        addr.setLine1("123 Main Street");

        Customer customer = new Customer.Builder().firstName("Tina").lastName("Fey").build();
        customer.setResidences(addr);

        Person person = new Person.Builder().firstName("Joe").lastName("Blow").phoneNumber("5551212").build();

        Item item = new Item.Builder().description("this is a test item").sku("12345").build();

        Order order = new Order();

        Cartographer mapper = new Cartographer();

        assertThat(person, notNullValue());
        assertThat(item, notNullValue());
        assertThat(order, notNullValue());
        assertThat(mapper, notNullValue());

        List<Object> list = new ArrayList<Object>();
        list.add(customer);
        list.add(person);
        list.add(item);

        mapper.mapObject(list, order);

        System.out.println(order);
        assertThat(order.getCustomerFirstName(), equalTo("Joe"));
        assertThat(order.getCustomerLastName(), equalTo("Blow"));
        assertThat(order.getSku(), equalTo("12345"));
        assertThat(order.getSkuDescription(), equalTo("this is a test item"));
        assertThat(order.getShipToLine1(), equalTo("123 Main Street"));
    }

    @Test
    public void testLikeObjectsCustomer() throws IllegalAccessException {
        Customer oldCust = new Customer.Builder().firstName("Joe").lastName("Blow").phoneNumber("4025551212").build();
        Customer newCust = new Customer.Builder().build();
        Cartographer mapper = new Cartographer();

        assertThat(oldCust, notNullValue());
        assertThat(newCust, notNullValue());
        assertThat(mapper, notNullValue());

        assertThat(oldCust.getFirstName(), equalTo("Joe"));
        assertThat(oldCust.getLastName(), equalTo("Blow"));
        assertThat(oldCust.getPhoneNumber(), equalTo("4025551212"));

        assertThat(newCust.getFirstName(), nullValue());
        assertThat(newCust.getLastName(), nullValue());
        assertThat(newCust.getPhoneNumber(), nullValue());

        mapper.mapObject(oldCust, newCust);

        assertThat(newCust.getFirstName(), equalTo(oldCust.getFirstName()));
        assertThat(newCust.getLastName(), equalTo(oldCust.getLastName()));
        assertThat(newCust.getPhoneNumber(), equalTo(oldCust.getPhoneNumber()));
    }

    @Test
    public void testLikeObjectsAddress() throws IllegalAccessException {
        Address oldAddr = new Address();
        Address newAddr = new Address();
        Cartographer mapper = new Cartographer();

        oldAddr.setLine1("123 Main St");
        oldAddr.setLine2("Attn: Payroll");
        oldAddr.setCity("Bellevue");
        oldAddr.setState("NE");
        oldAddr.setZip("68147");

        assertThat(oldAddr, notNullValue());
        assertThat(newAddr, notNullValue());
        assertThat(mapper, notNullValue());

        assertThat(oldAddr.getLine1(), equalTo("123 Main St"));
        assertThat(oldAddr.getLine2(), equalTo("Attn: Payroll"));
        assertThat(oldAddr.getCity(), equalTo("Bellevue"));
        assertThat(oldAddr.getState(), equalTo("NE"));
        assertThat(oldAddr.getZip(), equalTo("68147"));

        assertThat(newAddr.getLine1(), nullValue());
        assertThat(newAddr.getLine2(), nullValue());
        assertThat(newAddr.getCity(), nullValue());
        assertThat(newAddr.getState(), nullValue());
        assertThat(newAddr.getZip(), nullValue());

        mapper.mapObject(oldAddr, newAddr);

        assertThat(newAddr.getLine1(), equalTo(oldAddr.getLine1()));
        assertThat(newAddr.getLine2(), equalTo(oldAddr.getLine2()));
        assertThat(newAddr.getCity(), equalTo(oldAddr.getCity()));
        assertThat(newAddr.getState(), equalTo(oldAddr.getState()));
        assertThat(newAddr.getZip(), equalTo(oldAddr.getZip()));
    }

    @Test
    public void testLikeContainedObjectsAddressInCustomer() throws IllegalAccessException {
        Cartographer mapper = new Cartographer();

        Customer oldCust = new Customer.Builder().firstName("Joe").lastName("Blow").phoneNumber("4025551212").build();
        Customer newCust = new Customer.Builder().build();

        assertThat(oldCust, notNullValue());
        assertThat(newCust, notNullValue());
        assertThat(mapper, notNullValue());

        assertThat(oldCust.getFirstName(), equalTo("Joe"));
        assertThat(oldCust.getLastName(), equalTo("Blow"));
        assertThat(oldCust.getPhoneNumber(), equalTo("4025551212"));

        assertThat(newCust.getFirstName(), nullValue());
        assertThat(newCust.getLastName(), nullValue());
        assertThat(newCust.getPhoneNumber(), nullValue());

        Address oldAddr = new Address();
        Address newAddr = new Address();

        oldAddr.setLine1("123 Main St");
        oldAddr.setLine2("Attn: Payroll");
        oldAddr.setCity("Bellevue");
        oldAddr.setState("NE");
        oldAddr.setZip("68147");

        assertThat(oldAddr, notNullValue());
        assertThat(newAddr, notNullValue());
        assertThat(mapper, notNullValue());

        assertThat(oldAddr.getLine1(), equalTo("123 Main St"));
        assertThat(oldAddr.getLine2(), equalTo("Attn: Payroll"));
        assertThat(oldAddr.getCity(), equalTo("Bellevue"));
        assertThat(oldAddr.getState(), equalTo("NE"));
        assertThat(oldAddr.getZip(), equalTo("68147"));

        assertThat(newAddr.getLine1(), nullValue());
        assertThat(newAddr.getLine2(), nullValue());
        assertThat(newAddr.getCity(), nullValue());
        assertThat(newAddr.getState(), nullValue());
        assertThat(newAddr.getZip(), nullValue());

        oldCust.setResidences(oldAddr);
        newCust.setResidences(newAddr);

        mapper.mapObject(oldCust, newCust);

        assertThat(newCust.getFirstName(), equalTo(oldCust.getFirstName()));
        assertThat(newCust.getLastName(), equalTo(oldCust.getLastName()));
        assertThat(newCust.getPhoneNumber(), equalTo(oldCust.getPhoneNumber()));

        assertTrue(oldAddr != newAddr);
        assertThat(newCust.getResidences().getLine1(), equalTo(oldCust.getResidences().getLine1()));
        assertThat(newCust.getResidences().getLine2(), equalTo(oldCust.getResidences().getLine2()));
        assertThat(newCust.getResidences().getCity(), equalTo(oldCust.getResidences().getCity()));
        assertThat(newCust.getResidences().getState(), equalTo(oldCust.getResidences().getState()));
        assertThat(newCust.getResidences().getZip(), equalTo(oldCust.getResidences().getZip()));
    }

    @Test
    public void testLikeContainedObjectsNULLAddressInNEWCustomer() throws IllegalAccessException {
        Cartographer mapper = new Cartographer();

        Customer oldCust = new Customer.Builder().firstName("Joe").lastName("Blow").phoneNumber("4025551212").build();
        Customer newCust = new Customer.Builder().build();

        assertThat(oldCust, notNullValue());
        assertThat(newCust, notNullValue());
        assertThat(mapper, notNullValue());

        assertThat(oldCust.getFirstName(), equalTo("Joe"));
        assertThat(oldCust.getLastName(), equalTo("Blow"));
        assertThat(oldCust.getPhoneNumber(), equalTo("4025551212"));

        assertThat(newCust.getFirstName(), nullValue());
        assertThat(newCust.getLastName(), nullValue());
        assertThat(newCust.getPhoneNumber(), nullValue());

        Address oldAddr = new Address();

        oldAddr.setLine1("123 Main St");
        oldAddr.setLine2("Attn: Payroll");
        oldAddr.setCity("Bellevue");
        oldAddr.setState("NE");
        oldAddr.setZip("68147");

        assertThat(oldAddr, notNullValue());
        assertThat(mapper, notNullValue());

        assertThat(oldAddr.getLine1(), equalTo("123 Main St"));
        assertThat(oldAddr.getLine2(), equalTo("Attn: Payroll"));
        assertThat(oldAddr.getCity(), equalTo("Bellevue"));
        assertThat(oldAddr.getState(), equalTo("NE"));
        assertThat(oldAddr.getZip(), equalTo("68147"));

        oldCust.setResidences(oldAddr);
        newCust.setResidences(null);
        assertThat(newCust.getResidences(), nullValue());

        mapper.mapObject(oldCust, newCust);

        assertThat(newCust.getFirstName(), equalTo(oldCust.getFirstName()));
        assertThat(newCust.getLastName(), equalTo(oldCust.getLastName()));
        assertThat(newCust.getPhoneNumber(), equalTo(oldCust.getPhoneNumber()));
    }

    @Test
    public void testDifferentObjectsPersonToCustomer() throws IllegalAccessException {
        Person person = new Person.Builder().firstName("Joe").lastName("Blow").phoneNumber("4025551212").build();
        Customer customer = new Customer.Builder().build();
        Cartographer mapper = new Cartographer();

        assertThat(person, notNullValue());
        assertThat(customer, notNullValue());
        assertThat(mapper, notNullValue());

        assertThat(person.getFirstName(), equalTo("Joe"));
        assertThat(person.getLastName(), equalTo("Blow"));
        assertThat(person.getPhoneNumber(), equalTo("4025551212"));

        assertThat(customer.getFirstName(), nullValue());
        assertThat(customer.getLastName(), nullValue());
        assertThat(customer.getPhoneNumber(), nullValue());

        mapper.mapObject(person, customer);

        assertThat(customer.getFirstName(), equalTo(person.getFirstName()));
        assertThat(customer.getLastName(), equalTo(person.getLastName()));
        assertThat(customer.getPhoneNumber(), equalTo(person.getPhoneNumber()));
    }

    @Test
    public void testDifferentObjectsOrderForm() throws IllegalAccessException {
        Customer customer = new Customer.Builder().firstName("Joe").lastName("Blow").phoneNumber("4025551212").build();
        Address addr = new Address();
        addr.setLine1("123 Main St");
        addr.setLine2("Attn: Payroll");
        addr.setCity("Bellevue");
        addr.setState("NE");
        addr.setZip("68147");
        customer.setResidences(addr);
        OrderForm orderForm = new OrderForm();
        Cartographer mapper = new Cartographer();

        assertThat(customer, notNullValue());
        assertThat(customer.getResidences(), notNullValue());
        assertThat(mapper, notNullValue());

        mapper.mapObject(customer, orderForm);

        assertThat("Joe", equalTo(orderForm.getFirstName()));
        assertThat("Blow", equalTo(orderForm.getLastName()));
        assertThat("4025551212", equalTo(orderForm.getPhoneNumber()));

        assertThat("123 Main St", equalTo(orderForm.getLine1()));
        assertThat("Attn: Payroll", equalTo(orderForm.getLine2()));
        assertThat("Bellevue", equalTo(orderForm.getCity()));
        assertThat("NE", equalTo(orderForm.getState()));
        assertThat("68147", equalTo(orderForm.getZip()));
    }
}

