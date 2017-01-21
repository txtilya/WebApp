//noinspection JSUnusedGlobalSymbols

class Friends {

    //c2 element
    private contentDiv: HTMLDivElement;

    //c3 elements
    private myFriends: HTMLLinkElement;
    private requests: HTMLLinkElement;
    private search: HTMLLinkElement;

    //const part
    private static readonly WS_URI = `ws://` + window.location.host + `/websocket/message`;
    private static readonly WSS_URI = `wss://` + window.location.host + `/websocket/message`;

    private webSocket: WebSocket;
    private initParam: string;

    constructor() {

        //finding elements
        var contentId = `content`;
        this.contentDiv = document.getElementById(contentId) as HTMLDivElement;

        var myFriendsId = `c3friends`;
        this.myFriends = document.getElementById(myFriendsId) as HTMLLinkElement;
        var requestsId = `c3requests`;
        this.requests = document.getElementById(requestsId) as HTMLLinkElement;
        var searchId = `c3search`;
        this.search = document.getElementById(searchId) as HTMLLinkElement;

        //listeners

        // this.input.form.addEventListener(`submit`, (evt: Event) => {
        //     this.doSend(this.input.value);
        //     this.input.value = "";
        //     this.contentDiv.innerHTML = ``;
        //     evt.preventDefault();
        // }, true);

        this.myFriends.addEventListener(`click`, (evt: Event) => {
            evt.preventDefault();
            this.loadContent("friends");
            evt.preventDefault()
        }, true);

        this.requests.addEventListener(`click`, (evt: Event) => {
            evt.preventDefault();
            this.loadContent("requests");
            evt.preventDefault()
        }, true);

        this.search.addEventListener(`click`, (evt: Event) => {
            evt.preventDefault();
            this.loadContent("search");
            evt.preventDefault()
        }, true);


        //const part
        this.initParam = window.location.protocol == `http:` ? Friends.WS_URI : Friends.WSS_URI;

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
        this.changeContentFriends(m);
    }

    private changeContentFriends(m: any) {
        this.contentDiv.innerHTML = ``;
        for (var i = 0; i < m.length; i++) {
            var current = m[i];
            this.writeUser(current)
        }
    }

    private doSend(message: string) {
        if (message != ``) {
            this.webSocket.send(message);
        }
    }

    public sendMessage(contentType: string, contentBody: string) {
        var message: Message = {
            type: contentType,
            content: contentBody
        };
        this.doSend(JSON.stringify(message))
    }


    private loadContent(contentType: string) {
        var command: Message = {
            type: 'ChangeContent',
            content: contentType
        };
        this.doSend(JSON.stringify(command))
    }

    private errorDispatcher(evt: MessageEvent) {
        this.writeError(`ERROR: ${evt.data}`)
    }

    private onOpenDispatcher() {
        this.sendMessage('INFO', 'Hello!')
        this.loadContent("friends");
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

    public writeUser(u: User) {
        const paragraph = document.createElement(`p`);
        paragraph.style.wordWrap = `break-word`;
        paragraph.appendChild(document.createTextNode(`id:` + u.id + ` login:` + u.login));
        paragraph.addEventListener(`click`, (evt: Event) => {
            window.location.href = `/user?id=` + u.id
        }, true);
        this.contentDiv.appendChild(paragraph);
        while (this.contentDiv.childNodes.length > 25) {
            this.contentDiv.removeChild(this.contentDiv.firstChild);
        }
        this.contentDiv.scrollTop = this.contentDiv.scrollHeight;
    }
}