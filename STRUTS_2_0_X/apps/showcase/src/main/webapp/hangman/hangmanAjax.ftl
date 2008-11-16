
<html>
<head>
	<title>Showcase - Hangman</title>
	<@s.head theme="ajax" debug="false" />
</head>
<body>

<script>
    function destroyWidgets() {
      var div = dojo.byId("updateCharacterAvailableDiv");
      var anchors = div.getElementsByTagName("a");
      dojo.lang.forEach(anchors, function(anchor){
      	var widget = dojo.widget.byId(anchor);
      	widget.destroy();
      });
    }

	var _listeners = {
		   guessMade: function(sourceId, type) {
		        if(type == "before") {
			   		this.guessMadeFunc(sourceId);
			   		this.updateCharacterAvailable(sourceId);
			   		this.updateVocab(sourceId);
			   		this.updateScaffold(sourceId);
			   		this.updateGuessLeft(sourceId);
		   		}
		   },
	       guessMadeFunc: function(sourceId) {
	       				var requestAttr = { character: sourceId };
						dojo.io.bind({
							url: "<@s.url action="guessCharacter" namespace="/hangman" />",
							load: function(type, data, event) {

							},
							mimetype: "text/html",
							content: requestAttr
						});
	       			},
	       updateCharacterAvailable: function(sourceId) {
	       				dojo.io.bind({
	       					url: "<@s.url action="updateCharacterAvailable" namespace="/hangman/ajax" />",
	       					load: function(type, data, event) {
	       						var div = dojo.byId("updateCharacterAvailableDiv");
	       						destroyWidgets();
	       						div.innerHTML = data;

	       						try{
                        			var xmlParser = new dojo.xml.Parse();
                        			var frag  = xmlParser.parseElement(div, null, true);
                        			dojo.widget.getParser().createComponents(frag);
                        			// eval any scripts being returned
                        			var scripts = div.getElementsByTagName('script');
                        			for (var i=0; i<scripts.length; i++) {
                            			eval(scripts[i].innerHTML);
                        			}
                    			}
                    			catch(e){
                    				alert('dojo error '+e);
                        			dojo.debug("auto-build-widgets error: "+e);
                    			}
	       					},
	       					mimetype: "text/html"
	       				});
	       			},
	       	updateVocab: function(sourceId) {
	       			dojo.io.bind({
	       				url: "<@s.url action="updateVocabCharacters" namespace="/hangman/ajax" />",
	       				load: function(type, data, event) {
	       					var div = dojo.byId("updateVocabDiv");
	       					div.innerHTML = data;

	       					try {
	       						var xmlParser = new dojo.xml.Parse();
	       						var frag = xmlParser.parseElement(div, null, true);

	       						var scripts = div.getElementsByTagName("script");
	       						for(var i=0; i<scripts.length; i++) {
	       							eval(scripts[i].innerHTML);
	       						}
	       					}
	       					catch(e) {
	       						alert("dojo error"+e);
	       						dojo.debug("auto-build-widgets error: "+e);
	       					}
	       				},
	       				mimetype: "text/html"
	       			});
	       		},
	       	updateScaffold: function(sourceId) {
	       		dojo.io.bind({
	       			url: "<@s.url action="updateScaffold" namespace="/hangman/ajax" />",
	       			load: function(type, data, event) {
	       				var div = dojo.byId("updateScaffoldDiv");
	       				div.innerHTML = data;

	       				try {
	       					var xmlParser = new dojo.xml.Parse();
	       					var frag = xmlParser.parseElement(div, null, true);

	       					var scripts = div.getElementsByTagName("script");
	       					for(var i=0; i<scripts.length; i++) {
	       							eval(scripts[i].innerHTML);
	       					}
	       				}
	       				catch(e) {
	       					alert("dojo error"+e);
	       					dojo.debug("auto-build-widgets error: "+e);
	       				}
	       			},
	       			mimetype: "text/html"
	       		});
	       	},
	       	updateGuessLeft: function(sourceId) {
	       		dojo.io.bind({
	       			url: "<@s.url action="updateGuessLeft" namespace="/hangman/ajax" />",
	       			load: function(type, data, event) {
	       				var div = dojo.byId("updateGuessLeftDiv");
	       				div.innerHTML = data;

	       				try {
	       					var xmlParser = new dojo.xml.Parse();
	       					var frag = xmlParser.parseElement(div, null, true);

	       					var scripts = div.getElementsByTagName("script");
	       					for(var i=0; i<scripts.length; i++) {
	       							eval(scripts[i].innerHTML);
	       					}
	       				}
	       				catch(e) {
	       					alert("dojo error"+e);
	       					dojo.debug("auto-build-widgets error: "+e);
	       				}
	       			},
	       			mimetype: "text/html"
	       		});
	       	}
	    };
	dojo.event.topic.subscribe("topicGuessMade", _listeners, "guessMade");
</script>

<table bgcolor="green">
  <tr>
    <td>
    <@s.url id="url" value="/hangman/images/hangman.png" />
    <img alt="Hangman" src="<@s.property value="%{#url}" />"
           width="197" height="50" border="0"/>
    </td>
    <td width="70" align="right">
      <#-- Guesses Left -->
      <div id="updateGuessLeftDiv">
      <@s.set name="guessLeftImageName" value="%{'Chalkboard_'+hangman.guessLeft()+'.png'}" />
      <@s.url id="url" value="%{'/hangman/images/'+#guessLeftImageName}" />
      <img alt="No. Guesses Left"
      	   src="<@s.property value="%{#url}"/>" width="20" height="20" border="0" />
      </div>
    </td>
    <td>
    	<@s.url id="url" value="/hangman/images/guesses-left.png" />
    	<img alt="Guesses Left"
            src="<@s.property value="%{#url}" />" width="164" height="11" border="0"/>
    </td>
  </tr>
  <tr>
  	<td></td>
    <td align="left">
    <#-- Display Scaffold -->
  	<div id="updateScaffoldDiv">
    	<@s.set name="scaffoldImageName" value="%{'scaffold_'+hangman.guessLeft()+'.png'}" />
    	<@s.url id="url" value="%{'/hangman/images/'+#scaffoldImageName}" />
    	<img src="<@s.property value="%{#url}" />" border="0"/>
    </div>
    </td>
    <td></td>
    </tr>
  <tr>
    <td width="160">
      <p align="right">
      	<@s.url id="url" value="/hangman/images/guess.png" />
        <img alt="Current Guess" src="<@s.property value="%{#url}" />"
           align="MIDDLE" width="127" height="20" border="0"/></p>
    </td>
    <td>
    <#-- Display Vacab  -->
    <div id="updateVocabDiv">
    <@s.iterator id="currentCharacter" value="%{hangman.vocab.inCharacters()}" stat="stat">
    	<#if hangman.characterGuessedBefore(currentCharacter)>
    		<@s.set name="chalkboardImageName" value="%{'Chalkboard_'+#currentCharacter.toString()+'.png'}" />
    		<@s.url id="url" value="%{'/hangman/images/'+#chalkboardImageName}" />
    		<img height="36" alt="<@s.property value="%{#currentCharacter}" />"
        		src="<@s.property value="%{#url}" />" width="36" border="0"/>
    	<#else>
    		<@s.url id="url" value="/hangman/images/Chalkboard_underscroll.png" />
    		<img height="36" alt="_"
        		src="<@s.property value="%{#url}" />" width="36" border="0"/>
    	</#if>
	</@s.iterator>
	</div>
    </td>
  </tr>
  <tr>
    <td valign="top">
      <p align="right">
      	<@s.url id="url" value="/hangman/images/choose.png" />
        <img alt="Choose" src="<@s.property value="%{#url}" />"
             height="20" width="151" border="0"/>
      </p>
    </td>
    <td width="330">

    <#-- Show Characters Available -->
    <div id="updateCharacterAvailableDiv">
	<@s.iterator id="currentCharacter" value="%{hangman.charactersAvailable}" status="stat">
      <@s.set name="chalkboardImageName" value="%{'Chalkboard_'+#currentCharacter+'.png'}" />
      <@s.url id="chalkboardImageUrl" value="%{'/hangman/images/'+#chalkboardImageName}" />
      <@s.url id="spacerUrl" value="/hangman/images/letter-spacer.png" />

      <@s.url id="blankUrl" value="ajax/blank.action" includeContext="false" />
      <@s.a theme="ajax"
      		  href="%{blankUrl}"
      		  id="%{#currentCharacter}"
      		  notifyTopics="topicGuessMade"
      		  showErrorTransportText="true">
      	<img height="36" alt="" src="<@s.property value="%{#chalkboardImageUrl}" />" width="36" border="0" />
      </@s.a>
	</@s.iterator>
	</div>


   </td>
  </tr>
  <tr>
  	<td>

  	</td>
  </tr>
</table>
</body>
</html>


