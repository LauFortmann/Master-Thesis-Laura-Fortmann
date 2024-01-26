package com.example.beautifulmind

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.beautifulmind.databinding.FragmentEnterGenderBinding
import com.example.beautifulmind.databinding.FragmentEnterTimeBinding

//4.4.4 in master thesis
class enterTime : Fragment() {
    private lateinit var binding: FragmentEnterTimeBinding
    // the users should enter a time when to perform the task
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEnterTimeBinding.inflate(inflater, container, false)
        val name = requireArguments().getString("user_name")
        val age = requireArguments().getString("user_age")
        val gender= requireArguments().getString("user_gender")

        binding.btnTime.setOnClickListener{
            val time = binding.etTime.text.toString()

            if(time.contains(":")) {
                if(requireArguments().getString("edit")=="edit"){
                    Toast.makeText(
                        activity,
                        "vorher waren wir bei der profile page",
                        Toast.LENGTH_LONG
                    ).show()
                    val bundle = bundleOf(
                        "user_time" to binding.etTime.text.toString(),
                        "user_age" to requireArguments().getString("user_age"),
                        "user_gender" to requireArguments().getString("user_gender"),
                        "user_name" to requireArguments().getString("user_name")
                    )
                    it.findNavController()
                        .navigate(R.id.action_enterTime_to_overview_Profile, bundle)
                } else {
                    val bundle = bundleOf(
                        "user_name" to name,
                        "user_age" to age,
                        "user_gender" to gender,
                        "user_time" to time,
                        "edit" to "not_edit"
                    )
                    it.findNavController()
                        .navigate(R.id.action_enterTime_to_overview_Profile, bundle)

                }
            }
            else{

                Toast.makeText(activity, "You have to provide a valid time hh:mm", Toast.LENGTH_LONG).show()
            }

        }
        // Inflate the layout for this fragment
        return binding.root
    }
}