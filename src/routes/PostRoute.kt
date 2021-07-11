package com.upar.routes

import com.upar.data.*
import com.upar.data.collections.Post
import com.upar.data.requests.AddCommentRequest
import com.upar.data.requests.IDRequest
import com.upar.data.responses.SimpleResponse
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.postRoute(){
    route("/getPosts"){
        authenticate {
            get {
                val username= call.principal<UserIdPrincipal>()!!.name
                val posts= getPostForUser(username)
                call.respond(OK,posts)
            }
        }
    }
    route("/getAllPosts"){
        get{
            val posts = getAllPost()
            call.respond(OK,posts)
        }
    }
    route("/addPost"){
        authenticate {
            post {
                val post= try {
                    call.receive<Post>()
                }catch (e: ContentTransformationException){
                    call.respond(BadRequest)
                    return@post
                }
                if(savePost(post)){
                    call.respond(OK)
                }else{
                    call.respond(Conflict)
                }
            }
        }
    }
    route("/addCommentToPost"){
        authenticate {
            post {
                val request= try {
                    call.receive<AddCommentRequest>()
                }catch (e:ContentTransformationException){
                    call.respond(BadRequest)
                    return@post
                }
                if(!checkIfUserExists(request.comments)){
                    call.respond(
                        OK,
                        SimpleResponse(false,"No user with this username exists")
                    )
                    return@post
                }
                if(isCommentOfPost(request.postID,request.comments)){
                    call.respond(
                        OK,
                        SimpleResponse(false,"this username is already comment here")
                    )
                    return@post
                }
                if(addCommentToPost(request.postID, request.comments)){
                    call.respond(
                        OK,
                        SimpleResponse(true,"${request.comments} can now see this post")
                    )
                }else{
                    call.respond(Conflict)
                }
            }
        }
    }
    route("/deletePost"){
        authenticate {
            post {
                val username= call.principal<UserIdPrincipal>()!!.name
                val request= try {
                    call.receive<IDRequest>()
                }catch (e:ContentTransformationException){
                    call.respond(BadRequest)
                    return@post
                }
                if(deletePostForUser(username,request._id)){
                    call.respond(OK)
                }else{
                    call.respond(Conflict)
                }
            }
        }
    }
}