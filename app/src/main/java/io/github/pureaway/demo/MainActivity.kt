package io.github.pureaway.demo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import io.github.pureaway.nicetoast.NiceToast
import io.github.pureaway.nicetoast.OnClickEvent
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnCustomToast.setOnClickListener {
            NiceToast.make(this)
                .setCustomView(
                    LayoutInflater.from(this).inflate(R.layout.layout_remind_device_alarm, null)
                )
                .duration(10000)
                .onClickEvent(object : OnClickEvent {
                    override fun onClick() {
                        startActivity(Intent(this@MainActivity, SecondActivity::class.java))
                    }

                    override fun onBackPressed() {

                    }

                })
                .cancelable(true)
                .gravityStyle(NiceToast.BOTTOM_RIGHT)
                .margin(0, 0, 20, 20)
                .showCustomView()
        }
        btn.setOnClickListener {
            NiceToast.make(this@MainActivity)
                .setCustomView(
                    LayoutInflater.from(this@MainActivity).inflate(R.layout.layout_toast2, null)
                )
                .gravityStyle(NiceToast.CENTER)
                .cancelable(false)
                .duration(3000)
                .showCustomView()
        }
        toast.setOnClickListener {
            NiceToast.make(this@MainActivity)
                .cancelable(false)
                .gravityStyle(NiceToast.CENTER)
                .text("普通toast")
                .duration(5000)
                .show()
        }
    }

}