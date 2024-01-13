package com.dd2d.talkingrecipe2.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Modifier

fun Modifier.clickableWithoutRipple(onClick: ()->Unit): Modifier{
    return this.clickable(
        indication = null,
        interactionSource = MutableInteractionSource()
    ) { onClick() }
}