package duc.thanhhoa.chatduck.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import duc.thanhhoa.chatduck.R
import duc.thanhhoa.chatduck.Utils
import duc.thanhhoa.chatduck.databinding.FragmentSettingBinding
import duc.thanhhoa.chatduck.mvvm.ChatAppViewModel
import java.io.ByteArrayOutputStream
import java.util.UUID

class SettingFragment : Fragment() {

    private lateinit var settingBinding: FragmentSettingBinding
    lateinit var settingViewModel: ChatAppViewModel
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    var uri: Uri? = null
    lateinit var bitmap: Bitmap


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        settingBinding= DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false)
        return settingBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingViewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)

        settingBinding.viewModel = settingViewModel
        settingBinding.lifecycleOwner= viewLifecycleOwner

        storage= FirebaseStorage.getInstance()
        storageRef = storage.reference

        settingViewModel.imageUrl.observe(viewLifecycleOwner, Observer{

            loadImage(it)

        })

        settingBinding.settingBackBtn.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_settingFragment_to_homeFragment)
        }

        settingBinding.settingUpdateButton.setOnClickListener {
            settingViewModel.updateProfile()
        }

        settingBinding.settingUpdateImage.setOnClickListener {

            val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Choose your profile picture")
            builder.setItems(options) { dialog, item ->
                when {
                    options[item] == "Take Photo" -> {

                        takePhotoWithCamera()


                    }
                    options[item] == "Choose from Gallery" -> {
                        pickImageFromGallery()
                    }
                    options[item] == "Cancel" -> dialog.dismiss()
                }
            }
            builder.show()
        }
    }

    private fun loadImage(it: String?) {

        Glide.with(requireContext()).load(it).placeholder(R.drawable.person).dontAnimate()
            .into(settingBinding.settingUpdateImage)

    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun pickImageFromGallery() {
        val pickImage= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        if (pickImage.resolveActivity(requireActivity().packageManager)!=null){
            startActivityForResult(pickImage,Utils.REQUEST_IMAGE_PICK)
        }
    }
    @SuppressLint("QueryPermissionsNeeded")
    private fun takePhotoWithCamera() {

        val taskImage= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(taskImage,Utils.REQUEST_IMAGE_CAPTURE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (resultCode == AppCompatActivity.RESULT_OK) {
            when (requestCode) {
                Utils.REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap

                    uploadImageToFirebaseStorage(imageBitmap)
                }
                Utils.REQUEST_IMAGE_PICK -> {
                    val imageUri = data?.data
                    val imageBitmap =
                        MediaStore.Images.Media.getBitmap(context?.contentResolver, imageUri)
                    uploadImageToFirebaseStorage(imageBitmap)
                }
            }
        }


    }

    private fun uploadImageToFirebaseStorage(imageBitmap: Bitmap?) {

        val baos = ByteArrayOutputStream()
        imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()


        bitmap = imageBitmap!!

        settingBinding.settingUpdateImage.setImageBitmap(imageBitmap)

        val storagePath = storageRef.child("Photos/${UUID.randomUUID()}.jpg")
        val uploadTask = storagePath.putBytes(data)
        uploadTask.addOnSuccessListener {


            val task = it.metadata?.reference?.downloadUrl

            task?.addOnSuccessListener {

                uri = it
                settingViewModel.imageUrl.value = uri.toString()


            }






            Toast.makeText(context, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to upload image!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()


        settingViewModel.imageUrl.observe(viewLifecycleOwner, Observer {


            loadImage(it)




        })



    }

}



