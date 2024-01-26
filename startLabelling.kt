package com.example.beautifulmind


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.beautifulmind.databinding.FragmentStartLabellingBinding
import com.example.beautifulmind.dbRecording.Recording
import com.example.beautifulmind.dbRecording.RecordingDatabase
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.reflect.Field


class startLabelling : Fragment() {
    private lateinit var binding:FragmentStartLabellingBinding
    private lateinit var dictDiscreteToNumber:Map<Number, String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentStartLabellingBinding.inflate(inflater, container, false)
        val dao = RecordingDatabase.getInstance(requireActivity().application).recordingDao
        val factory = RecordingViewModelFactory(dao)
        val viewModel = ViewModelProvider(this, factory)[RecordingViewModel::class.java]

        dictDiscreteToNumber = mapOf(0 to "happy", 1 to "sad", 2 to "afraid", 3 to "disgusted", 4 to "angry", 5 to "neutral")


        if (! Python.isStarted()) {
            this.let { AndroidPlatform(requireActivity()) }?.let { Python.start(it) }
        }
        val python = Python.getInstance()
        val pyobj = python.getModule("labelling")

        activity?.let {
            viewModel.last.observe(viewLifecycleOwner) {

                val hr_converted = FloatArray(it.recording_heartrate.size)
                            // works!!!!
                for(i in 0..it.recording_heartrate.size-1){
                    hr_converted[i]= it.recording_heartrate[i].toFloat()
                }

                var score_hr = label_hr(
                    hr_converted
                )

                fix()

                //4.8.1 in master thesis
                // label speech by corresponding python functions
                val model = pyobj.callAttr("get_model")
                val processor = pyobj.callAttr("get_processor")
                val file = pyobj.callAttr("read_file", it.recording_voice)
                val pre_processed_data = pyobj.callAttr("preprocess_data", processor, file)

                //4.8.4 in master thesis
                val label_voice = pyobj.callAttr("label_data", pre_processed_data, model)



                var single_val = label_voice.toString().split(',')

                val single_val_hr = score_hr.take(6)

                // display results
                binding.tvDisplayResultHappy.setText(single_val_hr[0].toString())
                binding.tvDisplayResultSadness.setText(single_val_hr[1].toString())
                binding.tvDisplayResultFear.setText(single_val_hr[2].toString())
                binding.tvDisplayResultDisgust.setText(single_val_hr[3].toString())
                binding.tvDisplayResultAnger.setText(single_val_hr[4].toString())
                binding.tvDisplayResultNeutral.setText(single_val_hr[5].toString())
                binding.tvDisplayArousal.setText(single_val.take(1)[0].replace("[",""))
                binding.tvDisplayDom.setText(single_val.take(2)[1])
                binding.tvDisplayValence.setText(single_val.take(3)[2].replace("]",""))

                var overall = sumHrAndSpeechPrediction(single_val_hr[0]
                    ,single_val_hr[1]
                    ,single_val_hr[2],
                    single_val_hr[3],
                    single_val_hr[4],
                    single_val_hr[5],
                    single_val.take(1)[0].replace("[","").toFloat(),
                    single_val.take(2)[1].toFloat(),
                    single_val.take(3)[2].replace("]","").toFloat())

                binding.tvOverall.setText("you are ${dictDiscreteToNumber[overall]} !!!")

                viewModel.updateRecording(Recording(
                    it.id,
                    it.time,
                    it.recording_voice,
                    it.recording_heartrate,
                    overall

                ))

            }

        }

        return binding.root
    }

    fun assetFilePath(context: Context, asset: String): String {
        val file = File(context.filesDir, asset)

        try {
            val inpStream: InputStream = context.assets.open(asset)
            try {
                val outStream = FileOutputStream(file, false)
                val buffer = ByteArray(4 * 1024)
                var read: Int

                while (true) {
                    read = inpStream.read(buffer)
                    if (read == -1) {
                        break
                    }
                    outStream.write(buffer, 0, read)
                }
                outStream.flush()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    //4.8.3 master thesis
    private fun label_hr(hr_features: FloatArray): FloatArray {
        // labels heart rate by loading model and feeding recording to model
        val module = Module.load(activity?.let { assetFilePath(it, "hr_model_without_softmax.ptl") })

        val inputTensor = Tensor.fromBlob(hr_features, longArrayOf(1, 4))

        val output: IValue = module.forward(IValue.from(inputTensor))
        val outputTensor: Tensor = output.toTensor()



        return outputTensor.dataAsFloatArray

    }
    fun fix() {
        try {
            val clazz = Class.forName("java.lang.Daemons\$FinalizerWatchdogDaemon")
            val method = clazz.superclass.getDeclaredMethod("stop")
            method.isAccessible = true
            val field: Field = clazz.getDeclaredField("INSTANCE")
            field.setAccessible(true)
            method.invoke(field.get(null))
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    //4.8.5 in master thesis
    private fun sumHrAndSpeechPrediction(happy:Float, sad:Float, fear:Float, disgust:Float, angry:Float, neutral:Float, arousal:Float, valence:Float, dominance:Float): Int {
        // combines the two predictions
        val discrete = continuousToDiscrete(arousal, valence, dominance)
        val values = floatArrayOf(happy, sad, fear, disgust, angry, neutral)
        val maxIdx = values.indices.maxBy { values[it] } ?: -1
        if(discrete==maxIdx){
            return discrete
        }
        else{
            val values_cp = FloatArray(values.size-1)
            values_cp[maxIdx]= 0F
            val maxIdxSec = values_cp.indices.maxBy{values[it]}?:-1
            return if(discrete==maxIdxSec){
                discrete
            } else{
                maxIdx
            }
        }
    }

    //3.3.4 in master thesis
    private fun continuousToDiscrete(arousal:Float, valence:Float, dominance:Float):Int{
        // transforms continuous emotion prediction by speech model to a discrete one
        if (valence >0.5) {
            return 0
        }
        else if(arousal <0.5) {
                return 1
            }

        else if(arousal > 0.75 && dominance <0.5){
                return 2
            }

        else if (arousal == 0.75F) {
            return 3
        }
        else if (arousal > 0.75 && dominance >0.5) {
            return 4
        }
        else {
            return 5
        }

    }



}