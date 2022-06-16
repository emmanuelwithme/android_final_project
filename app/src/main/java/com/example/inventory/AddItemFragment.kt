/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.inventory

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.inventory.data.Item
import com.example.inventory.databinding.FragmentAddItemBinding
import java.text.DateFormat
import java.text.SimpleDateFormat


/**
 * Fragment to add or update an item in the Inventory database.
 */
class AddItemFragment : Fragment() {
    private val viewModel: InventoryViewModel by activityViewModels {
        InventoryViewModelFactory(
            (activity?.application as InventoryApplication).database
                .itemDao()
        )
    }

    lateinit var item: Item

    // what is this for? by?
    private val navigationArgs: AddItemFragmentArgs by navArgs()


    // Binding object instance corresponding to the fragment_add_item.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment
    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!
    private val PICK_IMAGE = 100
    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted. Continue the action or workflow in your
            // app.
            Log.d("Permission", "Granted")
        } else {
            // Explain to the user that the feature is unavailable because the
            // features requires a permission that the user has denied. At the
            // same time, respect the user's decision. Don't link to system
            // settings in an effort to convince the user to change their
            // decision.
            Log.d("Permission", "Denied")
        }
    }
    private val CHANNEL_ID = "Coder"


    // where you inflate the layout. The fragment has entered the CREATED state.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    // bind specific views to properties
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val id = navigationArgs.itemId
        if (id > 0) {
            viewModel.retrieveItem(id).observe(this.viewLifecycleOwner) { selectedItem ->
                item = selectedItem
                bind(item)
            }
        } else {
            binding.saveAction.setOnClickListener {
                addNewItem()
            }
        }
        //選照片囉!
        binding.pickImage.setOnClickListener {
            requestPermission()
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, PICK_IMAGE)
        }
    }

    private fun requestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                //  permission is granted

            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this.requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
                // Adsictional rationale should be display
                requestPermissionLauncher.launch(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
            else -> {
                // Permissison has not been asked yet
                requestPermissionLauncher.launch(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE && data != null) {
            val selectedImage: Uri = data.data!!
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor? =
                activity?.getContentResolver()
                    ?.query(selectedImage, filePathColumn, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                //  filePath是圖片的檔案
                val filePath: String = cursor.getString(columnIndex)
                cursor.close()
                val yourSelectedImage = BitmapFactory.decodeFile(filePath)

                //  path to uri
                binding.image.setImageURI(Uri.parse(filePath))
                binding.imageUri.setText(filePath)
            }
            //Now do whatever processing you want to do on it.
        }
    }

    // return false if one of these two strings is empty
    private fun isEntryValid(): Boolean {
        // TODO: 須改
        val dateFormat: DateFormat = SimpleDateFormat("yyyy.MM.dd")
        return viewModel.isEntryValid(
            binding.vocEnglish.text.toString(),
            binding.vocChinese.text.toString(),
            dateFormat.parse(binding.date.text.toString()),
            binding.phone.toString(),
            binding.email.toString(),
            binding.imageUri.text.toString().toUri()
        )
    }


    private fun addNewItem() {
        // if this is true, insert item to db table
        if (isEntryValid()) {
            // TODO: 須改
            val dateFormat: DateFormat = SimpleDateFormat("yyyy.MM.dd")
            viewModel.addNewItem(
                binding.vocEnglish.text.toString(),
                binding.vocChinese.text.toString(),
                dateFormat.parse(binding.date.text.toString()),
                binding.phone.text.toString(),
                binding.email.text.toString(),
                binding.imageUri.text.toString().toUri()
            )


            var bitmap = BitmapFactory.decodeFile(binding.imageUri.text.toString().toUri().path)
            bitmap = Bitmap.createScaledBitmap(bitmap!!, 100, 100, true)
            // Create an explicit intent for an Activity in your app
            val intent = Intent(requireContext(), MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            createNotificationChannel()
            val pendingIntent: PendingIntent =
                PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(binding.vocChinese.text.toString())
                .setLargeIcon(bitmap)
                .setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .bigLargeIcon(null)
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                // setAutoCancel(true) will let notification disappear after click it
                .setAutoCancel(false)
            //Vibration
            builder.setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            with(NotificationManagerCompat.from(requireContext())) {
                // TODO:
                // notificationId is a unique int for each notification that you must define
                notify(1, builder.build())
            }

            // and move fragment from add item to list item
            // create a action variable first, then use the method on the second line
            val action = AddItemFragmentDirections.actionAddItemFragmentToItemListFragment()
            findNavController().navigate(action)
        }
    }

    private fun bind(item: Item) {
        binding.apply {
            vocEnglish.setText(item.vocEnglish, TextView.BufferType.SPANNABLE)
            vocChinese.setText(item.vocChinese, TextView.BufferType.SPANNABLE)
            phone.setText(item.phone, TextView.BufferType.SPANNABLE)
            email.setText(item.email, TextView.BufferType.SPANNABLE)
            val dateFormat: DateFormat = SimpleDateFormat("yyyy.MM.dd")
            date.setText(dateFormat.format(item.birthday), TextView.BufferType.SPANNABLE)
            imageUri.setText(item.photo.toString())
            image.setImageURI(item.photo)
            saveAction.setOnClickListener { updateItem() }
        }
    }

    private fun updateItem() {
        if (isEntryValid()) {
            val dateFormat: DateFormat = SimpleDateFormat("yyyy.MM.dd")
            viewModel.updateItem(
                this.navigationArgs.itemId,
                this.binding.vocEnglish.text.toString(),
                this.binding.vocChinese.text.toString(),
                this.navigationArgs.favorite,
                dateFormat.parse(this.binding.date.text.toString()),
                this.binding.phone.text.toString(),
                this.binding.email.text.toString(),
                this.binding.imageUri.text.toString().toUri()
            )
            val action = AddItemFragmentDirections.actionAddItemFragmentToItemListFragment()
            findNavController().navigate(action)
        }
    }

    /**
     * Called before fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}