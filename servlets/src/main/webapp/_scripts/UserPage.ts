//noinspection JSUnusedGlobalSymbols

class UserPage {

    private header: HTMLHeadingElement;
    private userIdInput: HTMLInputElement;
    private requestedUserId: string;
    private relations: number;

    //c2 element
    private contentDiv: HTMLDivElement;

    //c3 elements
    private c3friends: HTMLLinkElement;
    private c3message: HTMLLinkElement;

    //const part
    private static readonly WS_URI = `ws://` + window.location.host + `/websocket/message`;
    private static readonly WSS_URI = `wss://` + window.location.host + `/websocket/message`;

    private webSocket: WebSocket;
    private initParam: string;

    constructor() {

        //finding elements

        var headerID = `headerName`;
        this.header = document.getElementById(headerID) as HTMLHeadingElement;

        var userIdElement = `userId`;
        this.userIdInput = document.getElementById(userIdElement) as HTMLInputElement;
        this.requestedUserId = this.userIdInput.value;

        var contentId = `content`;
        this.contentDiv = document.getElementById(contentId) as HTMLDivElement;

        var myFriendsId = `c3friends`;
        this.c3friends = document.getElementById(myFriendsId) as HTMLLinkElement;
        var requestsId = `c3message`;
        this.c3message = document.getElementById(requestsId) as HTMLLinkElement;


        //listeners

        // this.input.form.addEventListener(`submit`, (evt: Event) => {
        //     this.doSend(this.input.value);
        //     this.input.value = "";
        //     this.contentDiv.innerHTML = ``;
        //     evt.preventDefault();
        // }, true);

        this.c3friends.addEventListener(`click`, (evt: Event) => {
            evt.preventDefault();
            this.friendsClickDispatcher();
            evt.preventDefault()
        }, true);

        this.c3message.addEventListener(`click`, (evt: Event) => {
            evt.preventDefault();
            this.messageClickDispatcher();
            evt.preventDefault()
        }, true);

        //const part
        this.initParam = window.location.protocol == `http:` ? UserPage.WS_URI : UserPage.WSS_URI;

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
        if (m.type == `LoadMyPage`) this.setupPage(m, 1);
        if (m.type == `LoadUserPageFriends`) this.setupPage(m, 2);
        if (m.type == `LoadUserPageNotFriends`) this.setupPage(m, 3);
    }

    private setupPage(m: MessageWithUser, t: number) {
        this.header.innerHTML = m.user.login;
        this.contentDiv.innerHTML = `Here will be user info`;
        this.c3message.innerHTML = `Write message`

        if (t == 1) this.c3friends.innerHTML = `This is you`;
        if (t == 2) this.c3friends.innerHTML = `Remove from friends`;
        if (t == 3) this.c3friends.innerHTML = `Add to friends`;
        this.relations = t;

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
            type: 'LoadUserPage',
            content: contentType
        };
        this.doSend(JSON.stringify(command))
    }

    private errorDispatcher(evt: MessageEvent) {
        this.writeError(`ERROR: ${evt.data}`)
    }

    private onOpenDispatcher() {
        this.loadPage();
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

    private friendsClickDispatcher() {
        if (this.relations == 2) this.removeFromFriens();
        if (this.relations == 3) this.addTofriends();

    }

    private messageClickDispatcher() {
        window.location.href = `/message?id=` + this.requestedUserId;
    }

    private loadPage() {
        this.loadContent(this.requestedUserId)
    }

    private removeFromFriens() {
        window.location.href = `/del?id=` + this.requestedUserId;
    }

    private addTofriends() {
        window.location.href = `/add?id=` + this.requestedUserId;
    }
}