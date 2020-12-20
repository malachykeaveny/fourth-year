package com.example.seatpickerapp.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.seatpickerapp.DashboardActivity
import com.example.seatpickerapp.LoadRestaurantListener
import com.example.seatpickerapp.ReservationActivity
import com.example.seatpickerapp.databinding.FragmentRsrvStepOneBinding
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.jaredrummler.materialspinner.MaterialSpinner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.lang.StringBuilder

// TODO: Rename parameter arguments, choose names that match


/**
 * A simple [Fragment] subclass.
 * Use the [RsrvStepOneFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RsrvStepOneFragment : Fragment() {

    private var _binding: FragmentRsrvStepOneBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var allRestaurants: CollectionReference
    lateinit var branchReference: CollectionReference
    //lateinit var restaurantList: ArrayList<String>
    private val list: MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //loadRestaurantListener = this;
        allRestaurants = FirebaseFirestore.getInstance().collection("restaurants")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        loadRestaurant()

        _binding = FragmentRsrvStepOneBinding.inflate(inflater, container, false)
        val view = binding.root
        return view

    }

    private fun loadRestaurant() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val querySnapshot = allRestaurants.get().await()
            //val querySnapshot = tableCollectionRef.document()
            val sb = StringBuilder()
            list.add("Choose Restaurant")
            for(document in querySnapshot.documents) {
                //val table = document.toObject<Day>()
                list.add(document.id)
                val table = document.id
                sb.append("$table\n")
                Log.d("RsrvStepOneFragment", sb.toString())
                Log.d("RsrvStepOneFragment", list.toString())
            }
            withContext(Dispatchers.Main) {
                onRestaurantLoadSuccess(list)
            }
        }
        catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onRestaurantLoadFailed(e.message)
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    //fun onRestaurantLoadSuccess(restaurantList: List<String?>?) {
    private fun onRestaurantLoadSuccess(restaurantList: MutableList<String>) {
        binding.materialSpinner.setItems(restaurantList)

        binding.materialSpinner.setOnItemSelectedListener { view, position, id, item ->
            if (position > 0) {
                startActivity(Intent(context, DashboardActivity::class.java))
            }
        }
    }

    fun onRestaurantLoadFailed(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}