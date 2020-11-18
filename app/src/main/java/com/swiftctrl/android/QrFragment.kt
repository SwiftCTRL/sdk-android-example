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
import com.swiftctrl.sdk.core.toBase64
import net.glxn.qrgen.android.QRCode
import timber.log.Timber

class QrFragment : Fragment(), SwiftCtrlCallback {

    private lateinit var binding: FragmentQrBinding
    private lateinit var client: SwiftCtrlClient
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentQrBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.fragmentQrImage.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        binding.fragmentQrUserId.text = getString(R.string.user_id, arguments?.getInt(Const.KEY_USER_ID, 0))
        client = SwiftCtrlLifecycleClient(this, requireContext(), arguments?.getString(Const.KEY_USER_TOKEN) ?: "", this)
        binding.fragmentQrClose.setOnClickListener {
            client.unregisterCryptoFeed()
            (requireActivity() as DemoActivity).showLogin()
        }
    }

    override fun onSwiftCtrlReady() {

        client.registerCryptoFeed()
    }

    override fun onSwiftCtrlCrypto(text: String, content: ByteArray) {
        binding.fragmentQrImage.post {
            val imageSize = binding.fragmentQrImage.width
            binding.fragmentQrImage.setImageBitmap(QRCode.from(content.toBase64()).withSize(imageSize, imageSize).bitmap())
        }
    }

    override fun onSwiftCtrlError(test: String, e: Exception?) {
        Timber.e(e, test)
    }
}
