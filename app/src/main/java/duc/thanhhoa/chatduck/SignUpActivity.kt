@file:Suppress("DEPRECATION")

package duc.thanhhoa.chatduck

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import duc.thanhhoa.chatduck.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var signUpBinding: ActivitySignUpBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var signAuth: FirebaseAuth
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var signUpPd: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signUpBinding= DataBindingUtil.setContentView(this, R.layout.activity_sign_up)

        firestore= FirebaseFirestore.getInstance()
        signAuth= FirebaseAuth.getInstance()
        signUpPd= ProgressDialog(this)

        signUpBinding.signUpTextToSignIn.setOnClickListener {
            startActivity(Intent(this,SignInActivity::class.java))
        }

        signUpBinding.signUpBtn.setOnClickListener {
            name= signUpBinding.signUpEtName.text.toString()
            email= signUpBinding.signUpEmail.text.toString()
            password= signUpBinding.signUpPassword.text.toString()

            if (signUpBinding.signUpEtName.text.isEmpty()){

                Toast.makeText(this, "loi 5", Toast.LENGTH_SHORT).show()
            }
            if (signUpBinding.signUpEmail.text.isEmpty()){
                Toast.makeText(this, "loi 5", Toast.LENGTH_SHORT).show()

            }
            if (signUpBinding.signUpPassword.text.isEmpty()){
                Toast.makeText(this, "loi 5", Toast.LENGTH_SHORT).show()

            }
            if (signUpBinding.signUpEtName.text.isNotEmpty()
                && signUpBinding.signUpEtName.text.isNotEmpty()
                && signUpBinding.signUpPassword.text.isNotEmpty()
                ){

                signUp(name,email,password)

            }
        }
    }

    private fun signUp(name: String, email: String, password: String) {
        signUpPd.show()
        signUpPd.setMessage("Please wait...")

        signAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            if (it.isSuccessful) {
                signUpPd.dismiss()
                val user = signAuth.currentUser
                val hashMap = hashMapOf("userid" to user!!.uid!!,
                    "username" to name,
                    "useremail" to email,
                    "status" to "default",
                    "imageUrl" to "https://www.pngarts.com/files/6/User-Avatar-in-Suit-PNG.png"
                    )

                firestore.collection("Users").document(user.uid).set(hashMap)
                signUpPd.dismiss()
                startActivity(Intent(this,SignInActivity::class.java))

            }
        }
    }
}