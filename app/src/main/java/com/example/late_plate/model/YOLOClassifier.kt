import coil3.Bitmap
import com.example.late_plate.model.Classification

interface YOLOClassifier{
    fun classify(bitmap: Bitmap):List<Classification>
}