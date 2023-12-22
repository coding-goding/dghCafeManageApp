package kr.co.bsd.dghcafemanage

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
        var setPoint : Int = 0
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
                    var a : String = menu.a
                    for (i : Int in 0..a.length-1) {
                        if(a[i] == '/') {
                            if(menu.mainMenu == count+1) {
                                wholeMenu += '<'
                                wholeMenu += a.substring(count until i)
                                wholeMenu += '>'
                            }
                            else {
                                wholeMenu += a.substring(count until i)
                            }
                            wholeMenu += '\n'
                            count = i+1
                        }
                    }
                    if(menu.mainMenu == count+1) {
                        wholeMenu += '<'
                        wholeMenu += a.substring(count)
                        wholeMenu += '>'
                    }
                    else {
                        wholeMenu += a.substring(count)
                    }

                    binding.menuText.text = wholeMenu
                }
            }
            else {
                binding.menuText.text = "식단 정보가 없습니다."
            }
        }

        //this is temporary setting, update later
        var userName : String = intent.getStringExtra("id").toString()

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
                    binding.assignTextD.text = "이번 달 식사 횟수 : " + user.point + "회"
                    if(user.assigned) {
                        binding.assignTextB.visibility = View.VISIBLE
                        binding.assignTextC.text = "인증 시각 : " + user.assignTime
                        binding.assignTextC.visibility = View.VISIBLE

                        binding.assignTextD.visibility = View.VISIBLE
                    }
                    else {
                        binding.assignTextB.visibility = View.INVISIBLE
                        binding.assignTextC.visibility = View.INVISIBLE
                        binding.assignTextD.visibility = View.VISIBLE
                    }
                }
            }
        }
        binding.LogoutButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("로그아웃")
                .setMessage("로그아웃 하시겠습니까?.")
                .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialog, id ->
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    })
            // 다이얼로그를 띄워주기
            builder.show()
        }
        binding.numSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.numberText.text = "식사 인원 : " + (progress+ 1) + "명"
                setPoint = progress
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })
        binding.assignButton.setOnClickListener {
            usersRef.child(userName).get().addOnSuccessListener {
                if (it.exists()) {
                    it.getValue(User::class.java)?.let { user ->
                        if(user.assigned) {
                            Toast.makeText(this.applicationContext,"이미 인증되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            val password : String = binding.passwordEditText.text.toString()
                            if(binding.passwordEditText.text.toString() == user.password) {  // password correct
                                binding.passwordEditText.setText("")
                                usersRef.child(userName).child("assigned").setValue(true)
                                val currentTime : Long = System.currentTimeMillis()
                                val dataFormat = SimpleDateFormat("HH:mm")
                                var t : String = dataFormat.format(currentTime)
                                val point : Int = user.point
                                usersRef.child(userName).child("assignTime").setValue(t)
                                usersRef.child(userName).child("point").setValue(point+setPoint+1)
                                binding.assignTextB.visibility = View.VISIBLE
                                binding.assignTextC.text = "인증 시각 : " + t
                                binding.assignTextD.text = "이번 달 식사 횟수 : " + (point+setPoint+1)  + "회"
                                binding.assignTextC.visibility = View.VISIBLE
                                binding.assignTextD.visibility = View.VISIBLE
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