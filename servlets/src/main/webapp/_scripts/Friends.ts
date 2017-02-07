//noinspection JSUnusedGlobalSymbols

class Friends {

    //c2 element
    private contentDiv: HTMLDivElement;

    //c3 elements
    private myFriends: HTMLLinkElement;
    private requests: HTMLLinkElement;
    private searchInput: HTMLInputElement;

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
        var searchInputId = `search`;
        this.searchInput = document.getElementById(searchInputId) as HTMLInputElement;

        //listeners

        this.searchInput.form.addEventListener(`submit`, (evt: Event) => {
            this.searchFriends(this.searchInput.value);
            // this.contentDiv.innerHTML = ``;
            // this.searchInput.value = ``;
            evt.preventDefault();
        }, true);

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
        if (m.roleForRequester != ` `) this.changeContentFriends(m);
    }

    private changeContentFriends(m: MessageWithUsers) {
        this.contentDiv.innerHTML = ``;
        for (var i = 0; i < m.users.length; i++) {
            var current = m.users[i];
            this.writeUser(current, m.roleForRequester)
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
        this.sendMessage('INFO', 'Hello!');
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

    public writeUser(u: User, roleForRequester: String) {

        const paragraph = document.createElement(`p`);
        paragraph.style.wordWrap = `break-word`;
        paragraph.appendChild(document.createTextNode(`id:` + u.id + ` login:` + u.login));
        paragraph.addEventListener(`click`, (evt: Event) => {
            window.location.href = `/user?id=` + u.id
        }, true);
        this.contentDiv.appendChild(paragraph);

        // if (roleForRequester == `notFriends`) {
        //     const href1 = document.createElement(`a`);
        //     href1.style.wordWrap = `break-word`;
        //     href1.setAttribute(`href`, `/add?id=` + u.id);
        //     href1.innerHTML = `/Add to friends/ `;
        //
        //     const href2 = document.createElement(`a`);
        //     href2.style.wordWrap = `break-word`;
        //     href2.setAttribute(`href`, `/message?id=` + u.id);
        //     href2.innerHTML = `/Message/ `;
        //
        //     this.contentDiv.appendChild(href1);
        //     this.contentDiv.appendChild(href2);
        //
        // }

        if (roleForRequester == `friends`) {
            const href1 = document.createElement(`a`);
            href1.style.wordWrap = `break-word`;
            href1.setAttribute(`href`, `/remove?id=` + u.id);
            href1.innerHTML = `/Remove/ `;

            const href2 = document.createElement(`a`);
            href2.style.wordWrap = `break-word`;
            href2.setAttribute(`href`, `/message?id=` + u.id);
            href2.innerHTML = `/Message/ `;

            this.contentDiv.appendChild(href1);
            this.contentDiv.appendChild(href2);

        }

        if (roleForRequester == `requests`) {
            const href1 = document.createElement(`a`);
            href1.style.wordWrap = `break-word`;
            href1.setAttribute(`href`, `/confirm?id=` + u.id);
            href1.innerHTML = `/Confirm/ `;

            const href2 = document.createElement(`a`);
            href2.style.wordWrap = `break-word`;
            href2.setAttribute(`href`, `/reject?id=` + u.id);
            href2.innerHTML = `/Reject/ `;

            const href3 = document.createElement(`a`);
            href3.style.wordWrap = `break-word`;
            href3.setAttribute(`href`, `/message?id=` + u.id);
            href3.innerHTML = `/Message/ `;

            this.contentDiv.appendChild(href1);
            this.contentDiv.appendChild(href2);
            this.contentDiv.appendChild(href3);

        }
        this.contentDiv.appendChild(document.createElement(`br`));
        this.contentDiv.appendChild(document.createElement(`br`));


        // while (this.contentDiv.childNodes.length > 25) {
        //     this.contentDiv.removeChild(this.contentDiv.firstChild);
        // }
        // this.contentDiv.scrollTop = this.contentDiv.scrollHeight;
    }

    private searchFriends(value: string) {
        var command: Message = {
            type: 'SearchFriends',
            content: value
        };
        this.webSocket.send(JSON.stringify(command));
    }
}