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

package com.opensymphony.xwork2.inject.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
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

  private volatile Thread drainThread;

  void start() {
    Thread thread = new Thread("FinalizableReferenceQueue") {
      @Override
      public void run() {
        while (!Thread.currentThread().isInterrupted()) {
          try {
            cleanUp(remove());
          } catch (InterruptedException e) {
            break;
          }
        }
      }
    };
    thread.setDaemon(true);
    thread.start();
    this.drainThread = thread;
  }

  /**
   * Stops the background drain thread and releases the singleton instance,
   * preventing the webapp classloader from being pinned after undeploy.
   */
  public static synchronized void stopAndClear() {
    if (instance instanceof FinalizableReferenceQueue) {
      FinalizableReferenceQueue queue = (FinalizableReferenceQueue) instance;
      Thread t = queue.drainThread;
      if (t != null) {
        t.interrupt();
        try {
          t.join(5000);
        } catch (InterruptedException ignored) {
          Thread.currentThread().interrupt();
        }
        t.setContextClassLoader(null);
        queue.drainThread = null;
      }
    }
    instance = null;
  }

  static volatile ReferenceQueue<Object> instance = createAndStart();

  static FinalizableReferenceQueue createAndStart() {
    FinalizableReferenceQueue queue = new FinalizableReferenceQueue();
    queue.start();
    return queue;
  }

  /**
   * Gets instance.
   */
  public static ReferenceQueue<Object> getInstance() {
    return instance;
  }
}
