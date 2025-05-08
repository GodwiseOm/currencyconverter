package com.example.cowrywisecalculator.presentation.conversion_screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
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
import com.example.cowrywisecalculator.presentation.theme.CowrywiseCalculatorTheme
import com.example.cowrywisecalculator.R
import com.example.cowrywisecalculator.domain.model.Rates

@Composable
fun ConversionScreenRoot(
    modifier:Modifier = Modifier,
    viewModel: ConversionViewModel = hiltViewModel()
) {
    val state = viewModel.conversionScreenState.collectAsStateWithLifecycle()
    ConversionScreen(
        onBaseAmountChanged ={ viewModel.setBaseAmount(it)},
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
    onBaseAmountChanged: (String) -> Unit = {},
    onButtonClick: () -> Unit = {},

    ) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp)
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
            Text(text = "Sign up", color = MaterialTheme.colorScheme.primary, fontSize = 24.sp)
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
                readOnly = false,
                onValueChange = {onBaseAmountChanged(it)},
                currency = state.baseCurrency,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            ConversionAmountTab(
                readOnly = true,

                currency = state.conversionCurrency,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp), horizontalArrangement =
                Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
            ) {
                CurrencyTypeTab(
                    symbol = state.baseCurrency,
                    modifier = Modifier.fillMaxWidth(0.4f),
                    imageurl = state.baseImage,
                    symbols = state.symbols


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
                    symbols = state.symbols


                    )
            }
            Button(
                onClick = onButtonClick,
                shape = RoundedCornerShape(10),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Convert")
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
                            "!",
                            modifier = Modifier.align(Alignment.Center)
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
    currency: String = "Usd"

) {
    var amount by rememberSaveable { mutableDoubleStateOf(0.0) }
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
                value = amount.toString(),
                readOnly = readOnly,
                onValueChange = { amount = it.toDouble()
                                 onValueChange(it)},
                modifier = Modifier.background(color = Color.Transparent),
                colors = TextFieldDefaults.colors(

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

@Composable
fun CurrencyTypeTab(
    modifier: Modifier = Modifier,
    symbol: String,
    imageurl: String,
    symbols: List<String> ,
    onsymbolClicked: (String) -> Unit = {},
    listVisibility: Boolean = false,

    ) {
    var showSymbols by rememberSaveable { mutableStateOf(false) }
    var rotation by rememberSaveable { mutableFloatStateOf(0f) }
    var animatedRotation = animateFloatAsState(targetValue = rotation)
    Box(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color.Gray,
                    shape = RoundedCornerShape(16)
                )
                .padding(8.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(imageurl).crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(100)),
                placeholder = painterResource(R.drawable.baseline_downloading_24),
                error = painterResource(R.drawable.baseline_error_outline_24)
            )
            Text(text = symbol)
            Image(
                painter = painterResource(id = R.drawable.baseline_navigate_next_24),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        showSymbols = !showSymbols
                        if (rotation == 0f) {
                            rotation = 90f
                        } else {
                            rotation = 0f
                        }

                    }
                    .rotate(animatedRotation.value)
            )

        }
        DropdownMenu( modifier = Modifier.height(56.dp),expanded = showSymbols, onDismissRequest = { showSymbols = false }) {
            symbols.forEach {
                DropdownMenuItem(text = { Text(text = it) }, onClick = { onsymbolClicked(it) })

            }
        }
    }
}


@Composable
fun CurrencyList(currencies: List<String>, onclick: (String) -> Unit) {

    val entries = currencies
    LazyColumn {
        items(items = entries, key = { it }) {
            Text(text = it, modifier = Modifier.clickable { onclick(it) })
        }
    }
}

@Preview
@Composable
fun ConversionScreenPreview() {
    CowrywiseCalculatorTheme(darkTheme = false, dynamicColor = false) {
        ConversionScreenRoot()
    }
}