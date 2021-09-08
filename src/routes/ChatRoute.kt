package com.upar.routes

import com.upar.data.collections.Chat
import com.upar.data.collections.Wall
import com.upar.data.database.*
import com.upar.data.requests.OneRequest
import com.upar.data.responses.SimpleResponse
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.chatRoute(){
    route("/savechat"){
        authenticate {
            post {
                val username = call.principal<UserIdPrincipal>()!!.name
                val request = try {
                    call.receive<Chat>()
                }catch (e: ContentTransformationException){
                    call.respond(BadRequest)
                    return@post
                }
                if(request.chat?.length!! > 101){
                    call.respond(OK,SimpleResponse(false,"Messages too long"))
                    return@post
                }
                val chat= saveChat(username,request)
                if(chat){
                    call.respond(OK,SimpleResponse(true,"sent"))
                }else{
                    call.respond(OK,SimpleResponse(false,"An unknown error occured"))
                }
            }
        }
    }
    route("/getchat"){
        get {
            val chats = getAllChat()
            call.respond(OK,chats)
        }
    }

    route("/savewall"){
        authenticate {
            post {
                val username = call.principal<UserIdPrincipal>()!!.name
                val request = try {
                    call.receive<Wall>()
                }catch (e: ContentTransformationException){
                    call.respond(BadRequest)
                    return@post
                }
                if(request.chat?.length!! > 101){
                    call.respond(OK,SimpleResponse(false,"Messages too long"))
                    return@post
                }
                val wall= saveWall(username,request)
                if(wall){
                    call.respond(OK,SimpleResponse(true,"sent"))
                }else{
                    call.respond(OK,SimpleResponse(false,"An unknown error occured"))
                }
            }
        }
    }
    route("/getwall"){
        post {
            val request= try {
                call.receive<OneRequest>()
            }catch (e: ContentTransformationException){
                call.respond(BadRequest)
                return@post
            }
            val walls = getAllWall(request.property)
            call.respond(OK,walls)
        }
    }
    route("/deletewall"){
        authenticate {
            post {
                val username = call.principal<UserIdPrincipal>()!!.name
                val request=try {
                    call.receive<Wall>()
                }catch (e: ContentTransformationException){
                    call.respond(BadRequest)
                    return@post
                }
                val deleteWall= deleteWall(username,request)
                if(deleteWall)
                    call.respond(OK,SimpleResponse(true,"wall deleted"))
                else
                    call.respond(OK,SimpleResponse(false,"try delete again"))
            }
        }
    }

}