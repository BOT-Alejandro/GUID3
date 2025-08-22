package com.alexdev.guid3.util

import android.content.Context
import android.graphics.*
import androidx.core.graphics.createBitmap
import com.alexdev.guid3.R

object AvatarUtils {
    fun createInitialAvatar(context: Context, nameOrEmail: String?): Bitmap {
        val size = 128
        val bmp = createBitmap(size, size)
        val canvas = Canvas(bmp)
        val paintCircle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = context.resources.getColor(R.color.AzulSecundario, context.theme)
        }
        val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = size * 0.5f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
        }
        val radius = size / 2f
        canvas.drawCircle(radius, radius, radius, paintCircle)
        val base = (nameOrEmail?.trim().takeIf { !it.isNullOrEmpty() } ?: "U")
        val initial = base.first().uppercase()
        val y = radius - (paintText.descent() + paintText.ascent()) / 2
        canvas.drawText(initial, radius, y, paintText)
        return bmp
    }
}

