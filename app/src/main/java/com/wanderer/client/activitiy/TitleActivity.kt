package com.wanderer.client.activitiy

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.*
import android.util.Log
import android.view.Window
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.wanderer.client.R
import com.wanderer.client.User
import com.wanderer.client.Wanderer
import com.wanderer.client.databinding.ActivityTitleBinding
import org.json.JSONException
import org.json.JSONObject

class TitleActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var googleSignResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var mBinding: ActivityTitleBinding

    val wanderer: Wanderer = Wanderer.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityTitleBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initGoogle()

        mBinding.btnSignIn.setOnClickListener {
            signIn()
        }
    }

    private fun initGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    val map = HashMap<String, String>()
                    map["what"] = "103"
                    map["id"] = auth.currentUser!!.uid
                    wanderer.send(map)
                } else {
                    // Sign in fails
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    companion object {
        private const val TAG = "TitleActivity"
        private const val RC_SIGN_IN = 9001
    }

    private fun showRegisterDial() {
        val dial = Dialog(this)
        dial.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dial.setContentView(R.layout.dial_register)
        dial.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dial.show()

        val editRegister = dial.findViewById<EditText>(R.id.editRegister)
        val btnYes = dial.findViewById<ImageButton>(R.id.btnYes)
        var lastClickTime : Long = 0

        btnYes.setOnClickListener {
            // 욕설 필터링, 입력 값 검증 추가 해야함
            var validate = true
            val name = editRegister.text.toString()

            if(SystemClock.elapsedRealtime() - lastClickTime <= 1000) {
                validate = false
                Toast.makeText(this, "서버와 통신 중입니다. 잠시만 기다려 주세요..", Toast.LENGTH_SHORT).show()
            }

            if (name.length < 2) {
                validate = false
                Toast.makeText(this, "이름이 너무 짧습니다.", Toast.LENGTH_SHORT).show()
            }
            if (name.length > 15) {
                validate = false
                Toast.makeText(this, "이름이 너무 깁니다.", Toast.LENGTH_SHORT).show()
            }

            if(validate) {
                val map = HashMap<String, String>()
                map["what"] = "101"
                map["id"] = auth.currentUser!!.uid
                map["name"] = name
                wanderer.send(map)
                dial.dismiss()
            }
            lastClickTime = SystemClock.elapsedRealtime()
        }
    }

    override fun onStart() {
        super.onStart()
        wanderer.setHandler(TitleHandler())
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    inner class TitleHandler: Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            try {
                val receive = JSONObject(msg.obj.toString())
                when (msg.what) {
                    101 -> {
                        Toast.makeText(applicationContext, "이미 사용중인 이름입니다.", Toast.LENGTH_SHORT).show()
                        showRegisterDial()
                    }

                    103 -> {
                        val isUser = receive.getString("isUser")
                        if (isUser == "1") {
                            val name = receive.getString("name")
                            val money = receive.getInt("money")
                            val user = User(name, money)
                            if(wanderer.isUser(applicationContext)) {
                                wanderer.updateUser(applicationContext, user)
                            }else {
                                wanderer.addUser(applicationContext, user)
                            }
                            finish()
                        } else {
                            //닉네임 받기 창
                            showRegisterDial()
                        }
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }
}