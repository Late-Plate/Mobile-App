package com.example.late_plate.model

import android.content.Context
import android.content.res.AssetManager
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import org.tensorflow.lite.Interpreter.Options
import java.util.concurrent.locks.ReentrantLock

class TfliteClassifier(
    val context: Context,
    private val threshold: Float = 0.5f,
    model: String = "best_float16.tflite"
) : YOLOClassifier {


    private val modelBuffer: MappedByteBuffer by lazy { loadModelFile(context.assets, model) }
    private val interpreter: Interpreter by lazy {
        val options = Options()
        options.numThreads = Runtime.getRuntime().availableProcessors()
        Interpreter(modelBuffer, options)
    }
    private val modelLock = ReentrantLock()

    override fun classify(byteBuffer: ByteBuffer): List<Classification> {
        modelLock.lock()
        try {
            val outputArray = Array(1) { Array(71) { FloatArray(8400) } }
            interpreter.run(byteBuffer, outputArray)
            return processYoloOutput(outputArray, ClassNames())
        } finally {
            modelLock.unlock()
        }
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = java.io.FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength)
    }

    private fun processYoloOutput(outputArray: Array<Array<FloatArray>>, classNames: ClassNames): List<Classification> {
        val predictionLength = 71
        val numPredictions = 8400

        val topPredictions = mutableMapOf<String, Classification>()

        for (i in 0 until numPredictions) {
            var maxConfidence = 0f
            var maxClassIndex = -1
            for (j in 4 until predictionLength) {
                val confidence = outputArray[0][j][i]
                if (confidence > maxConfidence) {
                    maxConfidence = confidence
                    maxClassIndex = j - 4
                }
            }
            if (maxConfidence >= threshold) {
                val className = classNames.classNames[maxClassIndex]
                val prediction = Classification(className, maxConfidence)

                if (topPredictions.containsKey(className)) {
                    if (prediction.score > topPredictions[className]!!.score) {
                        topPredictions[className] = prediction
                    }
                } else {
                    topPredictions[className] = prediction
                }
            }
        }

        return topPredictions.values.sortedByDescending { it.score }
    }
    protected fun finalize() {
        interpreter.close()
    }
}
