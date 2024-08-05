package com.example.ittalian.quicksearchroute

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.ittalian.quicksearchroute.databinding.ActivityEditTimeBinding
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class EditTimeActivity() : AppCompatActivity() {
    private lateinit var realm: Realm
    private lateinit var _binding: ActivityEditTimeBinding
    private lateinit var searchType: String


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityEditTimeBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        realm = Realm.getDefaultInstance()
        searchType = getString(R.string.currentSearchType)
        var request = intent.getStringExtra("request")
        var reverseRequest = intent.getStringExtra("reverseRequest")

        _binding.currentRadio.setOnClickListener{
            searchType = getString(R.string.currentSearchType)
        }

        _binding.departRadio.setOnClickListener{
            searchType = getString(R.string.departSearchType)
        }

        _binding.arriveRadio.setOnClickListener{
            searchType = getString(R.string.arriveSearchType)
        }

        _binding.startRadio.setOnClickListener{
            searchType = getString(R.string.startSearchType)
        }

        _binding.lastRadio.setOnClickListener{
            searchType = getString(R.string.lastSearchType)
        }

        _binding.searchBtn.setOnClickListener {
            if (validateTime(_binding.hourText, _binding.minuteText)) courseTask(getRequest(request), it.context)
        }

        _binding.reverseSearchBtn.setOnClickListener {
            if (validateTime(_binding.hourText, _binding.minuteText)) courseTask(getRequest(reverseRequest), it.context)
        }
    }

    private fun getRequest(request: String?): String? {
        if (searchType == getString(R.string.departSearchType) || searchType == getString(R.string.arriveSearchType)) {
                return "${request}&time=${_binding.hourText.text}${_binding.minuteText.text}&searchType=${searchType}"
        } else if (searchType == getString(R.string.startSearchType) || searchType == getString(R.string.lastSearchType)) {
            return "${request}&searchType=${searchType}"
        }
        return request
    }

    private fun validateTime(hour: EditText, minute: EditText): Boolean {
        if (searchType == getString(R.string.departSearchType) || searchType == getString(R.string.arriveSearchType)) {
            if (hour.text.isNullOrEmpty() || minute.text.isNullOrEmpty()) {
                hour.requestFocus()
                Toast.makeText(applicationContext, "入力してください", Toast.LENGTH_SHORT).show()
                return false
            }
            if (hour.length() != 0 && hour.length() != 2 || minute.length() != 0 && minute.length() != 2) {
                hour.requestFocus()
                Toast.makeText(applicationContext, "00:00の形式で入力てください", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentTime(): String {
        val currentTime = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HHmm")
        return currentTime.format(formatter)
    }

    private fun courseTask(request: String?, context: Context) {
        GlobalScope.launch {
            try {
                val result:String? = courseBackGroundTask(request)
                courseJsonTask(result, context)
            } catch (e: IOException) {

            }
        }
    }

    private suspend fun courseBackGroundTask(request: String?) : String {
        val response = withContext(Dispatchers.IO) {
            var httpResult = ""

            val urlObj = URL(request)
            val br = BufferedReader(InputStreamReader(urlObj.openStream()))
            httpResult = br.readText()

            return@withContext httpResult
        }

        return response
    }

    private fun courseJsonTask(result: String?, context: Context) {
        val jsonObj = JSONObject(result)
        val resourceUrl = jsonObj.getJSONObject("ResultSet").getString("ResourceURI")
        val uri = Uri.parse(resourceUrl)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }
}