package com.getir.patika.week4_assignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getProfile("1")

        createPost("username", "title", "description")
    }

    private fun getProfile(userId: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val profileUrl = URL("https://jsonplaceholder.typicode.com/posts")
                val connection = profileUrl.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode
                val response = StringBuilder()

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                        var line: String? = reader.readLine()
                        while (line != null) {
                            response.append(line)
                            line = reader.readLine()
                        }
                    }
                    val profileJson = JSONObject(response.toString())
                    val name = profileJson.getString("name")
                    val email = profileJson.getString("email")
                    val formattedText = "Name: $name\nEmail: $email"
                    runOnUiThread {
                        findViewById<TextView>(R.id.profileResultTextView).text = formattedText
                    }
                } else {
                    runOnUiThread {
                        findViewById<TextView>(R.id.profileResultTextView).text = "Failed"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun createPost(username: String, title: String, body: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val postUrl = URL("https://jsonplaceholder.typicode.com/posts")
                val connection = postUrl.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json")

                val postBody = JSONObject()
                postBody.put("username", username)
                postBody.put("title", title)
                postBody.put("body", body)

                OutputStreamWriter(connection.outputStream).use { it.write(postBody.toString()) }

                val responseCode = connection.responseCode
                val response = StringBuilder()

                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                        var line: String? = reader.readLine()
                        while (line != null) {
                            response.append(line)
                            line = reader.readLine()
                        }
                    }
                    runOnUiThread {
                        findViewById<TextView>(R.id.postResultTextView).text = response.toString()
                    }
                } else {
                    runOnUiThread {
                        findViewById<TextView>(R.id.postResultTextView).text = "Failed"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
