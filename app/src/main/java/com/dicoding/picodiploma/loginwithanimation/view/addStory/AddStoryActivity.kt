package com.dicoding.picodiploma.loginwithanimation.view.addStory

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityAddStoryBinding
import com.dicoding.picodiploma.loginwithanimation.viewmodel.ViewModelProviderFactory
import com.dicoding.picodiploma.loginwithanimation.viewmodel.addStory.AddStoryViewModel
import com.dicoding.picodiploma.loginwithanimation.view.story.StoryActivity
import kotlinx.coroutines.launch

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private var selectedImageUri: Uri? = null
    private val addStoryViewModel by viewModels<AddStoryViewModel> {
        ViewModelProviderFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeViewModelState()
    }

    private fun setupListeners() {
        binding.addGallery.setOnClickListener { fromGallery() }
        binding.addCamera.setOnClickListener { fromCamera() }
        binding.buttonAdd.setOnClickListener { handleAddStory() }
    }

    private fun handleAddStory() {
        val imageFile = selectedImageUri?.let { uri ->
            convertUriToFile(uri, this).compressImage()
        }
        val description = binding.edAddDescription.text.toString()

        if (imageFile != null) {
            lifecycleScope.launch{
                addStoryViewModel.uploadstory(imageFile, description)
            }
        } else {
            showAlertDialog("Please select an image first.", SweetAlertDialog.ERROR_TYPE)
        }
    }

    private fun fromGallery() {
        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun fromCamera() {
        selectedImageUri = generateImageUri(this)
        cameraLauncher.launch(selectedImageUri!!)
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            displaySelectedImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            displaySelectedImage()
        } else {
            selectedImageUri = null
        }
    }

    private fun displaySelectedImage() {
        selectedImageUri?.let {
            Log.d("Image URI", "displaySelectedImage: $it")
            binding.addImage.setImageURI(it)
        }
    }

    private fun observeViewModelState() {
        addStoryViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        addStoryViewModel.errorMessage.observe(this) { error ->
            if (error.isNotEmpty()) showAlertDialog(error, SweetAlertDialog.ERROR_TYPE)
        }

        addStoryViewModel.resultAddStory.observe(this) { response ->
            if (response != null) {
                showAlertDialog("Successfully added a new story.", SweetAlertDialog.SUCCESS_TYPE, true)
            }
        }
    }

    private fun showAlertDialog(message: String?, dialogType: Int, isSuccess: Boolean = false) {
        SweetAlertDialog(this, dialogType)
            .setTitleText(if (isSuccess) "Success!" else "Error!")
            .setContentText(message)
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()
                if (isSuccess) navigateToMainActivity()
            }
            .show()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this@AddStoryActivity, StoryActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
