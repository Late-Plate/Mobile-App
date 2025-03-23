import android.content.Context
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.late_plate.model.Classification
import com.example.late_plate.model.ImageAnalyzer
import com.example.late_plate.model.TfliteClassifier

@Composable
fun IngredientDetectionScreen(context: Context, modifier: Modifier = Modifier) {
    var classifications by remember { mutableStateOf(emptyList<Classification>()) }
    val analyzer = remember { ImageAnalyzer(classifier = TfliteClassifier(context=context), onResult = {classifications=it}) }
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_ANALYSIS
            )
            setImageAnalysisAnalyzer(ContextCompat.getMainExecutor(context),analyzer)
        }
    }
    Column(modifier = modifier
        .fillMaxSize()
        .statusBarsPadding()) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)) {
            CameraPreview(controller, modifier = Modifier.fillMaxSize())
        }
        classifications.forEach{
            Text(it.classification)
        }
    }
}