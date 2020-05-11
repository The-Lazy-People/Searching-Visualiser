package com.example.searchingvisualiser

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    val buttons: MutableList<MutableList<Button>> = ArrayList()
    //size of grid
    var size = 5
    //array to store numbers in array to be sorted
    var arrayToBeSearched:MutableList<Int> = ArrayList()
    //white color
    val whiteColor:String="#FFFFFF"
    //red color
    val redColor:String="#FF0000"
    val pink:String="#ffdab9"
    //green color
    val greenColor:String="#228B22"
    val bluecolr:String="#0000FF"
    var selected:Int=-1
    var numberShouldNotBeDublicate:MutableMap<Int,Int> = mutableMapOf()
    var searchAlgoSelectedValue:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        //seekbar added
        arraySizeSeekBar.setOnSeekBarChangeListener(this)

        createButtonGrid(size)
        searchbtn.text = "Linear Search"

        randamizebtn.setOnClickListener {
            //cancelAllJobs()
            paintAllButtonsWhiteAgain(size)
            randamize(size)
        }
        searchbtn.setOnClickListener {
            if(selected!=-1) {
                if (searchAlgoSelectedValue == 0) {
                    LinearSearch()
                } else if (searchAlgoSelectedValue == 1) {
                    GlobalScope.launch(Dispatchers.Main) {
                        BinarySearch(0, size, selected)
                    }
                }
            }
            else if(selected==-1){
                Toast.makeText(this,"Select bar to be searched",Toast.LENGTH_LONG).show()
            }
        }

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.searching_algo_name, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.linearSearch -> {
                paintAllButtonsWhiteAgain(size)
                randamize(size)
                searchAlgoSelectedValue=0
                searchbtn.text="Linear Search"
                return true }
            R.id.binarySearch -> {
                paintAllButtonsWhiteAgain(size)
                randamize(size)
                searchAlgoSelectedValue=1
                searchbtn.text="Binary Search"
                return true }
            R.id.jumpSearch -> {
                paintAllButtonsWhiteAgain(size)
                randamize(size)
                searchAlgoSelectedValue=2
                searchbtn.text="Jump Search"
                return true }
            R.id.interpolationSearch -> {
                paintAllButtonsWhiteAgain(size)
                randamize(size)
                searchAlgoSelectedValue=3
                searchbtn.text="Interpolation Search"
                return true }
            R.id.exponentialSearch -> {
                paintAllButtonsWhiteAgain(size)
                randamize(size)
                searchAlgoSelectedValue=4
                searchbtn.text="Exponential Search"
                return true }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun LinearSearch() {
        GlobalScope.launch(Dispatchers.IO)
        {
            if (selected == -1) {
                //Toast.makeText(this, "Element not selected", Toast.LENGTH_LONG).show()
            } else {
                for (i in 0..size) {
                    colorButton(i, arrayToBeSearched[i], redColor)
                    delay(100)
                    paintSingleColWhite(i)
                    colorButton(i, arrayToBeSearched[i], greenColor)
                    if (arrayToBeSearched[i] == selected) {
                        //Toast.makeText(this, "Element found at $i", Toast.LENGTH_LONG).show()
                        colorButton(i, arrayToBeSearched[i], bluecolr)
                        break;
                    }
                }
            }
        }
    }

    private suspend fun BinarySearch(p: Int, q: Int, x: Int){
        var job=GlobalScope.launch(Dispatchers.Main) {
            insertionSort()
        }
        job.join()
        GlobalScope.launch (Dispatchers.IO) {
            //Toast.makeText(this, "$p $q $x", Toast.LENGTH_LONG).show()
            var l = p
            var r = q
            while (l <= r) {
                val m = l + (r - l) / 2
                colorButton(m, arrayToBeSearched[m], pink)
                delay(200)
                paintSingleColWhite(m)
                colorButton(m, arrayToBeSearched[m], greenColor)
                if (arrayToBeSearched[m] == x) {
                    colorButton(m, arrayToBeSearched[m], bluecolr)
                    break
                }

                if (arrayToBeSearched[m] < x) {
                    l = m + 1
                } else {
                    r = m - 1
                }
            }
        }
    }

    private suspend fun insertionSort(){
        var job =GlobalScope.launch (Dispatchers.Main )
        {
            for (i in 1..size) {
                // println(items)
                val item = arrayToBeSearched[i]
                var j = i - 1
                while (j >= 0 && arrayToBeSearched[j] > item) {
                    colorButton(j+1,arrayToBeSearched[j+1],redColor)
                    delay(100)
                    paintSingleColWhite(j + 1)
                    colorButton(j + 1, arrayToBeSearched[j], greenColor)
                    arrayToBeSearched[j + 1] = arrayToBeSearched[j]
                    j = j - 1
                }
                colorButton(j+1,arrayToBeSearched[j+1],redColor)
                delay(100)
                paintSingleColWhite(j + 1)
                colorButton(j + 1, item, greenColor)
                arrayToBeSearched[j + 1] = item
            }
        }
        job.join()
    }

    private fun paintSingleColWhite(col: Int) {
        for (i in 0..size){
            buttons[col][i].setBackgroundColor(Color.parseColor("#FFFFFF"))
        }
    }

    private fun numberShouldNotBeDublicateIntializer(){
        for (i in 0..size){
            numberShouldNotBeDublicate.put(i,0)
        }
    }

    private fun randamize(size: Int) {
        selected=-1
        numberShouldNotBeDublicate.clear()
        arrayToBeSearched.removeAll(arrayToBeSearched)
        numberShouldNotBeDublicateIntializer()
        for(col in 0..size)
        {
            var row = (0..size).random()
            if(numberShouldNotBeDublicate.get(row)==1){
                for (i in 0..size){
                    if(numberShouldNotBeDublicate.get(i)==0){
                        row=i
                        numberShouldNotBeDublicate.put(row,1)
                        break
                    }
                }
            }
            else{
                numberShouldNotBeDublicate.put(row,1)
            }

            arrayToBeSearched.add(row)
            colorButton(col,row,greenColor)
        }

    }
    // color a coloumn of grid till a specific row
    private fun colorButton(col: Int, row: Int,color:String) {
        for (i in 0..row){
            buttons[col][i].isEnabled=true
            buttons[col][i].setOnClickListener {
                Toast.makeText(this, "${arrayToBeSearched[col]} is selected", Toast.LENGTH_LONG).show()
                selected = arrayToBeSearched[col]
            }
            buttons[col][i].setBackgroundColor(Color.parseColor(color))
        }
        for (i in row+1..size)
        {
            buttons[col][i].isEnabled=false
        }
    }

    private fun createButtonGrid(size: Int) {
        // xml declared LIinear layout
        var screenid = resources.getIdentifier("screen", "id", packageName)
        val screen=findViewById<LinearLayout>(screenid)


        // new dynamically declared linear layout inside screen linearlayout so grid can be deleted at any time
        val mainscreen = LinearLayout(this)
        mainscreen.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        mainscreen.orientation = LinearLayout.HORIZONTAL
        var mainscreenid = resources.getIdentifier("mainscreen", "id", packageName)
        mainscreen.id=mainscreenid
        screen.addView(mainscreen)

        for (i in 0..size) {

            val arrayLinearLayout = LinearLayout(this)
            arrayLinearLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT,1.0f
            )
            arrayLinearLayout.orientation = LinearLayout.VERTICAL
            arrayLinearLayout.setPadding(2,2,2,2)

            val buttoncol: MutableList<Button> = ArrayList()
            for (j in 0..size) {
                val button = Button(this)
                button.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    1.0f
                )
                buttoncol.add(button)
                arrayLinearLayout.addView(button)
            }

            buttons.add(buttoncol)

            mainscreen.addView(arrayLinearLayout)
        }
        paintAllButtonsWhiteAgain(size)
    }

    private fun paintAllButtonsWhiteAgain(size: Int) {
        for (i in 0..size){
            for (j in 0..size){
                buttons[i][j].setBackgroundColor(Color.parseColor("#FFFFFF"))
            }
        }
        selected=-1
    }

    private fun deleteMainScreen() {
        var mainscreenid = resources.getIdentifier("mainscreen", "id", packageName)
        val mainscreen=findViewById<LinearLayout>(mainscreenid)
        (mainscreen.getParent() as ViewGroup).removeView(mainscreen)
        buttons.removeAll(buttons)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        size=(progress/4)+5
        deleteMainScreen()
        createButtonGrid(size)
        randamize(size)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        paintAllButtonsWhiteAgain(size)
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }
}
