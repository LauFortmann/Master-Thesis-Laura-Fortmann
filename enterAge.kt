package com.example.beautifulmind

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.beautifulmind.databinding.FragmentEnterAgeBinding



class enterAge : Fragment() {
    private lateinit var binding: FragmentEnterAgeBinding
    // the users are asked to enter their age

    //4.4.2 in master thesis
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEnterAgeBinding.inflate(inflater, container, false)
        val input = requireArguments().getString("user_name")
        binding.btnAge.setOnClickListener{
            if(!TextUtils.isEmpty(binding.etAge.text.toString())){
                if(requireArguments().getString("edit")=="edit"){

                    val bundle = bundleOf("user_age" to binding.etAge.text.toString(),
                        "user_name" to requireArguments().getString("user_name"),
                        "user_gender" to requireArguments().getString("user_gender"),
                        "user_time" to requireArguments().getString("user_time") )
                    it.findNavController().navigate(R.id.action_enterAge_to_overview_Profile, bundle)
                }else{


                val bundle = bundleOf("user_age" to binding.etAge.text.toString(), "user_name" to input.toString(), "edit" to "not_edit")
                it.findNavController().navigate(R.id.action_enterAge_to_enterGender,bundle)
            }}
            else{
                Toast.makeText(activity, "You have to provide an age!", Toast.LENGTH_LONG).show()
            }

        }
        // Inflate the layout for this fragment
        return binding.root
    }
}