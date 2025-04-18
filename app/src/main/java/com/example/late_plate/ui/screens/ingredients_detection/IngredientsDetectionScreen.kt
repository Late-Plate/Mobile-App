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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.late_plate.model.Classification
import com.example.late_plate.model.ImageAnalyzer
import com.example.late_plate.model.TfliteClassifier
import com.example.late_plate.ui.components.CustomButton
import com.example.late_plate.ui.components.DetectedIngredientCard
import com.example.late_plate.ui.components.ExpandableSelectionCard
import com.example.late_plate.ui.components.OfflineImageCard
import com.example.late_plate.ui.screens.FABState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IngredientDetectionScreen(context: Context, modifier: Modifier = Modifier, fabState: FABState) {
    fabState.changeFAB(newIcon = Icons.Outlined.CameraAlt, newOnClick = {})


    var classifications by remember { mutableStateOf(emptyList<Classification>()) }
    var selectedSpeed by remember { mutableStateOf("Normal") }
    val speeds = listOf("Fast", "Normal", "Slow")
    var selectedScore by remember { mutableStateOf("Medium") }
    val scores = listOf("High", "Medium", "Low")
    val analyzer = remember(selectedScore, selectedSpeed) {
        ImageAnalyzer(
            score = selectedScore,
            speed = selectedSpeed,
            classifier = TfliteClassifier(context = context),
            onResult = { classifications = it }
        )
    }
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_ANALYSIS
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
    val lifecycleOwner = LocalContext.current as? LifecycleOwner

    LaunchedEffect(Unit) {
        lifecycleOwner?.lifecycle?.addObserver(
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
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
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                        "open settings",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable {
                            activity?.let {
                                val intent =
                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = Uri.fromParts("package", it.packageName, null)
                                    }
                                it.startActivity(intent)
                            }
                        }
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            ExpandableSelectionCard(
                options = speeds, label = "speed", selectedOption = selectedSpeed,
                modifier = Modifier.weight(1f),
                onOptionSelected = { selectedSpeed = it }
            )
            ExpandableSelectionCard(
                options = scores, label = "scores", selectedOption = selectedScore,
                modifier = Modifier.weight(1f),
                onOptionSelected = { selectedScore = it }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                ),
            verticalArrangement = Arrangement.Top
        ) {
            if (classifications.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    classifications.forEach { item ->
                        DetectedIngredientCard(modifier = Modifier.padding(bottom = 8.dp), item)
                    }
                }

            } else {
                Text(
                    "detected ingredients will show up here!",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}


