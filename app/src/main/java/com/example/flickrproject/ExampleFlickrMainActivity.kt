package com.example.flickrproject

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.flickrproject.databinding.ActivityMainBinding


class ExampleFlickrMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ExampleFlickrViewModel

    private val SELECT_PICTURE = 1
    private var selectedImagePath: String? = null

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
                is ExampleFlickrViewModel.Events.onError -> {
                    showToast(events.throwable.message.toString())
                }
            }


        })

        viewModel.viewState.observe(this, { viewState ->


            viewState.flickrGalleries?.gallery?.let { gallery ->
               //Just for example of exposed data
                val title = gallery[0].title._content
                val desc = gallery[0].description._content

                showToast("Title = $title, Description = $desc")
            }

            if (viewState.flickrGalleryPhotoUrls.isNotEmpty()) {
                val urlStrings = StringBuilder()
                viewState.flickrGalleryPhotoUrls.forEachIndexed { index, s ->
                    urlStrings.append("URL ${index + 1} = $s")
                }

                showToast(urlStrings.toString())
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
                data?.data?.let {
                    // val descriptor = contentResolver.openInputStream(it)

                    val selectedImageURI: Uri = it
                    val input = getContentResolver().openInputStream(selectedImageURI)
                    val de = BitmapFactory.decodeStream(input)

                }


            }
        }

    }

    override fun onStop() {
        super.onStop()
        println("onstop")
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}