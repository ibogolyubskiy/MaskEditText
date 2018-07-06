package com.santalu.maskedittext

import android.content.Context
import android.support.v7.widget.AppCompatEditText
import android.util.AttributeSet

/**
 * Created by fatih.santalu on 6/27/2018.
 */

class MaskEditText : AppCompatEditText {

    private lateinit var textWatcher: MaskTextWatcher

    val rawText get() = if (::textWatcher.isInitialized) textWatcher.rawText(text) else text

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }
    private fun init(context: Context, attrs: AttributeSet?) {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.MaskEditText)
            if (typedArray.hasValue(R.styleable.MaskEditText_mask)) {
                val mask = typedArray.getString(R.styleable.MaskEditText_mask)
                MaskTextWatcher.withMask(mask).bindTo(this)
            }
            typedArray?.recycle()
        }
    }
}