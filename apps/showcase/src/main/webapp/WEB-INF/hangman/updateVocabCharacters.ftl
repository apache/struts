<#--
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
-->
<#if hangman.gameEnded()>
<@s.iterator var="currentCharacter" value="%{hangman.vocab.inCharacters()}" stat="stat">
	<@s.url var="url" value="%{'/hangman/images/Chalkboard_'+#currentCharacter.toString()+'.png'}" />
	<img height="36" alt="<@s.property value="%{#currentCharacter}" />"
		 src="<@s.property value="%{#url}" />" width="36" border="0" />
</@s.iterator>
<#else>
<@s.iterator var="currentCharacter" value="%{hangman.vocab.inCharacters()}" stat="stat">
    	<#if hangman.characterGuessedBefore(currentCharacter)>
    		<@s.set var="chalkboardImageName" value="%{'Chalkboard_'+#currentCharacter.toString()+'.png'}" />
    		<@s.url var="url" value="%{'/hangman/images/'+#chalkboardImageName}" />
    		<img height="36" alt="<@s.property value="%{#currentCharacter}" />"
        		src="<@s.property value="%{#url}" />" width="36" border="0"/>
    	<#else>
    		<@s.url var="url" value="/hangman/images/Chalkboard_underscroll.png" />
    		<img height="36" alt="_"
        		src="<@s.property value="%{#url}" />" width="36" border="0"/>
    	</#if>
</@s.iterator>
</#if>
