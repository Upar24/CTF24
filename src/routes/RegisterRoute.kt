package com.upar.routes

import com.upar.data.checkIfUserExists
import com.upar.data.collections.User
import com.upar.data.registerUser
import com.upar.data.requests.AccountRequest
import com.upar.data.responses.SimpleResponse
import io.ktor.application.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.registerRoute(){
    route("/register"){
        post {
            val request= try{
                call.receive<AccountRequest>()
            }catch (e: ContentTransformationException){
                call.respond(BadRequest)
                return@post
            }
            val userExists= checkIfUserExists(request.username)
            if(!userExists){
                if(registerUser(User(request.username,request.password))){
                    call.respond(OK,SimpleResponse(true,"Successfully created account"))
                }else{
                    call.respond(OK,SimpleResponse(false,"An unknown error occured"))
                }
            }else{
                call.respond(OK,SimpleResponse(false,"A user with that usernamae already exists"))
            }
        }
    }

}