package com.camerax.presentation.ui.util

import java.text.DecimalFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

object FormatUtils {
    fun formatFileSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB")
        val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
        val index = digitGroups.coerceAtMost(units.lastIndex)
        val formatter = DecimalFormat("#,##0.#")
        return "${formatter.format(bytes / Math.pow(1024.0, index.toDouble()))} ${units[index]}"
    }

    fun formatDuration(ms: Long): String {
        val totalSeconds = ms / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0) {
            String.format(Locale.ROOT, "%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.ROOT, "%02d:%02d", minutes, seconds)
        }
    }

    fun formatTimestamp(
        timestampMs: Long,
        locale: Locale = Locale.getDefault(),
    ): String {
        val instant = Instant.ofEpochMilli(timestampMs)
        val formatter =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withLocale(locale)
                .withZone(ZoneId.systemDefault())
        return formatter.format(instant)
    }

    fun formatDate(
        timestampMs: Long,
        locale: Locale = Locale.getDefault(),
    ): String {
        val instant = Instant.ofEpochMilli(timestampMs)
        val formatter =
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                .withLocale(locale)
                .withZone(ZoneId.systemDefault())
        return formatter.format(instant)
    }

    fun formatRecordingTimer(elapsedMs: Long): String {
        val totalSeconds = elapsedMs / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.ROOT, "%02d:%02d", minutes, seconds)
    }
}
