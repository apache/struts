<#if (hangman.guessLeft() >= 0)>
	<@s.set name="guessLeftImageName" value="%{'Chalkboard_'+hangman.guessLeft()+'.png'}" />
    <@s.url id="url" value="%{'/hangman/images/'+#guessLeftImageName}" />
    <img alt="No. Guesses Left"
      	   src="<@s.property value="%{#url}"/>" width="20" height="20" border="0" />
</#if>
      	   