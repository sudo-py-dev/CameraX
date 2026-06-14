package com.camerax.presentation.ui.scanner

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.camerax.R
import com.camerax.domain.model.BarcodeContentType
import com.camerax.domain.model.ScanResult

@Composable
fun ScanResultSheet(
    result: ScanResult?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (result == null) return

    val context = LocalContext.current

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp),
    ) {
        // Handle bar
        Spacer(
            modifier =
                Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                    .align(Alignment.CenterHorizontally),
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Type icon + label
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = getContentTypeIcon(result.contentType),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = stringResource(getContentTypeLabel(result.contentType)),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = result.format.name.replace("_", " "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content
        Text(
            text = result.displayValue.ifEmpty { result.rawValue },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 6,
            overflow = TextOverflow.Ellipsis,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp),
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Primary action
            Button(
                onClick = { performPrimaryAction(context, result) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
            ) {
                Text(
                    text = stringResource(getPrimaryActionLabel(result.contentType)),
                    style = MaterialTheme.typography.labelLarge,
                )
            }

            // Copy
            IconButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("scan_result", result.rawValue))
                    Toast.makeText(context, context.getString(R.string.copied), Toast.LENGTH_SHORT).show()
                },
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = stringResource(R.string.copy),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }

            // Share
            IconButton(
                onClick = {
                    val shareIntent =
                        Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, result.rawValue)
                        }
                    context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share)))
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = stringResource(R.string.share),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Dismiss / Scan Again
        Text(
            text = stringResource(R.string.scan_again),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onDismiss() }
                    .padding(12.dp),
        )
    }
}

private fun getContentTypeIcon(type: BarcodeContentType): ImageVector =
    when (type) {
        BarcodeContentType.URL -> Icons.Default.Link
        BarcodeContentType.WIFI -> Icons.Default.Wifi
        BarcodeContentType.CONTACT -> Icons.Default.Person
        BarcodeContentType.EMAIL -> Icons.Default.Email
        BarcodeContentType.PHONE -> Icons.Default.Phone
        BarcodeContentType.SMS -> Icons.Default.Sms
        BarcodeContentType.GEO -> Icons.Default.Map
        else -> Icons.Default.QrCode
    }

private fun getContentTypeLabel(type: BarcodeContentType): Int =
    when (type) {
        BarcodeContentType.URL -> R.string.barcode_type_url
        BarcodeContentType.WIFI -> R.string.barcode_type_wifi
        BarcodeContentType.CONTACT -> R.string.barcode_type_contact
        BarcodeContentType.EMAIL -> R.string.barcode_type_email
        BarcodeContentType.PHONE -> R.string.barcode_type_phone
        BarcodeContentType.SMS -> R.string.barcode_type_sms
        BarcodeContentType.GEO -> R.string.barcode_type_location
        BarcodeContentType.CALENDAR_EVENT -> R.string.barcode_type_event
        BarcodeContentType.PRODUCT -> R.string.barcode_type_product
        BarcodeContentType.TEXT -> R.string.barcode_type_text
        BarcodeContentType.UNKNOWN -> R.string.barcode_type_unknown
    }

private fun getPrimaryActionLabel(type: BarcodeContentType): Int =
    when (type) {
        BarcodeContentType.URL -> R.string.barcode_action_open_url
        BarcodeContentType.PHONE -> R.string.barcode_action_call
        BarcodeContentType.EMAIL -> R.string.barcode_action_email
        BarcodeContentType.SMS -> R.string.barcode_action_sms
        BarcodeContentType.GEO -> R.string.barcode_action_map
        BarcodeContentType.WIFI -> R.string.barcode_action_connect
        BarcodeContentType.CONTACT -> R.string.barcode_action_add_contact
        else -> R.string.barcode_action_open_url
    }

private fun performPrimaryAction(
    context: Context,
    result: ScanResult,
) {
    try {
        val intent =
            when (result.contentType) {
                BarcodeContentType.URL -> Intent(Intent.ACTION_VIEW, Uri.parse(result.rawValue))
                BarcodeContentType.PHONE -> Intent(Intent.ACTION_DIAL, Uri.parse("tel:${result.rawValue}"))
                BarcodeContentType.EMAIL -> Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${result.rawValue}"))
                BarcodeContentType.SMS -> Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${result.rawValue}"))
                BarcodeContentType.GEO -> Intent(Intent.ACTION_VIEW, Uri.parse(result.rawValue))
                else -> Intent(Intent.ACTION_VIEW, Uri.parse(result.rawValue))
            }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, context.getString(R.string.no_app_found), Toast.LENGTH_SHORT).show()
    }
}
