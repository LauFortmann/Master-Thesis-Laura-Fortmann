package com.example.beautifulmind

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.beautifulmind.databinding.FragmentEnterNameBinding

// 4.4.1 in master thesis
class enterName : Fragment() {
    private lateinit var binding: FragmentEnterNameBinding
    // the users should enter their name
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEnterNameBinding.inflate(inflater, container, false)
        val userName = binding.etName
        binding.btnName.setOnClickListener {


            if(!TextUtils.isEmpty(binding.etName.text.toString())) {

                if(requireArguments().getString("edit")=="edit"){
                    Toast.makeText(activity, "vorher waren wir bei der profile page", Toast.LENGTH_LONG).show()
                    val bundle = bundleOf("user_name" to binding.etName.text.toString(),
                        "user_age" to requireArguments().getString("user_age"),
                        "user_gender" to requireArguments().getString("user_gender"),
                        "user_time" to requireArguments().getString("user_time") )
                    it.findNavController().navigate(R.id.action_enterName_to_overview_Profile, bundle)
                }
                else {

                    val bundle = bundleOf("user_name" to binding.etName.text.toString(), "edit" to "not_edit")
                    it.findNavController().navigate(R.id.action_enterName_to_enterAge, bundle)
                }
            }

            else{
                Toast.makeText(activity, "You have to provide a name!", Toast.LENGTH_LONG).show()
        }

        }
        // Inflate the layout for this fragment
        return binding.root
    }


}