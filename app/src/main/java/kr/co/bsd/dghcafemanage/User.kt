package kr.co.bsd.dghcafemanage

class User {
    var name : String = ""
    var password : String = ""
    var assigned : Boolean = false
    var assignTime : String = ""

    constructor()

    constructor(name:String, password:String) {
        this.name = name
        this.password = password
    }
}