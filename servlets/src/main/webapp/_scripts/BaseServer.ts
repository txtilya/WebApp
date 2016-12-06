class BaseServer {

    //noinspection SpellCheckingInspection
    constructor(private readonly method: string = `GET`, readonly baseUrl: string = `/webapi/`) {
    }

    protected getObject<T>(urlPart: string): Promise<T> {
        return new Promise((resolve, reject) => this.getObjectAsync(urlPart, resolve, reject));
    }

    protected getObjectAsync<T>(path: string, resolve: (o: T) => void, reject: (err: Error) => void) {
        this.getTextAsync(path,
            text => resolve(JSON.parse(text)),
            status => reject(Error(`JSON didn't load successfully; error code: ${status}`)));
    }

    private getTextAsync(path: string, resolve: (result: string) => void, reject: (errMsg: string) => void) {
        //noinspection SpellCheckingInspection, JSUnusedGlobalSymbols
        const xhr = Object.assign(new XMLHttpRequest(), {
            onreadystatechange: () => {
                if (xhr.readyState !== 4) return;
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
