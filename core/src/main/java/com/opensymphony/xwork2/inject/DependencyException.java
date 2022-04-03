/**
 * Copyright (C) 2006 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.opensymphony.xwork2.inject;

/**
 * Thrown when a dependency is misconfigured.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public class DependencyException extends RuntimeException {

  public DependencyException(String message) {
    super(message);
  }

  public DependencyException(String message, Throwable cause) {
    super(message, cause);
  }

  public DependencyException(Throwable cause) {
    super(cause);
  }
}
