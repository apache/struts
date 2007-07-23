
<html>
	<head>
		<title>Showcase - Chat - Show Room </title>
		<@s.head theme="ajax" />
		<style type="text/css">
			div.box {
				border: 1px solid darkblue;
				margin: 5px;
			}
			
			div.box h3 {
				color: white;
				background: darkblue;
				margin: 3px;
				padding: 2px;
			}
			
			div.nobox {
				margin: 5px;
			}
			
			table.table {
				border: 1px solid darkblue;
				width: 98%;
				margin: 5px;
			}
			
			table.table tr.tableHeader {
				color: white;
				background: darkblue;
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
				margin-left: auto;
				margin-right: auto;
				width: 100%;
			}
			
			div.left {
				width: 20%;
				float: left;
			}
			
			div.right {
				width: 20%;
				float: left;
			}
			
			div.center {
				width: 60%;
				float: left;
			}
			
		</style>
	</head>
	<body>
		<div class="container">
		<div class="left">
		<div class="box">
			<h3>Operation</h3>
			<@s.url id="url" action="exitRoom" namespace="/chat">
				<@s.param name="roomName" value="%{roomName}" />
			</@s.url>
			<ul>
				<li><@s.a href="%{#url}">Exit Room</@s.a></li>
			</ul>
		</div>
		<div class="box">
		<h3>Users Available In Chat</h3>
        <@s.url id="usersAvailableUrl" action="usersAvailable" namespace="/chat/ajax" />
        <@s.div id="usersAvailable" href="%{usersAvailableUrl}"
				  theme="ajax" delay="1" updateFreq="%{@org.apache.struts2.showcase.chat.Constants@UPDATE_FREQ}">
			Initial Users Available ...
		</@s.div>
		</div>
		</div>
		
		<div class="center">
		<div class="box">
		<h3>Messages Posted In Room [${roomName?default('')}]</h3>
		<@s.url id="url" value="/chat/ajax/messagesAvailableInRoom.action" includeContext="true">
			<@s.param name="roomName" value="%{roomName}" />
		</@s.url>
		<@s.div id="messagesInRoom" href="%{#url}" includeContext="true"
				  theme="ajax" delay="1" updateFreq="%{@org.apache.struts2.showcase.chat.Constants@UPDATE_FREQ}" 
				  listenTopics="topicMessageSend">
			Initial Messages In Room ...
		</@s.div>
		</div>
		
		<div class="box">
		<h3>Send Messages</h3>
		<@s.form id="sendMessageForm" action="sendMessageToRoom" namespace="/chat/ajax" method="POST" theme="ajax">
			<div id="sendMessageResult"></div>
			<@s.textarea label="Message"name="message" theme="xhtml" />
			<@s.hidden name="roomName" value="%{roomName}" />
			<@s.submit id="submit" theme="ajax" resultDivId="sendMessageResult" notifyTopics="topicMessageSend" value="%{'Send'}" />
		</@s.form>
		</div>
		</div>
		

		<div class="right">
		<div class="box">
		<h3>Users Available In Room [${roomName?default('')}]</h3>
		<@s.url id="url" value="/chat/ajax/usersAvailableInRoom.action" includeContext="true">
			<@s.param name="roomName" value="%{roomName}" />
		</@s.url>
		<@s.div id="usersAvailableInRoom" href="%{#url}" includeContext="true"
				  theme="ajax" delay="1" updateFreq="%{@org.apache.struts2.showcase.chat.Constants@UPDATE_FREQ}">
			Initial Users Available In Room ...
		</@s.div>
		</div>
		</div>
		
		
		</div>
	
	</body>
</html>
