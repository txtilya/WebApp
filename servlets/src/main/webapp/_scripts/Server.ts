//noinspection JSUnusedGlobalSymbols
class Server extends BaseServer {

    public getUsers(): Promise<Array<User>> {
        return this.getObject(`users`);
    }

    // //noinspection JSUnusedGlobalSymbols
    // public getInstances(): Promise<Array<Instance>> {
    //     return this.getObject(`instance`);
    // }
}
