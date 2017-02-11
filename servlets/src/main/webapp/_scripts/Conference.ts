//noinspection JSUnusedGlobalSymbols

class Conference {

    private header: HTMLHeadingElement;
    private conferenceIdInput: HTMLInputElement;
    private state: number;
    private messageInput: HTMLInputElement;
    private requestedConferenceId: string;

    //c2 element
    private contentDiv: HTMLDivElement;

    //c3 elements
    private c3showMoreMessages: HTMLLinkElement;
    private c3addFriend: HTMLLinkElement;

    //const part
    private static readonly WS_URI = `ws://` + window.location.host + `/websocket/message`;
    private static readonly WSS_URI = `wss://` + window.location.host + `/websocket/message`;

    private webSocket: WebSocket;
    private initParam: string;
    private messagesCount: number;


    constructor() {
        this.state = 0;
        this.messagesCount = 100;

        //finding elements

        var headerID = `headerName`;
        this.header = document.getElementById(headerID) as HTMLHeadingElement;

        var conferenceId = `conferenceId`;
        this.conferenceIdInput = document.getElementById(conferenceId) as HTMLInputElement;
        this.requestedConferenceId = this.conferenceIdInput.value;

        var contentId = `content`;
        this.contentDiv = document.getElementById(contentId) as HTMLDivElement;


        var c3showMoreMessagesId = `c3showMoreMessages`;
        this.c3showMoreMessages = document.getElementById(c3showMoreMessagesId) as HTMLLinkElement;

        var c3addFriendId = `c3addFriend`;
        this.c3addFriend = document.getElementById(c3addFriendId) as HTMLLinkElement;

        var messageInputId = `message`;
        this.messageInput = document.getElementById(messageInputId) as HTMLInputElement;


        //listeners

        this.messageInput.form.addEventListener(`submit`, (evt: Event) => {
            this.sendMessageToConference(this.requestedConferenceId, this.messageInput.value)
            // this.doSend(this.messageInput.value);
            this.messageInput.value = "";
            // this.contentDiv.innerHTML = ``;
            evt.preventDefault();
        }, true);

        this.c3showMoreMessages.addEventListener(`click`, (evt: Event) => {
            evt.preventDefault();
            this.moreClickDispatcher();
            evt.preventDefault()
        }, true);

        this.c3addFriend.addEventListener(`click`, (evt: Event) => {
            evt.preventDefault();
            this.addClickDispatcher();
            evt.preventDefault()
        }, true);

        //const part
        this.initParam = window.location.protocol == `http:` ? Conference.WS_URI : Conference.WSS_URI;

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
        if (m.type == `ConferenceMessage`) {
            this.writeMessage(m)
        }
        if (m.type == `getConferenceName`) {
            this.setConferenceName(m.content)
        }

    }

    private writeMessage(m: IncConfMsg) {
        var d = new Date(m.timestamp);
        const paragraph = document.createElement(`p`);
        paragraph.style.wordWrap = `break-word`;
        paragraph.appendChild(document.createTextNode(d.getHours() + `:` + d.getMinutes() + `:` +
            d.getSeconds() + ` id:` + m.messageId + ` ` + m.creator + `: ` + m.content));
        // paragraph.addEventListener(`click`, (evt: Event) => {
        //     window.location.href = `/user?id=` + u.id
        // }, true);
        this.contentDiv.appendChild(paragraph);
        while (this.contentDiv.childNodes.length > 250) {
            this.contentDiv.removeChild(this.contentDiv.firstChild);
        }
        this.contentDiv.scrollTop = this.contentDiv.scrollHeight;
    }


    private errorDispatcher(evt: MessageEvent) {
        this.writeError(`ERROR: ${evt.data}`)
    }

    private onOpenDispatcher() {
        this.getConferenceName(this.requestedConferenceId);
        this.getMessages(this.requestedConferenceId, this.messagesCount)
    }

    public writeError(message: string) {
        this.writeToScreen(message);
    }

    public writeToScreen(message: string) {
        const paragraph = document.createElement(`p`);
        paragraph.style.wordWrap = `break-word`;
        paragraph.appendChild(document.createTextNode(message));
        this.contentDiv.appendChild(paragraph);
        // while (this.contentDiv.childNodes.length > 25) {
        //     this.contentDiv.removeChild(this.contentDiv.firstChild);
        // }
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
        // while (this.contentDiv.childNodes.length > 25) {
        //     this.contentDiv.removeChild(this.contentDiv.firstChild);
        // }
        this.contentDiv.scrollTop = this.contentDiv.scrollHeight;
    }


    private moreClickDispatcher() {
        this.messagesCount *= 2;
        this.getMessages(this.requestedConferenceId, this.messagesCount)
    }

    private addClickDispatcher() {

    }


    private sendMessageToConference(conferenceId: string, content: string) {
        if (content != ``) {
            var command: ConferenceMessage = {
                type: 'ConferenceMessage',
                content: content,
                conferenceId: conferenceId
            };
            this.webSocket.send(JSON.stringify(command))
        }
    }

    private getConferenceName(requestedConferenceId: string) {
        var command: Message = {
            type: `getConferenceName`,
            content: requestedConferenceId
        };
        this.webSocket.send(JSON.stringify(command))

    }

    private getMessages(requestedConferenceId: string, messagesCount: number) {
        var command: Message = {
            type: `getMessages`,
            content: requestedConferenceId
        };
        this.webSocket.send(JSON.stringify(command))
    }

    private setConferenceName(content: string) {
        this.header.innerHTML = content
    }
}