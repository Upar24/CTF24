package com.upar.data.database

import com.upar.data.collections.User
import com.upar.data.requests.UpdateUserRequest
import org.litote.kmongo.`in`
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo

private val client = KMongo.createClient().coroutine
private val database= client.getDatabase("CTF060621")
private val users= database.getCollection<User>()

suspend fun registerUser(user: User): Boolean{
    val registerUser= User(
        username=user.username,
        password = user.password,
        name="",
        clubName = "",
        ign="",
        bio="",
        created = System.currentTimeMillis()
    )
    return users.insertOne(registerUser).wasAcknowledged()
}
suspend fun checkIfUserExists(username: String):Boolean{
    return users.findOne(User::username eq username) != null
}
suspend fun checkPasswordForUsername(username: String,passwordToCheck: String): Boolean{
    val actualPassword= users.findOne(User::username eq username)?.password ?: return false
    return actualPassword == passwordToCheck
}
suspend fun getUser(username: String): User? {
    return users.findOne(User::username eq username)
}
suspend fun updateUser(username: String,updateUserReq:UpdateUserRequest):Boolean{
    val user = users.findOne(User::username eq username) ?: return false
    val userUpdate= User(
        user.username,
        user.password,
        updateUserReq.name,
        updateUserReq.clubName,
        updateUserReq.ign,
        updateUserReq.bio,
        user.created,
        user._id
    )
    return users.updateOneById(user._id,userUpdate).wasAcknowledged()
}
//suspend fun updatePassword(username: String,oneRequest: String):Boolean{
//    val user = users.findOne(User::username eq username) ?: return false
//    return users.updateOneById(user._id, setValue(User::password,oneRequest)).wasAcknowledged()
//}
suspend fun getListUser(listUsername:List<String>):List<User> {
    return users.find(User::username `in` listUsername).toList()
}
suspend fun getListUserClub(oneRequest:String):List<User>{
    return users.find(User::clubName eq oneRequest).toList()
}
suspend fun getListUserIGN(oneRequest: String):List<User>{
    return users.find(User::ign eq oneRequest).toList()
}










