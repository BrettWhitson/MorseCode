package com.example.brett.morsecode

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        //..needed for scrolling
        mTextView.movementMethod = ScrollingMovementMethod();

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            // had it here
        }
        // has to go outside the {}
        buildDictsWithJSON(loadMorseJSON())

        showButton.setOnClickListener{ view ->
            showCodes()
            hideKeyboard()
        }


        translateButton.setOnClickListener{ view ->
            if(inputText.text.toString() == ""){
                appendTextAndScroll("")
            }
            else if(isMorse(inputText.text.toString())){
                appendTextAndScroll("Input: ${inputText.text.toString()}")
                appendTextAndScroll("Translation: ${translateMorseToText(inputText.text.toString())}")
                hideKeyboard()
            }
            else {
                appendTextAndScroll("Input: ${inputText.text.toString()}")
                appendTextAndScroll("Translation: ${translateTextToMorse(inputText.text.toString())}")
                hideKeyboard()
            }
        }

        clearButton.setOnClickListener{ view ->
            mTextView.setText("")
        }
    }

    private fun appendTextAndScroll(text: String){
        if (mTextView != null){
            mTextView.append(text + "\n")
            val layout = mTextView.getLayout()
            if(layout != null){
                val scrollDelta = (layout.getLineBottom(mTextView.getLineCount() - 1) - mTextView.getScrollY()
                        - mTextView.getHeight())
                if(scrollDelta > 0)
                    mTextView.scrollBy(0, scrollDelta)
            }
        }
    }

    fun Activity.hideKeyboard(){
        hideKeyboard(if (currentFocus == null) View(this) else currentFocus)
    }

    fun Context.hideKeyboard(view: View){
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken,0)
    }

    fun translateTextToMorse(inputString: String) : String{

        var new_string = ""

        var i = 0
        while(i < inputString.length){
            new_string += letToCodeDict[inputString[i].toLowerCase().toString()]
            new_string += ' '
            i++
        }


        return new_string
    }

    fun translateMorseToText(inputString: String) : String{

        var new_string = ""
        var temp_string = ""
        var delim = ' '

        var i = 0

        while(i < inputString.length){
            if(inputString[i] != delim) {
                temp_string += inputString[i]
                i++
            }
            else if(inputString[i] == delim) {
                new_string += codeToLetDict[temp_string].toString()
                temp_string = ""
                i++
            }
        }
        new_string += codeToLetDict[temp_string].toString()

        return new_string
    }

    fun isMorse(inputString : String) : Boolean{
        var check = true
        var i = 0

        val checkDot = '.'
        val checkDash = '-'
        val checkSlash = '/'
        val checkSpace = ' '

        while(i < inputString.length) {
            if (inputString[i] == checkDot || inputString[i] == checkDash || inputString[i] == checkSlash || inputString[i] == checkSpace){
                i++
                continue
            }
            else{
                check = false
                break
            }
        }
        return check
    }

    fun loadMorseJSON() : JSONObject {
        val filePath = "morse.json"

        val jsonStr = application.assets.open(filePath).bufferedReader().use{
            it.readText()
        }

        val jsonObj = JSONObject(jsonStr.substring(jsonStr.indexOf("{"), jsonStr.lastIndexOf("}") + 1))

        return jsonObj
    }

    var letToCodeDict: HashMap<String, String> = HashMap()
    var codeToLetDict: HashMap<String, String> = HashMap()

    fun buildDictsWithJSON(jsonObj : JSONObject){

        for(k in jsonObj.keys()){
            val code = jsonObj[k]

            letToCodeDict.put(k, code.toString())
            codeToLetDict.put(code.toString(), k)

        }
    }

    fun showCodes(){
        appendTextAndScroll("HERE ARE THE CODES\n")
        for(k in letToCodeDict.keys.sorted())
            appendTextAndScroll("$k: ${letToCodeDict[k]}")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
