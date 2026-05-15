package com.example.shilpakala.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.shilpakala.domain.model.BackgroundTheme
import com.example.shilpakala.domain.model.PhotoMetadata
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

data class ProcessedImage(
    val originalUri: Uri,
    val processedUri: Uri,
    val shareUri: Uri
)

@Singleton
class ImageProcessor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun process(
        sourceUri: Uri,
        metadata: PhotoMetadata,
        backgroundRemovalEngine: BackgroundRemovalEngine
    ): ProcessedImage = withContext(Dispatchers.IO) {
        val source = context.contentResolver.openInputStream(sourceUri).use { input ->
            BitmapFactory.decodeStream(input)
        } ?: error("Unable to decode captured image")

        val cutout = backgroundRemovalEngine.removeBackground(source)
        val catalog = createCatalogBitmap(cutout, metadata)
        val galleryUri = saveToMediaStore(catalog)
        val shareUri = saveToCache(catalog)
        ProcessedImage(sourceUri, galleryUri, shareUri)
    }

    private fun createCatalogBitmap(source: Bitmap, metadata: PhotoMetadata): Bitmap {
        val width = 1080
        val height = 1350
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        drawBackground(canvas, paint, width, height, metadata.backgroundTheme)

        val photoFrame = RectF(70f, 120f, 1010f, 925f)
        paint.setColor(Color.argb(60, 70, 45, 25))
        canvas.drawRoundRect(RectF(86f, 138f, 1026f, 943f), 28f, 28f, paint)
        paint.setColor(Color.WHITE)
        canvas.drawRoundRect(photoFrame, 28f, 28f, paint)

        canvas.drawBitmap(source, centerCrop(source, photoFrame), RectF(92f, 142f, 988f, 903f), null)

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 8f
        paint.color = Color.rgb(214, 160, 57)
        canvas.drawRoundRect(photoFrame, 28f, 28f, paint)
        paint.style = Paint.Style.FILL

        drawTopBadges(canvas)
        drawOverlayPanel(canvas, metadata)
        return output
    }

    private fun drawBackground(canvas: Canvas, paint: Paint, width: Int, height: Int, theme: BackgroundTheme) {
        when (theme) {
            BackgroundTheme.Studio -> paint.shader = LinearGradient(
                0f, 0f, 0f, height.toFloat(),
                Color.rgb(255, 250, 241), Color.rgb(232, 224, 212), Shader.TileMode.CLAMP
            )
            BackgroundTheme.Wooden -> paint.shader = LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                Color.rgb(115, 73, 42), Color.rgb(238, 214, 174), Shader.TileMode.CLAMP
            )
            BackgroundTheme.Festival -> paint.shader = LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                Color.rgb(126, 28, 46), Color.rgb(245, 186, 71), Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        paint.shader = null
    }

    private fun drawTopBadges(canvas: Canvas) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.rgb(169, 43, 43)
        canvas.drawRoundRect(RectF(112f, 48f, 614f, 124f), 18f, 18f, paint)
        paint.color = Color.WHITE
        paint.textSize = 34f
        paint.isFakeBoldText = true
        canvas.drawText("Handmade in Karnataka", 148f, 97f, paint)

        paint.color = Color.rgb(42, 33, 29)
        canvas.drawRoundRect(RectF(690f, 48f, 962f, 124f), 18f, 18f, paint)
        paint.color = Color.rgb(214, 160, 57)
        paint.textSize = 30f
        canvas.drawText("Heritage Label", 718f, 97f, paint)
    }

    private fun drawOverlayPanel(canvas: Canvas, metadata: PhotoMetadata) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val left = 70f + metadata.overlayX.coerceIn(0f, 0.2f) * 300f
        val top = 960f + metadata.overlayY.coerceIn(0.6f, 0.9f) * 40f
        val panel = RectF(left, top, 1010f, 1288f)

        paint.color = Color.WHITE
        canvas.drawRoundRect(panel, 24f, 24f, paint)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        paint.color = Color.rgb(217, 201, 178)
        canvas.drawRoundRect(panel, 24f, 24f, paint)
        paint.style = Paint.Style.FILL

        paint.color = Color.rgb(31, 26, 23)
        paint.textSize = 52f
        paint.isFakeBoldText = true
        canvas.drawText(metadata.productName.ifBlank { "Wood Craft" }, left + 36f, top + 76f, paint)

        paint.textSize = 34f
        paint.isFakeBoldText = false
        canvas.drawText("By ${metadata.artisanName.ifBlank { "Local Artisan" }}", left + 36f, top + 132f, paint)
        canvas.drawText("Material: ${metadata.woodType.ifBlank { "Natural Wood" }}", left + 36f, top + 184f, paint)

        paint.color = Color.rgb(169, 43, 43)
        paint.textSize = 44f
        paint.isFakeBoldText = true
        val price = metadata.price.ifBlank { "On request" }.let { if (it.startsWith("Rs.") || it == "On request") it else "Rs. $it" }
        canvas.drawText(price, left + 36f, top + 246f, paint)
    }

    private fun centerCrop(bitmap: Bitmap, target: RectF): Rect {
        val bitmapRatio = bitmap.width / bitmap.height.toFloat()
        val targetRatio = target.width() / target.height()
        var cropWidth = bitmap.width
        var cropHeight = bitmap.height
        if (bitmapRatio > targetRatio) cropWidth = (bitmap.height * targetRatio).toInt() else cropHeight = (bitmap.width / targetRatio).toInt()
        val left = (bitmap.width - cropWidth) / 2
        val top = (bitmap.height - cropHeight) / 2
        return Rect(left, top, left + cropWidth, top + cropHeight)
    }

    private fun saveToMediaStore(bitmap: Bitmap): Uri {
        val name = "ShilpaKala_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date()) + ".jpg"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ShilpaKala")
        }
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: error("Unable to create gallery image")
        context.contentResolver.openOutputStream(uri).use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, requireNotNull(output))
        }
        return uri
    }

    private fun saveToCache(bitmap: Bitmap): Uri {
        val dir = File(context.cacheDir, "shared").also { it.mkdirs() }
        val file = File(dir, "shilpa_kala_share.jpg")
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it) }
        return androidx.core.content.FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }
}
