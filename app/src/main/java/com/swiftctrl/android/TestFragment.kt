package com.swiftctrl.android

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.swiftctrl.android.databinding.FragmentTestBinding
import com.swiftctrl.sdk.SwiftCtrlCallback
import com.swiftctrl.sdk.SwiftCtrlFullCallback
import com.swiftctrl.sdk.connector.SwiftCtrlClient
import com.swiftctrl.sdk.connector.SwiftCtrlLifecycleClient
import com.swiftctrl.sdk.connector.SwiftCtrlManualClient
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class TestFragment : Fragment(), SwiftCtrlFullCallback {

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
                binding.fragmentTestScroll.scrollTo(0, binding.fragmentTestScroll.bottom)
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
        if (type == Const.TEST_TYPE_MANUAL) {
            client = SwiftCtrlManualClient(requireContext(), getString(R.string.license), getString(R.string.secret), this)
            binding.fragmentTestConnect.setOnClickListener {
                if (client.isConnected()) {
                    (client as SwiftCtrlManualClient).disconnect()
                } else {
                    (client as SwiftCtrlManualClient).connect()
                }
            }
        } else {
            client = SwiftCtrlLifecycleClient(this, getString(R.string.license), getString(R.string.secret), this)
        }
        binding.fragmentTestType.text = client::class.java.simpleName
        binding.fragmentTestRegister.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                client.registerCryptoFeed()
            } else {
                client.unregisterCryptoFeed()
            }
        }
    }

    private fun initialState(isError:Boolean = false){
        if( binding.fragmentTestConnect.text != getString(R.string.disconnected)) {
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

    override fun onSwiftCtrlConnected() {
        binding.fragmentTestConnect.iconTint = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.holo_orange_dark))
        binding.fragmentTestConnect.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_orange_dark))
        binding.fragmentTestConnect.setText(R.string.connecting)
    }

    override fun onSwiftCtrlDisconnected() {
        initialState()
    }

    override fun onSwiftCtrlAuthSuccess() {

    }

    override fun onSwiftCtrlAuthClosed() {
        initialState()
    }

    override fun onSwiftCtrlReady() {
        binding.fragmentTestConnect.iconTint = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark))
        binding.fragmentTestConnect.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark))
        binding.fragmentTestConnect.setText(R.string.connected)
        binding.fragmentTestRegister.isEnabled = true
    }

    override fun onSwiftCtrlCrypto(text: String) {
        val bitmap = getQRFromText(text)
        binding.fragmentTestQrText.setText(text)
        binding.fragmentTestTimestamp.setText( SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()) )
        binding.fragmentTestQr.setImageBitmap(bitmap)
    }

    override fun onSwiftCtrlError(test: String, e: Exception?) {
        initialState(true)
        AlertDialog.Builder(requireActivity()).setMessage(test).setTitle(R.string.error).setNegativeButton(R.string.ok) { dialog, id ->
            dialog.dismiss()
        }.create().show()
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

    @Throws(WriterException::class)
    private fun getQRFromText(Value: String): Bitmap? {
        val bitMatrix = try {
            MultiFormatWriter().encode(
                Value,
                BarcodeFormat.QR_CODE,
                500, 500, null
            )
        } catch (Illegalargumentexception: IllegalArgumentException) {
            return null
        }
        val bitMatrixWidth = bitMatrix.width
        val bitMatrixHeight = bitMatrix.height
        val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)
        for (y in 0 until bitMatrixHeight) {
            val offset = y * bitMatrixWidth
            for (x in 0 until bitMatrixWidth) {
                pixels[offset + x] = if (bitMatrix[x, y]) ContextCompat.getColor(requireContext(), R.color.colorPrimary) else ContextCompat.getColor(requireContext(), android.R.color.white)
            }
        }
        val bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444)
        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight)
        return bitmap
    }
}
