package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.calculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(),View.OnClickListener {

    //用于拼接字符 显示到界面上
    private val currentInputNum = StringBuilder()
    //存储输入的数字
    private val numList = mutableListOf<Int>()
    //存储输入的计算符
    private val operatorList = mutableListOf<String>()
    //定义标记，用于判断是否是数字输入的开始
    private var isNumStart = true
    lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //清空按钮
        binding.clearBtn.setOnClickListener {
            clearButtonClick(it)
        }
        binding.backBtn.setOnClickListener {
            backButtonClick(it)
        }
        //除法
        binding.textView6.setOnClickListener {
            operatorButtonClick(it)
        }
        //乘法
        binding.textView9.setOnClickListener {
            operatorButtonClick(it)
        }
        //加法
        binding.textView14.setOnClickListener {
            operatorButtonClick(it)
        }
        //减法
        binding.textView18.setOnClickListener {
            operatorButtonClick(it)
        }
        //0
        binding.zeroBtn.setOnClickListener(this)
        //1
        binding.oneBtn.setOnClickListener(this)
        //2
        binding.twoBtn.setOnClickListener(this)
        //3
        binding.threeBtn.setOnClickListener(this)
        //4
        binding.fourBtn.setOnClickListener(this)
        //5
        binding.fiveBtn.setOnClickListener(this)
        //6
        binding.sixBtn.setOnClickListener(this)
        //7
        binding.sevenBtn.setOnClickListener(this)
        //8
        binding.eightBtn.setOnClickListener(this)
        //9
        binding.nineBtn.setOnClickListener(this)

    }

    //数字键
    private fun numberButtonClick(view:View){
        //将view强制转化为textView
        val tv = view as TextView
        //当前输入的是一个新的数字，添加到数组中
        currentInputNum.append(tv.text)
        if (isNumStart){
            numList.add(tv.text.toString().toInt())
            //更改状态 已经不是一个新数字的开始了
            isNumStart = false
        }else{
            //用当前的数字替换数组中最后的一个元素
            numList[numList.size-1] = currentInputNum.toString().toInt()
        }
        //显示内容
        showUI()
        //计算结果
        calculate()
    }
    //运算符
    private fun operatorButtonClick(view: View){
        //将view强制转化为textView
        val tv = view as TextView
        //保存当前运算符
        operatorList.add(tv.text.toString())
        //改变状态
        isNumStart  = true
        currentInputNum.clear()
        showUI()
    }
    //清空键
    private fun clearButtonClick(view: View){
        binding.processTextview.text = ""
        binding.resultTextview.text = "0"
        currentInputNum.clear()
        operatorList.clear()
        numList.clear()
        isNumStart = true
    }
    //返回键
    private fun backButtonClick(view: View){
        //判断是撤销运算符还是数字
        if(numList.size>operatorList.size){
            //撤销数字
                if (numList.size>0){
                    numList.removeLast()
                    isNumStart = true
                    currentInputNum.clear()
                }
        }else{
            //撤销运算符
                if (operatorList.size>0){
                    operatorList.removeLast()
                    isNumStart = false
                    if (numList.size>0){
                        currentInputNum.append(numList.last())
                    }
                }
        }
        showUI()
        calculate()
    }
    //等号键
    fun equalButtonClick(view:View){

    }
    //拼接当前运算的表达式 显示到界面上
    private fun showUI(){
        val str = StringBuilder()
        for ((i,num) in numList.withIndex()){
            //将当前的数字拼接上去
            str.append(num)
            //判断运算符数组中对应的位置是否有内容
            if (operatorList.size > i){
                //将i对应的运算符拼接到字符串中
                str.append(" ${operatorList[i]}")
            }
        }
        binding.processTextview.text = str.toString()
    }
    //实现逻辑运算功能
    private fun calculate(){
        if (numList.size>0){
            //用来记录运算符数组遍历时的下标
            var i = 0
            //记录第一个运算数 == 数字组的第一个数
            var param1 = numList[0].toFloat()
            var param2 = 0f
            if (operatorList.size >0){
                while (true){
                    //获取i对应的运算符
                    val operator = operatorList[i]
                    //判断是不是乘除
                    if (operator == "×" || operator == "÷"){
                        if (i+1 < numList.size){
                            //乘除直接运算
                            //找到第二个运算数
                            param2 = numList[i+1].toFloat()
                            //运算
                            param1 = realCalculate(param1,operator,param2)
                        }
                    }else{
                        //判断是不是最后一个 如果是加法或者减法  需要判断下一个运算符是不是乘除
                        if (i==operatorList.size-1 || operatorList[i+1] != "×"&&operatorList[i+1]!="÷"){
                            //可以直接计算
                            if (i < numList.size-1){
                                param2 = numList[i+1].toFloat()
                                param1 = realCalculate(param1,operator,param2)
                            }
                        }else{
                            //后面有 而且是乘 或者 除法
                            var j = i+1
                            var mparam1 = numList[j].toFloat()
                            var mparam2 = 0f
                            while (true){
                                //获取j对应的运算符
                                if (operatorList[j] == "×" || operatorList[j] == "÷"){
                                        mparam2 = numList[j+1].toFloat()
                                        mparam1 = realCalculate(mparam1,operatorList[j],mparam2)
                                }else{
                                    //之前的那个运算符后面所有连续的乘除都运算结束了
                                    break
                                }
                                j++
                                if (j == operatorList.size){
                                    break
                                }
                            }
                            param2 = mparam1
                            //运算
                            param1 = realCalculate(param1,operator,param2)
                            i = j - 1
                        }
                    }
                    i++
                    if (i == operatorList.size){
                        //遍历结束
                        break
                    }
                }
            }

            //显示结果
            binding.resultTextview.text = "$param1"
        }else{
            binding.resultTextview.text = "0"
        }
    }
    //运算
    private fun realCalculate(param1:Float,operator:String,param2:Float):Float{
        var result:Float = 0f
        when(operator){
            "+" ->{
                result = (param1 +param2)
            }
            "-" ->{
                result = (param1-param2)
            }
            "×" ->{
                result = (param1*param2)
            }
            "÷" ->{
                result = (param1/param2)
            }
        }

        return result
    }

    override fun onClick(v: View?) {
        numberButtonClick(v!!)
    }
}