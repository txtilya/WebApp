class BaseServer {
    constructor(method = `GET`, baseUrl = `/webapi/`) {
        this.method = method;
        this.baseUrl = baseUrl;
    }
    getObject(urlPart) {
        return new Promise((resolve, reject) => this.getObjectAsync(urlPart, resolve, reject));
    }
    getObjectAsync(path, resolve, reject) {
        this.getTextAsync(path, text => resolve(JSON.parse(text)), status => reject(Error(`JSON didn't load successfully; error code: ${status}`)));
    }
    getTextAsync(path, resolve, reject) {
        const xhr = Object.assign(new XMLHttpRequest(), {
            onreadystatechange: () => {
                if (xhr.readyState !== 4)
                    return;
                if (xhr.status === 200)
                    resolve(xhr.responseText);
                else
                    reject(xhr.statusText);
            },
        });
        xhr.open(this.method, this.baseUrl + path, true);
        xhr.send(null);
    }
}
//# sourceMappingURL=BaseServer.js.map