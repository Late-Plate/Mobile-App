package com.example.late_plate.model

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import coil3.Bitmap
import kotlin.math.min

class ImageAnalyzer(val classifier: TfliteClassifier,val onResult: (List<Classification>)->Unit):ImageAnalysis.Analyzer {
    var frameCount:Int=0
    override fun analyze(image: ImageProxy) {
        if(frameCount%30==0){
            val bitMap=image.toBitmap().crop(640)
            val results=classifier.classify(bitMap)
            onResult(results)
        }
        frameCount++
        image.close()
    }
}
fun Bitmap.crop(targetSize: Int): Bitmap {
    val size = min(width, height)
    val xStart = (width - size) / 2
    val yStart = (height - size) / 2

    val croppedBitmap = Bitmap.createBitmap(this, xStart, yStart, size, size)
    val bitmap = Bitmap.createScaledBitmap(croppedBitmap, targetSize, targetSize, true)

    return bitmap;
}