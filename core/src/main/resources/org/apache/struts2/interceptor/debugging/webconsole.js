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

function printResult(resultString) {
    var resultDiv = document.getElementById('wc-result');
    var resultArray = resultString.split('\n');

    var newCommand = document.getElementById('wc-command').value;
    resultDiv.appendChild(document.createTextNode(newCommand));
    resultDiv.appendChild(document.createElement('br'));

    for (var lineIndex in resultArray) {
        if (resultArray.hasOwnProperty(lineIndex)) {
            var resultWrap = document.createElement('pre'),
                line = document.createTextNode(resultArray[lineIndex]);
            resultWrap.appendChild(line);
            resultDiv.appendChild(resultWrap);
            resultDiv.appendChild(document.createElement('br'));
        }
    }
    resultDiv.appendChild(document.createTextNode(':-> '));

    resultDiv.scrollTop = resultDiv.scrollHeight;
    document.getElementById('wc-command').value = '';
}

function keyEvent(event, url) {
    switch (event.keyCode) {
        case 13:
            var theShellCommand = document.getElementById('wc-command').value;
            if (theShellCommand) {
                commandsHistory[commandsHistory.length] = theShellCommand;
                historyPointer = commandsHistory.length;
                var theUrl = url ? url : window.opener.location.pathname;

                var request = new XMLHttpRequest();
                request.open('POST', theUrl, true);
                request.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8');

                request.onreadystatechange = function() {
                  if (this.readyState === 4) {
                    if (this.status >= 200 && this.status < 400) {
                      // Success!
                    	printResult(this.responseText);
                    }
                  }
                };

                request.send("debug=command&expression="+encodeURIComponent(document.getElementById('wc-command').value));
            }
            break;
        case 38: // this is the arrow up
            if (historyPointer > 0) {
                historyPointer--;
                document.getElementById('wc-command').value = commandsHistory[historyPointer];
            }
            break;
        case 40: // this is the arrow down
            if (historyPointer < commandsHistory.length - 1) {
                historyPointer++;
                document.getElementById('wc-command').value = commandsHistory[historyPointer];
            }
            break;
        default:
            break;
    }
}

var commandsHistory = [];
var historyPointer;
