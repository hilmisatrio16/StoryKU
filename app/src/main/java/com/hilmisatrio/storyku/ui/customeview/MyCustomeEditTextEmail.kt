package com.hilmisatrio.storyku.ui.customeview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.hilmisatrio.storyku.R

class MyCustomeEditTextEmail : AppCompatEditText, View.OnTouchListener {

    private lateinit var clearButtonIcon: Drawable

    constructor(context: Context) : super(context) {
        initEditText()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initEditText()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initEditText()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    private fun initEditText() {
        clearButtonIcon = ContextCompat.getDrawable(context, R.drawable.ic_clear) as Drawable
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isNotEmpty()) {
                    showClearButtonIcon()
                    if (Patterns.EMAIL_ADDRESS.matcher(p0.toString()).matches()) {
                        error = null
                    } else {
                        setError(resources.getString(R.string.error_email_text_field), null)
                    }
                } else {
                    hideClearButtonIcon()
                }
            }

            override fun afterTextChanged(p0: Editable?) {}

        })
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        if (compoundDrawables[2] != null) {
            val clearBtnStart: Float
            val clearBtnEnd: Float
            var isClearBtnClicked = false

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearBtnEnd = (clearButtonIcon.intrinsicWidth + paddingStart).toFloat()
                if (event != null) {
                    when {
                        event.x < clearBtnEnd -> isClearBtnClicked = true
                    }
                }
            } else {
                clearBtnStart = (width - paddingEnd - clearButtonIcon.intrinsicWidth).toFloat()
                if (event != null) {
                    when {
                        event.x > clearBtnStart -> isClearBtnClicked = true
                    }
                }
            }
            if (isClearBtnClicked) {
                if (event != null) {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            clearButtonIcon =
                                ContextCompat.getDrawable(context, R.drawable.ic_clear) as Drawable
                            showClearButtonIcon()
                            return true
                        }

                        MotionEvent.ACTION_UP -> {
                            clearButtonIcon =
                                ContextCompat.getDrawable(context, R.drawable.ic_clear) as Drawable
                            when {
                                text != null -> text?.clear()
                            }

                            hideClearButtonIcon()
                            return true
                        }

                        else -> return false
                    }
                }
            } else return false
        }
        return false
    }

    private fun showClearButtonIcon() {
        setButton(endOfTheText = clearButtonIcon)
    }

    private fun hideClearButtonIcon() {
        setButton()
    }

    private fun setButton(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }
}