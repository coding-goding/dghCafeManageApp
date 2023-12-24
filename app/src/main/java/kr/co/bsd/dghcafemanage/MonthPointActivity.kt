package kr.co.bsd.dghcafemanage

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.co.bsd.dghcafemanage.databinding.ActivityMonthPointBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class MonthPointActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMonthPointBinding

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonthPointBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("id").toString()
        val password = intent.getStringExtra("password").toString()

        var searching : Int = 0
        var allign : Int = 0 // 0 : name, 1 : id

        FirebaseApp.initializeApp(this)
        val database =
            Firebase.database("https://dghcafeadmin-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val usersRef = database.getReference("users")
        val sysRef = database.getReference("system")

        sysRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.lastResetText.text = "마지막 초기화일 : " + snapshot.child("lastReset").value.toString()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.resetButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)

            builder.setTitle("식사횟수 초기화")
                .setMessage("월 식사횟수를 초기화하시겠습니까?")
                .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialog, id ->
                        builder.setTitle("식사횟수 초기화")
                            .setMessage("정말 정리하시겠습니까?\n초기화된 데이터는 복구가 불가능합니다.")
                            .setPositiveButton("확인",
                                DialogInterface.OnClickListener { dialog, id ->
                                    usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            snapshot.children.forEach { nameData ->
                                                if (nameData.child("point").getValue(Int::class.java) != 0) {
                                                    nameData.ref.child("point").setValue(0)
                                                }
                                            }
                                        }
                                        override fun onCancelled(error: DatabaseError) {
                                        }
                                    })
                                    var now : String = ""
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
                                    sysRef.child("lastReset").setValue(now)
                                    binding.lastResetText.text = "마지막 초기화일 : " + now
                                    Toast.makeText(
                                        baseContext,
                                        "초기화되었습니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    finish()
                                })
                            .setNegativeButton("취소",
                                DialogInterface.OnClickListener { dialog, id ->
                                })
                        builder.show()

                    })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialog, id ->

                    })
            builder.show()
        }
        val t = listOf("id", "name")
        binding.allignButton.setOnClickListener {

            allign = 1 - allign
            if(allign == 0) {
                binding.allignButton.text = "정렬 전환(ID->이름)"
            }
            else {
                binding.allignButton.text = "정렬 전환(Id<-이름)"
            }
            if(searching == 1) {
                usersRef.orderByChild(t[allign]).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var text : String = ""
                        snapshot.children.forEach { nameData ->
                            text += nameData.child("name").getValue(String::class.java) + "  " + nameData.child("id").getValue(String::class.java) + "  " + nameData.child("point").getValue(Int::class.java) + "\n"
                        }
                        binding.dataText.text = text
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            }
            else if(searching == 2) {
                usersRef.orderByChild(t[allign]).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var text : String = ""
                        snapshot.children.forEach { nameData ->
                            val auth : Char = nameData.child("id").getValue(String::class.java)!![0]
                            if(auth == 'A') {
                                text += nameData.child("name").getValue(String::class.java) + "  " + nameData.child("id").getValue(String::class.java) + "  " + nameData.child("point").getValue(Int::class.java) + "\n"
                            }
                        }
                        binding.dataText.text = text
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            }
            else if(searching == 3) {
                usersRef.orderByChild(t[allign]).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var text : String = ""
                        snapshot.children.forEach { nameData ->
                            val auth : Char = nameData.child("id").getValue(String::class.java)!![0]
                            if(auth == 'B') {
                                text += nameData.child("name").getValue(String::class.java) + "  " + nameData.child("id").getValue(String::class.java) + "  " + nameData.child("point").getValue(Int::class.java) + "\n"
                            }
                        }
                        binding.dataText.text = text
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            }
            else if(searching == 4) {
                usersRef.orderByChild(t[allign]).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var text : String = ""
                        snapshot.children.forEach { nameData ->
                            val auth : Char = nameData.child("id").getValue(String::class.java)!![0]
                            if(auth == 'C') {
                                text += nameData.child("name").getValue(String::class.java) + "  " + nameData.child("id").getValue(String::class.java) + "  " + nameData.child("point").getValue(Int::class.java) + "\n"
                            }
                        }
                        binding.dataText.text = text
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            }
        }

        binding.allSearchButton.setOnClickListener {
            searching = 1
            usersRef.orderByChild(t[allign]).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var text : String = ""
                    snapshot.children.forEach { nameData ->
                        text += nameData.child("name").getValue(String::class.java) + "  " + nameData.child("id").getValue(String::class.java) + "  " + nameData.child("point").getValue(Int::class.java) + "\n"
                    }
                    binding.dataText.text = text
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }

        binding.aSearchButton.setOnClickListener {
            searching = 2
            usersRef.orderByChild(t[allign]).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var text : String = ""
                    snapshot.children.forEach { nameData ->
                        val auth : Char = nameData.child("id").getValue(String::class.java)!![0]
                        if(auth == 'A') {
                            text += nameData.child("name").getValue(String::class.java) + "  " + nameData.child("id").getValue(String::class.java) + "  " + nameData.child("point").getValue(Int::class.java) + "\n"
                        }
                    }
                    binding.dataText.text = text
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }

        binding.bSearchButton.setOnClickListener {
            searching = 3
            usersRef.orderByChild(t[allign]).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var text : String = ""
                    snapshot.children.forEach { nameData ->
                        val auth : Char = nameData.child("id").getValue(String::class.java)!![0]
                        if(auth == 'B') {
                            text += nameData.child("name").getValue(String::class.java) + "  " + nameData.child("id").getValue(String::class.java) + "  " + nameData.child("point").getValue(Int::class.java) + "\n"
                        }
                    }
                    binding.dataText.text = text
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }

        binding.cSearchButton.setOnClickListener {
            searching = 4
            usersRef.orderByChild(t[allign]).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var text : String = ""
                    snapshot.children.forEach { nameData ->
                        val auth : Char = nameData.child("id").getValue(String::class.java)!![0]
                        if(auth == 'C') {
                            text += nameData.child("name").getValue(String::class.java) + "  " + nameData.child("id").getValue(String::class.java) + "  " + nameData.child("point").getValue(Int::class.java) + "\n"
                        }
                    }
                    binding.dataText.text = text
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }
}