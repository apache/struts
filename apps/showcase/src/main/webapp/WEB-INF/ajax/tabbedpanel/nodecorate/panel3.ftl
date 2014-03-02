
<div id="result">
</div>

<@s.form action="panel3Submit" namespace="/nodecorate">
	<@sx.autocompleter label="Gender" name="gender" list="%{#{'Male':'Male','Female':'Female'}}"  />
	<@sx.submit targets="result" />
</@s.form>

