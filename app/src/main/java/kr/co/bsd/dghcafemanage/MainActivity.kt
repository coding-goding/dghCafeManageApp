package kr.co.bsd.dghcafemanage

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.co.bsd.dghcafemanage.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {

        FirebaseApp.initializeApp(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)
        val database =
            Firebase.database("https://dghcafeadmin-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val usersRef = database.getReference("users")
        val menuRef = database.getReference("menu")

        var now : String = ""

        //insert date
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val current = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            now =  current.format(formatter)
        }
        else {
            var date = Date()
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            now = formatter.format(date)
        }
        binding.dateText.text = now


        //insert menu
        var wholeMenu : String = ""
        menuRef.child(now).get().addOnSuccessListener {
            if (it.exists()) {
                it.getValue(Menu::class.java)?.let { menu ->
                    var count : Int = 0
                    var countMainA : Int = 0
                    var countMainB : Int = 1
                    var a : String = menu.a
                    for (i : Int in 0..a.length-1) {
                        if(a[i] == '/') {
                            if(menu.mainMenu[countMainA].toString() == countMainB.toString()) {
                                wholeMenu += '<'
                                wholeMenu += a.substring(count until i)
                                wholeMenu += '>'
                                countMainA += 2
                            }
                            else {
                                wholeMenu += a.substring(count until i)
                            }
                            countMainB++
                            wholeMenu += '\n'
                            count = i+1
                        }
                    }
                    wholeMenu += a.substring(count)
                    binding.menuText.text = wholeMenu
                }
            }
            else {
                binding.menuText.text = "식단 정보가 없습니다."
            }
        }

        //this is temporary setting, update later
        var userName : String = "박준성"


        //insert user
        usersRef.child(userName).get().addOnSuccessListener {
            if (it.exists()) {
                it.getValue(User::class.java)?.let { user ->
                    var name : String = user.name
                    binding.teacherText.text = "교직원 " + name
                }
            }
            else {
                binding.teacherText.text = "교직원 정보가 없습니다."
            }
        }


        //set assign system
        usersRef.child(userName).get().addOnSuccessListener {
            if (it.exists()) {
                it.getValue(User::class.java)?.let { user ->
                    if(user.assigned) {
                        binding.assignTextB.visibility = View.VISIBLE
                    }
                    else {
                        binding.assignTextB.visibility = View.INVISIBLE
                    }
                }
            }
        }
        binding.assignButton.setOnClickListener {
            usersRef.child(userName).get().addOnSuccessListener {
                if (it.exists()) {
                    it.getValue(User::class.java)?.let { user ->
                        if(user.assigned) {
                            Toast.makeText(this.applicationContext,"이미 인증되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            val password : String = binding.passwordEditText.text.toString()
                            if(binding.passwordEditText.text.toString() == user.password) {
                                binding.assignTextB.visibility = View.VISIBLE
                                usersRef.child(userName).child("assigned").setValue(true)
                            }
                            else {
                                Toast.makeText(this.applicationContext,"비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }

    }

}