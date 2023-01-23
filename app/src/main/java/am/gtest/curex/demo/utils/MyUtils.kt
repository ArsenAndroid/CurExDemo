package am.gtest.curex.demo.utils

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import kotlin.math.roundToInt

object MyUtils {

    fun hideKeypad(view: View?) {
        if (view != null) {
            val inputMethodService = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) ?: return
            val inputMethodManager = inputMethodService as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun dpToPx(ctx: Context, dp: Int): Int {
        val metrics = ctx.resources.displayMetrics
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            metrics
        ).roundToInt()
    }
}
