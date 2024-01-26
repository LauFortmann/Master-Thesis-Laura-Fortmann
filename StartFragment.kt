package com.example.beautifulmind

import androidx.navigation.findNavController
import com.example.beautifulmind.R
import com.example.beautifulmind.UserViewModel
import com.example.beautifulmind.UserViewModelFactory

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.beautifulmind.databinding.FragmentStartBinding
import com.example.beautifulmind.databinding.FragmentStartLabellingBinding
import com.example.beautifulmind.dbUser.UserDatabase

class StartFragment : Fragment() {
    private lateinit var binding: FragmentStartBinding



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        binding = FragmentStartBinding.inflate(inflater, container, false)
        val dao = UserDatabase.getInstance(requireActivity().application).userDao
        val factory = UserViewModelFactory(dao)
        val viewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]
        binding.btnStart.setOnClickListener{ it ->
            viewModel.users.observe(viewLifecycleOwner){
            // check if users are already registered, if yes skip getting name, age, gender and time
                if(it.isEmpty()){
                    findNavController()
                        .navigate(R.id.action_startFragment_to_describePurpose)
                }
                else{
                    findNavController().navigate(R.id.action_startFragment_to_saveRecording)
                }
            }


        }
        // Inflate the layout for this fragment
        return binding.root
    }


}