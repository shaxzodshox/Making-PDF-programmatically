package com.shakhzod.pdfreceipt

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.pdf.PdfDocument
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.shakhzod.model.Food
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var layoutList: LinearLayout

    lateinit var bitmap: Bitmap
    lateinit var scaledBitmap: Bitmap

    var pageWidth = 1200

    val prices = intArrayOf(0, 12000, 23000, 30000) //0 for select
    val foodList = listOf<String>("Tanlang", "Burger", "Lavash", "Donar")
    val list: MutableList<Food> = ArrayList()

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        layoutList = findViewById(R.id.layout_list)
        btn_add.setOnClickListener {
            addView()
        }


        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //Ask for permission
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1);
        } else {
            //We have permission!

            bitmap = BitmapFactory.decodeResource(resources, R.drawable.header)
            scaledBitmap = Bitmap.createScaledBitmap(bitmap, pageWidth, 518, false)

            btn_generate.setOnClickListener {
                if (checkValidAndRead()) {

                    val myPdfDocument = PdfDocument()
                    val titlePaint = Paint()
                    val myPaint = Paint()
                    val myPageInfo1: PdfDocument.PageInfo = PdfDocument.PageInfo.Builder(pageWidth, 2010, 1).create()
                    val myPage1 = myPdfDocument.startPage(myPageInfo1)
                    val canvas: Canvas = myPage1.canvas

                    canvas.drawBitmap(scaledBitmap, 0f, 0f, myPaint)

                    myPaint.color = Color.WHITE
                    myPaint.textSize = 35f
                    myPaint.textAlign = Paint.Align.RIGHT
                    canvas.drawText("Tel: +998977398840", pageWidth.toFloat() - 40, 50f, myPaint)

                    titlePaint.textAlign = Paint.Align.CENTER
                    titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    titlePaint.textSize = 65f
                    titlePaint.color = Color.BLACK
                    canvas.drawText("Hisobot:", (pageWidth / 2).toFloat(), 500f, titlePaint)

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

                    var y = 950f
                    var counter = 1
                    var subTotal = 0
                    for (i in list.indices) {
                        var total = 0
                        canvas.drawText("$counter.", 40f, y, myPaint)
                        canvas.drawText(list[i].food, 200f, y, myPaint)
                        canvas.drawText(list[i].price.toString(), 700f, y, myPaint)
                        canvas.drawText(list[i].qty.toString(), 900f, y, myPaint)
                        total = list[i].qty * list[i].price
                        canvas.drawText(total.toString(), 1050f, y, myPaint)
                        y += 100
                        counter++
                        subTotal += total
                    }

                    y += 50
                    canvas.drawLine(680f, y, pageWidth.toFloat() - 20, y, myPaint)
                    y += 50
                    canvas.drawText("Jami", 700f, y, myPaint)
                    canvas.drawText(":", 900f, y, myPaint)
                    myPaint.textAlign = Paint.Align.RIGHT
                    canvas.drawText(subTotal.toString(), pageWidth.toFloat() - 40, y, myPaint)

                    y += 50

                    myPaint.textAlign = Paint.Align.LEFT
                    canvas.drawText("10%", 700f, y, myPaint)
                    canvas.drawText(":", 900f, y, myPaint)
                    myPaint.textAlign = Paint.Align.RIGHT
                    canvas.drawText((subTotal / 100 * 10).toString(), pageWidth.toFloat() - 40, y, myPaint)
                    myPaint.textAlign = Paint.Align.LEFT

                    myPaint.color = Color.rgb(0, 161, 84)
                    y += 50

                    canvas.drawRect(680f, y, pageWidth.toFloat() - 20, y + 100, myPaint)

                    y += 100

                    myPaint.color = Color.WHITE
                    myPaint.textSize = 50f
                    myPaint.textAlign = Paint.Align.LEFT
                    canvas.drawText("To'lov", 700f, y - 30, myPaint)
                    canvas.drawText(":", 900f, y - 30, myPaint)
                    myPaint.textAlign = Paint.Align.RIGHT
                    canvas.drawText((subTotal + (subTotal / 100 * 10)).toString(), pageWidth.toFloat() - 40, y - 30, myPaint)

                    myPdfDocument.finishPage(myPage1)

                    val file = File(Environment.getExternalStorageDirectory(), "/invoice.pdf")
                    try {
                        myPdfDocument.writeTo(FileOutputStream(file))

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    myPdfDocument.close()
                }
            }


//            btn_generate.setOnClickListener {

//            }
        }
    }

    private fun checkValidAndRead(): Boolean {
        var result = true
        for (i in 0 until layoutList.childCount) {
            val view = layoutList.getChildAt(i)
            val etQty = view.findViewById<EditText>(R.id.et_qty)
            val spinner = view.findViewById<AppCompatSpinner>(R.id.spinner)

            if (etQty.text.toString().isNotEmpty() && spinner.selectedItemPosition != 0) {
                val food = Food(spinner.selectedItem.toString(), etQty.text.toString().toInt(), prices[spinner.selectedItemPosition])
                list.add(food)
            } else {
                result = false
            }
        }

        if (list.isEmpty()) {
            Toast.makeText(applicationContext, "Iltimos avval taom kiriting..", Toast.LENGTH_SHORT).show()
        } else if (!result) {
            Toast.makeText(applicationContext, "Ma'lumotlarni to'liq kiriting", Toast.LENGTH_SHORT).show()
        }

        return result
    }

    private fun addView() {
        val view = layoutInflater.inflate(R.layout.row_add_food, null, false)
        val spinner = view.findViewById<AppCompatSpinner>(R.id.spinner)
        val ivClose = view.findViewById<ImageView>(R.id.iv_close)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, foodList)
        spinner.adapter = adapter

        ivClose.setOnClickListener {
            removeView(view)
        }

        layoutList.addView(view)
    }

    private fun removeView(view: View) {
        layoutList.removeView(view)
    }
}