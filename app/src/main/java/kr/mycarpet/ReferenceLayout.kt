package kr.mycarpet

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView

/** Place all overlays before the first layout, then scale their bounds on each measure. */
class ReferenceLayout(context: Context) : FrameLayout(context) {
    private data class Region(val params: LayoutParams, val x: Int, val y: Int, val w: Int, val h: Int)
    private val regions = mutableListOf<Region>()
    private val fonts = mutableMapOf<TextView, Int>()

    fun registerFont(view: TextView, size: Int) { fonts[view] = size }

    fun addRegion(view: View, x: Int, y: Int, w: Int, h: Int) {
        val params = LayoutParams(0, 0)
        regions.add(Region(params, x, y, w, h))
        addView(view, params)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        for (r in regions) {
            r.params.leftMargin = width * r.x / 1670
            r.params.topMargin = height * r.y / 941
            r.params.width = width * r.w / 1670
            r.params.height = height * r.h / 941
        }
        val scale = minOf(width / 1670f, height / 941f)
        for ((view, size) in fonts) {
            if (view.textSize != size * scale) view.setTextSize(TypedValue.COMPLEX_UNIT_PX, size * scale)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}
