package com.swiftctrl.android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.swiftctrl.android.databinding.FragmentLoginBinding
import com.swiftctrl.sdk.SwiftCtrlSDK
import java.util.*

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    data class Account(val username: String, val password: String, val license: String, val secret: String, val userId: Int)

    private lateinit var accounts: Map<String, Account>
    private val dependencies = WeakHashMap<Account, AppDependency>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        accounts = mapOf<String, Account>(
            "demo" to Account("demo", "1234", getString(R.string.license), getString(R.string.secret), 2345),
            "demo2" to Account("demo2", "5678", getString(R.string.license2), getString(R.string.secret2), 2346)
        )
        binding.fragmentLoginSubmit.setOnClickListener {
            val username = binding.fragmentLoginUsername.text.toString()
            val password = binding.fragmentLoginPassword.text.toString()
            getAccount(username, password)?.let {
                dependencies.getOrPut(it, {AppDependency(it.userId)}).getToken(
                    requireContext(),
                    it.license,
                    it.secret,
                    it.userId,
                    object : SwiftCtrlSDK.AuthCallback {
                        override fun onSuccess(token: String) {
                            (requireActivity() as DemoActivity).showQrCode(token, it.userId)
                        }

                        override fun onError(e: Throwable) {
                            Toast.makeText(requireContext(), R.string.error_authentication, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            } ?: run {
                Toast.makeText(requireContext(), R.string.error_credentials, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getAccount(username: String, password: String): Account? {
        accounts[username]?.let {
            if (password == it.password) {
                return it
            }
        }
        return null
    }
}
