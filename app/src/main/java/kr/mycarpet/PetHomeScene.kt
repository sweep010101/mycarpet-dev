package kr.mycarpet

import android.content.Context
import android.graphics.*
import android.view.View
import kotlin.math.sin

class PetHomeScene(context: Context, val guest: Boolean, val scarf: Boolean, val sick: Boolean, val hungry: Boolean) : View(context) {
    private val p = Paint(Paint.ANTI_ALIAS_FLAG)
    private fun image(name: String): Bitmap? = try {
        context.assets.open("images/$name.png").use { BitmapFactory.decodeStream(it) }
    } catch (_: java.io.IOException) { null }

    private val backdrop = image("bg_yangjae_hq")
    private val petName = if (guest) "bami" else "reureureu"
    private val pose = when { sick -> "sick"; hungry -> "hungry"; else -> "idle" }
    private val sprite = image("${petName}_$pose") ?: if (pose != "idle") image("${petName}_idle") else null
    private val imagePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

    private fun color(s: String) { p.color = Color.parseColor(s) }
    private fun oval(c: Canvas, x: Float, y: Float, w: Float, h: Float, shade: String) {
        color(shade); c.drawOval(x-w,y-h,x+w,y+h,p)
    }
    private fun shape(c: Canvas, shade: String, vararg points: Float) {
        val path = Path(); path.moveTo(points[0],points[1])
        for (i in 2 until points.size step 2) path.lineTo(points[i],points[i+1])
        path.close(); color(shade); c.drawPath(path,p)
    }
    override fun onDraw(c: Canvas) {
        if (backdrop != null) {
            val k = maxOf(width.toFloat()/backdrop.width, height.toFloat()/backdrop.height)
            val dx = (width - backdrop.width*k)/2
            val dy = (height - backdrop.height*k)/2
            c.drawBitmap(backdrop, null, RectF(dx,dy,width-dx,height-dy), imagePaint)
        } else {
        c.save(); c.scale(width/1000f,height/420f)
        p.shader = LinearGradient(0f,0f,0f,300f,intArrayOf(0xff687cc9.toInt(),0xffccb6de.toInt(),0xffffd0ad.toInt()),null,Shader.TileMode.CLAMP)
        c.drawRect(0f,0f,1000f,420f,p); p.shader=null
        oval(c,162f,190f,39f,39f,"#FFF3CE")
        shape(c,"#8589B7",0f,222f,110f,199f,162f,209f,260f,183f,340f,216f,556f,190f,680f,224f,0f,245f)
        color("#87A9CA"); c.drawRect(0f,222f,1000f,350f,p)
        for(i in 0..35) { val x=(i*79%515).toFloat(); val y=(230+i*17%85).toFloat()
            oval(c,x,y,10f+i%17,1.2f,if(i%4==0) "#FFDEBB" else "#B6D4E3") }
        shape(c,"#658B89",490f,234f,633f,158f,710f,136f,800f,150f,904f,91f,1000f,82f,1000f,340f)
        shape(c,"#AC9FAB",583f,219f,714f,208f,623f,236f,504f,263f,751f,307f,1000f,355f,1000f,398f,367f,309f,392f,264f,581f,233f,680f,216f)
        shape(c,"#383B59",0f,353f,350f,320f,700f,307f,1000f,325f,1000f,420f,0f,420f)
        c.restore()
        }
        c.save()
        val scale = minOf(width/680f,height/361f)
        c.translate(width*.445f,height*.704f)
        c.scale(scale,scale)
        oval(c,0f,66f,78f,9f,"#424465")
        val bob = sin(android.os.SystemClock.uptimeMillis()/600.0).toFloat()*2
        c.translate(0f,bob)
        if (sprite != null) {
            val k = minOf(178f/sprite.width, 158f/sprite.height)
            val half = sprite.width*k/2
            c.drawBitmap(sprite,null,RectF(-half,67f-sprite.height*k,half,67f),imagePaint)
        } else {
        val fur = if(guest) "#D8AF91" else "#FFF3E5"
        oval(c,-43f,45f,27f,23f,fur); oval(c,43f,45f,27f,23f,fur)
        oval(c,0f,23f,58f,42f,fur)
        oval(c,-61f,-20f,22f,31f,"#DBCDE1"); oval(c,61f,-20f,22f,31f,"#DBCDE1")
        oval(c,0f,-18f,66f,49f,fur)
        for(x in listOf(-25f,25f)){
            oval(c,x,-57f,24f,17f,"#BB884F"); oval(c,x,-59f,19f,12f,"#354478")
            oval(c,x-5,-63f,6f,2f,"#A7CCFF")
            oval(c,x,-18f,5f,if(sick) 2f else 7f,"#343047")
            oval(c,x*1.6f,-5f,9f,5f,"#F3B5B8")
        }
        oval(c,0f,-2f,if(hungry) 7f else 5f,if(hungry) 7f else 3f,"#93596D")
        }
        if(scarf) { oval(c,0f,23f,48f,9f,"#525CAC"); shape(c,"#6875CB",17f,24f,36f,23f,31f,49f,18f,40f) }
        c.restore(); postInvalidateDelayed(80)
    }
}


