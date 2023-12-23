package kr.co.bsd.dghcafemanage

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import kr.co.bsd.dghcafemanage.databinding.ActivityAdminChoiceBinding

class AdminChoiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminChoiceBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminChoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("id").toString()
        val password = intent.getStringExtra("password").toString()
        binding.jtext2.text = "관리자 $name"
        binding.logoutButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("로그아웃")
                .setMessage("로그아웃 하시겠습니까?.")
                .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialog, id ->
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialog, id ->

                    })
            builder.show()
        }
        binding.cafeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("id", name)
            intent.putExtra("password", password)
            startActivity(intent)
        }
        binding.adminButton.setOnClickListener {
            val intent = Intent(this, AdminActivity::class.java)
            intent.putExtra("id", name)
            intent.putExtra("password", password)
            startActivity(intent)
        }

    }
}