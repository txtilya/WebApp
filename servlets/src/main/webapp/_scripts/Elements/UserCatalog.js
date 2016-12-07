class UsersCatalog {
    constructor(id = "users") {
        this.targetElement = document.getElementById(id);
    }
    add(user) {
        const instanceLi = document.createElement("li");
        instanceLi.appendChild(document.createTextNode(user.login));
        this.targetElement.appendChild(instanceLi);
    }
    addAll(users = []) {
        users.forEach(this.add.bind(this));
    }
}
//# sourceMappingURL=UserCatalog.js.map