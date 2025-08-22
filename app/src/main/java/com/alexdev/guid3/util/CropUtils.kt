package com.alexdev.guid3.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.alexdev.guid3.R
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import java.io.File

object CropUtils {
    /**
     * Crea un Intent configurado de UCrop para recortar una imagen cuadrada (avatar / icono)
     * con título localizado y gestos completos (mover, escalar, rotar).
     */
    fun buildCropIntent(
        context: Context,
        source: Uri,
        title: String = context.getString(R.string.crop_image_title),
        maxSize: Int = 512,
        square: Boolean = true
    ): Intent {
        val destination = Uri.fromFile(File(context.cacheDir, "crop_${System.currentTimeMillis()}.jpg"))
        val uCrop = if (square) {
            UCrop.of(source, destination)
                .withAspectRatio(1f, 1f)
        } else {
            UCrop.of(source, destination)
        }
        val options = UCrop.Options().apply {
            setToolbarTitle(title)
            setHideBottomControls(false)
            setFreeStyleCropEnabled(!square)
            setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL)
            setCompressionQuality(95)
            withMaxResultSize(maxSize, maxSize)
            setActiveControlsWidgetColor(context.resources.getColor(R.color.AzulSecundario, context.theme))
        }
        return uCrop.withOptions(options).getIntent(context)
    }
}

