package com.dasumma.mobileskinav.ui

import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.dasumma.mobileskinav.ApiService
import com.dasumma.mobileskinav.R
import com.google.gson.GsonBuilder
import com.google.gson.InstanceCreator
import com.google.gson.Strictness
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.neo4j.driver.AccessMode
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.SessionConfig.builder
import org.neo4j.driver.Transaction
import org.neo4j.driver.Values.parameters
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Dictionary
import java.util.Hashtable


class ApiActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val content = layoutInflater.inflate(R.layout.activity_api, null)
        val retrofit = Retrofit.Builder()
            .baseUrl("https://skiontology.dasumma1.net/ds/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder()
                .registerTypeAdapter(Dictionary::class.java, InstanceCreator<Dictionary<Any, Any>> {
                    Hashtable<Any, Any>()
                })
                .setStrictness(Strictness.LENIENT)
                .create()))
            //.addConverterFactory(ScalarsConverterFactory.create())
            .build()

        val button = content.findViewById<Button>(R.id.button)
        val button2 = content.findViewById<Button>(R.id.button2)

        button.setOnClickListener { view ->
            lifecycleScope.launch{
                try{
                    val queryReturn = retrofit.create(ApiService::class.java).getQuery(
                        "PREFIX : <http://www.semanticweb.org/dasum/ontologies/2026/1/ski-map-ontology%23> select * {:Mount_Southington_Ski_Area :hasSkiRun ?SkiRun. ?SkiRun :difficulty \"Green\"}" //select * {?s ?p ?o}
                    )
                    Toast.makeText(this@ApiActivity, queryReturn.toString(), Toast.LENGTH_LONG).show()
                    Log.e("API", queryReturn.toString())
                } catch (e: Exception){
                    Log.e("API", e.toString())
                    Toast.makeText(this@ApiActivity, e.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }

        button2.setOnClickListener { view ->
            // In your Activity or ViewModel
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val dbUri = "neo4j+s://skibackend.dasumma1.net:7687"
                    val dbUser = "neo4j"
                    val dbPassword = "Ae3@b24CDa!!"

                    val driver = GraphDatabase.driver(dbUri, AuthTokens.basic(dbUser, dbPassword)).use { driver ->
                        driver.verifyConnectivity()
                        val session = driver.session(builder().withDefaultAccessMode(AccessMode.WRITE).build())

                        // Update UI on Main Thread
                        withContext(Dispatchers.Main) {
                            Log.d("API", "Connection established.")
                        }

                        val result = session.run("MATCH (n) RETURN n")
                        while (result.hasNext()) {
                            val record = result.next()
                            val node = record["n"].asNode()
                            val label = node.labels().joinToString(",")
                            withContext(Dispatchers.Main) {
                                Log.d("API", "Node label: $label")
                            }
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.e("API", "Connection failed: ${e.toString()}")
                        Toast.makeText(this@ApiActivity, "Connection Error", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        setContentView(content)
    }
}