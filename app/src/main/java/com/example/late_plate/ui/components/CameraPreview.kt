import android.view.MotionEvent
import androidx.camera.core.FocusMeteringAction
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.FlashOff
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.late_plate.ui.components.CustomCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraPreview(controller: LifecycleCameraController, modifier: Modifier = Modifier) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var exposureCompensation by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    var isFlashOn by remember { mutableStateOf(false) }
    CustomCard(modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f), contentPadding = 0) {
        Box(
            modifier = modifier
                .aspectRatio(1f)
                .clipToBounds()
                .clip(RoundedCornerShape(16.dp))
        ) {

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    PreviewView(context).apply {
                        this.controller = controller
                        controller.bindToLifecycle(lifecycleOwner)

                        setOnTouchListener { view, event ->
                            if (event.action == MotionEvent.ACTION_DOWN) {
                                val previewView = view as PreviewView
                                val meteringPointFactory = previewView.meteringPointFactory
                                val meteringPoint =
                                    meteringPointFactory.createPoint(event.x, event.y)

                                val focusAction = FocusMeteringAction.Builder(meteringPoint).build()
                                controller.cameraControl?.startFocusAndMetering(focusAction)

                                view.performClick()
                            }
                            true
                        }
                    }
                }

            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .rotate(90f)
            ) {
                IconButton(modifier = Modifier.rotate(-90f),
                    onClick = {
                        isFlashOn = !isFlashOn
                        controller.cameraControl?.enableTorch(isFlashOn)
                    }) {
                    Icon(
                        if (!isFlashOn) Icons.Outlined.FlashOff else Icons.Filled.FlashOn,
                        contentDescription = null,
                        tint = if (!isFlashOn) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(Modifier.width(8.dp))
                Slider(
                    modifier = Modifier.rotate(180f),
                    value = exposureCompensation.toFloat(),
                    onValueChange = { newValue ->
                        exposureCompensation = newValue.toInt()
                        coroutineScope.launch {
                            controller.cameraControl?.setExposureCompensationIndex(
                                exposureCompensation
                            )
                        }
                    },
                    valueRange = -10f..10f,
                    colors = SliderDefaults.colors(
                        activeTrackColor = lerp(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.onPrimary,
                            (exposureCompensation + 10) / 20f
                        ),
                        inactiveTrackColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    thumb = {
                        Icon(
                            imageVector = Icons.Filled.WbSunny,
                            contentDescription = null,
                            tint = lerp(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.onPrimary,
                                (exposureCompensation + 10) / 20f
                            )
                        )
                    }
                )

            }
        }
    }
}

