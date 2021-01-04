package com.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.media.MediaRecorder
import android.os.*
import android.view.View
import android.widget.Chronometer
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.kotlinpermissions.KotlinPermissions
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {
    val ACTIVITY_RECORD_SOUND = 0

    val DIRECTORY_NAME_TEMP = "AudioTemp"
    val REPEAT_INTERVAL = 40
    var txtRecord: TextView? = null
    var txtReset: TextView? = null
    var txtDelete: TextView? = null
    var chronometer: Chronometer? = null

    var visualizerView: VisualizerView? = null

    private var recorder: MediaRecorder? = null

    var audioDirTemp: File? = null
    private var isRecording = false


    private var handler // Handler for updating the visualizer
            : Handler? = null

    // private boolean recording; // are we currently recording?
    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        visualizerView = findViewById<View>(R.id.visualizer) as VisualizerView

        txtRecord = findViewById<View>(R.id.txtRecord) as TextView
        txtReset = findViewById<View>(R.id.txtReset) as TextView
        txtDelete = findViewById<View>(R.id.txtDelete) as TextView
        chronometer = findViewById<View>(R.id.chronometer) as Chronometer



    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun deleteRecorder() {
        val root = Environment.getExternalStorageDirectory().toString()
        if (recorder != null) {
            isRecording = false // stop recording
            handler!!.removeCallbacks(updateVisualizer)
            chronometer!!.setBase(SystemClock.elapsedRealtime())
            visualizerView!!.clear()
            visualizerView!!.visibility = View.INVISIBLE
            recorder!!.stop()
            recorder!!.reset()
            recorder!!.release()
            recorder = null
            chronometer!!.clearComposingText()

            chronometer!!.stop()
            chronometer!!.resetPivot()

            chronometer!!.isCountDown = false

            val file = File(
                root + "/AudioTemp/audio_file"
                        + ".mp3"
            )
            deleteFilesInDir(file)
        } else {
            chronometer!!.clearComposingText()
            chronometer!!.setBase(SystemClock.elapsedRealtime())
            chronometer!!.stop()
            chronometer!!.resetPivot()

            chronometer!!.isCountDown = false
            visualizerView!!.visibility = View.INVISIBLE
            val file = File(
                root + "/AudioTemp/audio_file"
                        + ".mp3"
            )
            deleteFilesInDir(file)
        }
    }

    override fun onResume() {
        super.onResume()
        methodWithPermissions()
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun releaseRecorder() {
        if (recorder != null) {
            isRecording = false // stop recording
            handler!!.removeCallbacks(updateVisualizer)
            visualizerView!!.clear()

            recorder!!.stop()
            recorder!!.reset()
            recorder!!.release()
            recorder = null
            chronometer!!.clearComposingText()

            chronometer!!.stop()
            chronometer!!.resetPivot()

            chronometer!!.isCountDown = false

        }
    }

    fun deleteFilesInDir(path: File): Boolean {
        if (path.exists()) {
            val files = path.listFiles() ?: return true
            for (i in files.indices) {
                if (files[i].isDirectory) {
                } else {
                    files[i].delete()
                }
            }
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseRecorder()
    }

    // updates the visualizer every 50 milliseconds
    var updateVisualizer: Runnable = object : Runnable {
        override fun run() {
            if (isRecording) // if we are already recording
            {
                // get the current amplitude
                val x = recorder!!.maxAmplitude
                visualizerView!!.addAmplitude(x.toFloat()) // update the VisualizeView
                visualizerView!!.invalidate() // refresh the VisualizerView

                // update in 40 milliseconds
                handler!!.postDelayed(this, REPEAT_INTERVAL.toLong())
            }
        }
    }


    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.N)
    fun methodWithPermissions() {
        KotlinPermissions.with(this) // where this is an FragmentActivity instance
            .permissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .onAccepted { permissions ->
                //List of accepted permissions
                clickListener()
            }
            .onDenied { permissions ->
                //List of denied permissions
           Toast.makeText(this,"Please Enable All Permissions",Toast.LENGTH_SHORT).show()
            }
            .onForeverDenied { permissions ->
                //List of forever denied permissions
                Toast.makeText(this,"Please Enable All Permissions",Toast.LENGTH_SHORT).show()

            }
            .ask()
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun clickListener() {
        // Do the stuff with permissions safely
        txtReset!!.setOnClickListener {
            txtRecord!!.text = "Start Recording"
            if (recorder != null) {
                isRecording = false // stop recording
                handler!!.removeCallbacks(updateVisualizer)
                chronometer!!.setBase(SystemClock.elapsedRealtime())
                visualizerView!!.clear()
                visualizerView!!.visibility = View.INVISIBLE
                recorder!!.stop()
                recorder!!.reset()
                recorder!!.release()
                recorder = null
                chronometer!!.clearComposingText()
                chronometer!!.stop()
                chronometer!!.resetPivot()
                chronometer!!.isCountDown = false
            } else {
                chronometer!!.clearComposingText()
                chronometer!!.setBase(SystemClock.elapsedRealtime())
                chronometer!!.stop()
                chronometer!!.resetPivot()
                chronometer!!.isCountDown = false
                visualizerView!!.visibility = View.INVISIBLE
            }
        }
        txtDelete!!.setOnClickListener {
            deleteRecorder()
        }

        txtRecord!!.setOnClickListener {
//            val intent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
//            startActivityForResult(intent, ACTIVITY_RECORD_SOUND)
            if (!isRecording) {
                // isRecording = true;
                chronometer!!.start()
                chronometer!!.setBase(SystemClock.elapsedRealtime())
                txtRecord!!.text = "Stop Recording"
                visualizerView!!.visibility = View.VISIBLE
                recorder = MediaRecorder()
                recorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
                recorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                recorder!!.setOutputFile(
                    audioDirTemp.toString() + "/audio_file"
                            + ".mp3"
                )
                val errorListener: MediaRecorder.OnErrorListener? = null
                recorder!!.setOnErrorListener(errorListener)
                val infoListener: MediaRecorder.OnInfoListener? = null
                recorder!!.setOnInfoListener(infoListener)
                try {
                    recorder!!.prepare()
                    recorder!!.start()
                    isRecording = true // we are currently recording
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                handler!!.post(updateVisualizer)
            } else {
                txtRecord!!.text = "Start Recording"
                releaseRecorder()
            }
        }
        audioDirTemp = File(
            Environment.getExternalStorageDirectory(),
            DIRECTORY_NAME_TEMP
        )
        if (audioDirTemp!!.exists()) {
            deleteFilesInDir(audioDirTemp!!)
        } else {
            audioDirTemp!!.mkdirs()
        }
        // create the Handler for visualizer update
        handler = Handler()
    }



}