	<@s.set var="scaffoldImageName" value="%{'scaffold_'+hangman.guessLeft()+'.png'}" />
    <@s.url var="url" value="%{'/hangman/images/'+#scaffoldImageName}" />
    <img src="<@s.property value="%{#url}" />" border="0"/>
