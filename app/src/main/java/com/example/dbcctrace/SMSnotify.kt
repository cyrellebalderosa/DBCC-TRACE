package com.example.dbcctrace

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.dbcctrace.databinding.ActivitySmsNotifyBinding

class SMSnotify : AppCompatActivity() {

    //viewBinding
    private lateinit var binding: ActivitySmsNotifyBinding

  override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySmsNotifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED)

        {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS),111)
        }
        else
            //recieveMsg()

        binding.sendsmsbtn.setOnClickListener {

            //val userInput = binding.phonenumET.text.toString()

                        // split it between any commas, stripping whitespace afterwards
            //val numbers = userInput.split(",".toRegex()).toTypedArray()

            var sms = SmsManager.getDefault()
            sms.sendTextMessage(binding.phonenumET.text.toString(),"ME", binding.messageET.text.toString() , null, null)

        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==111 && grantResults[0] ==  PackageManager.PERMISSION_GRANTED)
            recieveMsg()
    }
    private fun recieveMsg() {
       var br = object : BroadcastReceiver() {
           override fun onReceive(context: Context?, intent: Intent?) {
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                   for (sms in Telephony.Sms.Intents.getMessagesFromIntent(intent)){
                       //Toast.makeText(applicationContext,sms.displayMessageBody,Toast.LENGTH_SHORT).show()

                       binding.phonenumET.setText(sms.originatingAddress)
                       binding.messageET.setText(sms.displayMessageBody)
                   }
               }
           }

       }
        registerReceiver(br, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
    }
}
