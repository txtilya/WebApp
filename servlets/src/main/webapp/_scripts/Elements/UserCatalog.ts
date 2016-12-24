//noinspection JSUnusedGlobalSymbols
class UsersCatalog {

    private readonly targetElement: HTMLOListElement;

    //noinspection JSUnusedGlobalSymbols
    constructor(id = "users") {
        this.targetElement = document.getElementById(id) as HTMLOListElement;
    }

    public add(user: User) {
        //noinspection JSValidateTypes
        const instanceLi: HTMLLIElement = document.createElement("li");
        instanceLi.appendChild(document.createTextNode(user.login));
        this.targetElement.appendChild(instanceLi);
    }

    //noinspection JSUnusedGlobalSymbols
    public addAll(users: Array<User> = []) {
        users.forEach(this.add.bind(this));
    }
}
