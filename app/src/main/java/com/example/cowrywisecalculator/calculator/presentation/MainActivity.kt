package com.example.cowrywisecalculator.calculator.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.cowrywisecalculator.calculator.presentation.conversion_screen.ConversionScreen
import com.example.cowrywisecalculator.calculator.presentation.conversion_screen.ConversionScreenRoot
import com.example.cowrywisecalculator.calculator.presentation.conversion_screen.ConversionScreenState
import com.example.cowrywisecalculator.calculator.presentation.theme.CowrywiseCalculatorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CowrywiseCalculatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                   ConversionScreenRoot(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CowrywiseCalculatorTheme {
        Greeting("Android")
    }
}