package com.upar.routes

import com.upar.data.collections.User
import com.upar.data.database.*
import com.upar.data.requests.AccountRequest
import com.upar.data.requests.ListStringRequest
import com.upar.data.requests.OneRequest
import com.upar.data.requests.UpdateUserRequest
import com.upar.data.responses.SimpleResponse
import com.upar.util.getHashWithSalt
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.userRoute(){
    route("/register"){
        post {
            val request= try{
                call.receive<AccountRequest>()
            }catch (e: ContentTransformationException){
                call.respond(BadRequest)
                return@post
            }
            if(request.username.length <3 || request.password.length <3){
                call.respond(OK,SimpleResponse(false,"must be at least 3 characters"))
                return@post
            }
            if(request.username.length >24 || request.password.length >24){
                call.respond(OK,SimpleResponse(false,"characters too long"))
                return@post
            }
            val userExists= checkIfUserExists(request.username)
            if(!userExists){
                if(registerUser(User(request.username, getHashWithSalt(request.password)))){
                    call.respond(OK,SimpleResponse(true,"Successfully created account"))
                }else{
                    call.respond(OK,SimpleResponse(false,"An unknown error occured"))
                }
            }else{
                call.respond(OK,SimpleResponse(false,"A user with that username already exists"))
            }
        }
    }
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
    route("/getuser"){
        post {
            val request= try {
                call.receive<OneRequest>()
            }catch (e:ContentTransformationException){
                call.respond(OK,SimpleResponse(false,"cant get user data"))
                return@post
            }
            val user= getUser(request.property) ?: return@post
            call.respond(OK,user)
        }
    }
    route("/getlistuser"){
        post {
            val request=try{
                call.receive<ListStringRequest>()
            }catch (e:ContentTransformationException) {
                call.respond(OK, SimpleResponse(false, "cant get the user data"))
                return@post
            }
            val userList= getListUser(request.listString)
            call.respond(OK,userList)
        }
    }
    route("/updateuser"){
        authenticate {
            post {
                val username = call.principal<UserIdPrincipal>()!!.name
                val request=try{
                    call.receive<UpdateUserRequest>()
                }catch (e:ContentTransformationException){
                    call.respond(OK,SimpleResponse(false,"cant send the update user data"))
                    return@post
                }
                val update= updateUser(username,request)
                if(update)
                    call.respond(OK,SimpleResponse(true,"successfully updated"))
                else
                    call.respond(OK,SimpleResponse(false,"can not update the profile"))
            }
        }
    }
//    route("/updatepassword"){
//        authenticate {
//            post {
//                val username= call.principal<UserIdPrincipal>()!!.name
//                val request=try {
//                    call.receive<OneRequest>()
//                }catch (e:ContentTransformationException){
//                    call.respond(OK,SimpleResponse(false,"cant send the update user data"))
//                    return@post
//                }
//                val update= updatePassword(username,request.property)
//                if(update)call.respond(OK) else call.respond(BadRequest)
//            }
//        }
//    }
    route("/getlistuserclub"){
        post {
            val request= try {
                call.receive<OneRequest>()
            }catch (e:ContentTransformationException){
                call.respond(OK)
                return@post
            }
            val result = getListUserClub(request.property)
            call.respond(OK,result)
        }
    }
    route("/getlistuserign"){
        post {
            val request= try {
                call.receive<OneRequest>()
            }catch (e:ContentTransformationException){
                call.respond(OK)
                return@post
            }
            val result = getListUserIGN(request.property)
            call.respond(OK,result)
        }
    }
}