package com.upar

import com.upar.data.checkPasswordForUsername
import com.upar.routes.loginRoute
import com.upar.routes.postRoute
import com.upar.routes.registerRoute
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.netty.handler.codec.DefaultHeaders

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation){
        gson {
            setPrettyPrinting()
        }
    }
    install(Authentication){
        configureAuth()
    }
    install(Routing){
        registerRoute()
        loginRoute()
        postRoute()
    }

}
private fun Authentication.Configuration.configureAuth(){
    basic {
        realm = "CTF Server"
        validate {crudentials ->
            val username = crudentials.name
            val password = crudentials.password
            if(checkPasswordForUsername(username,password)){
                UserIdPrincipal(username)
            }else null
        }
    }
}

