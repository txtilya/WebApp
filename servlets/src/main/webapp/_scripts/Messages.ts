//noinspection JSUnusedGlobalSymbols

class Messages {

    //c2 element
    private contentDiv: HTMLDivElement;

    //const part
    private static readonly WS_URI = `ws://` + window.location.host + `/websocket/message`;
    private static readonly WSS_URI = `wss://` + window.location.host + `/websocket/message`;

    private webSocket: WebSocket;
    private initParam: string;

    constructor() {

        //finding elements
        var contentId = `content`;
        this.contentDiv = document.getElementById(contentId) as HTMLDivElement;

        //const part
        this.initParam = window.location.protocol == `http:` ? Messages.WS_URI : Messages.WSS_URI;

        // noinspection JSUnusedGlobalSymbols,SpellCheckingInspection
        this.webSocket = Object.assign(new WebSocket(this.initParam), {
            onerror: (evt: MessageEvent) => this.errorDispatcher(evt),
            onmessage: (evt: MessageEvent) => this.messageDispatcher(evt),
            onopen: () => this.onOpenDispatcher(),
        });

        // noinspection SpellCheckingInspection
        addEventListener(`beforeunload`,
            this.webSocket.close
                .bind(this.webSocket));

    }

    private messageDispatcher(evt: MessageEvent) {
        // this.writeToScreen(`Message Received: ${evt.data}`);
        var m = JSON.parse(evt.data);
        if (m.type = `getConferences`) this.printConferences(m);
    }

    private errorDispatcher(evt: MessageEvent) {
        this.writeError(`ERROR: ${evt.data}`)
    }

    private onOpenDispatcher() {
        this.getConferences();
    }

    public writeError(message: string) {
        this.writeToScreen(message);
    }

    public writeToScreen(message: string) {
        const paragraph = document.createElement(`p`);
        paragraph.style.wordWrap = `break-word`;
        paragraph.appendChild(document.createTextNode(message));
        this.contentDiv.appendChild(paragraph);
    }

    private getConferences() {
        var command: Message = {
            type: `getConferences`,
            content: `getConferences`
        };
        this.webSocket.send(JSON.stringify(command));
    }

    private printConferences(m: MessageWithConferences) {

        this.contentDiv.innerHTML = ``;
        for (var i = 0; i < m.conferences.length; i++) {
            var current = m.conferences[i];
            this.writeConference(current)
        }

    }

    private writeConference(c: ConferenceObj) {
        const paragraph = document.createElement(`p`);
        paragraph.style.wordWrap = `break-word`;
        paragraph.appendChild(document.createTextNode(`id:` + c.id + ` ` + c.name));
        paragraph.addEventListener(`click`, (evt: Event) => {
            window.location.href = `/conference?id=` + c.id
        }, true);
        this.contentDiv.appendChild(paragraph);
    }
}