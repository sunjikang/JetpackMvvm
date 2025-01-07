package me.hgj.jetpackmvvm.demo.viewmodel.request

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.SPUtils
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.demo.app.network.apiService
import me.hgj.jetpackmvvm.demo.app.network.yygApiService
import me.hgj.jetpackmvvm.demo.data.model.bean.IntegralResponse
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.state.ResultState
import org.json.JSONObject

/**
 * 作者　: hegaojian
 * 时间　: 2019/12/27
 * 描述　:
 */
class RequestMeViewModel : BaseViewModel() {

    var meData = MutableLiveData<ResultState<IntegralResponse>>()

    fun getIntegral() {
        request({ apiService.getIntegral() }, meData)
    }

    fun sendMQTT(sn: String, cmd: String, needCustom: Boolean = true) {
        var excuteSn = ""
        if (sn.isEmpty()) {
            excuteSn = SPUtils.getInstance().getString("sn", "")
        } else {
            excuteSn = sn
        }
        val hj = SPUtils.getInstance().getBoolean("hj", false)
        var cmdPrefix = ""
        if (!hj) {
            cmdPrefix = "dev_"
        }
        if (needCustom) {
            val jsonObject = ReqyestBody(
                deviceTokens = excuteSn,
                custom = "${cmdPrefix}custom:action:custom_cmd-${cmd}",
                version = "2762"
            )
            request({ yygApiService.sendMQTT(jsonObject) }, meData)
            return
        } else {
            val jsonObject = ReqyestBody(
                deviceTokens = excuteSn,
                custom = "${cmdPrefix}${cmd}",
                version = "2762"
            )
            request({ yygApiService.sendMQTT(jsonObject) }, meData)
        }
    }


}