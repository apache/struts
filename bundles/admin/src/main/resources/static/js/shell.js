/*
 * $Id: webconsole.js 590812 2007-10-31 20:32:54Z apetrelli $
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

  function printResult(result_string)
  {
      var result_div = $('#wc-result')[0];
      var result_array = result_string.split('\n');

      var new_command = $('#wc-command').val();
      result_div.appendChild(document.createTextNode('$ ' + new_command));
      result_div.appendChild(document.createElement('br'));

      for (var line_index in result_array) {
          var result_wrap = document.createElement('pre')
          line = document.createTextNode(result_array[line_index]);
          result_wrap.appendChild(line);
          result_div.appendChild(result_wrap);
          result_div.appendChild(document.createElement('br'));

      }

      result_div.scrollTop = result_div.scrollHeight;
      $('#wc-command').val('');
  }

  function keyEvent(event, url)
  {
      switch(event.keyCode){
          case 13:
              var the_shell_command = $('#wc-command').val();
              if (the_shell_command) {
                  commands_history[commands_history.length] = the_shell_command;
                  history_pointer = commands_history.length;
                  $.ajax({
                     type: "GET",
                     url: url,
                     data: $("#wc-command").serialize(),
                     success: function (data, textStatus) {
                        printResult(data);
                     },
                     error: function (request, textStatus) {
                        printResult(textStatus);
                     }
                 });
              }
              break;
          case 38: // this is the arrow up
              if (history_pointer > 0) {
                  history_pointer--;
                  $('#wc-command').val(commands_history[history_pointer]);
              }
              break;
          case 40: // this is the arrow down
              if (history_pointer < commands_history.length - 1 ) {
                  history_pointer++;
                  $('#wc-command').val(commands_history[history_pointer]);
              }
              break;
          default:
              break;
      }
  }

  var commands_history = new Array();
  var history_pointer;
