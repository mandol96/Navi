package com.cho.navi

import android.app.Application
import com.kakao.vectormap.KakaoMapSdk

class NaviApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        KakaoMapSdk.init(this, BuildConfig.KAKAO_API_KEY)
    }
}