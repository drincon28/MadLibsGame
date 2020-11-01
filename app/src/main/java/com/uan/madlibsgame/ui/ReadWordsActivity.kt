package com.uan.madlibsgame.ui

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.service.quicksettings.Tile
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.uan.madlibsgame.R
import org.w3c.dom.Text
import java.util.*


class ReadWordsActivity : AppCompatActivity() {

    /*
     * Almacena un valor entre 0 y 4 para seleccionar una historia de manera aleatoria
     */
    private var rsn = 0

    /*
     * Guarda el titulo de la historia actual del juego.
     */
    private var storyTitleText = ""

    /*
     * Guarda el contenido de la historia actual del juego.
     */
    private var storyContentText = ""

    /*
     * Guarda la cantidad de palabras que se deben reemplazar en la historia.
     */
    private var numberOfMadWords = 0

    /*
     * Guarda la cantidad de palabras que falta por reemplazar en la historia.
     */
    private var madWordsLeft = 0

    /*
     * Guarda la lista de palabras locas (Mad Words)
     */
    var l: MutableList<String> = mutableListOf()

    /*
     *
     */
    var index = 0

    /*
     * Inicio de la actividad.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_words)

        val btnRwOk = findViewById<Button>(R.id.btn_read_words_ok)
        val tVwl = findViewById<TextView>(R.id.text_view_words_left)
        val tVit = findViewById<TextView>(R.id.text_view_input_type)
        val tIli = findViewById<TextInputLayout>(R.id.text_input_layout_hint)
        val inputWord = findViewById<TextInputEditText>(R.id.input_mad_word)
        val reqCode = 20
        var iWord = ""

        // Inicializar variables.
        initStoryValues()

        // Inicializar valores GUI
        initStoryGUIValues(tVwl, tVit, tIli)

        btnRwOk.setOnClickListener {
            iWord = inputWord.text.toString()

            if (iWord != "") {
                println("INDEX: " + index + ", LEFT: " + madWordsLeft)
                println("MAD WORDS: ")
                println(l)

                storyContentText = Regex("<.*?>").replaceFirst(storyContentText, iWord)
                println(storyContentText)

                madWordsLeft -= 1
                tVwl.text = madWordsLeft.toString() + " word(s) left"

                inputWord.setText("")
                iWord = ""

                index = numberOfMadWords - madWordsLeft
                if (index < l.size) {
                    tVit.text = "please type a/an " + l[index].substring(1, l[index].length - 1)
                    tIli.hint = l[index].substring(1, l[index].length - 1)
                }
                println("INDEX: " + index + ", LEFT AFTER: " + madWordsLeft)
                Toast.makeText(this@ReadWordsActivity, "Great! Keep going!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@ReadWordsActivity, "Please fill in all the words!", Toast.LENGTH_SHORT).show()
            }

            if (madWordsLeft == 0) {
                val i = Intent(this@ReadWordsActivity, StoryActivity::class.java)
                val bundle = Bundle()
                bundle.putString("storyTitle", storyTitleText)
                bundle.putString("storyContent", storyContentText)
                i.putExtras(bundle)
                startActivityForResult(i, reqCode)
            }
        }
    }

    /*
     * Inicializar / Re-inicializar valores de las variables usadas.
     */
    private fun initStoryValues() {
        // Obtener un número de historia aleatorio
        rsn = getRandomStoryNumber()
        // Leer titulo de la historia
        storyTitleText = readMads(rsn)[0]
        // Leer contenido de la historia
        storyContentText = readMads(rsn)[1]
        // Contar la cantidad de << palabras locas >> que tiene la historia.
        numberOfMadWords = countMadWords(storyContentText)
        // Establecer inicialmente la cantidad de palabras restantes igual a la cantidad de MadWords del texto.
        madWordsLeft = countMadWords(storyContentText)
        // Obtener una lista que contiene los MadWords encontrados en el texto.
        l = getMadWords(storyContentText)
    }

    /*
     * Inicializar / Re-inicializar valores de los elementos de la IGU
     */
    private fun initStoryGUIValues(tVwl: TextView, tVit: TextView, tIli: TextInputLayout){
        tVwl.text = madWordsLeft.toString() + " word(s) left"
        index = numberOfMadWords - madWordsLeft
        tVit.text = "please type a/an " + l[index].substring(1, l[index].length - 1)
        tIli.hint = l[index].substring(1, l[index].length - 1)
    }

    /*
     * Usamos esta función para inicializar nuevamente algunos valores mientras la aplicación sigue ejecutandose.
     * Asi podremos cambiar entre la actividad << story_activity >> y la actividad <<activity_read_words>> y seguir el
     * flujo de la aplicación.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            // Inicializar variables.
            initStoryValues()

            // Inicializar valores GUI
            initStoryGUIValues(findViewById<TextView>(R.id.text_view_words_left),
                    findViewById<TextView>(R.id.text_view_input_type),
                    findViewById<TextInputLayout>(R.id.text_input_layout_hint))

            findViewById<TextInputEditText>(R.id.input_mad_word).setText("")
        }
    }

    /*
     * La función lee una historia que esta almacenada en los recursos de la aplicación en un
     * archivo de texto, la historia es seleccionada de manera aleatoria de la lista de historias
     * que hay.
     * La función retorna una lista que contiene el titulo de la historia y el texto de la misma.
     */
    private fun readMads(rsn: Int): List<String> {
        val stories = mapOf("Simple" to R.raw.madlib0_simple,
                "Tarzan" to R.raw.madlib1_tarzan, "University" to R.raw.madlib2_university,
                "Clothes" to R.raw.madlib3_clothes, "Dance" to R.raw.madlib4_dance)

        val storyNames = stories.keys.toList()
        var story = ""
        val inputFile = Scanner(stories[storyNames[rsn]]?.let { resources.openRawResource(it) })

        while (inputFile.hasNextLine())
            story += (inputFile.nextLine())

        return listOf(storyNames[rsn], story)
    }

    /*
     * Retorna la cantidad de Mad Words que hay en el texto de la historia.
     */
    private fun countMadWords(myStory: String): Int {
        var nWords = 1
        val rgx = Regex("<.*?>")
        val blanks = rgx.findAll(myStory)
        return blanks!!.count()
    }

    /*
     * Devuelve una lista con las Mad Words.
     */
    private fun getMadWords(myStory: String): MutableList<String> {
        var madWordsList: MutableList<String> = mutableListOf()

        val rgx = Regex("<.*?>")
        val madWords = rgx.findAll(myStory)

        for (madWord in madWords) {
            madWordsList.add(madWord!!.value.toString())
        }

        return madWordsList
    }

    /*
     * Retorna un número aleatorio entre 0 y 4
     */
    private fun getRandomStoryNumber(): Int {
        val randomInteger = (0..4).shuffled().first()
        return randomInteger.toInt()
    }
}