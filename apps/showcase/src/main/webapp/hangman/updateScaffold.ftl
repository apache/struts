	<@s.set name="scaffoldImageName" value="%{'scaffold_'+hangman.guessLeft()+'.png'}" />
    <@s.url id="url" value="%{'/hangman/images/'+#scaffoldImageName}" />
    <img src="<@s.property value="%{#url}" />" border="0"/>
