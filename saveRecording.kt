package com.example.beautifulmind

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.renderscript.Element
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.health.services.client.HealthServices
import androidx.health.services.client.awaitWithException
import androidx.health.services.client.data.DataType
import androidx.health.services.client.proto.DataProto
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.beautifulmind.databinding.FragmentSaveRecordingBinding
import com.example.beautifulmind.dbRecording.Recording
import com.example.beautifulmind.dbRecording.RecordingDatabase
import com.example.beautifulmind.dbUser.UserDatabase
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.wearable.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime
import java.util.Timer
import kotlin.coroutines.suspendCoroutine
import kotlin.math.pow

// 4.5.2 in master thesis

class saveRecording : Fragment() {

    private lateinit var binding: FragmentSaveRecordingBinding
    private var MICROPHONE_PERMISSION_CODE:Int = 200
    private lateinit var mediaRecorder : MediaRecorder
    private var trialNr: Int=0
    private lateinit var hrRecording : ArrayList<Int>
    private lateinit var heartRate: ArrayList<Double>
    private lateinit var hr_computed: ArrayList<Double>
    private lateinit var last_rec_path:String




    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSaveRecordingBinding.inflate(inflater, container, false)

        if (isMicrophonePresent() == true) {

            getMicrophonePermission()
        }




        val dao = RecordingDatabase.getInstance(requireActivity().application).recordingDao
        val factory = RecordingViewModelFactory(dao)
        val viewModel = ViewModelProvider(this, factory)[RecordingViewModel::class.java]


        binding.btnRecord.setOnClickListener{
            val path = commonDocumentDirPath("recordings_beatiful_mind")
            mediaRecorder = MediaRecorder()
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            last_rec_path= LocalDateTime.now().toString().replace(":", ".")
            mediaRecorder.setOutputFile("$path/$last_rec_path")
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mediaRecorder.prepare()
            mediaRecorder.start()
            binding.tvRecording.setText("Recording...")



        }

        binding.btnStop.setOnClickListener{
            mediaRecorder.stop()
            mediaRecorder.release()
            trialNr += 1
            binding.tvRecording.setText("saved")
            binding.btnSave.setVisibility(View.VISIBLE)
        }

        binding.btnSave.setOnClickListener{
            val path = commonDocumentDirPath("recordings_beatiful_mind")
            val completePath = ("$path/$last_rec_path")
            val dataClient: DataClient = Wearable.getDataClient(requireActivity())
            val dataItems = dataClient.dataItems
            var computed = ArrayList<Double>()


            dataItems.addOnCompleteListener { task ->
                // get heart rate data from data layer
                if (task.isSuccessful) {
                    val result = task.result
                    if (result != null && result.count != 0) {
                        result.forEach { dataItem ->
                            if (dataItem.uri.path == "/heart_rate_data") {
                                val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
                                val heartRate = dataMap.getIntegerArrayList("heart_rate")
                                if (heartRate != null) {
                                    val computedValue = computeStatistics(heartRate)
                                    computed=computedValue
                                }
                            }
                        }

                        // insert recording instance to database
                        viewModel.insertRecording(
                            Recording(
                                null,
                                LocalDateTime.now().toString(),
                                completePath,
                                computed as ArrayList<Double>,
                                0
                            )
                        )

                    }
                }
            }


                // Use the computed data here




            //var computed = getHeartRate()

            it.findNavController().navigate(R.id.action_saveRecording_to_askForLabel)
        }
        //getPermissionsHeartRate()


    return binding.root
    }

    private fun isMicrophonePresent(): Boolean? {
        return this.activity?.packageManager?.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)

    }

    private fun getMicrophonePermission() {

        if (activity?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.RECORD_AUDIO,

                    )
            } == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.RECORD_AUDIO), MICROPHONE_PERMISSION_CODE
            )
        }
    }


    @RequiresApi(Build.VERSION_CODES.S)
    fun commonDocumentDirPath(FolderName: String): File? {
        var dir: File? = null
        dir =
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                    .toString() + "/" + FolderName
            )

        // Make sure the path directory exists.
        if (!dir.exists()) {
            // Make it, if it doesn't exit
            val success = dir.mkdirs()
            if (!success) {
                dir = null
            }
            Log.i("folder", "$success has been created")
        }
        return dir
    }

    //4.7.1 in master thesis
    private fun computeStatistics(hr:ArrayList<Int>):ArrayList<Double>{

        if(hr.size!=0) {

            var range = hr.maxOrNull()?.minus(hr.minOrNull()!!)?.toDouble()
            var median = getMedian(hr).toDouble()
            var mean = hr.average()
            var variance = getVariance(hr, mean)
            Log.i("heartRate", "range $range median $median mean $mean variance $variance")
            if (range != null) {
                return arrayListOf(range, median, mean, variance)
            } else {
                return arrayListOf(0.0)
            }
        }
        else{

            return arrayListOf(0.0)
        }
    }

    private fun getMedian(list:ArrayList<Int>)= list.sorted().let {
        if (it.size % 2 == 0)
            (it[it.size / 2] + it[(it.size - 1) / 2]) / 2
        else
            it[it.size / 2]

    }

    private fun getVariance(list:ArrayList<Int>, mean:Double):Double{
        var result:Double= 0.0

        for (i in list){
            result += (i-mean)*(i-mean)
        }
        return result/(list.size-1)
    }

    private fun storeRecording(hr:ArrayList<Double>, path_recording:String){

    }





}