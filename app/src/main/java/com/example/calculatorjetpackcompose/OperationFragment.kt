package com.example.calculatorjetpackcompose

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.example.calculatorjetpackcompose.constants.Constants

class OperationFragment: Fragment() {
    private lateinit var number1: MutableState<String>
    private lateinit var number2: MutableState<String>
    private val zeroInDouble = 0E308
    private lateinit var enableButtonAndTextField: MutableState<Boolean>
    lateinit var operation: MutableState<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            parentFragmentManager
                .beginTransaction()
                .show(this)
                .commit()
        }else{
            if(arguments?.getString(Constants.Operation.message) == null){
                    parentFragmentManager
                        .beginTransaction()
                        .hide(this)
                        .commit()

            }else{
                parentFragmentManager.findFragmentById(R.id.main_fragment_container)?.let {
                    parentFragmentManager
                        .beginTransaction()
                        .show(this)
                        .hide(it)
                        .commit()
            }
        }
       }
        return setContentForOperationFragmentScreen()
    }
    private fun setContentForOperationFragmentScreen(): ComposeView{
        return ComposeView(requireContext()).apply {
            setContent {
                enableButtonAndTextField =
                    if (arguments?.getString(Constants.Operation.message) == null) {
                        rememberSaveable { mutableStateOf(false) }
                    } else {
                        rememberSaveable { mutableStateOf(true) }
                    }
                operation = rememberSaveable {
                    mutableStateOf(
                        arguments?.getString(Constants.Operation.message)
                            ?: getString(R.string.empty_string)
                    )
                }
                OperationScreen()
            }
        }
    }
    @Composable
    fun OperationScreen(){
        val context = LocalContext.current
        number1 = remember {
            mutableStateOf(arguments?.getString(Constants.Number1.message)?:"")
        }
        number2 = remember{
            mutableStateOf(arguments?.getString(Constants.Number2.message)?:"")
        }
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = number1.value,
                onValueChange = {
                    if(it.isNotEmpty()) {
                        if (it.last()
                                .toString() in Constants.Zero.message..Constants.Nine.message
                        ) {
                            if (it.length < 10) {
                                number1.value = it
                            }
                        }
                    }
                    else{
                        number1.value = getString(R.string.empty_string)
                    }
                },
                label = { Text(getString(R.string.enter_a_number))},
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = enableButtonAndTextField.value
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = number2.value,
                onValueChange = {
                    if(it.isNotEmpty()) {
                        if ((it.last()
                                .toString() in Constants.Zero.message..Constants.Nine.message)
                        ) {
                            if (it.length < 10) {
                                number2.value = it
                            }
                        }
                    }
                    else{
                        number2.value = getString(R.string.empty_string)
                    }
                },
                label = { Text(getString(R.string.enter_a_number))},
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = enableButtonAndTextField.value
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                onButtonClick(number1.value,number2.value,context)
            },
            enabled = enableButtonAndTextField.value,
            modifier = Modifier
                .height(60.dp)
                .width(110.dp)
                .clip(RoundedCornerShape(10.dp))) {
                if(operation.value == getString(R.string.empty_string)){
                    Text(getString(R.string.choose_operation),
                    textAlign = TextAlign.Center)
                }
                else {
                    Text(operation.value)
                }
            }
        }
    }
    private fun operation(a: Double, b: Double, operation: String): String {
        return when (operation) {
            Constants.Add.message -> {
                with(a + b) {
                    if (this.isDecimalZero())
                        this.toInt()
                    else
                        this
                }
            }
            Constants.Sub.message -> {
                with(a - b) {
                    if (this.isDecimalZero())
                        this.toInt()
                    else
                        this
                }
            }
            Constants.Mul.message -> {
                with(a * b) {
                    if ((this.isDecimalZero()) && (this < Long.MAX_VALUE.toDouble())) {
                        this.toLong()
                    } else
                        this
                }
            }
            else -> {
                    with(a / b) {
                        if (this.isDecimalZero())
                            this.toInt()
                        else
                            this
                    }
            }
        }.toString()
    }

    private fun Double.isDecimalZero() = when (this % 1) {
        zeroInDouble -> true
        else -> false
    }
    private fun onButtonClick(number1: String,number2: String,context: Context){
        if(!(number1.isEmpty() || number2.isEmpty())){
            if(!(number2 == "0" && operation.value == Constants.Div.message)) {
                setFragmentResult(
                    generateResultString(
                        number1,
                        number2,
                        arguments?.getString(Constants.Operation.message).toString()
                    )
                )
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    hideFragment()
                }
                disableButtonAndEditText()
            }
            else{
                Toast.makeText(context,R.string.non_zero_alert,Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(context,R.string.edit_text_empty_message,Toast.LENGTH_SHORT).show()
        }

    }
    fun disableButtonAndEditText(){
        number1.value = ""
        number2.value = ""
        operation.value = getString(R.string.empty_string)
        enableButtonAndTextField.value = false
        arguments = null
    }
    private fun setFragmentResult(resultString: String){
        val result = Bundle().apply{
            putString(Constants.Result.message,resultString)
        }
        parentFragmentManager.setFragmentResult(Constants.Result.message,result)
    }
    private fun hideFragment(){
            parentFragmentManager.findFragmentById(R.id.main_fragment_container)?.let {
                parentFragmentManager
                    .beginTransaction()
                    .hide(this@OperationFragment)
                    .show(it)
                    .commit()
        }
    }
    private fun generateResultString(number1: String, number2: String, operation: String): String{
         return getString(
            R.string.result_string,
            number1.toLong(),
            operation.operationToSymbol(),
            number2.toLong(),
            operation(number1.toDouble(),number2.toDouble(),arguments?.getString(Constants.Operation.message).toString())
        )
    }
    private fun String.operationToSymbol(): String = when(this){
        Constants.Add.message -> getString(R.string.plus)
        Constants.Sub.message -> getString(R.string.minus)
        Constants.Mul.message -> getString(R.string.asterisk)
        else -> getString(R.string.slash)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        arguments?.putString(Constants.Number1.message,number1.value)
        arguments?.putString(Constants.Number2.message,number2.value)
    }
}