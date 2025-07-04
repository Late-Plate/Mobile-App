package com.example.late_plate.model

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import coil3.Bitmap
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import kotlin.math.min

class ImageAnalyzer(
    var score:String,
    speed:String,
    val classifier: TfliteClassifier,
    val onResult: (List<Classification>) -> Unit
) : ImageAnalysis.Analyzer {
    private var frameCount: Int = 0
    private var frameSkip:Int=when (speed) {
        "Fast" -> 5
        "Normal" -> 10
        "Slow" -> 15
        else -> 0
    }
    private val threadCount = Runtime.getRuntime().availableProcessors()
    private val executor = Executors.newFixedThreadPool(threadCount)
    @Volatile
    private var isAnalyzing = false

    override fun analyze(image: ImageProxy) {
        if (frameCount % frameSkip == 0 && !isAnalyzing) {
            isAnalyzing = true
            val bitmap = image.toBitmap().crop()

            executor.execute {
                try {
                    val byteBuffer = bitmap.convertBitmapToByteBuffer(640)
                    val results = classifier.classify(byteBuffer, score)
                    onResult(results)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    isAnalyzing = false
                }
            }
        }

        frameCount++
        image.close()
    }
}

fun Bitmap.crop(): Bitmap {
    val size = min(width, height)
    val xStart = (width - size) / 2
    val yStart = (height - size) / 2
    val croppedBitmap = Bitmap.createBitmap(this, xStart, yStart, size, size)
    return croppedBitmap
}

fun Bitmap.convertBitmapToByteBuffer(targetSize: Int): ByteBuffer {
    val tensorImage = TensorImage.fromBitmap(this)

    val imageProcessor = ImageProcessor.Builder()
        .add(ResizeOp(targetSize, targetSize, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
        .add(NormalizeOp(0f, 255f))
        .build()

    val processedTensorImage = imageProcessor.process(tensorImage)

    val tensorBuffer = processedTensorImage.tensorBuffer
    return tensorBuffer.buffer
}