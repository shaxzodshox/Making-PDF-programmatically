package com.shakhzod.pdfreceipt

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.pdf.PdfDocument
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var bitmap: Bitmap
    lateinit var scaledBitmap: Bitmap

    var pageWidth = 1200

    val prices = intArrayOf(0, 12000, 23000, 30000) //0 for select

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            //Ask for permission
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),1);
        }
        else{
            //We have permission!

            bitmap = BitmapFactory.decodeResource(resources, R.drawable.header)
            scaledBitmap = Bitmap.createScaledBitmap(bitmap, pageWidth, 518, false)

            btn_generate.setOnClickListener {
                if(et_username.text.toString().isEmpty() || et_phone.text.toString().isEmpty() || et_qty1.text.toString().isEmpty()
                    || et_qty2.text.toString().isEmpty()){
                    Toast.makeText(applicationContext, "Please fill up all the fields!", Toast.LENGTH_SHORT).show()
                }
                else{
                    val myPdfDocument = PdfDocument()
                    val titlePaint = Paint()
                    val myPaint = Paint()
                    val myPageInfo1: PdfDocument.PageInfo = PdfDocument.PageInfo.Builder(pageWidth, 2010, 1).create()
                    val myPage1 = myPdfDocument.startPage(myPageInfo1)
                    val canvas: Canvas = myPage1.canvas

                    canvas.drawBitmap(scaledBitmap, 0f,0f, myPaint)

                    myPaint.color = Color.WHITE
                    myPaint.textSize = 35f
                    myPaint.textAlign = Paint.Align.RIGHT
                    canvas.drawText("Tel: +998977398840", pageWidth.toFloat() - 40, 50f, myPaint)

                    titlePaint.textAlign = Paint.Align.CENTER
                    titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    titlePaint.textSize = 65f
                    titlePaint.color = Color.BLACK
                    canvas.drawText("Hisobot:", (pageWidth/2).toFloat(), 500f, titlePaint)

                    myPaint.textAlign = Paint.Align.LEFT
                    myPaint.textSize = 35f
                    myPaint.color = Color.BLACK
                    canvas.drawText("Mijoz: ${et_username.text.toString()}", 20f, 590f, myPaint)
                    canvas.drawText("Tel: ${et_phone.text.toString()}", 20f, 640f, myPaint)

                    myPaint.textAlign = Paint.Align.RIGHT

                    canvas.drawText("Buyurtma kodi: ${UUID.randomUUID().toString().substring(0, 5)}", pageWidth.toFloat() - 20f, 590f, myPaint)


                    val date = Date()
                    var dateFormat = SimpleDateFormat("dd/MM/yy")
                    canvas.drawText("Sana: ${dateFormat.format(date)}", pageWidth.toFloat() - 20f, 640f, myPaint)

                    dateFormat = SimpleDateFormat("HH:mm:ss")
                    canvas.drawText("Vaqt: ${dateFormat.format(date)}", pageWidth.toFloat() - 20f, 690f, myPaint)


                    //Lines
                    myPaint.style = Paint.Style.STROKE
                    myPaint.strokeWidth = 2f
                    canvas.drawRect(20f, 780f, pageWidth.toFloat() - 20, 860f, myPaint)


                    myPaint.textAlign = Paint.Align.LEFT
                    myPaint.style = Paint.Style.FILL
                    canvas.drawText("No.", 40f, 830f, myPaint)
                    canvas.drawText("Maxsulot", 200f, 830f, myPaint)
                    canvas.drawText("Narxi", 700f, 830f, myPaint)
                    canvas.drawText("Soni", 900f, 830f, myPaint)
                    canvas.drawText("Jami", 1050f, 830f, myPaint)

                    canvas.drawLine(180f, 790f, 180f, 850f, myPaint)
                    canvas.drawLine(680f, 790f, 680f, 850f, myPaint)
                    canvas.drawLine(880f, 790f, 880f, 850f, myPaint)
                    canvas.drawLine(1030f, 790f, 1030f, 850f, myPaint)

                    var total1 = 0

                    if(spinner1.selectedItemPosition != 0 ){
                        canvas.drawText("1.", 40f, 950f, myPaint)
                        canvas.drawText(spinner1.selectedItem.toString(), 200f, 950f, myPaint)
                        canvas.drawText(prices[spinner1.selectedItemPosition].toString(), 700f, 950f, myPaint)
                        canvas.drawText(et_qty1.text.toString(), 900f, 950f, myPaint)
                        total1 = et_qty1.text.toString().toInt() * prices[spinner1.selectedItemPosition]
                        canvas.drawText(total1.toString(), 1050f, 950f, myPaint)
                    }

                    var total2 = 0

                    if(spinner2.selectedItemPosition != 0 ){
                        canvas.drawText("2.", 40f, 1050f, myPaint)
                        canvas.drawText(spinner2.selectedItem.toString(), 200f, 1050f, myPaint)
                        canvas.drawText(prices[spinner2.selectedItemPosition].toString(), 700f, 1050f, myPaint)
                        canvas.drawText(et_qty2.text.toString(), 900f, 1050f, myPaint)
                        total2 = et_qty2.text.toString().toInt() * prices[spinner2.selectedItemPosition]
                        canvas.drawText(total2.toString(), 1050f, 1050f, myPaint)
                    }


                    val subTotal = total1 + total2
                    canvas.drawLine(680f, 1200f,pageWidth.toFloat() - 20, 1200f, myPaint)
                    canvas.drawText("Jami", 700f, 1250f, myPaint)
                    canvas.drawText(":", 900f, 1250f, myPaint)
                    myPaint.textAlign = Paint.Align.RIGHT
                    canvas.drawText(subTotal.toString(), pageWidth.toFloat() - 40, 1250f, myPaint)

                    myPaint.textAlign = Paint.Align.LEFT
                    canvas.drawText("10%", 700f, 1300f, myPaint)
                    canvas.drawText(":", 900f, 1300f, myPaint)
                    myPaint.textAlign = Paint.Align.RIGHT
                    canvas.drawText((subTotal / 100 * 10).toString(), pageWidth.toFloat() - 40, 1300f, myPaint)
                    myPaint.textAlign = Paint.Align.LEFT

                    myPaint.color = Color.rgb(0, 161, 84)
                    canvas.drawRect(680f, 1350f, pageWidth.toFloat() - 20, 1450f, myPaint)

                    myPaint.color = Color.WHITE
                    myPaint.textSize = 50f
                    myPaint.textAlign = Paint.Align.LEFT
                    canvas.drawText("To'lov", 700f, 1415f, myPaint)
                    canvas.drawText(":", 900f, 1415f, myPaint)
                    myPaint.textAlign = Paint.Align.RIGHT
                    canvas.drawText((subTotal + (subTotal / 100 * 10)).toString(), pageWidth.toFloat() - 40, 1415f, myPaint)

                    myPdfDocument.finishPage(myPage1)

                    val file = File(Environment.getExternalStorageDirectory(), "/invoice.pdf")
                    try {
                        myPdfDocument.writeTo(FileOutputStream(file))

                    }catch (e: IOException){
                        e.printStackTrace()
                    }
                    myPdfDocument.close()
                }
            }
        }
    }
}