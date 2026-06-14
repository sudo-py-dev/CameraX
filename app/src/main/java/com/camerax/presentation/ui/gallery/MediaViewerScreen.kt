package com.camerax.presentation.ui.gallery

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.camerax.R
import com.camerax.domain.model.MediaItem
import com.camerax.domain.model.MediaType
import com.camerax.presentation.ui.util.FormatUtils
import androidx.media3.common.MediaItem as Media3Item

@Composable
fun MediaViewerScreen(
    mediaId: Long,
    viewModel: GalleryViewModel,
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    var mediaItem by remember { mutableStateOf<MediaItem?>(null) }
    var showInfo by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val exoPlayer =
        remember {
            ExoPlayer.Builder(context).build()
        }

    LaunchedEffect(mediaId) {
        mediaItem = viewModel.getMediaById(mediaId)
        mediaItem?.let { item ->
            if (item.type == MediaType.VIDEO) {
                val media3Item = Media3Item.fromUri(Uri.parse(item.uri))
                exoPlayer.setMediaItem(media3Item)
                exoPlayer.prepare()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    val item = mediaItem
    if (item == null) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Black),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.loading),
                color = Color.White,
            )
        }
        return
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.Black),
    ) {
        if (item.type == MediaType.PHOTO) {
            AsyncImage(
                model =
                    ImageRequest.Builder(context)
                        .data(Uri.parse(item.uri))
                        .crossfade(true)
                        .build(),
                contentDescription = item.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = true
                        setBackgroundColor(android.graphics.Color.BLACK)
                    }
                },
                modifier = Modifier.fillMaxSize(),
            )
        }

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier =
                    Modifier
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f)),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = Color.White,
                )
            }
        }

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MediaActionItem(
                icon = Icons.Default.Share,
                label = stringResource(R.string.share),
                tint = Color.White,
                onClick = {
                    val shareIntent =
                        Intent(Intent.ACTION_SEND).apply {
                            type = if (item.type == MediaType.PHOTO) "image/jpeg" else "video/mp4"
                            putExtra(Intent.EXTRA_STREAM, Uri.parse(item.uri))
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                    context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share)))
                },
            )

            MediaActionItem(
                icon = Icons.Default.Info,
                label = stringResource(R.string.info),
                tint = Color.White,
                onClick = { showInfo = !showInfo },
            )

            MediaActionItem(
                icon = Icons.Default.Delete,
                label = stringResource(R.string.delete),
                tint = MaterialTheme.colorScheme.error,
                onClick = { showDeleteConfirmation = true },
            )
        }

        if (showInfo) {
            Column(
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.8f))
                        .padding(24.dp),
            ) {
                Text(
                    text = stringResource(R.string.details),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                )
                Spacer(modifier = Modifier.height(12.dp))
                InfoRow(stringResource(R.string.name_label), item.name)
                InfoRow(stringResource(R.string.type_label), item.type.name)
                InfoRow(stringResource(R.string.size_label), FormatUtils.formatFileSize(item.sizeBytes))
                InfoRow(stringResource(R.string.date_label), FormatUtils.formatTimestamp(item.timestampMs))
                if (item.durationMs != null) {
                    InfoRow(stringResource(R.string.duration_label), FormatUtils.formatDuration(item.durationMs))
                }
                if (item.width > 0 && item.height > 0) {
                    InfoRow(stringResource(R.string.resolution_label), "${item.width} × ${item.height}")
                }
            }
        }

        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text(stringResource(R.string.confirm_delete_title)) },
                text = { Text(stringResource(R.string.confirm_delete_message)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteMedia(item.id)
                            showDeleteConfirmation = false
                            onNavigateBack()
                        },
                    ) {
                        Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmation = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                },
            )
        }
    }
}

@Composable
private fun MediaActionItem(
    icon: ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true),
                    onClick = onClick,
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = tint,
        )
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.6f),
            modifier = Modifier.weight(0.4f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            modifier = Modifier.weight(0.6f),
        )
    }
}
