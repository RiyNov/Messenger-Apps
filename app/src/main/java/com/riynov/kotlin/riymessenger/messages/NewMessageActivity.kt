package com.riynov.kotlin.riymessenger.messages

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.riynov.kotlin.riymessenger.R
import com.riynov.kotlin.riymessenger.messages.NewMessageActivity.companion.USER_KEY
import com.riynov.kotlin.riymessenger.models.Users
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.new_message_activity.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*


class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_message_activity)

        supportActionBar?.title = "Select User"

//        val adapter = GroupAdapter<ViewHolder>()
//
//        adapter.add(userItem())
//        adapter.add(userItem())
//        adapter.add(userItem())
//
//        rv_newMessage.adapter = adapter

        fetchUsers()
    }

    object companion {
        val USER_KEY = "USER_KEY"
    }

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                p0.children.forEach{
                    Log.d("NewMessage",it.toString())
                    val user = it.getValue(Users::class.java)
                    if (user != null) {
                        adapter.add(userItem(user))
                    }
                }
                
                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as userItem
                    val intent = Intent(view.context, ChatActivity::class.java)
//                    intent.putExtra(USER_KEY,userItem.users.username)
                    intent.putExtra(USER_KEY,userItem.users)
                    startActivity(intent)

                    finish()
                }

                rv_newMessage.adapter = adapter

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

}



class userItem(val users: Users): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.tv_new_message_username.text = users.username
        Picasso.get().load(users.profileImageURL).into(viewHolder.itemView.iv_new_message_photo)
    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
}

//class CustomAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder> {
//    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//}