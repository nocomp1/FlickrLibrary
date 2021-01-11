package com.example.flickrproject

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import com.example.flickrproject.databinding.ActivityMainBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class ExampleFlickrMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ExampleFlickrViewModel

    private val SELECT_PICTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProviders.of(this).get(ExampleFlickrViewModel::class.java)

        viewModel.event.observe(this, { events ->

            when (events) {
                is ExampleFlickrViewModel.Events.RequestIntentForFlickrAuthPage -> {
                    startActivity(events.flickerAuthPageIntent)
                }
                is ExampleFlickrViewModel.Events.IsUserAuthenticated -> {
                    showToast("Is user Auth = ${events.results}")
                }
                is ExampleFlickrViewModel.Events.OnError -> {
                    showToast(events.message.toString())
                }
            }


        })

        viewModel.viewState.observe(this, { viewState ->

            binding.progress.isVisible = viewState.isLoading

            viewState.flickrGalleries?.gallery?.let { gallery ->
                //Just for example of exposed data
                val title = gallery[0].title._content
                val desc = gallery[0].description._content

                showToast("Galleries: Size= ${gallery.size} Title = $title, Description = $desc")
            }

            if (viewState.flickrGalleryPhotoUrls.isNotEmpty()) {
                val urlStrings = StringBuilder()
                viewState.flickrGalleryPhotoUrls.forEachIndexed { index, s ->
                    urlStrings.append("URL ${index + 1} = $s")
                }

                showToast("Gallery Photo URLs: $urlStrings")
            }


        })

        binding.auth.setOnClickListener {
            viewModel.onAction(ExampleFlickrViewModel.Action.AuthenticateFlickrUser)

        }
        binding.getGallery.setOnClickListener {
            viewModel.onAction(ExampleFlickrViewModel.Action.GetFlickrGalleries)

        }

        binding.getGalleryPhotos.setOnClickListener {
            viewModel.onAction(ExampleFlickrViewModel.Action.GetFlickrGalleryPhotos)
        }

        binding.uploadPhoto.setOnClickListener { pickImage() }

    }

    override fun onResume() {
        super.onResume()
        viewModel.onAction(ExampleFlickrViewModel.Action.GetOauthVerifier(intent))


    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(
                intent,
                "Select Picture"
            ), SELECT_PICTURE
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        println("on destroyed")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode === RESULT_OK) {
            if (requestCode === SELECT_PICTURE) {
                data?.data?.let { selectedImage ->

                    //Getting the file path from content - you must copy it and cache it
                    val parcelFileDescriptor =
                        contentResolver.openFileDescriptor(selectedImage, "r", null)
                    var name = ""
                    val cursor = contentResolver.query(selectedImage, null, null, null, null)
                    cursor?.use {
                        it.moveToFirst()
                        name = cursor.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                    //copy the file to the cache so we can send it
                    val inputStream = FileInputStream(parcelFileDescriptor?.fileDescriptor)
                    val file = File(cacheDir, name)
                    val outputStream = FileOutputStream(file)
                    inputStream.copyTo(outputStream)

                    val bmOptions = BitmapFactory.Options()
                    var bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions)

                    //send to flickr api to post it
                    viewModel.onAction(ExampleFlickrViewModel.Action.UploadImageToFlickr(bitmap))
                    binding.myImage.setImageBitmap(bitmap)


                }


            }
        }

    }

    override fun onStop() {
        super.onStop()
        println("onstop")
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

}