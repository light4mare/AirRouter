package com.example.svc.honeycomb

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import router.air.api.AirRouter

/**
 * @author wuxi
 * @since 2019/6/11
 */
class TestFragment: Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AirRouter.post(this, "/chat/activity", 999)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}