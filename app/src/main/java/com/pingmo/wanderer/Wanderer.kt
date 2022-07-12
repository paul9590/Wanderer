package com.pingmo.wanderer

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.database.sqlite.SQLiteDatabase
import android.os.Handler
import android.os.IBinder
import android.util.Log
object Wanderer : Application(){

    lateinit var service: ClientService

    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, serv: IBinder) {
            val binder = serv as ClientService.MyBinder
            service = binder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.i("SocketManager", "onServiceDisconnected()")
        }
    }

    fun send(s: String) {
        service.send(s)
    }

    fun disconnect() {
        service.disconnect()
    }

    fun setHandler(handler: Handler) {
        service.setHandler(handler)
    }

    fun isConnected() = service.isConnected()

    fun isUser(context: Context): Boolean {
        val myDb = MyDBHelper(context)
        val sqlDb: SQLiteDatabase
        sqlDb = myDb.getReadableDatabase()
        val cur = sqlDb.rawQuery("SELECT * FROM userTB", null)
        val check = cur.count > 0
        cur.close()
        sqlDb.close()
        return check
    }

    fun getUser(context: Context): User {
        val myDb = MyDBHelper(context)
        val sqlDb: SQLiteDatabase
        sqlDb = myDb.getReadableDatabase()
        var curUser = User()
        val cur = sqlDb.rawQuery("SELECT * FROM userTB", null)
        if (cur.count > 0) {
            cur.moveToFirst()
            val arr = intArrayOf(
                cur.getColumnIndex("name"),
                cur.getColumnIndex("money")
            )
            curUser = User(cur.getString(arr[0]), cur.getInt(arr[1]))
        }
        cur.close()
        sqlDb.close()
        return curUser
    }

    override fun onCreate() {
        super.onCreate()
        val context = applicationContext
        val intent = Intent(context, ClientService::class.java)
        context.bindService(intent, connection, BIND_AUTO_CREATE)
    }
}

