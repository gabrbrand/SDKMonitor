package com.bernaferrari.sdkmonitor.ui.settings

import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.bernaferrari.sdkmonitor.R

@Composable
fun AboutDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val maxHeight = (configuration.screenHeightDp * 0.7f).dp
    val minHeight = 200.dp

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(28.dp),
        text = {
            Column(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .heightIn(min = minHeight, max = maxHeight)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(R.string.designed_developed_by) + "\n")
                        pushStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        append(stringResource(R.string.bernardo_ferrari))
                        pop()
                    },
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.privacy_first),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = stringResource(R.string.privacy_description),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    maxItemsInEachRow = 4,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(),
                ) {
                    SocialLink("GitHub", R.drawable.github_logo) {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                "https://github.com/bernaferrari".toUri()
                            )
                        )
                    }
                    SocialLink("X", R.drawable.x_logo) {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                "https://x.com/bernaferrari".toUri()
                            )
                        )
                    }
                    SocialLink("Reddit", R.drawable.reddit_logo) {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                "https://www.reddit.com/user/bernaferrari".toUri()
                            )
                        )
                    }
                    SocialLink("LinkedIn", R.drawable.linkedin_logo) {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                "https://www.linkedin.com/in/bernaferrari".toUri()
                            )
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.open_source_project),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clickable {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    "https://github.com/bernaferrari/SDKMonitor".toUri()
                                )
                            )
                        }
                        .animateContentSize()
                )
            }
        },
        confirmButton = {
            ElevatedButton(
                onClick = {
                    val email = "bernaferrari2+sdk@gmail.com"
                    val emailIntent =
                        Intent(Intent.ACTION_SENDTO, "mailto:$email".toUri()).apply {
                            putExtra(Intent.EXTRA_SUBJECT, "SDK Monitor help")
                        }
                    context.startActivity(Intent.createChooser(emailIntent, "Contact"))
                },
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(stringResource(R.string.contact))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(stringResource(R.string.dismiss))
            }
        }
    )
}

@Composable
private fun SocialLink(
    text: String,
    @DrawableRes iconRes: Int,
    onClick: () -> Unit
) {
    FilledIconButton(
        onClick = onClick,
        modifier = Modifier.animateContentSize(),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary
        ),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
//            Text(
//                text = text,
//                style = MaterialTheme.typography.labelLarge
//            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AboutDialogPreview() {
    AboutDialog(
        onDismiss = {}
    )
}
