package am.gtest.curex.demo.utils

import android.content.Context
import android.view.View
import android.view.ViewGroup

fun View.setCustomMargins(
    ctx: Context,
    position: Int,
    itemCount: Int,
    startDp: Int,
    topDp: Int,
    endDp: Int,
    bottomDp: Int,
    lastItemBottomDp: Int
) {

    val mlp = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)

    mlp.marginStart = MyUtils.dpToPx(ctx, startDp)
    mlp.marginEnd = MyUtils.dpToPx(ctx, endDp)

    when (position) {
        0 -> {
            if (itemCount == 1) {
                mlp.topMargin = MyUtils.dpToPx(ctx, topDp)
                mlp.bottomMargin = MyUtils.dpToPx(ctx, bottomDp)
            } else {
                mlp.topMargin = MyUtils.dpToPx(ctx, topDp)
                mlp.bottomMargin = MyUtils.dpToPx(ctx, bottomDp / 2)
            }
        }
        itemCount - 1 -> {
            mlp.topMargin = MyUtils.dpToPx(ctx, topDp / 2)
            mlp.bottomMargin = MyUtils.dpToPx(ctx, lastItemBottomDp)
        }
        else -> {
            mlp.topMargin = MyUtils.dpToPx(ctx, topDp / 2)
            mlp.bottomMargin = MyUtils.dpToPx(ctx, bottomDp / 2)
        }
    }

    this.layoutParams = mlp
}