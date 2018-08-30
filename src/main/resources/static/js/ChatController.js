class ChatController {

    constructor() {
        this._onConnected = this._onConnected.bind(this);
    }

    _onConnected(frame) {
        this.setConnected(true);
        console.log('Connected: ' + frame);
        this.stompClient.subscribe('/topic/notifications', this.showMessage);
    }

    setConnected(connected) {
        document.getElementById('connect').disabled = connected;
        document.getElementById('disconnect').disabled = !connected;
        document.getElementById('notifications').style.visibility = connected ? 'visible' : 'hidden';
        document.getElementById('response').innerHTML = '';
    }

    connect() {
        var socket = new SockJS('/socket/sockJs');
        this.stompClient = Stomp.over(socket);
        this.stompClient.connect({}, this._onConnected);

    }

    onMessageReceived(payload) {
        var message = JSON.parse(payload.body);
        alert(message);
    }
    disconnect() {
        if(this.stompClient != null) {
            this.stompClient.disconnect();
        }
        this.setConnected(false);
        console.log("Disconnected");
    }

    sendMessage() {
        var message = document.getElementById('text').value;
        this.stompClient.send("/app/message", {}, JSON.stringify({author :message}));
    }

    showMessage(message) {
        var response = document.getElementById('response');
        var p = document.createElement('p');
        p.style.wordWrap = 'break-word';
        p.appendChild(document.createTextNode(message.body));
        response.appendChild(p);
    }

}
var webSocket = new ChatController();