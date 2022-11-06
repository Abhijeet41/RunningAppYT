package com.abhi41.runnigappyt.presentation.screen

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.abhi41.runnigappyt.R
import com.abhi41.runnigappyt.databinding.FragmentSettingsBinding
import com.abhi41.runnigappyt.utils.Constants.KEY_NAME
import com.abhi41.runnigappyt.utils.Constants.KEY_WEIGHT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    lateinit var binding: FragmentSettingsBinding
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_settings,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment =
            activity?.supportFragmentManager?.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        loadFieldFromSharedPref()
        binding.btnApplyChanges.setOnClickListener {
            val success = appChangesToSharedPref()
            if (success){
                Snackbar.make(view,"Saved Changes",Snackbar.LENGTH_LONG).show()
            }else{
                Snackbar.make(view,"Saved Changes",Snackbar.LENGTH_LONG).show()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // in here you can do logic when backPress is clicked
                navController.navigate(R.id.action_settingsFragment_to_runFragment)
            }
        })
    }

    private fun appChangesToSharedPref(): Boolean {
        val nameText = binding.etName.text.toString()
        val weightText = binding.etWeight.text.toString()

        if (nameText.isEmpty() || weightText.isEmpty()){
            false
        }
        sharedPreferences.edit()
            .putString(KEY_NAME,nameText)
            .putFloat(KEY_WEIGHT,weightText.toFloat())
            .apply()

        val toolBartext = "Let's Go $nameText"
        requireActivity().tvToolbarTitle.text = toolBartext
        return true
    }

    private fun loadFieldFromSharedPref(){
        val name = sharedPreferences.getString(KEY_NAME,"")
        val weight = sharedPreferences.getFloat(KEY_WEIGHT,80f)
        binding.etName.setText(name)
        binding.etWeight.setText(weight.toString())
    }

}