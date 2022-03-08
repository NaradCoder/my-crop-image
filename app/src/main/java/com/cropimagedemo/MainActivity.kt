package com.cropimagedemo

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.mycropimage.CropImage
import java.io.File
import java.lang.Exception
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private var adapter: MediaAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rvMedia = findViewById<RecyclerView>(R.id.rv_media)
        val switchMultiImage = findViewById<SwitchCompat>(R.id.switch_multi_image)
        adapter = MediaAdapter(
            ArrayList(),
            this
        ) { _, _, _ -> }

        rvMedia.adapter = adapter

        val ivAddImage: ImageView = findViewById(R.id.iv_add_image)

        ivAddImage.setOnClickListener {
            CropImage.activity()
                .start(startActivityForCropResult, this@MainActivity, switchMultiImage.isChecked)
        }
    }


    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    private var startActivityForGalleryImageResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent: Intent? = result.data
                try {
                    if (intent != null)
                        if (intent.data != null) {
                            val imageUri: Uri = intent.data!!
                            adapter!!.setItem(MoreBean(0, imageUri.toString(), 0))
                            Log.e(
                                "ShowCropImage",
                                "registerForActivityResult: ${imageUri}\n${imageUri.path}"
                            )
                        } else if (intent.clipData != null) {
                            val mClipData: ClipData = intent.clipData!!
                            for (i in 0 until mClipData.itemCount) {
                                val item: ClipData.Item = mClipData.getItemAt(i)
                                val imageUri: Uri = item.uri
                                adapter!!.setItem(MoreBean(0, imageUri.toString(), 0))
                                Log.e(
                                    "ShowCropImage",
                                    "registerForActivityResult_getClipData_$i: ${imageUri}\n${imageUri.path}"
                                )
                            }
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        this@MainActivity,
                        "Something went wrong",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    private var startActivityForCropResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent: Intent? = result.data
                val uriList: List<String> = CropImage.getActivityResultList(intent)
                for (i in uriList.indices) {
                    adapter!!.setItem(MoreBean(0, uriList[i], 0))
                    Log.e(
                        "MainActivity",
                        "registerForActivityResult_getClipData_$i: ${uriList[i]}\n${Uri.parse(uriList[i]).path}"
                    )
                }
            }
        }
}