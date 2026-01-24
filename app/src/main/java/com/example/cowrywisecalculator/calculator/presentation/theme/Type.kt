package com.example.cowrywisecalculator.calculator.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.cowrywisecalculator.R

@OptIn(ExperimentalTextApi::class)
val montserrat = FontFamily(
    Font(
        R.font.montserrat_variablefont_wght,
        variationSettings = FontVariation.Settings(FontVariation.weight(700)),
       weight = FontWeight.Bold
    )
)
val golostext = FontFamily(Font(R.font.golostext_variablefont_wght, weight = FontWeight.Bold ) )

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = montserrat, fontWeight = FontWeight.Bold
    ),
//    titleMedium = TextStyle(
//        fontFamily = golostext, fontWeight = FontWeight.Bold
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
