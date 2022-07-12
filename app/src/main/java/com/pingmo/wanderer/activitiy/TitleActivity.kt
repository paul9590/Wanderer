package com.pingmo.wanderer.activitiy

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.pingmo.wanderer.MyDBHelper
import com.pingmo.wanderer.R
import com.pingmo.wanderer.Wanderer
import com.pingmo.wanderer.databinding.ActivityTitleBinding
import org.json.JSONException
import org.json.JSONObject

class TitleActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var mBinding: ActivityTitleBinding
    private lateinit var titleHandler : Handler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityTitleBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val myDb = MyDBHelper(this)

        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        mBinding.btnSignIn.setOnClickListener {
            signIn()
        }

        titleHandler = Handler { msg: Message ->
            // 아이디 확인 되면 메인 액티비티로
            try {
                val receive = JSONObject(msg.obj.toString())
                when(msg.what) {
                    101 -> {
                        val isUser = receive.getString("isUser").toInt()
                        if (isUser == 1) {
                            addUser(myDb, receive)
                            finish()
                        } else {
                            //닉네임 받기 창
                            val dial = Dialog(this)
                            dial.requestWindowFeature(Window.FEATURE_NO_TITLE)
                            dial.setContentView(R.layout.dial_register)
                            dial.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            showRegisterDial(dial)
                        }
                    }

                    103 -> {
                        Toast.makeText(this, "이미 사용중인 이름입니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            true
        }
    }

    private fun showRegisterDial(dial : Dialog) {
        dial.show()
        val editRegister = dial.findViewById<EditText>(R.id.editRegister)
        val btnYes = dial.findViewById<Button>(R.id.btnYes)
        var lastClickTime : Long = 0

        btnYes.setOnClickListener {
            // 욕설 필터링, 입력 값 검증 추가 해야함
            if(SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                try {
                    val sendData = JSONObject()
                    sendData.put("what", 103)
                    sendData.put("name", editRegister.text)
                    Wanderer.send(sendData.toString())
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }else {
                Toast.makeText(this, "서버와 통신 중입니다. 잠시만 기다려 주세요..", Toast.LENGTH_SHORT).show()
            }
            lastClickTime = SystemClock.elapsedRealtime()
        }
    }

    private fun addUser(myDb : MyDBHelper, receive : JSONObject) {
        val name = receive.getString("name")
        val money = receive.getInt("money")
        val sqlDB = myDb.writableDatabase
        sqlDB.execSQL("INSERT INTO userTB (name, money) VALUES ('${name}', '${money}');")
        sqlDB.close()
    }

    override fun onStart() {
        super.onStart()
        // 초기 핸들러 설정
        Thread {
            while (true) {
                try {
                    Thread.sleep(1)
                    if (Wanderer.isConnected()) {
                        Wanderer.setHandler(titleHandler)
                        break
                    }
                } catch (ignored: Exception) {
                }
            }
        }.start()
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    // 서버측에 유저 정보 확인 해야댐
                    val user = auth.currentUser
                    try {
                        val sendData = JSONObject()
                        sendData.put("what", 101)
                        sendData.put("name", user!!.uid)
                        Wanderer.send(sendData.toString())
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    companion object {
        private const val TAG = "TitleActivity"
        private const val RC_SIGN_IN = 9001
    }
}