
<html>
<head>
	<title>Showcase - Hangman</title>
	<@saf.head theme="ajax" debug="false" />
</head>
<body>

<script>
	var _listeners = {
		   guessMade: function(sourceId) {
		   		this.guessMadeFunc(sourceId);
		   		this.updateCharacterAvailable(sourceId);
		   		this.updateVocab(sourceId);
		   		this.updateScaffold(sourceId);
		   		this.updateGuessLeft(sourceId);
		   }, 
	       guessMadeFunc: function(sourceId) { 
	       				var requestAttr = { character: sourceId };
						dojo.io.bind({
							url: "<@saf.url action="guessCharacter" namespace="/hangman" />",
							load: function(type, data, event) {
								
							},
							mimetype: "text/html",
							content: requestAttr
						}); 
	       			},
	       updateCharacterAvailable: function(sourceId) {
	       				dojo.io.bind({
	       					url: "<@saf.url action="updateCharacterAvailable" namespace="/hangman/ajax" />",
	       					load: function(type, data, event) {
	       						var div = dojo.byId("updateCharacterAvailableDiv");
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
	       				url: "<@saf.url action="updateVocabCharacters" namespace="/hangman/ajax" />",
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
	       			url: "<@saf.url action="updateScaffold" namespace="/hangman/ajax" />",
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
	       			url: "<@saf.url action="updateGuessLeft" namespace="/hangman/ajax" />",
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
	// dojo.event.topic.subscribe("topicGuessMade", _listeners, "guessMadeFunc"); 
	// dojo.event.topic.subscribe("topicGuessMade", _listeners, "updateCharacterAvailable");
	// dojo.event.topic.subscribe("topicGuessMade", _listeners, "updateVocab");
	// dojo.event.topic.subscribe("topicGuessMade", _listeners, "updateScaffold");
	// dojo.event.topic.subscribe("topicGuessMade", _listeners, "updateGuessLeft");
</script>

<table bgcolor="green"> 
  <tr> 
    <td>
    <@saf.url id="url" value="/hangman/images/hangman.png" />
    <img alt="Hangman" src="<@saf.property value="%{#url}" />" 
           width="197" height="50" border="0"/> 
    </td> 
    <td width="70" align="right">
      <#-- Guesses Left -->
      <div id="updateGuessLeftDiv">
      <@saf.set name="guessLeftImageName" value="%{'Chalkboard_'+hangman.guessLeft()+'.png'}" />
      <@saf.url id="url" value="%{'/hangman/images/'+#guessLeftImageName}" />
      <img alt="No. Guesses Left"
      	   src="<@saf.property value="%{#url}"/>" width="20" height="20" border="0" />
      </div>
    </td>
    <td>
    	<@saf.url id="url" value="/hangman/images/guesses-left.png" /> 
    	<img alt="Guesses Left"
            src="<@saf.property value="%{#url}" />" width="164" height="11" border="0"/> 
    </td>
  </tr> 
  <tr> 
  	<td></td>
    <td align="left">
    <#-- Display Scaffold -->
  	<div id="updateScaffoldDiv">
    	<@saf.set name="scaffoldImageName" value="%{'scaffold_'+hangman.guessLeft()+'.png'}" />
    	<@saf.url id="url" value="%{'/hangman/images/'+#scaffoldImageName}" />
    	<img src="<@saf.property value="%{#url}" />" border="0"/> 
    </div>
    </td>
    <td></td>
    </tr> 
  <tr>
    <td width="160"> 
      <p align="right">
      	<@saf.url id="url" value="/hangman/images/guess.png" />
        <img alt="Current Guess" src="<@saf.property value="%{#url}" />"
           align="MIDDLE" width="127" height="20" border="0"/></p> 
    </td> 
    <td>
    <#-- Display Vacab  -->
    <div id="updateVocabDiv">
    <@saf.iterator id="currentCharacter" value="%{hangman.vocab.inCharacters()}" stat="stat">
    	<#if hangman.characterGuessedBefore(currentCharacter)>
    		<@saf.set name="chalkboardImageName" value="%{'Chalkboard_'+#currentCharacter.toString()+'.png'}" />
    		<@saf.url id="url" value="%{'/hangman/images/'+#url}" />
    		<img height="36" alt="<@saf.property value="%{#currentCharacter}" />"
        		src="<@saf.property value="%{#url}" />" width="36" border="0"/>
    	<#else>
    		<@saf.url id="url" value="/hangman/images/Chalkboard_underscroll.png" />
    		<img height="36" alt="_"
        		src="<@saf.property value="%{#url}" />" width="36" border="0"/>
    	</#if>
	</@saf.iterator>
	</div>
    </td> 
  </tr>
  <tr> 
    <td valign="top"> 
      <p align="right">
      	<@saf.url id="url" value="/hangman/images/choose.png" />
        <img alt="Choose" src="<@saf.property value="%{#url}" />" 
             height="20" width="151" border="0"/>
      </p> 
    </td> 
    <td width="330">
    
    <#-- Show Characters Available -->
    <div id="updateCharacterAvailableDiv">
	<@saf.iterator id="currentCharacter" value="%{hangman.charactersAvailable}" status="stat">
      <@saf.set name="chalkboardImageName" value="%{'Chalkboard_'+#currentCharacter+'.png'}" />
      <@saf.url id="chalkboardImageUrl" value="%{'/hangman/images/'+#chalkboardImageName}" />
      <@saf.url id="spacerUrl" value="/hangman/images/letter-spacer.png" />
      
      <@saf.a theme="ajax"
      		  href="ajax/blank.action"
      		  id="%{#currentCharacter}" 
      		  notifyTopics="topicGuessMade"
      		  showErrorTransportText="true">
      	<img height="36" alt="" src="<@saf.property value="%{#chalkboardImageUrl}" />" width="36" border="0" />
      </@saf.a>
	</@saf.iterator>
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

