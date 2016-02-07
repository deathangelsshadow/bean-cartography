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
import com.darkstar.beanCartography.annotations.NamedClassComposite;
import com.darkstar.beanCartography.annotations.NamedField;

/**
 * @author michael snavely
 */
@NamedClass(name = "Order")
@NamedClassComposite(names = {"Person", "Item"})
public class Order {

    @NamedField(name = "FirstName")
    private String customerFirstName = null;

    @NamedField(name = "LastName")
    private String customerLastName  = null;

    @NamedField(name = "Sku")
    private String sku               = null;

    @NamedField(name = "Description")
    private String skuDescription    = null;

    @NamedField(name = "LineOne")
    private String ShipToLine1 = null;

    @NamedField(name = "HomeAddress")
    private Address billingAddr = null;

    /**
     * Constructor
     */
    public Order() {
        super();
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getSkuDescription() {
        return skuDescription;
    }

    public void setSkuDescription(String skuDescription) {
        this.skuDescription = skuDescription;
    }

    public String getShipToLine1() {return ShipToLine1;}

    public void setShipToLine1(String shipToLine1) {ShipToLine1 = shipToLine1;}

    public Address getBillingAddr() { return billingAddr; }

    public void setBillingAddr(Address billingAddr) { this.billingAddr = billingAddr; }

    @Override
    public String toString() {
        return "Order{" +
                "customerFirstName='" + customerFirstName + '\'' +
                ", customerLastName='" + customerLastName + '\'' +
                ", sku='" + sku + '\'' +
                ", skuDescription='" + skuDescription + '\'' +
                ", ShipToLine1='" + ShipToLine1 + '\'' +
                ", billingAddr=" + billingAddr +
                '}';
    }
}

