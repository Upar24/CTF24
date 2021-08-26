package com.upar.data.database

import com.upar.data.collections.Trading
import com.upar.data.collections.User
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

private val client = KMongo.createClient().coroutine
private val database= client.getDatabase("CTF060621")
private val users= database.getCollection<User>()
private val tradings= database.getCollection<Trading>()

suspend fun saveTrading(username:String,trading: Trading):Boolean{
    val user=users.findOne(User::username eq username)
    val date = System.currentTimeMillis()
    val trading1= Trading(
        username,
        user?.name,
        user?.ign,
        trading.title,
        trading.desc,
        trading.itemBuying.toString().toLowerCase(),
        trading.amountBuying,
        trading.itemSelling.toString().toLowerCase(),
        trading.amountSelling,
        date,
        trading._id
    )
    val traidngExists= tradings.findOneById(trading._id) != null
    return if(traidngExists){
        tradings.updateOneById(trading._id,trading1).wasAcknowledged()
    }else{
        tradings.insertOne(trading1).wasAcknowledged()
    }
}
suspend fun deleteTrading(username: String,trading: Trading):Boolean{
    if(username==trading.username){
        return tradings.deleteOneById(trading._id).wasAcknowledged()
    }else{
        return false
    }
}
suspend fun getAllTrading():List<Trading>{
    return tradings.find().sort(descending(Trading::date)).toList()
}
suspend fun getAllUserTrading(username: String):List<Trading>{
    return tradings.find(Trading::username eq username).sort(descending(Trading::date)).toList()
}
suspend fun getTrading(trading: Trading): Trading?{
    return tradings.findOne(Trading::_id eq trading._id)
}

suspend fun getBuyingSearch(query:String):List<Trading>{
    val search = query.toLowerCase()
    return tradings.find(Trading::itemBuying eq search).sort(descending(Trading::date)).toList()
}
suspend fun getSellingSearch(query: String):List<Trading>{
    val search = query.toLowerCase()
    return tradings.find(Trading::itemSelling eq search).sort(descending(Trading::date)).toList()
}
suspend fun getListTradingTitle(oneRequest:String):List<Trading>{
    val request= oneRequest.toLowerCase()
    return tradings.find(Trading::title eq request).sort(descending(Trading::date)).toList()
}
//suspend fun getBuyingSearch(query:String):List<Trading>{
//   tradings.ensureIndex(Trading::itemBuying.textIndex())
//    val list= tradings.find(text(query, TextSearchOptions().caseSensitive(false))).sort(ascending(Trading::date)).toList()
//    tradings.dropIndex( "itemBuying_text")
//    return list
//}
//
//suspend fun getSellingSearch(query:String):List<Trading>{
//    tradings.ensureIndex(Trading::itemSelling.textIndex())
//    val list= tradings.find(text(query,TextSearchOptions().caseSensitive(false))).sort(ascending(Trading::date)).toList()
//    tradings.dropIndex("itemSelling_text")
//    return list
//}
//suspend fun getAllSearch(query:String):List<Trading>{
//    tradings.ensureIndex(Trading::desc.textIndex())
//    val list= tradings.find(text(query,TextSearchOptions().caseSensitive(false))).sort(ascending(Trading::date)).toList()
//    tradings.dropIndex("desc_text")
//    return list
//}
















