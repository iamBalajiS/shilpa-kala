package com.example.shilpakala.utils

import android.graphics.Bitmap
import android.graphics.Color
import javax.inject.Inject
import javax.inject.Singleton

interface BackgroundRemovalEngine {
    suspend fun removeBackground(source: Bitmap): Bitmap
}

@Singleton
class HeuristicBackgroundRemovalEngine @Inject constructor() : BackgroundRemovalEngine {
    override suspend fun removeBackground(source: Bitmap): Bitmap {
        val mutable = source.copy(Bitmap.Config.ARGB_8888, true)
        if (mutable.width < 3 || mutable.height < 3) return mutable

        val borderPixels = buildList {
            for (x in 0 until mutable.width step (mutable.width / 24).coerceAtLeast(1)) {
                add(mutable.getPixel(x, 0))
                add(mutable.getPixel(x, mutable.height - 1))
            }
            for (y in 0 until mutable.height step (mutable.height / 24).coerceAtLeast(1)) {
                add(mutable.getPixel(0, y))
                add(mutable.getPixel(mutable.width - 1, y))
            }
        }

        val avgR = borderPixels.map { Color.red(it) }.average().toInt()
        val avgG = borderPixels.map { Color.green(it) }.average().toInt()
        val avgB = borderPixels.map { Color.blue(it) }.average().toInt()
        val threshold = 50

        for (x in 0 until mutable.width) {
            for (y in 0 until mutable.height) {
                val pixel = mutable.getPixel(x, y)
                val diff = kotlin.math.abs(Color.red(pixel) - avgR) +
                    kotlin.math.abs(Color.green(pixel) - avgG) +
                    kotlin.math.abs(Color.blue(pixel) - avgB)
                if (diff < threshold) {
                    mutable.setPixel(x, y, Color.argb(0, Color.red(pixel), Color.green(pixel), Color.blue(pixel)))
                }
            }
        }
        return mutable
    }
}
