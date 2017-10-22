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
<#if (hangman.guessLeft() >= 0)>
	<@s.set var="guessLeftImageName" value="%{'Chalkboard_'+hangman.guessLeft()+'.png'}" />
    <@s.url var="url" value="%{'/hangman/images/'+#guessLeftImageName}" />
    <img alt="No. Guesses Left"
      	   src="<@s.property value="%{#url}"/>" width="20" height="20" border="0" />
</#if>
      	   