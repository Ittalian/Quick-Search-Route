package com.example.ittalian.quicksearchroute

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL

class CustomRecyclerViewAdapter(realmResults: RealmResults<Course>, apiKey: String) : RecyclerView.Adapter<ViewHolder>() {
    private var rResult: RealmResults<Course> = realmResults
    private val mainUrl = "https://api.ekispert.jp/v1/json/search/course/light"
    private val apiKey = apiKey

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.one_result, parent, false)
        val viewHolder = ViewHolder(view)


        return viewHolder
    }

    override fun getItemCount() = rResult.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val course = rResult[position]
        holder.departStationText?.text = course?.departStaion.toString()
        holder.arriveStationText?.text = course?.arriveStation.toString()
        holder.itemView.setBackgroundColor(if (position % 2 == 0) Color.LTGRAY else Color.WHITE)
        holder.itemView.setOnClickListener {
            val departStation = rResult[position]?.departStaion
            val arriveStation = rResult[position]?.arriveStation
            val request = "$mainUrl?key=$apiKey&from=${departStation}&to=${arriveStation}&shinkansen=true&plane=true&limitedExpress=true"
            courseTask(request, it.context)
        }
        holder.editBtn?.setOnClickListener {
            val intent = Intent(it.context, EditActivity::class.java)
            intent.putExtra("id", course?.id)
            it.context.startActivity(intent)
        }
    }

    private fun courseTask(request: String, context: Context) {
        GlobalScope.launch {
            try {
                val result = courseBackGroundTask(request)
                courseJsonTask(result, context)
            } catch (e: IOException) {

            }
        }
    }

    private suspend fun courseBackGroundTask(request: String) : String {
        val response = withContext(Dispatchers.IO) {
            var httpResult = ""

            val urlObj = URL(request)
            val br = BufferedReader(InputStreamReader(urlObj.openStream()))
            httpResult = br.readText()

            return@withContext httpResult
        }

        return response
    }

    private fun courseJsonTask(result: String, context: Context) {
        val jsonObj = JSONObject(result)
        val resourceUrl = jsonObj.getJSONObject("ResultSet").getString("ResourceURI")
        val uri = Uri.parse(resourceUrl)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }
}