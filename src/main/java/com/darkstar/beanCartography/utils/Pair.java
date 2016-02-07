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
package com.darkstar.beanCartography.utils;

/**
 * This class is used to associate two objects.
 *
 * @param <T1> the first object type
 * @param <T2> the second object type
 *
 * @author michael snavely
 */
public class Pair <T1, T2> {
    private T1 x = null;
    private T2 y = null;

    /**
     * Constructor
     *
     * @param x the first object instance
     * @param y the second object instance
     */
    public Pair(T1 x, T2 y) {
        super();
        this.x = x;
        this.y = y;
    }

    public T1 getX() {
        return x;
    }

    public void setX(T1 x) {
        this.x = x;
    }

    public T2 getY() {
        return y;
    }

    public void setY(T2 y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Pair [x=" + x + ", y=" + y + "]";
    }
}

