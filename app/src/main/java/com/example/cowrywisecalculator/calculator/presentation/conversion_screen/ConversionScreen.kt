package com.example.cowrywisecalculator.calculator.presentation.conversion_screen

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme

import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.cowrywisecalculator.calculator.presentation.theme.CowrywiseCalculatorTheme
import com.example.cowrywisecalculator.R
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Dp
import com.example.cowrywisecalculator.calculator.presentation.Components.AnimatedDots
import kotlinx.coroutines.delay


private const val TAG = "ConversionScreen"

@Composable
fun ConversionScreenRoot(
    modifier: Modifier = Modifier, viewModel: ConversionViewModel = hiltViewModel()
) {
    val state = viewModel.conversionScreenState.collectAsStateWithLifecycle()

    ConversionScreen(onBaseCurrencyChanged = {
        viewModel.setBaseCurrency(it)
        viewModel.getFlag(it, false)
    },
        onConversionCurrencyChanged = {
            viewModel.setConversionCurrency(it)
            viewModel.getFlag(it, true)
        },
        onBaseAmountChanged = { viewModel.setBaseAmount(it) },
        modifier = modifier.fillMaxSize(),
        state = state.value,
        onButtonClick = {
            viewModel.convert(
                baseCurrency = state.value.baseCurrency,
                conversionCurrency = state.value.conversionCurrency,
                baseAmount = state.value.baseAmount
            )
        })


}

@Composable

fun ConversionScreen(
    modifier: Modifier = Modifier,
    state: ConversionScreenState,
    onBaseCurrencyChanged: (String) -> Unit = {},
    onConversionCurrencyChanged: (String) -> Unit = {},
    onBaseAmountChanged: (String) -> Unit = {},
    onButtonClick: () -> Unit = {},

    ) {

    var showCurrencyDropDown by remember { mutableStateOf(true) }
    val textFieldFocusRequester = remember { FocusRequester() }
    val dropDownInteractionSource  = remember { MutableInteractionSource() }


    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painterResource(R.drawable.img),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Column {
                var showAccountPrompt by remember { mutableStateOf(false) }

                Text(text = "Sign up",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .clickable { showAccountPrompt = true }

                        )
                if (showAccountPrompt) {
                    UnavailabilityText(text = "You don't need to sign up",
                        exit = { showAccountPrompt = false })
                }
            }

        }
        Column {


            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                        append("Calculate Currency")
                    }
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append(".")
                    }

                },

                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(vertical = 48.dp)
                    .fillMaxWidth(0.65f),
                fontSize = 44.sp
            )
            ConversionAmountTab(
                amount = state.baseAmount,
                readOnly = false,
                onValueChange = { onBaseAmountChanged(it) },
                currency = state.baseCurrency,
                modifier = Modifier
                    .padding(bottom = 20.dp)

                )
            ConversionAmountTab(
                readOnly = true,
                amount = state.conversionAmount,
                currency = state.conversionCurrency,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CurrencyTypeTab(
                    clickerIconModifier = Modifier
                        .focusable(interactionSource = dropDownInteractionSource)
                        .focusRequester(textFieldFocusRequester)
                        .onFocusChanged {
                            if (it.isFocused) {
                                Log.d(TAG, "onFocusChanged: card responds to focus ")
                                showCurrencyDropDown = true
                            }
                            if (!it.isFocused) {
                                Log.d(TAG, "onFocusChanged: card loses focus ")
                                showCurrencyDropDown = false
                            }
                        },
                    dropDownFocusRequester = textFieldFocusRequester,
                    symbol = state.baseCurrency,
                    modifier = Modifier.fillMaxWidth(0.4f),
                    imageurl = state.baseImage,
                    symbols = state.symbols,
                    onsymbolClicked = { onBaseCurrencyChanged(it) },
                    externalControl = showCurrencyDropDown, dropDownInteractionSource = dropDownInteractionSource


                )
                Image(
                    modifier = Modifier
                        .size(24.dp)
                        .weight(0.3f),
                    painter = painterResource(id = R.drawable.conversion_image),
                    contentDescription = null
                )
                CurrencyTypeTab(
                    symbol = state.conversionCurrency,
                    modifier = Modifier.fillMaxWidth(0.66666f),
                    imageurl = state.conversionImage,
                    symbols = state.symbols,
                    onsymbolClicked = { onConversionCurrencyChanged(it) },
                    externalControl = showCurrencyDropDown,
                    dropDownFocusRequester = textFieldFocusRequester,
                    clickerIconModifier = Modifier

                )
            }
            Box() {
                Button(
                    onClick = onButtonClick,
                    shape = RoundedCornerShape(10),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Convert")
                }
                if(state.showLoading){
                    AnimatedDots(modifier = Modifier.align(Alignment.Center))
                }
            }
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.padding(end = 4.dp),
                    text = "Mid-market exchange rate @-- UTC",
                    color = MaterialTheme.colorScheme.secondary,
                    style = TextStyle(textDecoration = TextDecoration.Underline)
                )
                Card(modifier = Modifier.size(20.dp), shape = RoundedCornerShape(100)) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(
                            "!", modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }

        }
    }

}

@Composable
fun ConversionAmountTab(
    readOnly: Boolean,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = {},
    currency: String = "Usd",
    amount: String = ""

) {

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .background(color = Color.Transparent)
        ) {

            TextField(
                placeholder = { Text(text = "0.0", color = MaterialTheme.colorScheme.outline) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
                ),
                value = amount,
                readOnly = readOnly,
                onValueChange = {
                    onValueChange(it)
                },
                modifier = Modifier
                    .background(color = Color.Transparent)
                    .onFocusChanged {
                        if (it.isFocused) {
                            Log.d(TAG, "onFocusChanged: text field has focus ")
                        } else {
                            Log.d(TAG, "onFocusChanged: text field has no focus ")
                        }
                    },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = colorScheme.onSurface,
                    unfocusedTextColor = colorScheme.onSurface,
                    focusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedLabelColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(text = currency)
        }

    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyTypeTab(
    modifier: Modifier = Modifier,
    clickerIconModifier: Modifier,
    symbol: String,
    imageurl: String,
    dropDownFocusRequester: FocusRequester ,
    dropDownInteractionSource: MutableInteractionSource = MutableInteractionSource(),
    symbols: List<String>,
    onsymbolClicked: (String) -> Unit = {},
    externalControl: Boolean,

    ) {

    var internalDropdownControl by rememberSaveable { mutableStateOf(false) }

    val showSymbols by remember(externalControl, internalDropdownControl) {
        derivedStateOf { externalControl && internalDropdownControl }
        
    }



    var rotation by rememberSaveable { mutableFloatStateOf(0f) }
    val animatedRotation = animateFloatAsState(targetValue = rotation)
   val focusManager =  LocalFocusManager.current
    var shouldRequestFocus by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier, onExpandedChange = { }, expanded = showSymbols
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .border(
                    width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(16)
                )
                .padding(8.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(imageurl).crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(24.dp)
                    .background(colorScheme.background),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.baseline_downloading_24),
                error = painterResource(R.drawable.baseline_error_outline_24)
            )
            Text(text = symbol)
            Image(
                painter = painterResource(id = R.drawable.baseline_navigate_next_24),
                contentDescription = null,
                modifier = clickerIconModifier
                    .size(24.dp)
                    .clickable(
                        interactionSource = dropDownInteractionSource,
                        indication = rememberRipple()

                    ) {

                        rotation = if (rotation == 0f) {
                          shouldRequestFocus = true
                            90f
                        } else {
                            dropDownFocusRequester.freeFocus()
                            0f
                        }
                        internalDropdownControl = !internalDropdownControl

                    }
                    .rotate(animatedRotation.value))

        }

        LaunchedEffect(shouldRequestFocus) {
            if (shouldRequestFocus) {
                Log.d(TAG, "CurrencyTypeTab: requesting focus")
                focusManager.clearFocus(true)
                delay(100) // Give time for clearFocus to complete
                val request = dropDownFocusRequester.requestFocus()
                Log.d(TAG, "CurrencyTypeTab: focus requested, answer is $request")
                shouldRequestFocus = false
            }
        }

        ExposedDropdownMenu(modifier = Modifier.heightIn(max = 200.dp),
            expanded = showSymbols,
            onDismissRequest = { }) {
            symbols.forEach {
                DropdownMenuItem(text = { Text(text = it, modifier = Modifier.fillMaxWidth()) },
                    onClick = {
                        onsymbolClicked(it)
                        internalDropdownControl = false
                    })

            }
        }
    }


}


@Composable
fun UnavailabilityText(
    modifier: Modifier = Modifier,
    text: String,
    exit: () -> Unit,


    ) {
    var vanishText by rememberSaveable { mutableStateOf(false) }
    var animate by remember { mutableStateOf(false) }


    // Animation for the "not available" text
    val animatedScale by animateFloatAsState(

        targetValue = if (animate) 1.5f else 1f, animationSpec = tween(
            durationMillis = 1000, easing = FastOutSlowInEasing
        ), finishedListener = {
            Log.d(TAG, "finished listener called")

            // Auto-hide after showing
            vanishText = true

        })

    val animatedAlpha by animateFloatAsState(targetValue = if (vanishText) 0f else 1f,
        animationSpec = tween(
            durationMillis = 500, easing = LinearEasing
        ),
        finishedListener = { exit() })

    LaunchedEffect(Unit) {
        animate = true
    }

    // Main clickable content
    Text(
        modifier = modifier
            .width(80.dp)

            .graphicsLayer {
                Log.d(TAG, "animated scale is $animatedScale")
                scaleX = animatedScale
                scaleY = animatedScale
                alpha = if (vanishText) animatedAlpha else 1f
            },
        text = text,
        color = colorScheme.tertiary,
        textAlign = TextAlign.Center,
        lineHeight = 16.sp
    )


}





@Preview
@Composable
fun ConversionScreenPreview() {
    CowrywiseCalculatorTheme(darkTheme = true, dynamicColor = false) {
        ConversionScreen(state = ConversionScreenState())
    }
}