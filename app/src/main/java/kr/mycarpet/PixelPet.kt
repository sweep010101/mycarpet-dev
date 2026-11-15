package kr.mycarpet

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class PixelPet(context: Context, private val asleep: Boolean, private val scarf: Boolean, private val guest: Boolean) : View(context) {
    private val paint = Paint()
    private val sprite = listOf(
        "..XX........XX..", "..XoX......XoX..", "..XooXXXXXXooX..",
        "..XooooooooooX..", ".XooooooooooooX.", ".XoooEooooEoooX.",
        ".XoopooXXoo pooX.".replace(" ", ""), "..XoooowwooooX..",
        "...XssssssssX...", "...XooooooooX...", "....XXooooXX....",
        "....XXX..XXX...."
    )
    init { contentDescription = if (asleep) "쉬고 있는 픽셀 펫 르르르" else "픽셀 펫 르르르" }
    override fun onDraw(canvas: Canvas) {
        val unit = (minOf(width / 20, height / 15)).coerceAtLeast(1).toFloat()
        canvas.translate((width - 16 * unit) / 2, (height - 12 * unit) / 2)
        sprite.forEachIndexed { y, row ->
            row.forEachIndexed { x, tile ->
                if (tile != '.') {
                    paint.color = Color.parseColor(when (tile) {
                        'X', 'E' -> "#443A37"
                        'p' -> "#F59887"
                        'w' -> "#FFF4D3"
                        's' -> if (scarf) "#287D78" else "#F3C58B"
                        else -> if (guest) "#9C805F" else "#F3C58B"
                    })
                    val top = if (asleep && tile == 'E') y + .65f else y.toFloat()
                    canvas.drawRect(x * unit, top * unit, (x + 1) * unit, (y + 1) * unit, paint)
                }
            }
        }
    }
}

