package com.dd2d.talkingrecipe2.ui.theme

import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.TextUnit

@Composable
fun textFieldColor(
    backgroundColor: Color = Color.Transparent,
    removeIndicator: Boolean = false
) = TextFieldDefaults
    .colors(
        focusedIndicatorColor = if(removeIndicator) Color.Transparent else MainColor,
        unfocusedIndicatorColor = if(removeIndicator) Color.Transparent else MainText,
        disabledIndicatorColor = if(removeIndicator) Color.Transparent else HintText,

        errorIndicatorColor = if(removeIndicator) Color.Transparent else MainColor,
        errorContainerColor = backgroundColor,


        focusedContainerColor = backgroundColor,
        unfocusedContainerColor = backgroundColor,

        selectionColors = TextSelectionColors(handleColor = MainColor, backgroundColor = SubColor),

        cursorColor = MainColor,

        unfocusedPlaceholderColor = HintText,
        focusedPlaceholderColor = MainText,
    )

@Composable
fun textFieldStyle(size: TextUnit) = TextStyle(
    color = MainText,
    fontSize = size,
    fontFamily = kopupFontFamily,
    fontWeight = FontWeight.Medium,
    baselineShift = BaselineShift.None
)