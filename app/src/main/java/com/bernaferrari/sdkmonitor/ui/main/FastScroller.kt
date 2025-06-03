package com.bernaferrari.sdkmonitor.ui.main

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.bernaferrari.sdkmonitor.domain.model.AppFilter
import com.bernaferrari.sdkmonitor.domain.model.AppVersion
import com.bernaferrari.sdkmonitor.ui.components.GenericFastScroller

@Composable
fun FastScroller(
    modifier: Modifier = Modifier,
    apps: List<AppVersion>,
    listState: LazyListState,
    appFilter: AppFilter,
    onLetterSelected: (String) -> Unit,
    onScrollFinished: () -> Unit,
    onInteractionStart: () -> Unit = {}
) {
    // Create letter to index mapping for the apps
    val letterToIndexMap = remember(apps, appFilter, apps.hashCode()) {
        val mapping = mutableMapOf<String, Int>()

        // Group apps by first letter to match the LazyColumn structure
        val groupedApps = apps.groupBy {
            val firstChar = it.title.firstOrNull()?.uppercaseChar()
            if (firstChar?.isLetter() == true) {
                firstChar.toString()
            } else {
                "#"
            }
        }.toSortedMap()

        var currentIndex = 0
        groupedApps.forEach { (letter, appsInSection) ->
            mapping[letter] = currentIndex
            // Move index past header + apps in this section
            currentIndex += 1 + appsInSection.size
        }
        mapping
    }

    GenericFastScroller(
        items = apps,
        listState = listState,
        getIndexKey = { it.title },
        letterToIndexMap = letterToIndexMap,
        modifier = modifier,
        onLetterSelected = onLetterSelected,
        onScrollFinished = onScrollFinished,
        onInteractionStart = onInteractionStart
    )
}
