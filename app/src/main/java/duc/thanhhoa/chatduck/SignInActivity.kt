@file:Suppress("DEPRECATION")

package duc.thanhhoa.chatduck

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import duc.thanhhoa.chatduck.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {
    private lateinit var goToSignIn: TextView
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialogSignIn: ProgressDialog
    private lateinit var signinBinding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signinBinding= DataBindingUtil.setContentView(this,R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser!=null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
        progressDialogSignIn= ProgressDialog(this)


        signinBinding.signInTextToSignUp.setOnClickListener{
            startActivity(Intent(this,SignUpActivity::class.java))
        }

        signinBinding.loginButton.setOnClickListener {
            email= signinBinding.loginetemail.text.toString()
            password= signinBinding.loginetpassword.text.toString()

            if (signinBinding.loginetemail.text.isEmpty()){
                Toast.makeText(this, "Email lỗi", Toast.LENGTH_SHORT).show()
            }
            if (signinBinding.loginetpassword.text.isEmpty()){
                Toast.makeText(this, "Password Lỗi", Toast.LENGTH_SHORT).show()
            }
            if (signinBinding.loginetemail.text.isNotEmpty() && signinBinding.loginetpassword.text.isNotEmpty()){

                signIn(email,password)
            }
        }
    }

    private fun signIn(email: String, password: String) {
        progressDialogSignIn.show()
        progressDialogSignIn.setMessage("Please wait...")

        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
            if (it.isSuccessful){
                progressDialogSignIn.dismiss()
                startActivity(Intent(this,MainActivity::class.java))
            }else{
                progressDialogSignIn.dismiss()
                Toast.makeText(this, "Vui lỏng thử lại", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            when(it){
                is FirebaseAuthInvalidCredentialsException ->{
                    Toast.makeText(this, "loi 3", Toast.LENGTH_SHORT).show()
                }else ->{
                    Toast.makeText(this, "loi 4", Toast.LENGTH_SHORT).show()
            }
            }
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        progressDialogSignIn.dismiss()
        finishAffinity()
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialogSignIn.dismiss()
    }
}