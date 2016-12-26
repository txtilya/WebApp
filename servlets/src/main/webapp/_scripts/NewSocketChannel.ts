//noinspection JSUnusedGlobalSymbols

class NewSocketChannel {

    private static readonly WS_URI = `ws://` + window.location.host + `/websocket/chat/1`;
    private static readonly WSS_URI = `wss://` + window.location.host + `/websocket/chat/1`;

    private messagesLink: HTMLLinkElement;
    private friendsLink: HTMLLinkElement;
    private contentDiv: HTMLDivElement;

    // private input: HTMLInputElement;
    // private output: HTMLDivElement;

    private webSocket: WebSocket;
    private initParam: string;

    //noinspection JSUnusedGlobalSymbols
    constructor() {

        var messagesId = `messages`;
        var friendsId = `friends`;
        var contentId = `content`;

        this.messagesLink = document.getElementById(messagesId) as HTMLLinkElement;
        this.friendsLink = document.getElementById(friendsId) as HTMLLinkElement;
        this.contentDiv = document.getElementById(contentId) as HTMLDivElement;

        // this.input.form.addEventListener(`submit`, (evt: Event) => {
        //     this.doSend(this.input.value);
        //     this.input.value = "";
        //     evt.preventDefault();
        // }, true);

        this.messagesLink.addEventListener(`click`, (evt: Event) => {
            evt.preventDefault();
            this.doSend("1");
            evt.preventDefault()
        },true);

        this.friendsLink.addEventListener(`click`, (evt: Event) => {
            evt.preventDefault();
            this.doSend("2");
            evt.preventDefault();
        });


        this.initParam = window.location.protocol == `http:` ? NewSocketChannel.WS_URI : NewSocketChannel.WSS_URI;

        // noinspection JSUnusedGlobalSymbols,SpellCheckingInspection
        this.webSocket = Object.assign(new WebSocket(this.initParam), {
            onerror: (evt: MessageEvent) => this.writeError(`ERROR: ${evt.data}`),
            onmessage: (evt: MessageEvent) => this.messageDispatcher(evt),
            onopen: () => this.writeToScreen(`Hello!`),
        });

        // noinspection SpellCheckingInspection
        addEventListener(`beforeunload`,
            this.webSocket.close
                .bind(this.webSocket));
    }

    public writeError(message: string) {
        this.writeToScreen(message);
    }

    public writeToScreen(message: string) {
        const paragraph = document.createElement(`p`);
        paragraph.style.wordWrap = `break-word`;
        paragraph.appendChild(document.createTextNode(message));
        this.contentDiv.appendChild(paragraph);
        while (this.contentDiv.childNodes.length > 25) {
            this.contentDiv.removeChild(this.contentDiv.firstChild);
        }
        this.contentDiv.scrollTop = this.contentDiv.scrollHeight;

    }

    public doSend(message: string) {
        if (message != ``) {
            this.webSocket.send(message);
            // this.writeToScreen(`Message Sent: ${message}`);
        }
    }

    private messageDispatcher(evt: MessageEvent) {
        this.writeToScreen(`Message Received: ${evt.data}`);
    }
}
