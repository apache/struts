
<html>
<head>
	<title>Showcase - Hangman - Menu</title>
</head>
<body>
	<ul>
		<li>
			<@s.url id="url" action="hangmanNonAjax" namespace="/hangman" />
			<@s.a href="%{#url}">Hangman (Non Ajax)</@s.a>
		</li>
        <li>
            <@s.url id="url" action="hangmanAjax" namespace="/hangman" />
            <@s.a href="%{#url}">Hangman (Ajax - Experimental)</@s.a>
        </li>
	</ul>
</body>
</html>
