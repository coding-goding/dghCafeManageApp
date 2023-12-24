package kr.co.bsd.dghcafemanage

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.co.bsd.dghcafemanage.databinding.ActivityAdminBinding
import java.util.*

class AdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)
        val database =
            Firebase.database("https://dghcafeadmin-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val usersRef = database.getReference("users")
        val menuRef = database.getReference("menu")


        val name = intent.getStringExtra("id").toString()
        val password = intent.getStringExtra("password").toString()

        var menuList = listOf(binding.editTextMenu1, binding.editTextMenu2, binding.editTextMenu3, binding.editTextMenu4, binding.editTextMenu5, binding.editTextMenu6,binding.editTextMenu7,binding.editTextMenu8)
        //date setting button
        var nDate : String = ""
        binding.loadButton.setOnClickListener {
            val cal = Calendar.getInstance()
            val dateListen = DatePickerDialog.OnDateSetListener { view, year, month, day ->
                binding.dateTextView.text = "${year}-${month+1}-${day}"
                nDate = binding.dateTextView.text.toString()
                var wholeMenu : String = ""
                menuRef.child(nDate).get().addOnSuccessListener {
                    if (it.exists()) {
                        it.getValue(Menu::class.java)?.let { menu ->
                            var menuNum = 0
                            var count : Int = 0
                            var a : String = menu.a
                            for (i : Int in a.indices) {
                                if(a[i] == '/') {
                                    wholeMenu += a.substring(count until i)
                                    menuList[menuNum].setText(wholeMenu)
                                    wholeMenu = ""
                                    count = i+1
                                    menuNum++
                                }
                            }
                            wholeMenu += a.substring(count)
                            menuList[menuNum].setText(wholeMenu)
                        }
                    }
                    else {
                        for(i in 0 .. 7) {
                            menuList[i].setText("")
                        }
                    }
                }
            }
            DatePickerDialog(
                this,
                dateListen,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        //menu setting button
        binding.applyButton.setOnClickListener {
            if(nDate == "") {
                Toast.makeText(
                    baseContext,
                    "날짜가 설정되지 않았습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else if(binding.editTextMenu1.text.toString() == "") {
                Toast.makeText(
                    baseContext,
                    "식단이 입력되지 않았습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {

                var wholeMenu : String = ""
                for(i in 0 .. 7) {
                    wholeMenu += menuList[i].text.toString()
                    if(i == 7) {
                        break
                    }
                    else if(menuList[i+1].text.toString() == "") {
                        break
                    }
                    else {
                        wholeMenu += "/"
                    }
                }
                val builder = AlertDialog.Builder(this)
                builder.setTitle("식단 설정")
                    .setMessage("날짜 : $nDate\n식단 : $wholeMenu\n이대로 설정합니까?")
                    .setPositiveButton("확인",
                        DialogInterface.OnClickListener { dialog, id ->
                            menuRef.child(nDate).child("a").setValue(wholeMenu)
                            menuRef.child(nDate).child("date").setValue(nDate)
                            Toast.makeText(
                                baseContext,
                                "설정되었습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        })
                    .setNegativeButton("취소",
                        DialogInterface.OnClickListener { dialog, id ->

                        })
                builder.show()

            }


        }

        binding.searchButton.setOnClickListener {
            if(binding.editTextSearch.text.toString().isNotEmpty()) {
                var id = binding.editTextSearch.text.toString()

                usersRef.child(id).get().addOnSuccessListener {
                    if (it.exists()) {
                        Toast.makeText(
                            baseContext,
                            "검색 성공!",
                            Toast.LENGTH_SHORT
                        ).show()
                        it.getValue(User::class.java)?.let { user ->
                            binding.editTextName.setText(user.name)
                            binding.editTextPw.setText(user.password)
                            binding.editTextID.setText(user.id)
                            binding.editTextPoint.setText(user.point.toString())
                            if(user.admin) {
                                binding.editTextAdmin.setText("O")
                            }
                            else {
                                binding.editTextAdmin.setText("X")
                            }
                            if(user.assigned) {
                                binding.editTextAssign.setText("O")
                            }
                            else {
                                binding.editTextAssign.setText("X")
                            }
                        }
                    }
                    else {
                        val query : Query = usersRef.orderByChild("id").equalTo(id)
                        query.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if(dataSnapshot.exists()) {
                                    for (snapshot in dataSnapshot.children) {
                                        Toast.makeText(
                                            baseContext,
                                            "검색 성공!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        binding.editTextName.setText(snapshot.child("name").value.toString())
                                        binding.editTextPw.setText(snapshot.child("password").value.toString())
                                        binding.editTextID.setText(snapshot.child("id").value.toString())
                                        binding.editTextPoint.setText(snapshot.child("point").value.toString())
                                        if(snapshot.child("admin").value.toString() == "true") {
                                            binding.editTextAdmin.setText("O")
                                        }
                                        else {
                                            binding.editTextAdmin.setText("X")

                                        }
                                        if(snapshot.child("assigned").value.toString() == "true") {
                                            binding.editTextAssign.setText("O")
                                        }
                                        else {
                                            binding.editTextAssign.setText("X")
                                        }
                                    }
                                }
                                else {
                                    Toast.makeText(
                                        baseContext,
                                        "교직원을 찾을 수 없습니다.",
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
        }

        binding.applyButton2.setOnClickListener {
            var id = binding.editTextName.text.toString()
            usersRef.child(id).get().addOnSuccessListener {
                if (it.exists()) { // edit user
                    it.getValue(User::class.java)?.let { user ->
                        var name = user.name
                        var password = user.password
                        var pid = user.id
                        var point = user.point
                        var admin = user.admin
                        var ad = ""
                        var ass = ""
                        if(admin){
                            ad = "O"
                        }
                        else {
                            ad = "X"
                        }
                        var assigned = user.assigned
                        if(assigned){
                            ass = "O"
                        }
                        else {
                            ass = "X"
                        }
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("교직원 정보 편집")
                            .setMessage("이미 존재하는 교직원입니다.\n이름 : ${binding.editTextName.text}\n고유 ID : $pid -> ${binding.editTextID.text}\n비밀번호 : $password -> ${binding.editTextPw.text}\n관리자 권한 : $ad -> ${binding.editTextAdmin.text}\n총 식사 횟수 :  $point -> ${binding.editTextPoint.text}\n일일 식사 여부 :  $ass -> ${binding.editTextAssign.text}\n편집하시겠습니까?")
                            .setPositiveButton("확인",
                                DialogInterface.OnClickListener { dialog, id ->
                                    usersRef.child(name).child("password").setValue(binding.editTextPw.text.toString())
                                    usersRef.child(name).child("id").setValue(binding.editTextID.text.toString())
                                    usersRef.child(name).child("point").setValue(binding.editTextPoint.text.toString().toInt())
                                    usersRef.child(name).child("assignTime").setValue("null")
                                    if((binding.editTextAdmin.text.toString() == "O") or (binding.editTextAdmin.text.toString() == "o")) {
                                        usersRef.child(name).child("admin").setValue(true)
                                    }
                                    else {
                                        usersRef.child(name).child("admin").setValue(false)
                                    }
                                    if((binding.editTextAssign.text.toString() == "O") or (binding.editTextAssign.text.toString() == "o")) {
                                        usersRef.child(name).child("assigned").setValue(true)
                                    }
                                    else {
                                        usersRef.child(name).child("assigned").setValue(false)
                                    }

                                        Toast.makeText(
                                        baseContext,
                                        "설정되었습니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                })
                            .setNegativeButton("취소",
                                DialogInterface.OnClickListener { dialog, id ->

                                })
                        builder.show()
                    }
                }
                else { // apply new user
                    var nm = binding.editTextName.text.toString()
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("신규 교직원 추가")
                        .setMessage("새로운 교직원을 추가합니다.\n이름 : $nm\n고유 ID : ${binding.editTextID.text}\n비밀번호 : ${binding.editTextPw.text}\n관리자 권한 : ${binding.editTextAdmin.text}\n총 식사 횟수 : ${binding.editTextPoint.text}\n일일 식사 여부 : ${binding.editTextAssign.text}\n추가하시겠습니까?")
                        .setPositiveButton("확인",
                            DialogInterface.OnClickListener { dialog, id ->
                                usersRef.child(nm).child("name").setValue(binding.editTextName.text.toString())
                                usersRef.child(nm).child("password").setValue(binding.editTextPw.text.toString())
                                usersRef.child(nm).child("id").setValue(binding.editTextID.text.toString())
                                usersRef.child(nm).child("point").setValue(binding.editTextPoint.text.toString().toInt())
                                usersRef.child(nm).child("assignTime").setValue("null")
                                if((binding.editTextAdmin.text.toString() == "O") or (binding.editTextAdmin.text.toString() == "o")) {
                                    usersRef.child(nm).child("admin").setValue(true)
                                }
                                else {
                                    usersRef.child(nm).child("admin").setValue(false)
                                }
                                if((binding.editTextAssign.text.toString() == "O") or (binding.editTextAssign.text.toString() == "o")) {
                                    usersRef.child(nm).child("assigned").setValue(true)
                                }
                                else {
                                    usersRef.child(nm).child("assigned").setValue(false)
                                }

                                Toast.makeText(
                                    baseContext,
                                    "추가되었습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            })
                        .setNegativeButton("취소",
                            DialogInterface.OnClickListener { dialog, id ->

                            })
                    builder.show()
                }
            }


        }

        binding.dailyButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("일일 데이터 정리")
                .setMessage("데이터를 정리하시겠습니까?")
                .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialog, id ->
                        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                snapshot.children.forEach { nameData ->
                                    val isAuth = nameData.child("assigned").getValue(Boolean::class.java) ?: false
                                    if (isAuth) {
                                        nameData.ref.child("assigned").setValue(false)
                                        nameData.ref.child("assignTime").setValue("null")
                                    }

                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                            }
                        })
                        Toast.makeText(
                            baseContext,
                            "정리되었습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialog, id ->

                    })
            builder.show()
        }

        binding.monthlyButton.setOnClickListener {
            val intent = Intent(this, MonthPointActivity::class.java)
            intent.putExtra("id", name)
            intent.putExtra("password", password)
            startActivity(intent)
        }
    }


}