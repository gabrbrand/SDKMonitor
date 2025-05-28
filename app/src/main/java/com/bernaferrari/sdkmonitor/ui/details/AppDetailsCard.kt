package com.bernaferrari.sdkmonitor.ui.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bernaferrari.sdkmonitor.domain.model.AppDetails
import com.bernaferrari.sdkmonitor.extensions.apiToVersion
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AppDetailsCard(
    appDetails: AppDetails,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Application Details",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            DetailRow(title = "Package Name", value = appDetails.packageName)
            DetailRow(title = "Target SDK", value = "${appDetails.targetSdk} (${appDetails.targetSdk.apiToVersion()})")
            DetailRow(title = "Min SDK", value = "${appDetails.minSdk} (${appDetails.minSdk.apiToVersion()})")
            DetailRow(title = "Version", value = "${appDetails.versionName} (${appDetails.versionCode})")
            DetailRow(
                title = "Last Update", 
                value = appDetails.lastUpdateTime
            )
            DetailRow(
                title = "App Size", 
                value = formatSize(appDetails.size)
            )
        }
    }
}

@Composable
fun DetailRow(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.6f)
        )
    }
}

private fun formatDate(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        "Unknown"
    }
}

private fun formatSize(sizeInBytes: Long): String {
    val kb = 1024
    val mb = kb * 1024
    val gb = mb * 1024

    return when {
        sizeInBytes < kb -> "$sizeInBytes B"
        sizeInBytes < mb -> String.format("%.1f KB", sizeInBytes.toFloat() / kb)
        sizeInBytes < gb -> String.format("%.1f MB", sizeInBytes.toFloat() / mb)
        else -> String.format("%.1f GB", sizeInBytes.toFloat() / gb)
    }
}
