
<html>
<head>
	<title>Showcase - Hangman - Menu</title>
</head>
<body>
	<ul>
		<li>
			<@saf.url id="url" action="hangmanAjax" namespace="/hangman" />
			<@saf.a href="%{#url}">Hangman (Ajax)</@saf.a>
		</li>
		<li>
			<@saf.url id="url" action="hangmanNonAjax" namespace="/hangman" />
			<@saf.a href="%{#url}">Hangman (Non Ajax)</@saf.a>
		</li>
	</ul>
</body>
</html>
