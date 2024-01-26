package com.example.beautifulmind

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.beautifulmind.databinding.FragmentAskForLabelBinding
import com.example.beautifulmind.databinding.FragmentDescribePurposeBinding


class askForLabel : Fragment() {
    // ask for whether to labelling should start
    private lateinit var binding:FragmentAskForLabelBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAskForLabelBinding.inflate(inflater, container, false)
        binding.btnLabel.setOnClickListener{
            it.findNavController().navigate(R.id.action_askForLabel_to_startLabelling)
        }

        // Inflate the layout for this fragment
        return binding.root
    }


}