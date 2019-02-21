package com.plumbaria.e_10_5_puntuacionesjson

import android.app.ProgressDialog
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), View.OnClickListener {


    companion object {

        private val urlObtener = "http://proves.iesperemaria.com/asteroides/puntuaciones/"
        private val urlGrabar = "http://proves.iesperemaria.com/asteroides/puntuaciones/nueva/"
    }

    lateinit var btnVerPuntuaciones: Button
    lateinit var btnCrearPuntuacion: Button
    lateinit var puntos: TextView
    var pDialog: ProgressDialog? = null
    var jsonManager = JSONManager()


    lateinit var jsonObject: JSONObject
    lateinit var jsonArray: JSONArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnVerPuntuaciones = findViewById(R.id.btnVerPuntuaciones)
        btnCrearPuntuacion = findViewById(R.id.btnCrearPuntuacion)
        puntos = findViewById(R.id.puntos)

        btnVerPuntuaciones.setOnClickListener(this)
        btnCrearPuntuacion.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btnCrearPuntuacion -> crearPuntuacion()
            R.id.btnVerPuntuaciones -> mostrarPuntuaciones()
        }
    }

    private fun crearPuntuacion() {
        PuntuacionesJSON().execute()
    }

    private fun mostrarPuntuaciones() {
        val puntos = Math.abs(Random().nextInt(99999))
        val fecha = System.currentTimeMillis()
        NuevaPuntuacion().execute(puntos.toString(), "Alex Goia", fecha.toString())
    }

    inner class PuntuacionesJSON : AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            pDialog = ProgressDialog(this@MainActivity)
            pDialog!!.setMessage("Obtenido puntuaciones...")
            pDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            pDialog!!.setCancelable(true)
            pDialog!!.show()
        }

        override fun doInBackground(vararg params: String?): String? {
            try {
                return jsonManager.getJsonString(urlObtener, "GET", null)
            } catch (e:Exception) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(jsonString: String?) {
            val salida = StringBuilder()
            pDialog!!.dismiss()
            try {
                jsonObject = JSONObject(jsonString)
                jsonArray = jsonObject.getJSONArray("puntuaciones")
                for (i in 0 until jsonArray.length()){
                    val nodo = jsonArray.getJSONObject(i)
                    salida.append(
                        nodo.getString("puntos")
                                + " " +
                                nodo.getString("nombre")
                                + "\n"
                    )
                }
                puntos.text = salida.toString()
            } catch (e:JSONException) {
                Toast.makeText(applicationContext,
                    "Error accediendo al servicio",
                    Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    inner class NuevaPuntuacion : AsyncTask<String, String, String> () {

        override fun onPreExecute() {
            super.onPreExecute()
            pDialog = ProgressDialog(this@MainActivity)
            pDialog!!.setMessage("Almacenando puntuación...")
            pDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            pDialog!!.setCancelable(false)
            pDialog!!.show()
        }


        override fun doInBackground(vararg params: String?): String? {
            try {
                val parametros = ArrayList<NameValuePair>()
                parametros.add(
                    BasicNameValuePair("puntos", params[0])
                )
                parametros.add(
                    BasicNameValuePair("nombre", params [1])
                )
                parametros.add(
                    BasicNameValuePair("nombre", params[2])
                )
                return jsonManager.getJsonString(urlGrabar, "POST", parametros)
            } catch (e:Exception) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(jsonString: String) {
            val salida: String
            pDialog!!.dismiss()
            try {
                jsonObject = JSONObject(jsonString)
                salida = jsonObject.getString("id") + " " +
                        jsonObject.getString("puntos") + " " +
                        jsonObject.getString("nombre")
                puntos.text = salida
            } catch (e: JSONException) {
                Toast.makeText(applicationContext,
                    "Error accediendo al servicio",
                    Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }

    }
}