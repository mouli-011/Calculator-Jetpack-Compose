package com.example.calculatorjetpackcompose.constants

sealed class Constants {
    abstract val message: String
    object Result: Constants() {
        override val message: String
            get() = "RESULT"
    }
    object FragmentArgs: Constants(){
        override val message: String
            get() =  "fragmentArgs"
    }
    object InResultScreen: Constants(){
        override val message: String
            get() = "inResultScreen"
    }
    object DataSet: Constants(){
        override val message: String
            get() = "dataSet"
    }
    object ResultString: Constants(){
        override val message: String
            get() = "resultString"
    }
    object Operation: Constants(){
        override val message: String
            get() = "operation"
    }
    object Zero: Constants(){
        override val message: String
            get() = "0"
    }
    object Nine: Constants(){
        override val message: String
            get() = "9"
    }
    object Add: Constants(){
        override val message: String
            get() = "ADD"
    }
    object Sub: Constants(){
        override val message: String
            get() = "SUB"
    }
    object Mul: Constants(){
        override val message: String
            get() = "MUL"
    }
    object Div: Constants(){
        override val message: String
            get() = "DIV"
    }
    object Number1: Constants(){
        override val message: String
            get() = "number1"
    }
    object Number2: Constants(){
        override val message: String
            get() = "number2"
    }
}