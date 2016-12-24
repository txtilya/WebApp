//noinspection JSUnusedGlobalSymbols

class SocketChannel {

    private static readonly WS_URI = `ws://` + window.location.host + `/websocket/chat/1`;
    private static readonly WSS_URI = `wss://` + window.location.host + `/websocket/chat/1`;

    private input: HTMLInputElement;
    private output: HTMLDivElement;
    private webSocket: WebSocket;
    private initParam: string;

    //noinspection JSUnusedGlobalSymbols
    constructor(outputElementId = `output`, inputElementId = `message`) {

        this.output = document.getElementById(outputElementId) as HTMLDivElement;
        this.input = document.getElementById(inputElementId) as HTMLInputElement;

        this.input.form.addEventListener(`submit`, (evt: Event) => {
            this.doSend(this.input.value);
            this.input.value = "";
            evt.preventDefault();
        }, true);

        this.initParam = window.location.protocol == `http:` ? SocketChannel.WS_URI : SocketChannel.WSS_URI;

        // noinspection JSUnusedGlobalSymbols,SpellCheckingInspection
        this.webSocket = Object.assign(new WebSocket(this.initParam), {
            onerror: (evt: MessageEvent) => this.writeToScreen(`ERROR: ${evt.data}`),
            onmessage: (evt: MessageEvent) => this.writeToScreen(`Message Received: ${evt.data}`),
            onopen: () => this.writeToScreen(`Hello!`),
        });

        // noinspection SpellCheckingInspection
        addEventListener(`beforeunload`,
            this.webSocket.close
                .bind(this.webSocket));
    }


    public writeToScreen(message: string) {
        const paragraph = document.createElement(`p`);
        paragraph.style.wordWrap = `break-word`;
        paragraph.appendChild(document.createTextNode(message));
        this.output.appendChild(paragraph);
        while (this.output.childNodes.length > 25) {
            this.output.removeChild(this.output.firstChild);
        }
        this.output.scrollTop = this.output.scrollHeight;

    }

    public doSend(message: string) {
        if (message != ``) {
            this.webSocket.send(message);
            // this.writeToScreen(`Message Sent: ${message}`);
        }
    }
}
