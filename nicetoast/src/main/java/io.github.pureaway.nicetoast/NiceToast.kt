package io.github.pureaway.nicetoast

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.NonNull

class NiceToast private constructor() {

    private constructor(mContext: Context) : this() {
        context = mContext
        if (niceToast == null) {
            initWm()
            relativeLayout = RelativeLayout(context)
            relativeLayout.setBackgroundColor(Color.argb(0, 0, 0, 0))
            val layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
            relativeLayout.layoutParams = layoutParams
            if (cancelable) {
                hide()
            }
            textView = TextView(context)
            addTextView()
            niceToast = this@NiceToast
        }
    }

    companion object {

        private var niceToast: NiceToast? = null

        fun make(context: Context): NiceToast {
            niceToast?.let {
                return it
            }
            return NiceToast(context)
        }
    }

    private lateinit var relativeLayout: RelativeLayout
    private var textView: TextView? = null
    private var context: Context? = null
    private var duration = 2000
    private var cancelable = false
    private var customView = false
    private var params: WindowManager.LayoutParams? = null
    private var wm: WindowManager? = null
    private var contentView: View? = null
    private val handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0x153 -> hide()
                0x154 -> show()
                0x155 -> {
                    contentView = null
                    hide()
                }

                else -> {
                }
            }
        }
    }

    private fun addTextView() {
        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        params.addRule(RelativeLayout.CENTER_HORIZONTAL)
        params.bottomMargin = 188
        textView?.layoutParams = params
        textView?.setPadding(30, 10, 30, 10)
        textView?.setBackgroundResource(R.drawable.sp_toast)
        textView?.minHeight = dp2px(36f)
        textView?.minWidth = dp2px(200f)
        textView?.maxWidth = dp2px(234f)
        textView?.gravity = Gravity.CENTER
        textView?.setTextColor(Color.parseColor("#ffffff"))
        relativeLayout.addView(textView)
    }

    private fun initWm() {
        params = WindowManager.LayoutParams()
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        params?.width = WindowManager.LayoutParams.WRAP_CONTENT
        params?.format = PixelFormat.TRANSLUCENT
        params?.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL
        params?.gravity = Gravity.TOP
        params?.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        wm = context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    fun gravity(gravity: Int): NiceToast {
        params?.gravity = gravity
        return niceToast!!
    }

    fun text(text: String?): NiceToast {
        textView?.text = text
        return niceToast!!
    }

    fun duration(duration: Int): NiceToast {
        this.duration = duration
        return niceToast!!
    }

    fun textSize(textSize: Int): NiceToast {
        textView?.textSize = textSize.toFloat()
        return niceToast!!
    }

    fun textSizePX(textSize: Float): NiceToast {
        textView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        return niceToast!!
    }

    fun textColor(textColor: Int): NiceToast {
        textView?.setTextColor(textColor)
        return niceToast!!
    }

    fun maxWidth(maxWidth: Int): NiceToast {
        textView?.maxWidth = maxWidth
        return niceToast!!
    }

    fun cancelable(cancelable: Boolean): NiceToast {
        this.cancelable = cancelable
        return niceToast!!
    }

    fun minWidth(minWidth: Int): NiceToast {
        textView?.minWidth = minWidth
        return niceToast!!
    }

    fun minHeight(minHeight: Int): NiceToast {
        textView?.minHeight = minHeight
        return niceToast!!
    }

    fun maxHeight(maxHeight: Int): NiceToast {
        textView?.maxHeight = maxHeight
        return niceToast!!
    }

    fun margin(leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int): NiceToast {
        val lp = textView?.layoutParams as RelativeLayout.LayoutParams
        lp.leftMargin = leftMargin
        lp.topMargin = topMargin
        lp.rightMargin = rightMargin
        lp.bottomMargin = bottomMargin
        textView?.layoutParams = lp
        return niceToast!!
    }

    fun backgroundResource(@DrawableRes resid: Int): NiceToast {
        textView?.setBackgroundResource(resid)
        return niceToast!!
    }


    fun setCustomView(view: View): CustomView {
        relativeLayout.removeAllViews()
        relativeLayout.addView(view)
        customView = true
        return CustomView(view)
    }

    class CustomView(@NonNull view: View) {
        private val view: View
        fun duration(duration: Int): CustomView {
            niceToast?.duration(duration)
            return this
        }

        fun margin(
            leftMargin: Int,
            topMargin: Int,
            rightMargin: Int,
            bottomMargin: Int
        ): CustomView {
            setMargins(leftMargin, topMargin, rightMargin, bottomMargin)
            return this
        }

        private fun setMargins(
            leftMargin: Int,
            topMargin: Int,
            rightMargin: Int,
            bottomMargin: Int
        ) {
            val lp = view.layoutParams as RelativeLayout.LayoutParams
            lp.leftMargin = leftMargin
            lp.topMargin = topMargin
            lp.rightMargin = rightMargin
            lp.bottomMargin = bottomMargin
            view.layoutParams = lp
        }

        fun cancelable(cancelable: Boolean): CustomView {
            niceToast?.cancelable = cancelable
            return this
        }

        fun gravity(gravity: Int): CustomView {
            niceToast?.gravity(gravity)
            return this
        }

        fun showCustomView() {
            niceToast?.show()
        }

        init {
            niceToast?.contentView = view
            this.view = view
        }
    }


    fun show() {
        hide()
        if (!customView) {
            relativeLayout.removeAllViews()
            relativeLayout.addView(textView)
        }
        handler.sendEmptyMessageDelayed(0x155, duration.toLong())
        if (relativeLayout.parent != null) {
            wm?.removeView(relativeLayout)
        }
        wm?.addView(relativeLayout, params)
        wm?.updateViewLayout(relativeLayout, params)
        customView = false
    }

    private fun hide() {
        handler.removeMessages(0x155)
        if (relativeLayout.parent != null) {
            wm?.removeView(relativeLayout)
        }
    }


    private fun dp2px(dp: Float): Int {
        val scale = context!!.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

}