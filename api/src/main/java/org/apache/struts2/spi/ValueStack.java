/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.spi;

/**
 * A central fixture of the Struts framework, the {@code ValueStack} is a stack which contains the actions
 * which have executed in addition to other objects. Users can get and set values on the stack using expressions. The
 * {@code ValueStack} will search down the stack starting with the most recent objects until it finds an object to
 * which the expression can apply.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public interface ValueStack extends Iterable<Object> {

    /**
     * Gets the top, most recent object from the stack without changing the stack.
     *
     * @return the top object
     */
    Object peek();

    /**
     * Removes the top, most recent object from the stack.
     *
     * @return the top object
     */
    Object pop();

    /**
     * Pushes an object onto the stack.
     *
     * @param o
     */
    void push(Object o);

    /**
     * Creates a shallow copy of this stack.
     *
     * @return a new stack which contains the same objects as this one
     */
    ValueStack clone();

    /**
     * Queries the stack. Starts with the top, most recent object. If the expression can apply to the object, this
     * method returns the result of evaluating the expression. If the expression does not apply, this method moves
     * down the stack to the next object and repeats. Returns {@code null} if the expression doesn't apply to any
     * objects.
     *
     * @param expression
     * @return the evaluation of the expression against the first applicable object in the stack
     */
    Object get(String expression);

    /**
     * Queries the stack and converts the result to the specified type. Starts with the top, most recent object. If
     * the expression can apply to the object, this method returns the result of evaluating the expression converted
     * to the specified type. If the expression does not apply, this method moves down the stack to the next object
     * and repeats. Returns {@code null} if the expression doesn't apply to any objects.
     *
     * @param expression
     * @param asType the type to convert the result to
     * @return the evaluation of the expression against the first applicable object in the stack converted to the
     *  specified type
     */
    <T> T get(String expression, Class<T> asType);

    /**
     * Queries the stack and converts the result to a {@code String}. Starts with the top, most recent object. If the
     * expression can apply to the object, this method returns the result of evaluating the expression converted to a
     * {@code String}. If the expression does not apply, this method moves down the stack to the next object and
     * repeats. Returns {@code null} if the expression doesn't apply to any objects.
     *
     * @param expression
     * @return the evaluation of the expression against the first applicable object in the stack converted to a {@code
     *  String}
     */
    String getString(String expression);

    /**
     * Sets a value on an object from the stack. This method starts at the top, most recent object. If the expression
     * applies to that object, this methods sets the given value on that object using the expression and converting
     * the type as necessary. If the expression does not apply, this method moves to the next object and repeats.
     *
     * @param expression
     * @param value
     */
    void set(String expression, Object value);

    /**
     * Returns the number of object on the stack.
     *
     * @return size of stack
     */
    int size();
}
