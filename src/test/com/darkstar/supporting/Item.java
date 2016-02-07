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
import com.darkstar.beanCartography.annotations.NamedField;

/**
 * @author michael snavely
 */
@NamedClass(name = "Item")
public class Item {

    @NamedField(name = "Sku")
    private String sku = null;

    @NamedField(name = "Description")
    private String description = null;

    /**
     * Builder
     */
    public static class Builder {

        private String sku = null;
        private String description = null;

        public Builder() {
            super();
        }

        public Builder sku(String s) {
            sku = s;
            return this;
        }

        public Builder description(String s) {
            description = s;
            return this;
        }

        public Item build() {
            return new Item(this);
        }
    }

    /**
     * Constructor
     *
     * @param builder
     */
    private Item(Item.Builder builder) {
        super();
        this.sku         = builder.sku;
        this.description = builder.description;
    }

    public Item() {
        super();
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

