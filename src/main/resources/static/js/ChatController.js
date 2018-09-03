class ChatController {

    constructor() {
        this.ENTRY_POINT_URL = '/socket/sockJs';
        this.userId = Math.random();
        this._onConnected = this._onConnected.bind(this);
    }

    _onConnected(frame) {
        this.setConnected(true);
        console.log('Connected: ' + frame);
        this.stompClient.subscribe('/topic/notifications', this.showMessage);
        this.stompClient.subscribe('/user/topic/stam', this.showUser);
        //this.stompClient.subscribe('/user/notifications', this.showUser);
    }

    setConnected(connected) {
        document.getElementById('connect').disabled = connected;
        document.getElementById('disconnect').disabled = !connected;
        document.getElementById('notifications').style.visibility = connected ? 'visible' : 'hidden';
        document.getElementById('response').innerHTML = '';
    }

    connect() {
        //var socket = new SockJS('/socket/sockJs');
        let params = new URL(window.location).searchParams;
        this.userId = params.get('user');
        var socket = new SockJS(this.getConnectionUrl(this.userId));
        this.stompClient = Stomp.over(socket);
        this.stompClient.connect({}, this._onConnected);

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
        this.stompClient.send("/app/message/" + this.userId, {}, JSON.stringify({author :message}));
    }

    showMessage(message) {
        var response = document.getElementById('response');
        var p = document.createElement('p');
        p.style.wordWrap = 'break-word';
        p.appendChild(document.createTextNode(message.body));
        response.appendChild(p);
    }
    showUser(message) {
        var response = document.getElementById('user_response');
        var p = document.createElement('p');
        p.style.wordWrap = 'break-word';
        p.appendChild(document.createTextNode(message.body));
        response.appendChild(p);
    }
    getConnectionUrl(username) {
        return `http://${username}@${window.location.host}${this.ENTRY_POINT_URL}`;
        //return `http://${window.location.host}${this.ENTRY_POINT_URL}`;
    }
}
var webSocket = new ChatController();