package com.swiftctrl.android

import android.content.Context
import android.content.SharedPreferences
import com.auth0.android.jwt.JWT
import com.swiftctrl.sdk.SwiftCtrlSDK
import java.util.*
import kotlin.reflect.KClass

class AppDependency(private val userId: Int) {
    private val store = WeakHashMap<KClass<*>, Any>()

    private fun getSharedPreference(context: Context) =
        store.getOrPut(SharedPreferences::class, { context.getSharedPreferences("app_$userId", Context.MODE_PRIVATE) }) as SharedPreferences

    fun getToken(context: Context, license: String, secret: String, userId: Int, callback: SwiftCtrlSDK.AuthCallback) {
        getSharedPreference(context).getString(Const.KEY_USER_TOKEN, null)?.let { userToken ->
            if (isCloseToExpire(userToken)) {
                SwiftCtrlSDK.refreshUserToken(
                    userToken,
                    object : SwiftCtrlSDK.AuthCallback {
                        override fun onSuccess(token: String) {
                            getSharedPreference(context).edit().putString(Const.KEY_USER_TOKEN, token).apply()
                            callback.onSuccess(token)
                        }

                        override fun onError(e: Throwable) {
                            callback.onError(e)
                        }
                    }
                )
            } else {
                callback.onSuccess(userToken)
            }
        } ?: run {
            getSharedPreference(context).getString(Const.KEY_SYSTEM_TOKEN, null)?.let { systemToken ->
                if (isCloseToExpire(systemToken)) {
                    SwiftCtrlSDK.refreshSystemToken(
                        systemToken,
                        object : SwiftCtrlSDK.AuthCallback {
                            override fun onSuccess(token: String) {
                                getSharedPreference(context).edit().putString(Const.KEY_SYSTEM_TOKEN, token).apply()
                                createUserToken(context, systemToken, userId, callback)
                            }

                            override fun onError(e: Throwable) {
                                callback.onError(e)
                            }
                        }
                    )
                } else {
                    createUserToken(context, systemToken, userId, callback)
                }
            } ?: run {
                createSystemToken(context, license, secret, userId, callback)
            }
        }
    }

    private fun createUserToken(context: Context, systemToken: String, userId: Int, callback: SwiftCtrlSDK.AuthCallback) {
        SwiftCtrlSDK.createUserToken(
            systemToken, userId,
            object : SwiftCtrlSDK.AuthCallback {
                override fun onSuccess(token: String) {
                    getSharedPreference(context).edit().putString(Const.KEY_USER_TOKEN, token).apply()
                    callback.onSuccess(token)
                }

                override fun onError(e: Throwable) {
                    callback.onError(e)
                }
            }
        )
    }

    private fun createSystemToken(context: Context, license: String, secret: String, userId: Int, callback: SwiftCtrlSDK.AuthCallback) {
        SwiftCtrlSDK.createSystemToken(
            license, secret,
            object : SwiftCtrlSDK.AuthCallback {
                override fun onSuccess(token: String) {

                    getSharedPreference(context).edit().putString(Const.KEY_SYSTEM_TOKEN, token).apply()
                    createUserToken(context, token, userId, callback)
                }

                override fun onError(e: Throwable) {
                    callback.onError(e)
                }
            }
        )
    }

    private fun isCloseToExpire(token: String): Boolean = JWT(token).isExpired(30)
}
