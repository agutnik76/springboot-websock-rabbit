<!DOCTYPE html>
<html>
<head>
    <title>Spring WebSocket Messaging</title>
    <script src="webjars/sockjs-client/1.0.2/sockjs.js"></script>
    <script src="webjars/stomp-websocket/2.3.3/stomp.js"></script>
    <script src="js/ChatController.js"></script>
</head>
<body onload="webSocket.disconnect()">
<div>
    <div>
        <button id="connect" onclick="webSocket.connect();">Connect</button>
        <button id="disconnect" disabled="disabled" onclick="webSocket.disconnect();">
            Disconnect
        </button>
    </div>
    <br />
    <div id="notifications">
        <input type="text" id="text" placeholder="Write a message..."/>
        <button id="sendMessage" onclick="webSocket.sendMessage();">Send</button>
        <p id="response"></p>
    </div>
</div>
</body>
</html>