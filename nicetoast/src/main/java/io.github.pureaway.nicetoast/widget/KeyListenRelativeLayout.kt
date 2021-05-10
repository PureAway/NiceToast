package io.github.pureaway.nicetoast.widget

import android.content.Context
import android.view.KeyEvent
import android.widget.RelativeLayout
import io.github.pureaway.nicetoast.OnClickEvent

class KeyListenRelativeLayout(context: Context?) : RelativeLayout(context) {

    private var onClickEvent: OnClickEvent? = null

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    onClickEvent?.let {
                        it.onBackPressed()
                        return true
                    }
                }
                KeyEvent.KEYCODE_DPAD_CENTER -> {
                    onClickEvent?.let {
                        it.onClick()
                        return true
                    }
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }

    fun setOnClickEvent(onClickEvent: OnClickEvent) {
        this.onClickEvent = onClickEvent
    }

}