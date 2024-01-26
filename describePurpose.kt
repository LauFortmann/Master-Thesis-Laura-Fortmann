package com.example.beautifulmind

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.example.beautifulmind.databinding.FragmentDescribePurposeBinding
import com.example.beautifulmind.databinding.FragmentEnterAgeBinding


class describePurpose : Fragment() {
    private lateinit var binding:FragmentDescribePurposeBinding

    //displays the introduction to the app
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDescribePurposeBinding.inflate(inflater, container, false)
        binding.btnStartBM.setOnClickListener{
            it.findNavController().navigate(R.id.action_describePurpose_to_enterName, bundleOf("edit" to "not_edit"))
        }
        return binding.root
    }



}