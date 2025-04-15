package com.example.late_plate.ui.screens.ingredients_detection

import CameraPreview
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.late_plate.FABState
import com.example.late_plate.model.Classification
import com.example.late_plate.model.ImageAnalyzer
import com.example.late_plate.model.TfliteClassifier
import com.example.late_plate.ui.components.CustomButton
import com.example.late_plate.ui.components.DetectedIngredientCard
import com.example.late_plate.ui.components.OfflineImageCard

@Composable
fun IngredientDetectionScreen(context: Context, modifier: Modifier = Modifier, fabState: FABState) {
    fabState.changeFAB(newIcon = Icons.Outlined.CameraAlt, newOnClick = {})


    var classifications by remember { mutableStateOf(emptyList<Classification>()) }
    val analyzer = remember {
        ImageAnalyzer(
            classifier = TfliteClassifier(context = context),
            onResult = { classifications = it })
    }
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_ANALYSIS or CameraController.IMAGE_CAPTURE
            )
            setImageAnalysisAnalyzer(ContextCompat.getMainExecutor(context), analyzer)
        }
    }
    val activity = LocalContext.current as? Activity

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val lifecycleOwner = LocalContext.current as? androidx.lifecycle.LifecycleOwner

    LaunchedEffect(Unit) {
        lifecycleOwner?.lifecycle?.addObserver(
            androidx.lifecycle.LifecycleEventObserver { _, event ->
                if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                    val permissionGranted = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED

                    hasPermission = permissionGranted
                }
            }
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .padding(16.dp)
            .fillMaxSize(),
    )
    {
        if (hasPermission) {
            CameraPreview(controller)
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f), contentAlignment = Alignment.Center
            ) {
                OfflineImageCard(
                    modifier = Modifier.fillMaxSize(),
                    imageRes = null,
                    onClick = null
                )
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    CustomButton(
                        onClick = { launcher.launch(Manifest.permission.CAMERA) },
                        content = {
                            Text(
                                "camera access",
                                Modifier.padding(horizontal = 8.dp),
                                fontSize = 18.sp
                            )
                        }
                    )
                    Text(
                        "open settings", color = MaterialTheme.colorScheme.onPrimary, fontSize = 14.sp,
                        modifier = Modifier.clickable {
                            activity?.let {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", it.packageName, null)
                                }
                                it.startActivity(intent)
                            }
                        }
                    )
                }
            }

        }
        Column(
            modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                ),
            verticalArrangement = Arrangement.Top
        ) {
            classifications.forEach {
                DetectedIngredientCard(modifier = Modifier.padding(vertical = 8.dp), it)
            }
        }
    }

}
