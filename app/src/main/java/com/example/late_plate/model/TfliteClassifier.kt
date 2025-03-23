package com.example.late_plate.model
import YOLOClassifier
import android.content.Context
import coil3.Bitmap
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage

class TfliteClassifier(
    val context: Context, val threshold: Float = 0.0001f,val maxResults:Int=5,val model:String="best_float32.tflite"
) :YOLOClassifier{

    private var classifier:ObjectDetector?=null

    private fun setupClassifier(){
        val baseOptions = BaseOptions.builder().setNumThreads(2).build()
        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setBaseOptions(baseOptions)
            .setMaxResults(maxResults)
            .setScoreThreshold(threshold)
            .build()
        try {
            classifier=ObjectDetector.createFromFileAndOptions(context,model,options)
        }
        catch (e:IllegalStateException){
            e.printStackTrace()
        }
    }

    override fun classify(bitmap: Bitmap): List<Classification> {
        if(classifier==null){
            setupClassifier()
        }
        val imageProcessor= ImageProcessor.Builder().build()
        val tensorImage=imageProcessor.process(TensorImage.fromBitmap(bitmap))
        val result=classifier?.detect(tensorImage)?: emptyList()
        return result.flatMap { detection ->
            detection.categories.map { category ->
                category.label to category.score
            }
        }
            .groupBy { it.first }
            .map { (label, scores) ->
                Classification(label, scores.maxOf { it.second })
            }
    }
}