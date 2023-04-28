package com.example.calculatorjetpackcompose


import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import com.example.calculatorjetpackcompose.stringvalues.StringValues

class MainActivity : AppCompatActivity() {
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private val fragmentManager = supportFragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val scaffoldState = rememberScaffoldState()
            Scaffold(
                scaffoldState = scaffoldState,
                topBar = {
                         TopBar()
                         },
            ){
                MainActivityScreen(it)
            }
            LaunchedEffect(key1 = Unit) {
                if (savedInstanceState == null) {
                    setFragmentsInContainer(R.id.main_fragment_container,MainFragment())
                    setFragmentsInContainer(R.id.operation_fragment_container,OperationFragment())
                } else {
                    setFragmentsInContainer(R.id.main_fragment_container,copyArguments(R.id.main_fragment_container,MainFragment()))
                    setFragmentsInContainer(R.id.operation_fragment_container,copyArguments(R.id.operation_fragment_container,OperationFragment()))
                }
            }
        }
        onBackListener()
    }
    private fun setFragmentsInContainer(container: Int,fragment: Fragment){
        fragmentManager
            .beginTransaction()
            .replace(container,fragment)
            .commit()
    }
    private fun copyArguments(container: Int,fragment: Fragment): Fragment{
        fragmentManager.findFragmentById(container)?.let{
            fragment.arguments = it.arguments
        }
        return fragment
    }
    @Composable
    fun MainActivityScreen(padding: PaddingValues){
        if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Row(
                Modifier
                    .padding(paddingValues = padding)
                    .fillMaxSize()
            ) {
                AndroidView(
                    factory = {
                        FrameLayout(it).apply {
                            id = R.id.main_fragment_container
                            println(id)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
                AndroidView(
                    factory = {
                        FrameLayout(it).apply {
                            id = R.id.operation_fragment_container
                            println(id)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        } else{
                Box(
                    Modifier
                        .fillMaxSize()){
                    AndroidView(factory = {
                        FrameLayout(it).apply {
                            id = R.id.main_fragment_container
                            println(id)
                        }
                    })
                    AndroidView(factory = {
                        FrameLayout(it).apply {
                            id = R.id.operation_fragment_container
                            println(id)
                        }
                    })
                }
            }
    }
    private fun onBackListener(){
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val mainFragment: MainFragment? = fragmentManager.findFragmentById(R.id.main_fragment_container) as MainFragment?
                val operationFragment: OperationFragment? = fragmentManager.findFragmentById(R.id.operation_fragment_container) as OperationFragment?
                mainFragment?.let {
                    operationFragment?.let {
                        if (operationFragment.isVisible) {
                            val fragmentTransaction = fragmentManager.beginTransaction()
                            if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                                fragmentTransaction.hide(operationFragment)
                                fragmentTransaction.show(mainFragment)
                                fragmentTransaction.commit()
                            }
                            else{
                                if(operationFragment.operation.value == getString(R.string.empty_string)){
                                    if(!mainFragment.inResultScreen.value){
                                        finish()
                                    }
                                    else{
                                        mainFragment.inResultScreen.value = false
                                        mainFragment.dataSet.value = StringValues.operations
                                    }
                                }else{
                                    operationFragment.disableButtonAndEditText()
                                }
                            }
                        } else {
                            if (mainFragment.inResultScreen.value) {
                                mainFragment.inResultScreen.value = false
                                mainFragment.dataSet.value = StringValues.operations
                            } else {
                                finish()
                            }
                        }
                    }
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }
    @Composable
    fun TopBar(){
        TopAppBar(
            title = { Text(text = getString(R.string.app_name)) },
            navigationIcon = {
                IconButton(onClick = {onBackPressedCallback.handleOnBackPressed()}) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = getString(R.string.back))
                }
            }
        )
    }
}
