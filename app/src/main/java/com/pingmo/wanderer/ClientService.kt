package com.pingmo.wanderer

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress
import java.nio.charset.Charset


var flagConnection = true
var isConnected = false

lateinit var writeHandler: Handler
lateinit var receiveHandler: Handler

lateinit var socket: Socket
lateinit var bin: BufferedInputStream
lateinit var bout: BufferedOutputStream

lateinit var st: ClientService.SocketThread
lateinit var rt: ClientService.ReadThread
lateinit var wt: ClientService.WriteThread

const val IP = "121.132.133.85"
const val PORT = 22459

class ClientService : Service() {

    private var mBinder: IBinder = MyBinder()

    inner class MyBinder : Binder() {
        // 서비스 객체를 리턴
        fun getService() : ClientService = this@ClientService
    }

    override fun onCreate() {
        super.onCreate()
        st = SocketThread()
        st.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return mBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    fun disconnect() {
        flagConnection = false
        isConnected = false
        writeHandler.looper.quit()
        try {
            bout.close()
            bin.close()
            socket.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun send(s: String) {
        val msg = Message()
        msg.obj = s
        writeHandler.sendMessage(msg)
    }

    fun setHandler(handler: Handler) {
        receiveHandler = handler
    }

    fun isConnected() : Boolean = isConnected

    class SocketThread : Thread() {
        override fun run() {
            while (flagConnection) {
                try {
                    if (!isConnected) {
                        writeHandler.looper.quit()
                        socket = Socket()
                        val remoteAddr: SocketAddress = InetSocketAddress(IP, PORT)
                        socket.connect(remoteAddr, 10000)
                        bout = BufferedOutputStream(socket.getOutputStream())
                        bin = BufferedInputStream(socket.getInputStream())
                        wt = WriteThread()
                        wt.start()
                        isConnected = true
                        rt = ReadThread()
                        rt.start()
                    } else {
                        SystemClock.sleep(10000)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    SystemClock.sleep(10000)
                }
            }
        }
    }


    class WriteThread : Thread() {
        override fun run() {
            Looper.prepare()
            writeHandler = Handler { msg: Message ->
                try {
                    bout.write((msg.obj as String).toByteArray())
                    bout.flush()
                } catch (e: Exception) {
                    e.printStackTrace()
                    isConnected = false
                    writeHandler.looper.quit()
                }
                true
            }
            Looper.loop()
        }
    }

    class ReadThread : Thread() {
        override fun run() {
            var buffer: ByteArray
            while (isConnected) {
                buffer = ByteArray(1024)
                try {
                    var message: String
                    val size: Int = bin.read(buffer)
                    if (size > 0) {
                        message = String(buffer, 0, size, Charset.forName("euc-kr"))
                        if (message != "") {
                            val msg = Message()
                            Log.e("핸들러는 : ", "" + receiveHandler)
                            Log.e("에러인가요", "" + message)
                            val receiveData = JSONObject(message)
                            msg.what = receiveData.getInt("what")
                            msg.obj = receiveData.toString()
                            receiveHandler.sendMessage(msg)
                        }
                    } else {
                        isConnected = false
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    isConnected = false
                }
            }
        }
    }

}
