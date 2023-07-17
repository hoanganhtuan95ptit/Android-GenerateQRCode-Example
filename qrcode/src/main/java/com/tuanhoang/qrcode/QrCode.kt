package com.tuanhoang.qrcode

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.EnumMap
import kotlin.math.max

object QrCode {

    fun generateQRCodeWithLogo(text: String, qrCodeSize: Int = 500, logo: Bitmap? = null, logoSize: Int = 100): Bitmap? {

        // Create a QR Code Writer
        val qrCodeWriter = QRCodeWriter()

        // Set the QR Code parameters
        val hintMap: MutableMap<EncodeHintType, Any> = EnumMap(EncodeHintType::class.java)
        hintMap[EncodeHintType.CHARACTER_SET] = "UTF-8"
        hintMap[EncodeHintType.MARGIN] = 0
        hintMap[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L

        // Encode the text
        val bitMatrix: BitMatrix = try {
            val size = 10
            qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, size, size, hintMap)
        } catch (e: WriterException) {
            e.printStackTrace()
            return null
        }


        var width = 0.0f
        for (x in 0 until bitMatrix.width) if (bitMatrix.get(x, 0)) width++
        else break

        val listDotStroke = arrayListOf(
            RectF(0f, 0f, width, width),
            RectF(bitMatrix.width - width, 0f, bitMatrix.width.toFloat(), width),
            RectF(0f, bitMatrix.height - width, width, bitMatrix.height.toFloat()),
        )

        if (bitMatrix.width >= 45) listDotStroke.addAll(
            listOf(
                RectF(bitMatrix.width / 2 - 2f, width - 3f, bitMatrix.width / 2 + 3f, width + 2f),
                RectF(bitMatrix.width - width - 2f, bitMatrix.width / 2 - 2f, bitMatrix.width - width + 3f, bitMatrix.width / 2 + 3f),
                RectF(bitMatrix.width - width - 2f, bitMatrix.height - width - 2f, bitMatrix.width - width + 3f, bitMatrix.height - width + 3f),
                RectF(bitMatrix.width / 2 - 2f, bitMatrix.height - width - 2f, bitMatrix.width / 2 + 3f, bitMatrix.height - width + 3f),
                RectF(width - 3f, bitMatrix.width / 2 - 2f, width + 2f, bitMatrix.width / 2 + 3f)
            )
        )

        val widthDotFill = width - 4
        val listDotFill = arrayListOf(
            RectF(2f, 2f, 2f + widthDotFill, 2f + widthDotFill),
            RectF(bitMatrix.width - widthDotFill - 2f, 2f, bitMatrix.width.toFloat() - 2f, 2f + widthDotFill),
            RectF(2f, bitMatrix.height - widthDotFill - 2f, 2f + widthDotFill, bitMatrix.height.toFloat() - 2f)
        )

        if (bitMatrix.width >= 45) listDotFill.addAll(
            listOf(
                RectF(bitMatrix.width / 2 + 0f, width - 1f, bitMatrix.width / 2 + 1f, width + 0f),
                RectF(bitMatrix.width - width + 0f, bitMatrix.width / 2 + 0f, bitMatrix.width - width + 1f, bitMatrix.width / 2 + 1f),
                RectF(bitMatrix.width - width + 0f, bitMatrix.height - width + 0f, bitMatrix.width - width + 1f, bitMatrix.height - width + 1f),
                RectF(bitMatrix.width / 2 + 0f, bitMatrix.height - width + 0f, bitMatrix.width / 2 + 1f, bitMatrix.height - width + 1f),
                RectF(width - 1f, bitMatrix.width / 2 + 0f, width + 0f, bitMatrix.width / 2 + 1f)
            )
        )

        val pixelSize = 1f

        // Convert the BitMatrix to a Bitmap
        val scale = qrCodeSize / bitMatrix.width
        val bitmap = Bitmap.createBitmap(bitMatrix.width * scale, bitMatrix.height * scale, Bitmap.Config.ARGB_8888)

        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.color = Color.BLACK
        paint.isAntiAlias = true

        val canvas = Canvas(bitmap)
        for (x in 0 until bitMatrix.width) {

            var rectF: RectF? = null

            for (y in 0 until bitMatrix.height) {

                val isNextBlack = y + 1 < bitMatrix.height && bitMatrix.get(x, y + 1) && !listDotStroke.any { x + pixelSize / 2f in it.left..it.right && y + 1 + pixelSize / 2f in it.top..it.bottom }
                val isCurrentBlack = bitMatrix.get(x, y) && !listDotStroke.any { x + pixelSize / 2f in it.left..it.right && y + pixelSize / 2f in it.top..it.bottom }

                if (rectF == null && isCurrentBlack) rectF = RectF(x.toFloat() * scale, y.toFloat() * scale, (x + pixelSize) * scale, (y + pixelSize) * scale)
                else if (isCurrentBlack && rectF != null) rectF.set(rectF.left, rectF.top, rectF.right, rectF.bottom + pixelSize * scale)

                if (rectF != null && !isNextBlack && isCurrentBlack) {
                    rectF.set(rectF.left + 2f, rectF.top + 2f, rectF.right - 2f, rectF.bottom - 2f)
                    val radius = max(rectF.width() / 2.5f, rectF.height() / 2.5f)
                    canvas.drawRoundRect(rectF, radius, radius, paint)
                    rectF = null
                }
            }
        }


        listDotFill.forEachIndexed { index, it ->

            val paddingLeft = 0f
            val paddingTop = 0f
            val paddingRight = 0f
            val paddingBottom = 0f

            val rectF = RectF(it.left * scale + 2f + paddingLeft, it.top * scale + 2f + paddingTop, it.right * scale - 2f - paddingRight, it.bottom * scale - 2f - paddingBottom)
            val radius = max(rectF.width() / 2.5f, rectF.height() / 2.5f)
            canvas.drawRoundRect(rectF, radius, radius, paint)
        }


        listDotStroke.forEachIndexed { index, it ->

            paint.style = Paint.Style.STROKE
            paint.strokeWidth = pixelSize * scale - 4f

            val paddingLeft = paint.strokeWidth / 2
            val paddingTop = paint.strokeWidth / 2
            val paddingRight = paint.strokeWidth / 2
            val paddingBottom = paint.strokeWidth / 2

            val rectF = RectF(it.left * scale + 2f + paddingLeft, it.top * scale + 2f + paddingTop, it.right * scale - 2f - paddingRight, it.bottom * scale - 2f - paddingBottom)
            val radius = max(rectF.width() / 2.5f, rectF.height() / 2.5f)
            canvas.drawRoundRect(rectF, radius, radius, paint)
        }


        if (logo != null) {
            drawLogo(bitmap, logo, logoSize)
        }


        return bitmap
    }

    private fun drawLogo(qrcode: Bitmap, _logo: Bitmap, logoSize: Int) {

        // resize logo
        val logo = Bitmap.createScaledBitmap(_logo, logoSize, logoSize, false)

        // Add the logo to the QR Code
        val logoWidth = logo.width
        val logoHeight = logo.height
        val logoX = (qrcode.width - logoWidth) / 2
        val logoY = (qrcode.height - logoHeight) / 2

        val canvas = Canvas(qrcode)

        val paint = Paint(Paint.FILTER_BITMAP_FLAG)

        canvas.drawBitmap(logo, logoX.toFloat(), logoY.toFloat(), paint)

        logo.recycle()
    }
}