package com.example.appcontrol

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import com.example.appcontrol.databinding.FragmentGardenBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.math.abs
import kotlin.properties.Delegates
import kotlinx.coroutines.*
private lateinit var binding: FragmentGardenBinding

/*class PowerStatusService : Service() {
    private lateinit var powerRef: DatabaseReference
    private lateinit var notificationManager: NotificationManager
    private lateinit var ringtone: Ringtone
    private val notificationChannelId = "PowerStatusChannel"
    private val powerStatusNotificationId = 1

    override fun onCreate() {
        super.onCreate()
        powerRef = FirebaseDatabase.getInstance().getReference("Node1/SOS")
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(applicationContext, notificationSoundUri)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelId,
                "Power Status Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for power status notifications"
                enableLights(true)
                lightColor = Color.RED
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationBuilder = NotificationCompat.Builder(applicationContext, notificationChannelId)
            .setContentTitle("Power Status")
            .setContentText("Checking power status...")
            .setSmallIcon(R.drawable.error)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        startForeground(powerStatusNotificationId, notificationBuilder.build())
        powerRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Get the data from the snapshot
                val powerStatus = snapshot.getValue<String>()
                Log.d("value_powerStatus", "Value is: $powerStatus")
                if (powerStatus == "true") {
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    val pendingIntent = PendingIntent.getActivity(
                        applicationContext,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    val notificationBuilder = NotificationCompat.Builder(applicationContext, notificationChannelId)
                        .setContentTitle("Power Status")
                        .setContentText("Power status is disconnected")
                        .setSmallIcon(R.drawable.error)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                    notificationManager.notify(powerStatusNotificationId, notificationBuilder.build())

                   *//* if (!ringtone.isPlaying) {
                        ringtone.play()
                    }*//*
                }else {
                    notificationBuilder.setContentText("Power status is connected")
                    notificationManager.notify(powerStatusNotificationId, notificationBuilder.build())

                    *//*if (ringtone.isPlaying) {
                        ringtone.stop()
                    }*//*
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        })
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}*/

class GardenFragment : Fragment() {

    val database = FirebaseDatabase.getInstance()
    val TimeOn = database.getReference("Node1/TimeOn")
    val TimeOff = database.getReference("Node1/TimeOff")
    val flag = database.getReference("Node1/Flag")
    val weather = database.getReference("Node1/Weather")
    val power = database.getReference("Node1/Current")
    val tempref = database.getReference("Node1/Temp")
    val humiref = database.getReference("Node1/Humi")
    val lightTime = database.getReference("Node1/TimeLight")
    val loraRef = database.getReference("Node1/Lora")
    val autoRef = database.getReference("Node1/Auto")
    val ledRef = database.getReference("Node1/Led")
    val buttonRef = database.getReference("Node1/Button")
    val rainRef = database.getReference("Node1/Rain")
    val openRef = database.getReference("Node1/Open")

    private val handler = Handler(Looper.getMainLooper())
    private var previousTemp: Float? = null
    private lateinit var runnableCode: Runnable



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGardenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        var startTime:String = "22:00"
        var endTime:String = "7:00"

        runnableCode = object : Runnable {
            override fun run() {
                tempref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val currentTemp = snapshot.getValue<String>()
                        binding.edtTemp.setText(currentTemp)

                        if (previousTemp != null && previousTemp == currentTemp!!.toFloat()) {
                            loraRef.setValue("false")
                        }else if(previousTemp != currentTemp!!.toFloat()){
                            loraRef.setValue("true")
                        }
                        previousTemp = currentTemp!!.toFloat()
                    }
                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })

                handler.postDelayed(this, 60000)
            }
        }

        handler.post(runnableCode)



        power.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val powerVal = dataSnapshot.getValue<String>()
                var newPower = 0F
                var count = 0
                var fireCount = 0
                if (powerVal != "") {
                    newPower = powerVal?.toFloatOrNull()!!
                    if (newPower != null) {
                        val sharedPref =
                            activity?.getSharedPreferences("PowerData", Context.MODE_PRIVATE)
                        val oldPower = sharedPref!!.getFloat("power", 0.34F)

                        if (newPower < 0.34) {
                            count++
                            if (count >= 3) {
                                val builder = AlertDialog.Builder(context)
                                builder.setMessage("Are you removing bulbs?")
                                    .setPositiveButton("Yes") { dialog, id ->
                                        with(sharedPref.edit()) {
                                            putFloat("power", newPower)
                                            apply()
                                            flag.setValue("true")
                                        }
                                        // setvalue flag on firebase to true
                                    }
                                    .setNegativeButton("No") { dialog, id ->
                                        // setvalue flag on firebase to false
                                        dialog.cancel()
                                        flag.setValue("false")
                                    }
                                    .setCancelable(false)

                                val dialog = builder.create()
                                dialog.show()

                                // If the user does not press anything for 3 minutes, automatically exit the popup and set the value flag on firebase to false
                                Handler(Looper.getMainLooper()).postDelayed({
                                    if (dialog.isShowing) {
                                        dialog.dismiss()
                                        // setvalue flag on firebase to false
                                    }
                                }, 180000) // 3 minutes in milliseconds
                            }
                        }else if (newPower < 0.14) {
                            fireCount++
                            if (fireCount >= 3) {
                                val builder = AlertDialog.Builder(context)
                                builder.setMessage("Have you received a notification of a system fire?")
                                    .setPositiveButton("Yes") { dialog, id ->
                                        with(sharedPref.edit()) {
                                            putFloat("power", newPower)
                                            apply()
                                            flag.setValue("true")
                                        }
                                        // setvalue flag on firebase to true
                                    }
                                    .setNegativeButton("No") { dialog, id ->
                                        // setvalue flag on firebase to false
                                        dialog.cancel()
                                        flag.setValue("false")
                                    }
                                    .setCancelable(false)

                                val dialog = builder.create()
                                dialog.show()

                                // If the user does not press anything for 3 minutes, automatically exit the popup and set the value flag on firebase to false
                                Handler(Looper.getMainLooper()).postDelayed({
                                    if (dialog.isShowing) {
                                        dialog.dismiss()
                                        // setvalue flag on firebase to false
                                    }
                                }, 180000) // 3 minutes in milliseconds
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })



        humiref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Get the data from the snapshot
                val himi = snapshot.getValue<String>()
//                Log.d("value_loraStatus", "Value is: $loraStatus")
                binding.edtHumi.setText(himi)

            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        weather.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Get the data from the snapshot
                val weatherIcon = snapshot.getValue<String>()
//                Log.d("value_loraStatus", "Value is: $loraStatus")
                binding.icWeather.setImageResource(R.drawable.cloudy_100)

            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        lightTime.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Get the data from the snapshot
                val timeLight = snapshot.getValue<String>()
//                Log.d("value_loraStatus", "Value is: $loraStatus")
                binding.edtLight.setText(timeLight + "h")

            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        binding.btnTimeOn.setOnClickListener {
            val currentTime = Calendar.getInstance()
            val hour = currentTime.get(Calendar.HOUR_OF_DAY)
            val minute = currentTime.get(Calendar.MINUTE)
//            startTime ="$hour:$minute"

            val timePickerDialog = TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                // Handle the selected time here
                // hourOfDay and minute are the selected time values
                // Format the time values with leading zeros if needed
                val formattedHour = String.format("%02d", hourOfDay)
                val formattedMinute = String.format("%02d", minute)

                startTime = "$formattedHour:$formattedMinute"
                binding.btnTimeOn.text = "$formattedHour:$formattedMinute"

                // Save the selected time in SharedPreferences
                val sharedPreferences = activity?.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences!!.edit()
                editor!!.putString("selectedTimeOn", "$formattedHour:$formattedMinute")
                editor.apply()

                TimeOn.setValue(startTime)
                // Calculate the time interval
                calculateTimeInterval(startTime, endTime)
                // You can use the selectedTime for further processing or display it in your UI
            }, hour, minute, false)

            timePickerDialog.show()
        }

        binding.btnTimeOff.setOnClickListener {

            val currentTime = Calendar.getInstance()
            val hour = currentTime.get(Calendar.HOUR_OF_DAY)
            val minute = currentTime.get(Calendar.MINUTE)

//            endTime ="$hour:$minute"

            val timePickerDialog = TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                // Handle the selected time here
                // hourOfDay and minute are the selected time values
                // Format the time values with leading zeros if needed
                val formattedHour = String.format("%02d", hourOfDay)
                val formattedMinute = String.format("%02d", minute)

                binding.btnTimeOff.text = "$formattedHour:$formattedMinute"

                endTime = "$formattedHour:$formattedMinute"

                // Save the selected time in SharedPreferences
                val sharedPreferences = activity?.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences!!.edit()
                editor!!.putString("selectedTimeOff", "$formattedHour:$formattedMinute")

                editor.apply()

                TimeOff.setValue(endTime)
                // You can use the selectedTime for further processing or display it in your UI
                // Calculate the time interval
                calculateTimeInterval(startTime, endTime)
            }, hour, minute, false)

            timePickerDialog.show()
        }


        buttonRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Get the data from the snapshot
                val buttonStatus = snapshot.getValue<String>()

                // Update the UI based on the data
                when(buttonStatus){
                    "true" -> {
                        binding.Led.visibility = View.GONE
                    }
                    "false" -> {
                        binding.Led.visibility = View.VISIBLE
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        binding.switchAuto.setOnCheckedChangeListener { _, isChecked ->
            // Save the state of the Switch in SharedPreferences
            val sharedPreferences = activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences!!.edit()
            editor!!.putBoolean("switchState", isChecked)
            editor.apply()
            if (isChecked) {
                // Switch is checked, show contentAuto and hide contentCustomer
                binding.contentCustome.visibility = View.GONE
                binding.contentAuto.visibility = View.VISIBLE

                // Turn off switch4
//                binding.switch4.isChecked = false

                // Update the dataset in Firebase
                ledRef.setValue("false")
                autoRef.setValue("true")
                openRef.setValue("true")
            }
            else {
                // Switch is unchecked, show constraintLayout and hide linearLayout
                binding.contentCustome.visibility = View.VISIBLE
                binding.contentAuto.visibility = View.GONE

                autoRef.setValue("false")
            }
        }


        binding.switchLed.setOnCheckedChangeListener { _, isChecked ->
            // Save the state of the Switch in SharedPreferences
            val sharedPreferences = activity?.getSharedPreferences("ledstatus", Context.MODE_PRIVATE)
            val editor = sharedPreferences!!.edit()
            editor!!.putBoolean("switchLedState", isChecked)
            editor.apply()
            if (isChecked) {
                ledRef.setValue("true")
            } else {
                ledRef.setValue("false")
            }
        }


        ledRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val switchState = snapshot.getValue<String>()
                val isChecked = switchState == "true"
                binding.switchLed.isChecked = isChecked
                val sharedPreferences = activity?.getSharedPreferences("ledstatus", Context.MODE_PRIVATE)
                val editor = sharedPreferences!!.edit()
                editor!!.putBoolean("switchLedState", isChecked)
                editor.apply()
            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(context, "Database Error", Toast.LENGTH_LONG).show()
            }
        })

        // Retrieve the state of the Switch3 from SharedPreferences
        val sharedPreferences = activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val switchState = sharedPreferences!!.getBoolean("switchState", false) // false is the default value
        binding.switchAuto.isChecked = switchState

// Retrieve the state of Switch4 from SharedPreferences
        val sharedPreferencesLed = activity?.getSharedPreferences("ledstatus", Context.MODE_PRIVATE)
        val switchLedState = sharedPreferencesLed!!.getBoolean("switchLedState", false) // false is the default value
        binding.switchLed.isChecked = switchLedState

        // Initialize the Firebase database reference
        val databasechild = FirebaseDatabase.getInstance().reference

        val edtActiveTime = binding.edtActiveTime

        // Retrieve the saved value from Firebase and set it to the EditText
        databasechild.child("Node1/TimeActive").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val activeTime = snapshot.getValue(String::class.java)
                edtActiveTime.setText(activeTime)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any error during data retrieval
            }
        })

        edtActiveTime.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val input = edtActiveTime.text.toString()
                if (input.matches(Regex("^([01]\\d|2[0-3]):([0-5]\\d)$"))) {
                    // Save the entered data to Firebase
                    databasechild.child("Node1/TimeActive").setValue(input)

                    // Hide the keyboard and clear focus from the EditText
                    val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(edtActiveTime.windowToken, 0)
                    edtActiveTime.clearFocus()
                }
                true
            } else {
                false
            }
        }
// Add a listener to listen for data changes
        loraRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Get the data from the snapshot
                val loraStatus = snapshot.getValue<String>()
//                Log.d("value_loraStatus", "Value is: $loraStatus")

                // Update the UI based on the data
                when(loraStatus){
                    "true" -> {
                        // Update the connected UI
                        binding.icConnectedDevices.setImageResource(R.drawable.connect)
                        binding.txtConnectedDevices.text = "Connected \nDevices"
                    }

                    "false" -> {
                        // Update the disconnected UI
                        binding.icConnectedDevices.setImageResource(R.drawable.disconnect)
                        binding.txtConnectedDevices.text = "Disonnected \nDevices"
                    }

                    null -> {
                        // Update the disconnected UI
                        binding.icConnectedDevices.setImageResource(R.drawable.loading)
                        binding.txtConnectedDevices.text = "Loading \nDevices"
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

// Add a listener to listen for data changes
        rainRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Get the data from the snapshot
                val rainStatus = snapshot.getValue<String>()
                Log.d("value_rainStatus", "Value is: $rainStatus")

                // Update the UI based on the data
                when(rainStatus){
                    "true" -> {
                        // Update the connected UI
                        binding.iconRainyNight.setImageResource(R.drawable.rainy_night)
                        binding.textRainyNight.text = "Rainy \nNight"
                        binding.Led.visibility = View.GONE

                    }

                    "false" -> {
                        // Update the disconnected UI
                        binding.iconRainyNight.setImageResource(R.drawable.night)
                        binding.textRainyNight.text = "Cloud \nNight"
                        binding.Led.visibility = View.VISIBLE
                    }

                    null -> {
                        Toast.makeText(context, "Rain status is nulll", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

/*
        val intent = Intent(context, PowerStatusService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context?.startForegroundService(intent)
        } else {
            context?.startService(intent)
        }
*/

    }
    // Function to calculate time interval between two times
    fun calculateTimeInterval(startTime: String, endTime: String) {
        val format = SimpleDateFormat("HH:mm")
        val calendar = Calendar.getInstance()

        // Parse the start time and set it to the calendar
        calendar.time = format.parse(startTime)
        val startTimeInMillis = calendar.timeInMillis

        // Parse the end time and set it to the calendar
        calendar.time = format.parse(endTime)
        val endTimeInMillis = calendar.timeInMillis

        // Calculate the time interval
        val interval = if (endTimeInMillis >= startTimeInMillis) {
            endTimeInMillis - startTimeInMillis
        } else {
            // Add 24 hours to the end time and calculate the interval
            val adjustedEndTimeInMillis = endTimeInMillis + (24 * 60 * 60 * 1000)
            adjustedEndTimeInMillis - startTimeInMillis
        }

        // Convert the interval to hours and minutes
        val hours = interval / (1000 * 60 * 60)
        val minutes = (interval % (1000 * 60 * 60)) / (1000 * 60)

        // Format the interval as HH:mm
        val formattedInterval = String.format("%02d:%02d", hours, minutes)
        val database = FirebaseDatabase.getInstance()
        val timeActive = database.getReference("Node1/TimeActive")
        timeActive.setValue(formattedInterval)

        // Save the selected time in SharedPreferences
        val sharedPreferences = activity?.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences!!.edit()
        editor.putString("timeActive", formattedInterval)

        editor.apply()
        // Update the UI with the calculated interval
        binding.edtHour.setText(formattedInterval)
    }

    override fun onResume() {
        super.onResume()

        // Retrieve the saved time values from SharedPreferences
        val sharedPreferences = activity?.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val selectedTimeOn = sharedPreferences!!.getString("selectedTimeOn", "")
        val selectedTimeOff = sharedPreferences.getString("selectedTimeOff", "")
        val rstimeActive = sharedPreferences.getString("timeActive", "")

        // Display the retrieved time values in your UI
        if (!selectedTimeOn.isNullOrEmpty() && !selectedTimeOff.isNullOrEmpty()) {
            // Update your UI with the retrieved time values, e.g., setText() on TextViews
            // Note: This is just an example, you may need to update your UI based on your app's UI components
            binding.btnTimeOn.text = "$selectedTimeOn"
            binding.btnTimeOff.text = "$selectedTimeOff"
            binding.edtHour.setText(rstimeActive)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(runnableCode)
    }

}
