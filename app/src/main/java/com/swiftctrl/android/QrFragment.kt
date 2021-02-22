package com.swiftctrl.android

import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.swiftctrl.android.databinding.FragmentQrBinding
import com.swiftctrl.sdk.SwiftCtrlCallback
import com.swiftctrl.sdk.connector.SwiftCtrlClient
import com.swiftctrl.sdk.connector.SwiftCtrlLifecycleClient
import com.swiftctrl.sdk.utils.QRHelper

class QrFragment : Fragment(), SwiftCtrlCallback {

    private lateinit var binding: FragmentQrBinding
    private lateinit var client: SwiftCtrlClient
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentQrBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val token = "" // TODO Insert your token here


        client = SwiftCtrlLifecycleClient(this, requireContext(), token, this)
        binding.fragmentQrImage.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    override fun onSwiftCtrlReady() {
        client.registerCryptoFeed()
    }

    override fun onSwiftCtrlCrypto(text: String, content: ByteArray) {
        binding.fragmentQrImage.post {
            val imageSize = binding.fragmentQrImage.width
            val base64 = String(Base64.encode(content, Base64.DEFAULT))
            binding.fragmentQrImage.setImageBitmap(QRHelper.encodeAsBitmap(base64, BarcodeFormat.QR_CODE_BINARY, imageSize))
        }
    }

    override fun onSwiftCtrlError(text: String, e: Exception?) {
        Log.e("SwiftCTRL", "text", e)
    }
}
