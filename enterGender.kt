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

// 4.4.3 in master thesis
class enterGender : Fragment() {
    private lateinit var binding: FragmentEnterGenderBinding
    // the users are asked to enter their gender via a single choice field, all genders are included
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEnterGenderBinding.inflate(inflater, container, false)
        val name = requireArguments().getString("user_name")
        val age = requireArguments().getString("user_age")


        binding.btnGender.setOnClickListener{
            val radioGroup = binding.rgGender
            val selectedId = radioGroup.getCheckedRadioButtonId()
            val radioButton = radioGroup.findViewById<RadioButton>(selectedId)

            if(radioGroup.getCheckedRadioButtonId() != -1){
                if(requireArguments().getString("edit")=="edit"){

                    val bundle = bundleOf(
                        "user_gender" to radioButton.getText(),
                        "user_name" to requireArguments().getString("user_name"),
                        "user_age" to requireArguments().getString("user_age"),
                        "user_time" to requireArguments().getString("user_time")
                    )
                    it.findNavController()
                        .navigate(R.id.action_enterGender_to_overview_Profile, bundle)
                }
                    else{
                    val bundle = bundleOf(
                        "user_name" to name,
                        "user_age" to age,
                        "user_gender" to radioButton.getText(),
                        "edit" to "not_edit"
                    )
                    it.findNavController().navigate(R.id.action_enterGender_to_enterTime, bundle)
                }
            }
            else{

                Toast.makeText(activity, "You have to provide a gender!", Toast.LENGTH_LONG).show()
            }

        }
        // Inflate the layout for this fragment
        return binding.root
    }
}
