<#--
/*
 * $Id: pom.xml 559206 2007-07-24 21:01:18Z apetrelli $
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
-->
<html>
<head>
	<title>Showcase - Hangman</title>
	<@s.head theme="xhtml" />
</head>
<body>
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
      <#if (hangman.guessLeft() >= 0)>
      <@s.set name="guessLeftImageName" value="%{'Chalkboard_'+hangman.guessLeft()+'.png'}" />
      <@s.url id="url" value="%{'/hangman/images/'+#guessLeftImageName}" />
      <img alt="No. Guesses Left"
      	   src="<@s.property value="%{#url}"/>" width="20" height="20" border="0" />
      </#if>
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
    <#if hangman.gameEnded()>
		<@s.iterator id="currentCharacter" value="%{hangman.vocab.inCharacters()}" stat="stat">
			<@s.url id="url" value="%{'/hangman/images/Chalkboard_'+#currentCharacter.toString()+'.png'}" />
			<img height="36" alt="<@s.property value="%{#currentCharacter}" />"
		 			src="<@s.property value="%{#url}" />" width="36" border="0" />
		</@s.iterator>
	<#else>
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
	</#if>
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
    <#if hangman.gameEnded()>
	<@s.set name="winImageName" value="%{'you-win.png'}" />
	<@s.set name="looseImageName" value="%{'you-lose.png'}" />
	<@s.set name="startImageName" value="%{'start.png'}" />
	<@s.url id="winImageUrl" value="%{'/hangman/images/'+#winImageName}"  />
	<@s.url id="looseImageUrl" value="%{'/hangman/images/'+#looseImageName}" />
	<@s.url id="startImageUrl" value="%{'/hangman/images/'+#startImageName}" />
	<@s.url id="startHref" action="hangmanNonAjax" namespace="/hangman" />
	
	<#if hangman.isWin()>
	<img src="<@s.property value="%{#winImageUrl}" />" width="341" height="44" />
	<#else>
	<img src="<@s.property value="%{#looseImageUrl}" />" width="381" height="44" />
	</#if>
	<@s.a href="%{#startHref}">
		<img src="<@s.property value="%{#startImageUrl}" />" width="250" height="43" />
	</@s.a>
	<#else>
	<@s.iterator id="currentCharacter" value="%{hangman.charactersAvailable}" status="stat">
      <@s.set name="chalkboardImageName" value="%{'Chalkboard_'+#currentCharacter+'.png'}" />
      <@s.url id="chalkboardImageUrl" value="%{'/hangman/images/'+#chalkboardImageName}" />
      <@s.url id="spacerUrl" value="/hangman/images/letter-spacer.png" />
      <@s.url id="url" action="guessCharacterNonAjax" namespace="/hangman">
      	<@s.param name="character" value="%{#currentCharacter}" />
      </@s.url>
      
      <@s.a href="%{#url}"
      		  id="%{#currentCharacter}" 
      		  >
      	<img height="36" alt="" src="<@s.property value="%{#chalkboardImageUrl}" />" width="36" border="0" />
      </@s.a>
	</@s.iterator>
	</#if>
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


