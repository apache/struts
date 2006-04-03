  function printResult(result_string)
  {
      var result_div = document.getElementById('wc-result');
      var result_array = result_string.split('\n');

      var new_command = document.getElementById('wc-command').value;
      result_div.appendChild(document.createTextNode(new_command));
      result_div.appendChild(document.createElement('br'));

      for (var line_index in result_array) {
          var result_wrap = document.createElement('pre')
          line = document.createTextNode(result_array[line_index]);
          result_wrap.appendChild(line);
          result_div.appendChild(result_wrap);
          result_div.appendChild(document.createElement('br'));

      }
      result_div.appendChild(document.createTextNode(':-> '));

      result_div.scrollTop = result_div.scrollHeight;
      document.getElementById('wc-command').value = '';
  }

  function keyEvent(event)
  {
      switch(event.keyCode){
          case 13:
              var the_shell_command = document.getElementById('wc-command').value;
              if (the_shell_command) {
                  commands_history[commands_history.length] = the_shell_command;
                  history_pointer = commands_history.length;
                  var the_url = window.opener.location.pathname + '?debug=command&expression='+escape(the_shell_command);
                  dojo.io.bind({
                        url: the_url,
                        load: function(type, data, evt){ printResult(data); },
                        mimetype: "text/plain"
                    });
              }
              break;
          case 38: // this is the arrow up
              if (history_pointer > 0) {
                  history_pointer--;
                  document.getElementById('wc-command').value = commands_history[history_pointer];
              }
              break;
          case 40: // this is the arrow down
              if (history_pointer < commands_history.length - 1 ) {
                  history_pointer++;
                  document.getElementById('wc-command').value = commands_history[history_pointer];
              }
              break;
          default:
              break;
      }
  }    

        var commands_history = new Array();
        var history_pointer;
