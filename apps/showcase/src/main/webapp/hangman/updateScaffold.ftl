	<@saf.set name="scaffoldImageName" value="%{'scaffold_'+hangman.guessLeft()+'.png'}" />
    <@saf.url id="url" value="%{'/hangman/images/'+#scaffoldImageName}" />
    <img src="<@saf.property value="%{#url}" />" border="0"/> 
