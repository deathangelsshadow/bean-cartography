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
@NamedClass(name = "AreaZipCodes")
public class AreaZipCodes {

    @NamedField(name = "ZipCodes")
    private int[] zipCodes = null;

    public AreaZipCodes() {
        super();
    }

    public int[] getZipCodes() {
        return zipCodes;
    }

    public void setZipCodes(int[] zipCodes) {
        this.zipCodes = zipCodes;
    }
}
