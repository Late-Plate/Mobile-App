package com.example.late_plate.ui.components
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.late_plate.R


@Composable
fun OnlineImageCard(modifier: Modifier = Modifier, imageUrl: String,onClick:(()->Unit)?=null) {
    CustomCard(modifier = modifier, contentPadding = 0,onClick=onClick) {
        val painter = rememberAsyncImagePainter(model = imageUrl)
        val painterState = painter.state
        if (painterState is AsyncImagePainter.State.Error) {
            ErrorImage(modifier = Modifier)
        } else {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun OfflineImageCard(modifier: Modifier = Modifier, imageRes: Int?,onClick:(()->Unit)?=null) {
    CustomCard(modifier = modifier, contentPadding = 0,onClick=onClick) {
        if (imageRes == null) {
            ErrorImage(modifier = Modifier)
        } else {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun ErrorImage(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.fillMaxSize().offset(y = 6.dp),
        painter = painterResource(id = R.drawable.default_image),
        contentDescription = "Error Image",
        contentScale = ContentScale.Crop,
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
    )
}
