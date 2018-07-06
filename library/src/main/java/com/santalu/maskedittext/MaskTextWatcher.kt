package com.santalu.maskedittext

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class MaskTextWatcher private constructor(private val mask: String?) : TextWatcher, MaskBinder {

    companion object {
        fun withMask(mask: String?): MaskBinder {
            return MaskTextWatcher(mask)
        }
    }

    override fun bindTo(editText: EditText) {
        this.editText = editText
        editText.addTextChangedListener(this)
    }

    private lateinit var editText: EditText

    private var selfChange: Boolean = false

    override fun onTextChanged(text: CharSequence?, start: Int, before: Int, after: Int) {
        if (text.isNullOrEmpty() || selfChange) return

        selfChange = true
        editText.setText(format(text))
        setCursorPosition(start, before, after)
        selfChange = false
    }

    override fun format(source: CharSequence?): CharSequence? {
        if (mask.isNullOrEmpty()) return source

        val builder = StringBuilder()
        val textLength = source!!.length
        var textIndex = 0
        mask?.forEach {
            if (textIndex >= textLength) return@forEach
            var c = source[textIndex]
            if (it.isPlaceHolder()) {
                if (c.isLetterOrDigit()) {
                    builder.append(c)
                    textIndex++
                } else {
                    // find closest letter or digit
                    for (i in textIndex until textLength) {
                        c = source[i]
                        if (c.isLetterOrDigit()) {
                            builder.append(c)
                            textIndex = i + 1
                            break
                        }
                    }
                }
            } else {
                builder.append(it)
                if (c == it) textIndex++
            }
        }

        return builder.toString()
    }

    private fun setCursorPosition(start: Int, lengthBefore: Int, lengthAfter: Int) {
        if (editText.text.isNullOrEmpty()) return

        val end = editText.text.length
        val cursor = when {
            lengthBefore > lengthAfter -> start
            lengthAfter > 1 -> end
            start < end -> findNextPlaceHolderPosition(start, end)
            else -> end
        }

        editText.setSelection(cursor)
    }

    private fun findNextPlaceHolderPosition(start: Int, end: Int): Int {
        mask?.let {
            for (i in start until end) {
                val m = it[i]
                val c = editText.text[i]
                if (m.isPlaceHolder() && c.isLetterOrDigit()) {
                    return i + 1
                }
            }
        }
        return start + 1
    }

    override fun rawText(source: CharSequence?): String? {
        if (source.isNullOrEmpty() || mask.isNullOrEmpty()) return null

        val builder = StringBuilder()
        val textLength = source!!.length
        mask?.forEachIndexed { index, m ->
            if (index >= textLength) return@forEachIndexed
            val c = source[index]
            if (m.isPlaceHolder()) {
                builder.append(c)
            }
        }

        return builder.toString()
    }


    override fun afterTextChanged(s: Editable?) { }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
}

interface MaskBinder {
    fun bindTo(editText: EditText)
    fun format(source: CharSequence?): CharSequence?
    fun rawText(source: CharSequence?): String?
}