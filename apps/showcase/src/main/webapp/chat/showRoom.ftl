<html>
<head>
	<title>Struts2 Showcase - Chat - Show Room </title>
<@sx.head />
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
<div class="page-header">
	<h1>Chat - Show Room</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12 container">
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
				<@sx.div id="usersAvailable" href="%{usersAvailableUrl}"
				updateFreq="%{@org.apache.struts2.showcase.chat.Constants@UPDATE_FREQ}">
					Initial Users Available ...
				</@sx.div>
				</div>
			</div>

			<div class="center">
				<div class="box">
					<h3>Messages Posted In Room [${roomName?default('')?html}]</h3>
				<@s.url id="url" value="/chat/ajax/messagesAvailableInRoom.action" includeContext="true">
					<@s.param name="roomName" value="%{roomName}" />
				</@s.url>
				<@sx.div id="messagesInRoom" href="%{#url}" includeContext="true"
				updateFreq="%{@org.apache.struts2.showcase.chat.Constants@UPDATE_FREQ}"
				listenTopics="topicMessageSend">
					Initial Messages In Room ...
				</@sx.div>
				</div>

				<div class="box">
					<h3>Send Messages</h3>
				<@s.form id="sendMessageForm" action="sendMessageToRoom" namespace="/chat/ajax" method="POST">
					<div id="sendMessageResult"></div>
					<@s.textarea label="Message"name="message" theme="xhtml" />
					<@s.hidden name="roomName" value="%{roomName}" />
					<@sx.submit id="submit" resultDivId="sendMessageResult" afterNotifyTopics="topicMessageSend" value="%{'Send'}"  cssClass="btn btn-primary"/>
				</@s.form>
				</div>
			</div>


			<div class="right">
				<div class="box">
					<h3>Users Available In Room [${roomName?default('')?html}]</h3>
				<@s.url id="url" value="/chat/ajax/usersAvailableInRoom.action" includeContext="true">
					<@s.param name="roomName" value="%{roomName}" />
				</@s.url>
				<@sx.div id="usersAvailableInRoom" href="%{#url}" includeContext="true"
				delay="1" updateFreq="%{@org.apache.struts2.showcase.chat.Constants@UPDATE_FREQ}">
					Initial Users Available In Room ...
				</@sx.div>
				</div>
			</div>


		</div>
	</div>
</div>
</body>
</html>
