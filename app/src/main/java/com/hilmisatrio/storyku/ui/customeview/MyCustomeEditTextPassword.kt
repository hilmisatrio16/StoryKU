package com.hilmisatrio.storyku.ui.customeview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.hilmisatrio.storyku.R

class MyCustomeEditTextPassword : AppCompatEditText, View.OnTouchListener {

    private lateinit var btnVisibile: Drawable
    private lateinit var btnVisibleOff: Drawable
    private var passwordVisible = false

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
        btnVisibile = ContextCompat.getDrawable(context, R.drawable.ic_visibility) as Drawable
        btnVisibleOff = ContextCompat.getDrawable(context, R.drawable.ic_visibility_off) as Drawable
        setVisibleIcon()

        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().length < 8) {
                    setError(resources.getString(R.string.error_password_text_field), null)
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(p0: Editable?) {}

        })
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {

        if (compoundDrawables[2] != null) {
            val visibleBtnStart: Float
            val visibleBtnEnd: Float
            var isVisibleBtnClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                visibleBtnEnd = (btnVisibile.intrinsicWidth + paddingStart).toFloat()
                if (event != null) {
                    when {
                        event.x < visibleBtnEnd -> isVisibleBtnClicked = true
                    }
                }
            } else {
                visibleBtnStart = (width - paddingEnd - btnVisibile.intrinsicWidth).toFloat()
                if (event != null) {
                    when {
                        event.x > visibleBtnStart -> isVisibleBtnClicked = true
                    }
                }
            }

            if (isVisibleBtnClicked) {
                if (event != null) {
                    return when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            updateTypeVisibility()
                            true
                        }

                        MotionEvent.ACTION_UP -> {
                            true
                        }

                        else -> false
                    }
                }
            } else return false
        }
        return false
    }

    private fun setVisibleIcon() {
        setButton(endOfTheText = if (passwordVisible) btnVisibile else btnVisibleOff)
    }

    private fun updateTypeVisibility() {
        passwordVisible = !passwordVisible
        inputType = if (passwordVisible) {
            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        setVisibleIcon()
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