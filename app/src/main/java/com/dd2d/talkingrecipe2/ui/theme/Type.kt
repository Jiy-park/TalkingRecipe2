package com.dd2d.talkingrecipe2.ui.theme

import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.dd2d.talkingrecipe2.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

val kopupFontFamily = FontFamily(
    Font(resId = R.font.kopup_d_bold, weight = FontWeight.Bold, style = FontStyle.Normal),
    Font(resId = R.font.kopup_d_medium, weight = FontWeight.Medium, style = FontStyle.Normal),
    Font(resId = R.font.kopup_d_light, weight = FontWeight.Light, style = FontStyle.Normal),
)

val MapleFontFamily = FontFamily(
    Font(resId = R.font.maple_bold, weight = FontWeight.Bold, style = FontStyle.Normal)
)


/** Text 뷰. font = Kopup */
@Composable
fun kotex(
    modifier: Modifier = Modifier,
    text: String = "",
    weight: FontWeight = FontWeight.Light,
    color: Color = MainText,
    size: TextUnit = 15.sp,
    maxLine: Int = 1,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    align: TextAlign = TextAlign.Start,
){
    Text(
        text = text,
        fontFamily = kopupFontFamily,
        fontWeight = weight,
        color = color,
        textAlign = align,
        fontSize = size,
        maxLines = maxLine,
        overflow = overflow,
        modifier = modifier,
    )
}

