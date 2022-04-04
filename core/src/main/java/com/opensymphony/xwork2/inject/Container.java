/**
 * Copyright (C) 2006 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.opensymphony.xwork2.inject;

import java.io.Serializable;
import java.util.Set;

/**
 * <p>
 * Injects dependencies into constructors, methods and fields annotated with
 * {@link Inject}. Immutable.
 * </p>
 *
 * <p>When injecting a method or constructor, you can additionally annotate
 * its parameters with {@link Inject} and specify a dependency name. When a
 * parameter has no annotation, the container uses the name from the method or
 * constructor's {@link Inject} annotation respectively.
 * </p>
 *
 * <p>For example:</p>
 *
 * <pre>
 *  class Foo {
 *
 *    // Inject the int constant named "i".
 *    &#64;Inject("i") int i;
 *
 *    // Inject the default implementation of Bar and the String constant
 *    // named "s".
 *    &#64;Inject Foo(Bar bar, @Inject("s") String s) {
 *      ...
 *    }
 *
 *    // Inject the default implementation of Baz and the Bob implementation
 *    // named "foo".
 *    &#64;Inject void initialize(Baz baz, @Inject("foo") Bob bob) {
 *      ...
 *    }
 *
 *    // Inject the default implementation of Tee.
 *    &#64;Inject void setTee(Tee tee) {
 *      ...
 *    }
 *  }
 * </pre>
 *
 * <p>To create and inject an instance of {@code Foo}:</p>
 *
 * <pre>
 *  Container c = ...;
 *  Foo foo = c.inject(Foo.class);
 * </pre>
 *
 * @see ContainerBuilder
 * @author crazybob@google.com (Bob Lee)
 */
public interface Container extends Serializable {

  /**
   * Default dependency name.
   */
  String DEFAULT_NAME = "default";

  /**
   * Injects dependencies into the fields and methods of an existing object.
   *
   * @param o object to inject
   */
  void inject(Object o);

  /**
   * Creates and injects a new instance of type {@code implementation}.
   *
   * @param <T> type
   * @param implementation of dependency
   * @return instance
   */
  <T> T inject(Class<T> implementation);

  /**
   * Gets an instance of the given dependency which was declared in
   * {@link com.opensymphony.xwork2.inject.ContainerBuilder}.
   *
   * @param <T> type
   * @param type of dependency
   * @param name of dependency
   * @return instance
   */
  <T> T getInstance(Class<T> type, String name);

  /**
   * Convenience method.&nbsp;Equivalent to {@code getInstance(type,
   * DEFAULT_NAME)}.
   *
   * @param <T> type
   * @param type of dependency
   * @return instance
   */
  <T> T getInstance(Class<T> type);
  
  /**
   * Gets a set of all registered names for the given type
   * @param type The instance type
   * @return A set of registered names or empty set if no instances are registered for that type
   */
  Set<String> getInstanceNames(Class<?> type);

  /**
   * Sets the scope strategy for the current thread.
   *
   * @param scopeStrategy scope strategy
   */
  void setScopeStrategy(Scope.Strategy scopeStrategy);

  /**
   * Removes the scope strategy for the current thread.
   */
  void removeScopeStrategy();
}
