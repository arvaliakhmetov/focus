package utils

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun rememberTopSafeArea(): Dp {
    val density = LocalDensity.current
    return with(density){ WindowInsets.safeContent.getTop(density).toDp()}
}