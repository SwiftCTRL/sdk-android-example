package com.swiftctrl.android

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException

object Utils {

    @Throws(WriterException::class)
    fun getQRFromText(context: Context, value: String, @ColorRes colorId: Int = R.color.colorPrimary): Bitmap? {
        val bitMatrix = try {
            MultiFormatWriter().encode(
                value,
                BarcodeFormat.QR_CODE,
                500, 500, null
            )
        } catch (Illegalargumentexception: IllegalArgumentException) {
            return null
        }
        val bitMatrixWidth = bitMatrix.width
        val bitMatrixHeight = bitMatrix.height
        val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)
        for (y in 0 until bitMatrixHeight) {
            val offset = y * bitMatrixWidth
            for (x in 0 until bitMatrixWidth) {
                pixels[offset + x] = if (bitMatrix[x, y]) ContextCompat.getColor(context, colorId) else ContextCompat.getColor(context, android.R.color.white)
            }
        }
        val bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444)
        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight)
        return bitmap
    }
}
