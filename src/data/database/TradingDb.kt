package com.upar.data.database

import com.upar.data.collections.*
import com.upar.data.requests.UpdateUserRequest
import com.upar.util.ListString.hotsale
import com.upar.util.ListString.lbhneeded
import com.upar.util.ListString.lbhpost
import com.upar.util.ListString.random
import com.upar.util.ListString.upar
import com.upar.util.checkHashForPassword
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

private val client = KMongo.createClient().coroutine
private val database= client.getDatabase("CTF060621")
private val users= database.getCollection<User>()
private val tradings= database.getCollection<Trading>()
private val chats= database.getCollection<Chat>()
private val walls= database.getCollection<Wall>()
private val parties= database.getCollection<Party>()
private val droppeds= database.getCollection<Dropped>()
private val todays= database.getCollection<Today>()
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
    return checkHashForPassword(passwordToCheck,actualPassword)
}
suspend fun getUser(username: String): User? {
    return users.findOne(User::username eq username)
}
suspend fun updateUser(username: String,updateUserReq: UpdateUserRequest):Boolean{
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
suspend fun getListUser(listUsername:List<String>):List<User> {
    return users.find(User::username `in` listUsername).toList()
}
suspend fun getListUserClub(oneRequest:String):List<User>{
    return users.find(User::clubName eq oneRequest).toList()
}
suspend fun getListUserIGN(oneRequest: String):List<User>{
    return users.find(User::ign eq oneRequest).toList()
}
suspend fun saveTrading(username:String,trading: Trading):Boolean{
    val user=users.findOne(User::username eq username)
    val date = System.currentTimeMillis()
    val trading1= Trading(
        username,
        user?.name,
        user?.ign,
        trading.title,
        trading.desc,
        trading.itemBuying.toString().lowercase(),
        trading.amountBuying,
        trading.itemSelling.toString().lowercase(),
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
    return tradings.find().sort(descending(Trading::date)).limit(1000).toList()
}
suspend fun getAllUserTrading(username: String):List<Trading>{
    return tradings.find(Trading::username eq username).sort(descending(Trading::date)).limit(1000).toList()
}
suspend fun getTrading(trading: Trading): Trading?{
    return tradings.findOne(Trading::_id eq trading._id)
}

suspend fun getBuyingSearch(query:String):List<Trading>{
    val search = query.lowercase()
    return tradings.find(Trading::itemBuying eq search).sort(descending(Trading::date)).limit(1000).toList()
}
suspend fun getSellingSearch(query: String):List<Trading>{
    val search = query.lowercase()
    return tradings.find(Trading::itemSelling eq search).sort(descending(Trading::date)).limit(1000).toList()
}
suspend fun getListTradingTitle(oneRequest:String):List<Trading>{
    val request= oneRequest.lowercase()
    return tradings.find(Trading::title eq request).sort(descending(Trading::date)).limit(1000).toList()
}
suspend fun saveChat(username:String,chat: Chat):Boolean{
    val listType = listOf(lbhpost,lbhneeded,hotsale,random)
    val type=if(chat.type in listType) chat.type else random
    val user=users.findOne(User::username eq username)
    val date = System.currentTimeMillis()
    val chat1= Chat(
        username,
        user?.name,
        user?.clubName,
        chat.chat,
        date,
        type
    )
    val chatExists= chats.findOneById(chat._id) != null
    return if(chatExists){
        chats.updateOneById(chat._id,chat1).wasAcknowledged()
    }else{
        chats.insertOne(chat1).wasAcknowledged()
    }
}
suspend fun getAllChat():List<Chat>{
    return chats.find().sort(descending(Chat::date)).limit(1250).toList()
}
suspend fun saveWall(username: String,wall: Wall):Boolean{
    val user=users.findOne(User::username eq username)
    val wall1= Wall(
        username,
        user?.ign,
        user?.clubName,
        wall.wallOwner,
        wall.chat,
        System.currentTimeMillis()
    )
    val wallExist= walls.findOneById(wall._id) != null
    return if(wallExist){
        walls.updateOneById(wall._id,wall1).wasAcknowledged()
    }else{
        walls.insertOne(wall1).wasAcknowledged()
    }
}
suspend fun getAllWall(wallOwner:String):List<Wall>{
    return walls.find(Wall::wallOwner eq wallOwner).sort(descending(Wall::date)).toList()
}
suspend fun deleteWall(username: String,wall: Wall):Boolean{
    return if(username==wall.wallOwner){
        walls.deleteOneById(wall._id).wasAcknowledged()
    }else{
        false
    }
}
suspend fun isPartyExists(idParty:String):Boolean{
    return parties.findOneById(idParty) != null
}
suspend fun isDroppedExists(idDropped:String):Boolean{
    return droppeds.findOneById(idDropped) != null
}
suspend fun isTodayExists(idToday:String):Boolean{
    return todays.findOneById(idToday) != null
}
suspend fun saveParty(username:String,party: Party):Boolean{
    return if(username== upar) {
        val partyExist = isPartyExists(party._id)
        if (partyExist) {
            parties.updateOneById(party._id, party).wasAcknowledged()
        } else {
            parties.insertOne(party).wasAcknowledged()
        }
    }else{
        false
    }
}
suspend fun getParty():List<Party>{
    return parties.find().sort(ascending(Party::no)).toList()
}
suspend fun getDropped():List<Dropped>{
    return droppeds.find().sort(descending(Dropped::day)).toList()
}
suspend fun getToday(): Today?{
    return todays.findOne(Today::_id eq "1")
}
suspend fun saveDropped(username: String,dropped: Dropped):Boolean{
    val dropExist= isDroppedExists(dropped._id)
    return if(username==upar) {
        if (dropExist) {
            droppeds.updateOneById(dropped._id, dropped).wasAcknowledged()
        } else {
            droppeds.insertOne(dropped).wasAcknowledged()
        }
    }else{
        false
    }
}
suspend fun deleteDropped(username:String,dropped: Dropped):Boolean{
    if(username==upar){
        return droppeds.deleteOneById(dropped._id).wasAcknowledged()
    }else{
        return false
    }
}
suspend fun saveToday(username: String,today: Today):Boolean{
    return if(username==upar){
        val todayExist= isTodayExists(today._id)
        if(todayExist){
            todays.updateOneById(today._id,today).wasAcknowledged()
        }else{
            todays.insertOne(today).wasAcknowledged()
        }
    }else{
        false
    }
}
suspend fun isUserCheck(username: String,party: Party):Boolean{
    val party1= parties.findOne(Party::_id eq party._id)  ?: return false
    return username in party1.check
}
suspend fun isUserNope(username: String,party: Party):Boolean{
    val party1= parties.findOne(Party::_id eq party._id)  ?: return false
    return username in party1.nope
}
suspend fun isUserDrop(username: String,party: Party):Boolean{
    val party1= parties.findOne(Party::_id eq party._id)  ?: return false
    return username in party1.drop
}

suspend fun ToggleCheck(username:String,party: Party):Boolean{
    val isCheck= isUserCheck(username,party)
    val isNope=isUserNope(username,party)
    val isDrop=isUserDrop(username,party)
    return if(isCheck && isNope != true && isDrop != true){
        val newCheck= party.check - username
        parties.updateOneById(party._id, setValue(Party::check,newCheck)).wasAcknowledged()
    }else{
        if(isNope){
            val newNope=party.nope - username
            parties.updateOneById(party._id, setValue(Party::nope,newNope)).wasAcknowledged()
            val newCheck=party.check + username
            parties.updateOneById(party._id, setValue(Party::check,newCheck)).wasAcknowledged()
        }else if(isDrop){
            val newDrop= party.drop - username
            parties.updateOneById(party._id, setValue(Party::drop,newDrop)).wasAcknowledged()
            val newCheck=party.check + username
            parties.updateOneById(party._id, setValue(Party::check,newCheck)).wasAcknowledged()
        }else{
            val newCheck=party.check + username
            parties.updateOneById(party._id, setValue(Party::check,newCheck)).wasAcknowledged()
        }
    }
}
suspend fun toggleNope(username:String,party: Party):Boolean{
    val isNope= isUserNope(username,party)
    val isCheck=(isUserCheck(username,party))
    val isDrop=(isUserDrop(username,party))
    return if(isNope && isCheck != true && isDrop != true){
        val newNope= party.nope - username
        parties.updateOneById(party._id, setValue(Party::nope,newNope)).wasAcknowledged()
    }else{
        if(isCheck){
            val newCheck= party.check - username
            parties.updateOneById(party._id, setValue(Party::check,newCheck)).wasAcknowledged()
            val newNope=party.nope + username
            parties.updateOneById(party._id, setValue(Party::nope,newNope)).wasAcknowledged()
        }else if (isDrop){
            val newDrop= party.drop - username
            parties.updateOneById(party._id, setValue(Party::drop,newDrop)).wasAcknowledged()
            val newNope=party.nope + username
            parties.updateOneById(party._id, setValue(Party::nope,newNope)).wasAcknowledged()
        }else{
            val newNope=party.nope + username
            parties.updateOneById(party._id, setValue(Party::nope,newNope)).wasAcknowledged()
        }
    }
}
suspend fun toggleDrop(username:String,party: Party):Boolean{
    val isDrop= isUserDrop(username,party)
    val isNope=(isUserNope(username,party))
    val isCheck= (isUserCheck(username,party))
    return if(isDrop && isNope != true && isCheck != true){
        val newDrop= party.drop - username
        parties.updateOneById(party._id, setValue(Party::drop,newDrop)).wasAcknowledged()
    }else{
        if(isNope){
            val newNope= party.nope - username
            parties.updateOneById(party._id, setValue(Party::nope,newNope)).wasAcknowledged()
            val newDrop=party.drop + username
            parties.updateOneById(party._id, setValue(Party::drop,newDrop)).wasAcknowledged()
        }else if(isCheck){
            val newCheck= party.check - username
            parties.updateOneById(party._id, setValue(Party::check,newCheck)).wasAcknowledged()
            val newDrop=party.drop + username
            parties.updateOneById(party._id, setValue(Party::drop,newDrop)).wasAcknowledged()
        }else{
            val newDrop=party.drop + username
            parties.updateOneById(party._id, setValue(Party::drop,newDrop)).wasAcknowledged()
        }
    }
}
suspend fun deleteTrading(username:String):Boolean{
    val timestart = System.currentTimeMillis() - 345600000
    if(username==upar){
        return tradings.deleteMany(Trading::date lte timestart).wasAcknowledged()
    }else return false
}
suspend fun deleteChat(username: String):Boolean{
    val timestart = System.currentTimeMillis() - 3600000
    if(username==upar){
        return chats.deleteMany(Chat::date lte timestart).wasAcknowledged()
    }else return false
}
















