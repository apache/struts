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

package org.apache.struts2.inject.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Starts a background thread that cleans up after reclaimed referents.
 *
 * @author Bob Lee (crazybob@google.com)
 */
public class FinalizableReferenceQueue extends ReferenceQueue<Object> {

  private static final Logger logger =
      Logger.getLogger(FinalizableReferenceQueue.class.getName());

  private final AtomicReference<Thread> cleanupThread = new AtomicReference<>();

  private FinalizableReferenceQueue() {}

  void cleanUp(Reference reference) {
    try {
      ((FinalizableReference) reference).finalizeReferent();
    } catch (Throwable t) {
      deliverBadNews(t);
    }
  }

  void deliverBadNews(Throwable t) {
    logger.log(Level.SEVERE, "Error cleaning up after reference.", t);
  }

  void start() {
    Thread thread = new Thread("FinalizableReferenceQueue") {
      @Override
      public void run() {
        while (!Thread.currentThread().isInterrupted()) {
          try {
            cleanUp(remove());
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
          }
        }
      }
    };
    thread.setDaemon(true);
    thread.start();
    cleanupThread.set(thread);
  }

  /**
   * Stops the background cleanup thread to prevent classloader memory leaks during hot redeployment.
   */
  void stop() {
    Thread t = cleanupThread.getAndSet(null);
    if (t != null) {
      t.interrupt();
      try {
        t.join(5000);
      } catch (InterruptedException ignored) {
        Thread.currentThread().interrupt();
      }
      t.setContextClassLoader(null);
    }
  }

  private static final AtomicReference<ReferenceQueue<Object>> instance =
      new AtomicReference<>(createAndStart());

  static FinalizableReferenceQueue createAndStart() {
    FinalizableReferenceQueue queue = new FinalizableReferenceQueue();
    queue.start();
    return queue;
  }

  /**
   * Gets instance.
   */
  public static ReferenceQueue<Object> getInstance() {
    return instance.get();
  }

  /**
   * Stops the cleanup thread and clears the instance to prevent classloader
   * memory leaks during hot redeployment.
   */
  public static void stopAndClear() {
    ReferenceQueue<Object> q = instance.getAndSet(null);
    if (q instanceof FinalizableReferenceQueue frq) {
      frq.stop();
    }
  }
}
