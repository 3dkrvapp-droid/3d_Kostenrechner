package com.example.a3dkostenrechner

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat

suspend fun createAndSharePdf(
    context: Context,
    totalCost: Float?,
    materialCost: Float?,
    machineCost: Float?,
    electricityCost: Float?,
    workCost: Float?
) {
    try {
        val uri = withContext(Dispatchers.IO) {
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint()
            val currencyFormat = NumberFormat.getCurrencyInstance()

            var yPos = 60f

            paint.textSize = 24f
            paint.isFakeBoldText = true
            canvas.drawText("Kostenaufstellung 3D-Druck", 20f, yPos, paint)
            yPos += 50f

            paint.textSize = 16f
            paint.isFakeBoldText = false

            materialCost?.let {
                canvas.drawText("Materialkosten: ${currencyFormat.format(it)}", 20f, yPos, paint)
                yPos += 25f
            }
            machineCost?.let {
                canvas.drawText("Maschinenkosten: ${currencyFormat.format(it)}", 20f, yPos, paint)
                yPos += 25f
            }
            electricityCost?.let {
                canvas.drawText("Stromkosten: ${currencyFormat.format(it)}", 20f, yPos, paint)
                yPos += 25f
            }
            workCost?.let {
                canvas.drawText("Arbeitskosten: ${currencyFormat.format(it)}", 20f, yPos, paint)
                yPos += 40f
            }

            paint.isFakeBoldText = true
            paint.textSize = 20f
            totalCost?.let {
                canvas.drawText("Gesamtkosten: ${currencyFormat.format(it)}", 20f, yPos, paint)
            }

            document.finishPage(page)

            val cacheDir = context.externalCacheDir ?: throw IllegalStateException("External cache directory not available")
            val file = File(cacheDir, "Kostenaufstellung.pdf")

            FileOutputStream(file).use { fos ->
                document.writeTo(fos)
            }
            document.close()
            
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        }

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "application/pdf"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "PDF teilen via"))

    } catch (e: Exception) {
        e.printStackTrace()
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Fehler beim Erstellen oder Teilen der PDF-Datei.", Toast.LENGTH_LONG).show()
        }
    }
}
