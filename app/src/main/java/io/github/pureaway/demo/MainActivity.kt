package io.github.pureaway.demo

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.github.pureaway.nicetoast.NiceToast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.btnCustomToast).setOnClickListener {
            NiceToast.make(this)
                .setCustomView(
                    LayoutInflater.from(this).inflate(R.layout.layout_remind_device_alarm, null)
                )
                .duration(10000)
                .cancelable(true)
                .gravity(Gravity.BOTTOM or Gravity.END)
                .margin(0, 0, 20, 20)
                .showCustomView()
        }
        findViewById<View>(R.id.btn).setOnClickListener {
            NiceToast.make(this@MainActivity)
                .setCustomView(
                    LayoutInflater.from(this@MainActivity).inflate(R.layout.layout_toast2, null)
                )
                .gravity(Gravity.CENTER)
                .cancelable(false)
                .duration(3000)
                .showCustomView()
        }
        findViewById<View>(R.id.toast).setOnClickListener {
            NiceToast.make(this@MainActivity)
                .cancelable(false)
                .gravity(Gravity.CENTER)
                .text("普通toast")
                .duration(5000)
                .show()
        }
    }

}