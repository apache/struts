
<div id="result">
</div>

<@saf.form action="panel3Submit" namespace="/nodecorate" theme="ajax">
	<@saf.select label="Gender" name="gender" list=r"%{#{'Male':'Male','Female':'Female'}}" theme="ajax" />
	<@saf.submit theme="ajax" resultDivId="result" />
</@saf.form>

