package com.swiftctrl.android

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import com.swiftctrl.android.databinding.FragmentTestBinding
import com.swiftctrl.sdk.SwiftCtrlFullCallback
import com.swiftctrl.sdk.SwiftCtrlSDK
import com.swiftctrl.sdk.connector.SwiftCtrlClient
import com.swiftctrl.sdk.connector.SwiftCtrlLifecycleClient
import com.swiftctrl.sdk.connector.SwiftCtrlManualClient
import timber.log.Timber
import java.util.*

class TestFragment : Fragment(), SwiftCtrlFullCallback {

    private var autoScroll = true
    private val userId = 37
    private var type: Int = Const.TEST_TYPE_AUTO
    private lateinit var client: SwiftCtrlClient
    private lateinit var binding: FragmentTestBinding
    private val tree = object : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            requireActivity().runOnUiThread {
                val row = layoutInflater.inflate(R.layout.part_log_row, binding.fragmentTestLogContainer, false) as TextView
                row.text = message
                row.setTextColor(getTextColor(priority))
                binding.fragmentTestLogContainer.addView(row)
                if (autoScroll) {
                    binding.fragmentTestScroll.fullScroll(ScrollView.FOCUS_DOWN)
                }
            }
        }
    }

    @ColorInt
    private fun getTextColor(priority: Int): Int {
        val colorId = when (priority) {
            Log.ASSERT, Log.ERROR -> R.color.error
            Log.DEBUG -> R.color.debug
            Log.INFO -> R.color.info
            Log.WARN -> R.color.warning
            else -> android.R.color.black
        }
        return ContextCompat.getColor(requireContext(), colorId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTestBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.type = arguments?.getInt(Const.KEY_TYPE) ?: Const.TEST_TYPE_AUTO

        initialState()
        AppDependency.getToken(
            requireContext(), getString(R.string.license), getString(R.string.secret), userId,
            object : SwiftCtrlSDK.AuthCallback {
                override fun onSuccess(token: String) {

                    if (type == Const.TEST_TYPE_MANUAL) {
                        client = SwiftCtrlManualClient(requireContext(), token, this@TestFragment)
                        binding.fragmentTestConnect.setOnClickListener {
                            if (client.isConnected()) {
                                (client as SwiftCtrlManualClient).disconnect()
                            } else {
                                (client as SwiftCtrlManualClient).connect()
                            }
                        }
                    } else {
                        client = SwiftCtrlLifecycleClient(this@TestFragment, token, this@TestFragment)
                    }
                    binding.fragmentTestType.text = client::class.java.simpleName
                    binding.fragmentTestConnect.isEnabled = true
                }

                override fun onError(e: Throwable) {
                    binding.fragmentTestConnect.isEnabled = false
                    Toast.makeText(requireContext(), R.string.error_authentication, Toast.LENGTH_SHORT).show()
                }
            }
        )

        binding.fragmentTestUserid.text = getString(R.string.user_id, userId)
        binding.fragmentTestRegister.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                client.registerCryptoFeed()
            } else {
                client.unregisterCryptoFeed()
            }
        }
        binding.fragmentTestAutoscroll.setOnCheckedChangeListener { buttonView, isChecked ->
            autoScroll = isChecked
            if (isChecked) {
                binding.fragmentTestScroll.fullScroll(ScrollView.FOCUS_DOWN)
            }
        }
    }

    private fun initialState(isError: Boolean = false) {
        activity?.let {
            if (binding.fragmentTestConnect.text != getString(R.string.disconnected)) {
                binding.fragmentTestRegister.isChecked = false
                binding.fragmentTestRegister.isEnabled = false
                if (isError) {
                    binding.fragmentTestConnect.iconTint = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                    binding.fragmentTestConnect.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                } else {
                    binding.fragmentTestConnect.iconTint = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.black))
                    binding.fragmentTestConnect.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
                }
                binding.fragmentTestConnect.setText(R.string.disconnected)
            }
        }
    }

    override fun onSwiftCtrlDisconnected() {
        initialState()
    }

    override fun onSwiftCtrlAuthSuccess() {
    }

    override fun onSwiftCtrlAuthClosed() {
        initialState()
    }

    override fun onQrValidated(valid: Boolean) {
        val color = if (valid) {
            binding.fragmentTestQrValid.text = getString(R.string.qr_valid)
            binding.fragmentTestQrValid.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_thumb_up, 0)
            ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark)
        } else {
            binding.fragmentTestQrValid.text = getString(R.string.qr_not_valid)
            binding.fragmentTestQrValid.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_thumb_down, 0)
            ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark)
        }
        binding.fragmentTestQrValid.setTextColor(color)
        TextViewCompat.setCompoundDrawableTintList(binding.fragmentTestQrValid, ColorStateList.valueOf(color))
    }

    override fun onSwiftCtrlConnecting() {
        binding.fragmentTestConnect.iconTint = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.holo_orange_dark))
        binding.fragmentTestConnect.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_orange_dark))
        binding.fragmentTestConnect.setText(R.string.connecting)
    }

    override fun onSwiftCtrlReady() {
        binding.fragmentTestConnect.iconTint = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark))
        binding.fragmentTestConnect.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark))
        binding.fragmentTestConnect.setText(R.string.connected)
        binding.fragmentTestRegister.isEnabled = true
    }

    override fun onSwiftCtrlCrypto(text: String) {
        val bitmap = Utils.getQRFromText(requireContext(), text)
        binding.fragmentTestQr.setImageBitmap(bitmap)

        client.validate(text)
    }

    override fun onSwiftCtrlError(test: String, e: Exception?) {
        Toast.makeText(requireContext(), test, Toast.LENGTH_LONG).show()
    }

    override fun onStart() {
        super.onStart()
        Timber.plant(tree)
    }

    override fun onStop() {
        super.onStop()
        if (client is SwiftCtrlManualClient) {
            (client as SwiftCtrlManualClient).disconnect()
        }
        Timber.uproot(tree)
    }
}
