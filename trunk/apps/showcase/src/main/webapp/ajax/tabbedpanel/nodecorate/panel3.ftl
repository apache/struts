
<div id="result">
</div>

<@s.form action="panel3Submit" namespace="/nodecorate" theme="ajax">
	<@s.select label="Gender" name="gender" list=r"%{#{'Male':'Male','Female':'Female'}}" theme="ajax" />
	<@s.submit theme="ajax" resultDivId="result" />
</@s.form>

