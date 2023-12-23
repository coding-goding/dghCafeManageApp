package kr.co.bsd.dghcafemanage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.co.bsd.dghcafemanage.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)
        val database =
            Firebase.database("https://dghcafeadmin-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val usersRef = database.getReference("users")

        val intentA = Intent(this, MainActivity::class.java)
        val intentB = Intent(this, AdminChoiceActivity::class.java)

        binding.button.setOnClickListener {
            val id = binding.nameText.text.toString()
            val password = binding.passwordText.text.toString()
            if (id.isNotEmpty() && password.isNotEmpty()) {
                usersRef.child(id).get().addOnSuccessListener {
                    if (it.exists()) {
                        it.getValue(User::class.java)?.let { user ->
                            if (user.password == password) {
                                Toast.makeText(
                                    baseContext,
                                    "환영합니다, " + user.name + "님.",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                if(!user.admin) {
                                    val intent = Intent(this, MainActivity::class.java)
                                    intent.putExtra("id", user.name)
                                    intent.putExtra("password", user.password)
                                    startActivity(intent)
                                    finish()
                                }
                                else {
                                    val intent = Intent(this, AdminChoiceActivity::class.java)
                                    intent.putExtra("id", user.name)
                                    intent.putExtra("password", user.password)
                                    startActivity(intent)
                                    finish()
                                }
                            } else {
                                Toast.makeText(
                                    baseContext,
                                    "입력된 정보가 계정 정보와 일치하지 않습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    else {
                        var num = 0
                        var name = ""
                        var password = ""
                        val query : Query = usersRef.orderByChild("id").equalTo(id)
                        query.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if(dataSnapshot.exists()) {

                                    for (snapshot in dataSnapshot.children) {
                                        var pw = snapshot.child("password").value.toString()
                                        var nm = snapshot.child("name").value.toString()
                                        var ad = snapshot.child("admin").value.toString().toBoolean()
                                        name = binding.nameText.text.toString()
                                        password = binding.passwordText.text.toString()
                                        if (pw == password) {
                                            Toast.makeText(
                                                baseContext,
                                                "환영합니다, " + nm + "님.",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                            if(!ad) {
                                                num = 1
                                                name = nm
                                                password = pw
                                            }
                                            else {
                                                num = 2
                                                name = nm
                                                password = pw
                                            }
                                            if(num == 1) {
                                                intentA.putExtra("id", name)
                                                intentA.putExtra("password", password)
                                                startActivity(intentA)
                                                finish()
                                            }
                                            else if(num == 2) {
                                                intentB.putExtra("id", name)
                                                intentB.putExtra("password", password)
                                                startActivity(intentB)
                                                finish()
                                            }
                                        } else {
                                            Toast.makeText(
                                                baseContext,
                                                "입력된 정보가 계정 정보와 일치하지 않습니다.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            num = 0
                                        }
                                    }
                                }
                                else {
                                    Toast.makeText(
                                        baseContext,
                                        "존재하지 않는 사용자입니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            override fun onCancelled(databaseError: DatabaseError) {
                            }
                        })
                    }
                }
            }
            else {
                Toast.makeText(baseContext, "모든 정보가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
