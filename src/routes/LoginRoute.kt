package com.upar.routes

import com.upar.data.checkPasswordForUsername
import com.upar.data.requests.AccountRequest
import com.upar.data.responses.SimpleResponse
import io.ktor.application.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.loginRoute(){
    route("/login"){
        post {
            val request = try {
                call.receive<AccountRequest>()
            }catch (e: ContentTransformationException){
                call.respond(BadRequest)
                return@post
            }
            val isPasswordCorrect= checkPasswordForUsername(request.username,request.password)
            if(isPasswordCorrect){
                call.respond(OK,SimpleResponse(true,"You are now logged in!"))
            }else{
                call.respond(OK,SimpleResponse(false,"The username or password is incorrect."))
            }
        }
    }
}