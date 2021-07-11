package com.upar.data

import com.upar.data.collections.Post
import com.upar.data.collections.User
import org.litote.kmongo.contains
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.setValue

private val client = KMongo.createClient().coroutine
private val database= client.getDatabase("CTF060621")
private val users= database.getCollection<User>()
private val posts= database.getCollection<Post>()

suspend fun registerUser(user: User): Boolean{
    return users.insertOne(user).wasAcknowledged()
}
suspend fun checkIfUserExists(username: String):Boolean{
    return users.findOne(User::username eq username) != null
}
suspend fun checkPasswordForUsername(username: String,passwordToCheck: String): Boolean{
    val actualPassword= users.findOne(User::username eq username)?.password ?: return false
    return actualPassword == passwordToCheck
}
suspend fun getPostForUser(username: String): List<Post>{
    return posts.find(Post::comment contains username).toList()
}
suspend fun getAllPost():List<Post>{
    return posts.find().toList()
}
suspend fun savePost(post:Post):Boolean{
    val postExists = posts.findOneById(post._id) != null
    return if(postExists){
        posts.updateOneById(post._id, post).wasAcknowledged()
    }else{
        posts.insertOne(post).wasAcknowledged()
    }
}
suspend fun isCommentOfPost(postID: String,comments:String):Boolean{
    val post = posts.findOneById(postID) ?: return false
    return comments in post.comment
}
suspend fun addCommentToPost(postID: String, comments: String):Boolean{
    val comment = posts.findOneById(postID)?.comment ?: return false
    return posts.updateOneById(postID, setValue(Post::comment, comment + comments)).wasAcknowledged()
}
suspend fun deletePostForUser(username: String, postID:String): Boolean{
    val post = posts.findOne(Post::_id eq postID, Post::comment contains username)
    post?.let { Post ->
        if(post.comment.size > 1){
            val newComment = post.comment - username
            val updateResult = posts.updateOne(Post::_id eq post._id, setValue(Post::comment, newComment))
            return updateResult.wasAcknowledged()
        }
        return posts.deleteOneById(post._id).wasAcknowledged()
    } ?: return false

}