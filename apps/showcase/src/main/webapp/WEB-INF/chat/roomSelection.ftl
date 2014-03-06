<html>
<head>
	<title>Struts2 Showcase - Chat - Room Selection</title>
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

		table.table td.tableOperationColumnOdd {
			background: gray;
			color: white;
			width: 20%
		}

		table.table td.tableNameColumnOdd {
			background: gray;
			color: white;
			width: 20%;
		}

		table.table td.tableDescriptionColumnOdd {
			background: gray;
			color: white;
			width: 40%;
		}

		table.table td.tableDateCreatedColumnOdd {
			background: gray;
			color: white;
			width: 20%;
		}

		table.table td.tableOperationColumnEven {
			background: white;
			color: gray;
			width: 20%
		}

		table.table td.tableNameColumnEven {
			background: white;
			color: gray;
			width: 20%;
		}

		table.table td.tableDescriptionColumnEven {
			background: white;
			color: gray;
			width: 40%;
		}

		table.table td.tableDateCreatedColumnEven {
			background: white;
			color: gray;
			width: 20%;
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
			float: right;
		}

		div.center {
			width: 60%;
			float: left;
		}

	</style>
</head>
<body>
<div class="page-header">
	<h1>Chat - Room Selection</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12 container">


			<div class="left">
				<div class="box">
					<h3>Operations</h3>
				<@s.url id="url" action="logout" namespace="/chat" />
					<ul>
						<li><@s.a href="%{#url}">Logout</@s.a></li>
					</ul>
				</div>
			<#if (actionErrors?size gt 0)>
				<div class="box">
					<h3>Action Errors</h3>
					<@s.actionerrors />
				</div>
			</#if>
				<div class="box">
					<h3>Users Available In Chat</h3>
				<@s.url id="usersAvailableUrl" action="usersAvailable" namespace="/chat/ajax" />
				<@sx.div id="usersAvailable" updateFreq="%{@org.apache.struts2.showcase.chat.Constants@UPDATE_FREQ}"
				href="%{usersAvailableUrl}"
				class="box">
					Initial Loading Users ...
				</@sx.div>
				</div>
			</div>


			<div class="center">
				<div class="box">
					<h3>Rooms Available In Chat</h3>
				<@s.url id="roomsAvailableUrl" action="roomsAvailable" namespace="/chat/ajax" />
				<@sx.div id="roomsAvailable" listenTopics="topicRoomCreated"
				updateFreq="%{@org.apache.struts2.showcase.chat.Constants@UPDATE_FREQ}"
				href="%{roomsAvailableUrl}" >
					Initial Loading Rooms ...
				</@sx.div>
				</div>

				<div id="createRoom" class="box">
					<h3>Create Room In Chat</h3>

					<div id="createRoomResult"></div>
				<@s.form id="createRoomId" action="createRoom" namespace="/chat/ajax" method="POST">
					<@s.textfield label="Room Name" required="true" name="name" />
					<@s.textarea theme="xhtml" label="Room Description" required="true" name="Description" />
					<@sx.submit value="%{'Create Room'}" targets="createRoomResult" afterNotifyTopics="topicRoomCreated" align="left" cssClass="btn btn-primary" />
				</@s.form>
				</div>
			</div>

		</div>
	</div>
</div>
</body>
</html>

