package com.upar.routes

import com.upar.data.collections.Trading
import com.upar.data.database.*
import com.upar.data.requests.OneRequest
import com.upar.data.responses.SimpleResponse
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.tradingRoute(){
    route("/savetrading"){
        authenticate {
            post {
                val username=call.principal<UserIdPrincipal>()!!.name
                val request= try {
                    call.receive<Trading>()
                }catch (e:ContentTransformationException){
                    call.respond(OK, SimpleResponse(false,"cant save the trading"))
                    return@post
                }
                val saveTrading= saveTrading(username,request)
                if(saveTrading)
                    call.respond(OK,SimpleResponse(true,"trading saved"))
                else
                    call.respond(OK,SimpleResponse(false,"trading not saved"))
            }
        }
    }
    route("/deletetrading"){
        authenticate {
            post{
                val username=call.principal<UserIdPrincipal>()!!.name
                val request=try{
                    call.receive<Trading>()
                }catch(e:ContentTransformationException){
                    call.respond(OK, SimpleResponse(false,"cant delete the trading"))
                    return@post
                }
                val deleteTrading = deleteTrading(username,request)
                if(deleteTrading)
                    call.respond(OK,SimpleResponse(true,"trading deleted"))
                else
                    call.respond(OK,SimpleResponse(false,"try to delete again"))
            }
        }
    }
    route("/getalltrading"){
        get{
            val tradings = getAllTrading()
            call.respond(OK,tradings)
        }
    }
    route("/getallusertrading"){
        post {
            val request= try {
                call.receive<OneRequest>()
            }catch (e:ContentTransformationException){
                call.respond(BadRequest)
                return@post
            }
            val tradings= getAllUserTrading(request.property)
            call.respond(OK,tradings)
        }
    }
    route("/gettrading"){
        post {
            val request= try {
                call.receive<Trading>()
            }catch (e:ContentTransformationException){
                call.respond(BadRequest)
                return@post
            }
            val trading= getTrading(request) ?: return@post
            call.respond(OK,trading)
        }
    }
    route("/getbuyingsearch"){
        post {
            val request= try {
                call.receive<OneRequest>()
            }catch (e:ContentTransformationException){
                call.respond(BadRequest)
                return@post
            }
            val tradings= getBuyingSearch(request.property)
            call.respond(OK,tradings)
        }
    }
    route("/getsellingsearch"){
        post {
            val request= try {
                call.receive<OneRequest>()
            }catch (e:ContentTransformationException){
                call.respond(BadRequest)
                return@post
            }
            val tradings= getSellingSearch(request.property)
            call.respond(OK,tradings)
        }
    }
    route("/gettitlesearch"){
        post {
            val request= try {
                call.receive<OneRequest>()
            }catch (e:ContentTransformationException){
                call.respond(BadRequest)
                return@post
            }
            val tradings= getListTradingTitle(request.property)
            call.respond(OK,tradings)
        }
    }
}












