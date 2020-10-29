package com.swiftctrl.android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.swiftctrl.android.databinding.FragmentQrBinding
import com.swiftctrl.sdk.SwiftCtrlCallback
import com.swiftctrl.sdk.connector.SwiftCtrlClient
import com.swiftctrl.sdk.connector.SwiftCtrlLifecycleClient

class QrFragment : Fragment(), SwiftCtrlCallback {

    private lateinit var binding: FragmentQrBinding
    private lateinit var client: SwiftCtrlClient
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentQrBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        client = SwiftCtrlLifecycleClient(this, requireContext(), arguments?.getString(Const.KEY_USER_TOKEN) ?: "", this)
    }

    override fun onSwiftCtrlReady() {
        client.registerCryptoFeed()
    }

    override fun onSwiftCtrlCrypto(text: String) {
        val bitmap = Utils.getQRFromText(requireContext(), text)
        binding.fragmentQrImage.setImageBitmap(bitmap)
    }

    override fun onSwiftCtrlError(test: String, e: Exception?) {
    }
}
