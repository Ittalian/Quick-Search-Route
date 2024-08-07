package com.example.ittalian.quicksearchroute

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ittalian.quicksearchroute.databinding.ActivityMainBinding
import io.realm.Realm
import io.realm.Sort

class MainActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityMainBinding
    private lateinit var realm: Realm
    private lateinit var adapter: CustomRecyclerViewAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        realm = Realm.getDefaultInstance()

        _binding.toEditPage.setOnClickListener {
            val editIntent = Intent(this, EditActivity::class.java)
            startActivity(editIntent)
        }
    }

    override fun onStart() {
        super.onStart()
        val realmResults = realm.where(Course::class.java).findAll().sort("id", Sort.DESCENDING)
        layoutManager = LinearLayoutManager(this)
        findViewById<RecyclerView>(R.id.recyclerView).layoutManager = layoutManager
        val apiKey = getString(R.string.api_key)
        adapter = CustomRecyclerViewAdapter(realmResults, apiKey)
        findViewById<RecyclerView>(R.id.recyclerView).adapter = this.adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}