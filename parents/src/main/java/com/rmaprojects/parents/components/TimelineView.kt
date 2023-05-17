package com.rmaprojects.parents.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.pushpal.jetlime.data.JetLimeItemsModel
import com.pushpal.jetlime.data.config.JetLimeViewConfig
import com.pushpal.jetlime.ui.JetLimeView

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TimelineView(
    visibilityValidator: Boolean,
    jetLimesModel: JetLimeItemsModel,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(visible = visibilityValidator) {
        JetLimeView(
            modifier = modifier.fillMaxSize(),
            jetLimeItemsModel = jetLimesModel,
            jetLimeViewConfig = JetLimeViewConfig(
                lineColor = MaterialTheme.colorScheme.primary,
                backgroundColor = Color.Transparent
            )
        )
    }
}