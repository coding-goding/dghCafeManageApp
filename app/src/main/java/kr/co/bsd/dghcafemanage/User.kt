package kr.co.bsd.dghcafemanage

class User {
    var name : String = ""
    var password : String = ""
    var assigned : Boolean = false
    var assignTime : String = ""
    var id : String = ""
    var point : Int = 0
    var admin : Boolean = false

    constructor()

    constructor(name:String, password:String) {
        this.name = name
        this.password = password
    }
}