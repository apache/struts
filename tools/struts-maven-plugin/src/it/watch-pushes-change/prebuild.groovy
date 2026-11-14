/*
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

def messages = new File(basedir, 'src/main/resources/messages.properties')
def buildLog = new File(basedir, 'build.log')
def editor = new Thread({
    long giveUp = System.nanoTime() + 60_000_000_000L
    while (System.nanoTime() < giveUp) {
        if (buildLog.exists() && buildLog.text.contains('Watching 1 resource root(s)')) {
            messages.text = messages.text.replace('-v1', '-v2\n')
            return
        }
        Thread.sleep(100)
    }
})
editor.setDaemon(true)
editor.start()
