package com.github.pureaway.nicetoast

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.Settings
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import androidx.annotation.NonNull
import com.github.pureaway.nicetoast.widget.KeyListenRelativeLayout

class NiceToast private constructor() {

    private constructor(mContext: Context) : this() {
        context = mContext
        if (niceToast == null) {
            initWm()
            relativeLayout = KeyListenRelativeLayout(context)
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
            relativeLayout.setOnClickEvent(object : OnClickEvent {
                override fun onClick() {
                    if (cancelable) {
                        hide()
                    }
                    onClickEvent?.onClick()
                }

                override fun onBackPressed() {
                    if (cancelable) {
                        hide()
                    }
                    onClickEvent?.onBackPressed()
                }
            })
            textView = TextView(context)
            addTextView()
            niceToast = this@NiceToast
        }
    }

    companion object {
        /**
         * 顶部居中
         */
        const val TOP_CENTER = 0x12

        /**
         * 顶部居左
         */
        const val TOP_LEFT = TOP_CENTER + 1

        /**
         * 顶部居右
         */
        const val TOP_RIGHT = TOP_LEFT + 1

        /**
         * 中间居左
         */
        const val CENTER_LEFT = TOP_RIGHT + 1

        /**
         * 中间居右
         */
        const val CENTER_RIGHT = CENTER_LEFT + 1

        /**
         * 中间
         */
        const val CENTER = CENTER_RIGHT + 1

        /**
         * 底部居中
         */
        const val BOTTOM_CENTER = CENTER + 1

        /**
         * 底部居左
         */
        const val BOTTOM_LEFT = BOTTOM_CENTER + 1

        /**
         * 底部居右
         */
        const val BOTTOM_RIGHT = BOTTOM_LEFT + 1

        var niceToast: NiceToast? = null

        fun make(context: Context): NiceToast {
            niceToast?.let {
                return it
            }
            return NiceToast(context)
        }
    }

    private lateinit var relativeLayout: KeyListenRelativeLayout
    private var textView: TextView? = null
    private var context: Context? = null
    private var duration = 3000
    private var cancelable = false
    private var customView = false
    private var onClickEvent: OnClickEvent? = null
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

    @IntDef(
        TOP_CENTER,  // 顶部居中
        TOP_LEFT,  // 顶部居左
        TOP_RIGHT,  // 顶部居右
        CENTER_LEFT,  // 中间居左
        CENTER_RIGHT,  // 中间居右
        CENTER,  // 中间
        BOTTOM_CENTER,  //  底部居中
        BOTTOM_LEFT,  // 底部居左
        BOTTOM_RIGHT // 底部居右
    )
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @Target(
        AnnotationTarget.VALUE_PARAMETER,
        AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.PROPERTY_SETTER,
        AnnotationTarget.LOCAL_VARIABLE,
        AnnotationTarget.FIELD
    )
    annotation class GravityStyle

    private fun addTextView() {
        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
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
        params?.height = WindowManager.LayoutParams.MATCH_PARENT
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.format = PixelFormat.TRANSLUCENT
        params?.type = WindowManager.LayoutParams.TYPE_TOAST
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params?.let {
                it.type += 32
            }
        }
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            params?.let {
                it.type -= 3
            }
        }
        params?.gravity = Gravity.CENTER
        params?.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        wm = context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
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

    fun clickable(clickable: Boolean): NiceToast {
        cancelable = clickable
        if (clickable) {
            params?.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        } else {
            params?.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        }
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

    fun onClickEvent(clickEvent: OnClickEvent?): NiceToast {
        onClickEvent = clickEvent
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

    fun gravityStyle(@GravityStyle gravityStyle: Int): NiceToast {
        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        when (gravityStyle) {
            TOP_CENTER -> {
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
            }
            TOP_LEFT -> layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
            TOP_RIGHT -> {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            }
            CENTER -> layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
            CENTER_LEFT -> layoutParams.addRule(RelativeLayout.CENTER_VERTICAL)
            CENTER_RIGHT -> {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                layoutParams.addRule(RelativeLayout.CENTER_VERTICAL)
            }
            BOTTOM_CENTER -> {
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            }
            BOTTOM_RIGHT -> {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            }
            BOTTOM_LEFT -> {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            }
        }
        textView?.layoutParams = layoutParams
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

        fun onClickEvent(clickEvent: OnClickEvent): CustomView {
            niceToast?.onClickEvent = clickEvent
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

        fun gravityStyle(@GravityStyle gravityStyle: Int): CustomView {
            val layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            when (gravityStyle) {
                TOP_CENTER -> {
                    layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                }
                TOP_LEFT -> layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                TOP_RIGHT -> {
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                }
                CENTER -> layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
                CENTER_LEFT -> layoutParams.addRule(RelativeLayout.CENTER_VERTICAL)
                CENTER_RIGHT -> {
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                    layoutParams.addRule(RelativeLayout.CENTER_VERTICAL)
                }
                BOTTOM_CENTER -> {
                    layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                }
                BOTTOM_RIGHT -> {
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                }
                BOTTOM_LEFT -> {
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                }
                else -> {
                }
            }
            view.layoutParams = layoutParams
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
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            if (!Settings.canDrawOverlays(context)) {
                return
            }
        }
        hide()
        if (customView == false) {
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
        if (null != contentView) {
            contentView?.setOnClickListener {
                if (null != onClickEvent) {
                    onClickEvent!!.onClick()
                    hide()
                }
            }
        } else {
            textView?.setOnClickListener {
                if (null != onClickEvent) {
                    onClickEvent!!.onClick()
                    hide()
                }
            }
        }
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