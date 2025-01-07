package me.hgj.jetpackmvvm.demo.ui.activity

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.blankj.utilcode.util.ToastUtils
import me.hgj.jetpackmvvm.demo.app.base.BaseActivity1
import me.hgj.jetpackmvvm.demo.databinding.ActivitySmainBinding
import me.hgj.jetpackmvvm.demo.viewmodel.state.SmainViewModel
import me.hgj.jetpackmvvm.network.manager.NetState

class SmainActivity : BaseActivity1<SmainViewModel, ActivitySmainBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // 设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState)

    }
    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun onNetworkStateChanged(netState: NetState) {
        super.onNetworkStateChanged(netState)

        if (netState.isSuccess) {
            ToastUtils.showShort("网络连接成功")
        } else {
            ToastUtils.showShort("网络连接失败")
        }
    }

}