package com.example.calculatorjetpackcompose

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.example.calculatorjetpackcompose.constants.Constants
import com.example.calculatorjetpackcompose.stringvalues.StringValues

class MainFragment : Fragment() {
    lateinit var inResultScreen: MutableState<Boolean>
    lateinit var dataSet: MutableState<List<String>>
    private var resultString = R.string.empty_string.toString()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if(savedInstanceState!=null){
            arguments = savedInstanceState.getBundle(Constants.FragmentArgs.message)
        }
        setFragmentResultListener()
        return setContentForMainFragmentScreen()
    }
    private fun setContentForMainFragmentScreen(): ComposeView{
        return ComposeView(requireContext()).apply {
            setContent {
                inResultScreen = rememberSaveable{mutableStateOf(arguments?.getBoolean(Constants.InResultScreen.message)?:false)}
                dataSet = rememberSaveable{mutableStateOf(arguments?.getStringArrayList(Constants.DataSet.message)?:StringValues.operations)}
                resultString = arguments?.getString(Constants.ResultString.message)?:getString(R.string.empty_string)
                MainFragmentScreen()
            }
        }
    }
    private fun replaceOperationFragment(operation: String){
        val operationFragment = OperationFragment()
        val operationFragmentArgument = Bundle()
        operationFragmentArgument.putString(Constants.Operation.message,operation)
        operationFragment.arguments = operationFragmentArgument
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.operation_fragment_container,operationFragment)
            .commit()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val fragmentArgs = Bundle()
        fragmentArgs.putString(Constants.ResultString.message,resultString)
        fragmentArgs.putStringArrayList(Constants.DataSet.message,ArrayList(dataSet.value))
        fragmentArgs.putBoolean(Constants.InResultScreen.message,inResultScreen.value)
        outState.putBundle(Constants.FragmentArgs.message,fragmentArgs)
    }
    @Composable
    fun MainFragmentScreen() {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(dataSet.value) { index, string ->
                if((index == 0) && (dataSet.value.size == 2)){
                    Text(string)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                 else {
                    Button(
                        onClick = {
                            if(inResultScreen.value){
                                inResultScreen.value = false
                                dataSet.value = StringValues.operations
                            }
                            else {
                                replaceOperationFragment(string)
                            }
                        },
                        modifier = Modifier
                            .width(110.dp)
                            .height(60.dp)
                            .clip(RoundedCornerShape(10.dp))
                    ) {
                        Text(text = string)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
    private fun setFragmentResultListener(){
        parentFragmentManager.setFragmentResultListener(
            Constants.Result.message,
            this
        ) { _, fragmentResult ->
            inResultScreen.value = true
            resultString = fragmentResult.getString(Constants.Result.message).toString()
            dataSet.value = listOf(resultString,StringValues.reset)
        }
    }
}