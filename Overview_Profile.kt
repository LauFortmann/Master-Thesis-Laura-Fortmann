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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.beautifulmind.databinding.FragmentEnterAgeBinding
import com.example.beautifulmind.databinding.FragmentOverviewProfileBinding
import com.example.beautifulmind.dbUser.User
import com.example.beautifulmind.dbUser.UserDatabase

class Overview_Profile : Fragment() {
    private lateinit var binding: FragmentOverviewProfileBinding
    // displays all information, give the users the opportunity changing information and create database instance
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOverviewProfileBinding.inflate(inflater, container, false)
        val dao = UserDatabase.getInstance(requireActivity().application).userDao
        val factory = UserViewModelFactory(dao)
        val viewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]

        val name = requireArguments().getString("user_name")

        Toast.makeText(activity, "${this.id}", Toast.LENGTH_LONG).show()
        val age = requireArguments().getString("user_age")
        val gender = requireArguments().getString("user_gender")
        val time = requireArguments().getString("user_time")
        binding.tvDisplayName.setText(name)
        binding.tvDisplayAge.setText(age)
        binding.tvDisplayGender.setText(gender)
        binding.tvDisplayTime.setText(time)

        val dictionary = mapOf("female" to 0, "male" to 1, "non binary" to 2, "no gender" to 3, "no answer" to 4, "not listed here" to 5)

        binding.btnEditAge.setOnClickListener{
            var bundle = bundleOf( "user_gender" to gender, "user_time" to time, "user_name" to name, "edit" to "edit")
            it.findNavController().navigate(R.id.action_overview_Profile_to_enterAge, bundle)
        }
        binding.btnEditName.setOnClickListener{
            var bundle = bundleOf( "user_gender" to gender, "user_time" to time, "user_age" to age, "edit" to "edit")
            it.findNavController().navigate(R.id.action_overview_Profile_to_enterName, bundle)
        }
        binding.btnEditGender.setOnClickListener{
            var bundle = bundleOf(  "user_time" to time, "user_name" to name, "user_age" to age, "edit" to "edit")
            it.findNavController().navigate(R.id.action_overview_Profile_to_enterGender, bundle)
        }
        binding.btnEditTime.setOnClickListener{
            var bundle = bundleOf( "user_gender" to gender, "user_name" to name, "user_age" to age, "edit" to "edit")
            it.findNavController().navigate(R.id.action_overview_Profile_to_enterTime, bundle)
        }

        binding.btnCreateProfile.setOnClickListener{
            val gender_converted = dictionary[gender]
            if(name != null && age != null && gender_converted != null && time != null){


                viewModel.insertUser(User(
                    null,
                    name.toString(),
                    age.toInt(),
                    gender_converted,
                    time.toString()

                ))
                Toast.makeText(activity, "User has been created succesfully", Toast.LENGTH_LONG).show()

                it.findNavController().navigate(R.id.action_overview_Profile_to_saveRecording)
        }}
        // Inflate the layout for this fragment
        return binding.root
    }
}