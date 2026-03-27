package com.example.a3dkostenrechner

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

suspend fun createAndSharePdf(
    context: Context,
    projectName: String?,
    totalCost: Float?,
    materialCost: Float?,
    machineCost: Float?,
    electricityCost: Float?,
    workCost: Float?,
    profit: Float? = 0f
) {
    try {
        val uri = withContext(Dispatchers.IO) {
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint()
            val currencyFormat = NumberFormat.getCurrencyInstance()
            val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

            paint.color = Color.rgb(240, 240, 240)
            canvas.drawRect(0f, 0f, 595f, 100f, paint)
            
            paint.color = Color.BLACK
            paint.textSize = 28f
            paint.isFakeBoldText = true
            canvas.drawText("3dkrv", 40f, 60f, paint)
            
            paint.textSize = 14f
            paint.isFakeBoldText = false
            paint.color = Color.GRAY
            canvas.drawText("Präzise 3D-Druck Kalkulation", 130f, 60f, paint)

            paint.textSize = 12f
            paint.color = Color.DKGRAY
            canvas.drawText("Erstellt am: ${dateFormat.format(Date())}", 40f, 85f, paint)

            var yPos = 160f
            paint.color = Color.BLACK
            paint.textSize = 22f
            paint.isFakeBoldText = true
            canvas.drawText(projectName ?: "Kostenaufstellung", 40f, yPos, paint)
            yPos += 50f

            fun drawRow(label: String, value: String, canvas: Canvas, y: Float) {
                val p = Paint().apply { textSize = 14f; color = Color.BLACK }
                canvas.drawText(label, 40f, y, p)
                canvas.drawText(value, 400f, y, p)
                val linePaint = Paint().apply { color = Color.LTGRAY; strokeWidth = 1f }
                canvas.drawLine(40f, y + 8f, 550f, y + 8f, linePaint)
            }

            materialCost?.let { drawRow("Materialkosten", currencyFormat.format(it), canvas, yPos); yPos += 35f }
            machineCost?.let { drawRow("Maschinenkosten", currencyFormat.format(it), canvas, yPos); yPos += 35f }
            electricityCost?.let { drawRow("Stromkosten", currencyFormat.format(it), canvas, yPos); yPos += 35f }
            workCost?.let { drawRow("Arbeitskosten", currencyFormat.format(it), canvas, yPos); yPos += 35f }
            if (profit != null && profit > 0) { drawRow("Gewinnaufschlag", currencyFormat.format(profit), canvas, yPos); yPos += 35f }

            yPos += 20f
            paint.color = Color.rgb(227, 242, 253)
            canvas.drawRect(40f, yPos - 35f, 550f, yPos + 15f, paint)
            paint.color = Color.BLACK
            paint.textSize = 18f
            paint.isFakeBoldText = true
            totalCost?.let {
                canvas.drawText("GESAMTKOSTEN (Netto)", 55f, yPos, paint)
                canvas.drawText(currencyFormat.format(it), 400f, yPos, paint)
            }

            paint.textSize = 10f
            paint.isFakeBoldText = false
            paint.color = Color.GRAY
            canvas.drawText("Dieses Dokument wurde mit der 3dkrv App erstellt.", 40f, 800f, paint)

            document.finishPage(page)
            val file = File(context.cacheDir, "3dkrv_Kalkulation.pdf")
            FileOutputStream(file).use { fos -> document.writeTo(fos) }
            document.close()
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "application/pdf"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooserIntent = Intent.createChooser(shareIntent, "PDF versenden")
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooserIntent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

suspend fun exportInventoryPdf(context: Context, spools: List<Spool>) {
    try {
        val uri = withContext(Dispatchers.IO) {
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint()
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

            // Header
            paint.color = Color.rgb(240, 240, 240)
            canvas.drawRect(0f, 0f, 595f, 80f, paint)
            paint.color = Color.BLACK
            paint.textSize = 24f
            paint.isFakeBoldText = true
            canvas.drawText("3dkrv - LAGERBESTAND", 40f, 50f, paint)
            paint.textSize = 12f
            paint.isFakeBoldText = false
            canvas.drawText("Stand: ${dateFormat.format(Date())}", 40f, 70f, paint)

            var yPos = 120f
            
            // Tabellenkopf
            paint.isFakeBoldText = true
            paint.textSize = 12f
            canvas.drawText("Material", 40f, yPos, paint)
            canvas.drawText("Farbe", 180f, yPos, paint)
            canvas.drawText("Restgewicht", 350f, yPos, paint)
            canvas.drawText("Gesamt", 480f, yPos, paint)
            yPos += 10f
            canvas.drawLine(40f, yPos, 550f, yPos, paint)
            yPos += 25f

            paint.isFakeBoldText = false
            spools.sortedBy { it.materialName }.forEach { spool ->
                if (yPos > 780f) { /* Neue Seite logik hier falls nötig */ }
                canvas.drawText(spool.materialName, 40f, yPos, paint)
                canvas.drawText(spool.color, 180f, yPos, paint)
                canvas.drawText("${spool.remainingWeight}g", 350f, yPos, paint)
                canvas.drawText("${spool.initialWeight}g", 480f, yPos, paint)
                yPos += 25f
                canvas.drawLine(40f, yPos - 5f, 550f, yPos - 5f, Paint().apply { color = Color.LTGRAY })
            }

            document.finishPage(page)
            val file = File(context.cacheDir, "3dkrv_Lagerbestand.pdf")
            FileOutputStream(file).use { fos -> document.writeTo(fos) }
            document.close()
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "application/pdf"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooserIntent = Intent.createChooser(shareIntent, "Bestand teilen")
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooserIntent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
