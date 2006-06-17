
<html>
	<head>
		<title>Showcase - Chat - Show Room </title>
		<@saf.head theme="ajax" />
		<style type="text/css">
			div.box {
				border: 1px solid red;
				margin: 5px;
			}
			
			div.box h3 {
				color: white;
				background: red;
				margin: 3px;
				padding: 2px;
			}
			
			div.nobox {
				margin: 5px;
			}
			
			table.table {
				border: 1px solid red;
				width: 98%;
				margin: 5px;
			}
			
			table.table tr.tableHeader {
				color: white;
				background: red;
				margin: 3px;
				padding: 2px;
				font-size: medium; 
				font-weight: bold;
			}

			table.table td.tableSenderColumnOdd {
				background: gray;
				color: white;
				width: 20%
			}
			
			table.table td.tableDateColumnOdd {
				background: gray;
				color: white;
				width: 20%;
			}
			
			table.table td.tableMessageColumnOdd {
				background: gray;
				color: white;
				width: 60%;
			}
			
			table.table td.tableSenderColumnEven {
				background: white;
				color: gray;
				width: 20%
			}
			
			table.table td.tableDateColumnEven {
				background: white;
				color: gray;
				width: 20%;
			}
			
			table.table td.tableMessageColumnEven {
				background: white;
				color: gray;
				width: 60%;
			}
			
			div.container {
				padding-left: 200px;
				padding-right: 200px;
				float: left;
			}
			
			div.left {
				width: 200px;
				margin-left: -200px;
				float: left;
			}
			
			div.right {
				width: 200px;
				margin-right: -200px;
				float: right;
			}
			
			div.center {
				width: 100%;
			}
			
		</style>
	</head>
	<body>
		<div class="container">
		<div class="left">
		<div class="box">
			<h3>Operation</h3>
			<@saf.url id="url" action="exitRoom" namespace="/chat">
				<@saf.param name="roomName" value="%{roomName}" />
			</@saf.url>
			<ul>
				<li><@saf.a href="%{#url}">Exit Room</@saf.a></li>
			</ul>
		</div>
		<div class="box">
		<h3>Users Available In Chat</h3>
		<@saf.div id="usersAvailable" href="/chat/ajax/usersAvailable.action" 
				  theme="ajax" delay="1" updateFreq="%{@org.apache.struts.action2.showcase.chat.Constants@UPDATE_FREQ}">
			Initial Users Available ...
		</@saf.div>
		</div>
		</div>

		<div class="right">
		<div class="box">
		<h3>Users Available In Room [${roomName?default('')}]</h3>
		<@saf.url id="url" value="/chat/ajax/usersAvailableInRoom.action" includeContext="false">
			<@saf.param name="roomName" value="%{roomName}" />
		</@saf.url>
		<@saf.div id="usersAvailableInRoom" href="%{#url}" includeContext="false"
				  theme="ajax" delay="1" updateFreq="%{@org.apache.struts.action2.showcase.chat.Constants@UPDATE_FREQ}">
			Initial Users Available In Room ...
		</@saf.div>
		</div>
		</div>
		
		<div class="center">
		<div class="box">
		<h3>Messages Posted In Room [${roomName?default('')}]</h3>
		<@saf.url id="url" value="/chat/ajax/messagesAvailableInRoom.action" includeContext="false">
			<@saf.param name="roomName" value="%{roomName}" />		
		</@saf.url>
		<@saf.div id="messagesInRoom" href="%{#url}" includeContext="false"
				  theme="ajax" delay="1" updateFreq="%{@org.apache.struts.action2.showcase.chat.Constants@UPDATE_FREQ}" 
				  listenTopics="topicMessageSend">
			Initial Messages In Room ...
		</@saf.div>
		</div>
		
		<div class="box">
		<h3>Send Messages</h3>
		<@saf.form id="sendMessageForm" action="sendMessageToRoom" namespace="/chat/ajax" method="POST" theme="ajax">
			<div id="sendMessageResult"></div>
			<@saf.textarea label="Message"name="message" theme="xhtml" />
			<@saf.hidden name="roomName" value="%{roomName}" />
			<@saf.submit id="submit" theme="ajax" resultDivId="sendMessageResult" notifyTopics="topicMessageSend" value="%{'Send'}" />
		</@saf.form>
		</div>
		</div>
		</div>
	
	</body>
</html>
