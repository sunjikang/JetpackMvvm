package me.hgj.jetpackmvvm.demo.ui.fragment.me

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import kotlinx.android.synthetic.main.fragment_me.me_integral
import kotlinx.android.synthetic.main.fragment_sme.*
import me.hgj.jetpackmvvm.demo.R
import me.hgj.jetpackmvvm.demo.app.appViewModel
import me.hgj.jetpackmvvm.demo.app.base.BaseFragment
import me.hgj.jetpackmvvm.demo.app.ext.init
import me.hgj.jetpackmvvm.demo.app.ext.joinQQGroup
import me.hgj.jetpackmvvm.demo.app.ext.jumpByLogin
import me.hgj.jetpackmvvm.demo.app.ext.setUiTheme
import me.hgj.jetpackmvvm.demo.data.model.bean.BannerResponse
import me.hgj.jetpackmvvm.demo.data.model.bean.IntegralResponse
import me.hgj.jetpackmvvm.demo.databinding.FragmentMeBinding
import me.hgj.jetpackmvvm.demo.databinding.FragmentSmeBinding
import me.hgj.jetpackmvvm.demo.viewmodel.request.RequestMeViewModel
import me.hgj.jetpackmvvm.demo.viewmodel.state.MeViewModel
import me.hgj.jetpackmvvm.ext.nav
import me.hgj.jetpackmvvm.ext.navigateAction
import me.hgj.jetpackmvvm.ext.parseState
import me.hgj.jetpackmvvm.ext.util.notNull

/**
 * 作者　: hegaojian
 * 时间　: 2019/12/23
 * 描述　: 我的
 */

class SMeFragment : BaseFragment<MeViewModel, FragmentSmeBinding>() {
    private var sn = ""
    private var hj = false
    private var rank: IntegralResponse? = null

    private val requestMeViewModel: RequestMeViewModel by viewModels()

    override fun initView(savedInstanceState: Bundle?) {


        mDatabind.vm = mViewModel
        mDatabind.click = ProxyClick()
        appViewModel.appColor.value?.let { setUiTheme(it, me_linear, me_integral) }
        appViewModel.userInfo.value?.let { mViewModel.name.set(if (it.nickname.isEmpty()) it.username else it.nickname) }
        me_swipe.init {
            requestMeViewModel.getIntegral()
        }
    }

    override fun onResume() {
        super.onResume()
        sn = SPUtils.getInstance().getString("sn")
        hj = SPUtils.getInstance().getBoolean("hj", false)
        me_name.text = "当前sn为： $sn"
        if (hj) {
            me_info.text = "当前环境为： 正式"
        } else {
            me_info.text = "当前环境为： 测试"
        }
        var shopname = SPUtils.getInstance().getString("shopname")
        me_shopname.text = shopname

    }

    override fun lazyLoadData() {
        appViewModel.userInfo.value?.run {
            me_swipe.isRefreshing = true
            requestMeViewModel.getIntegral()
        }
    }

    override fun createObserver() {

        requestMeViewModel.meData.observe(viewLifecycleOwner, Observer { resultState ->
            me_swipe.isRefreshing = false
            parseState(resultState, {
                rank = it
                mViewModel.info.set("id：${it.userId}　排名：${it.rank}")
                mViewModel.integral.set(it.coinCount)
            }, {
                ToastUtils.showShort(it.errorMsg)
            })
        })

        appViewModel.run {
            appColor.observeInFragment(this@SMeFragment, Observer {
                setUiTheme(it, me_linear, me_swipe, me_integral)
            })
            userInfo.observeInFragment(this@SMeFragment, Observer {
                it.notNull({
                    me_swipe.isRefreshing = true
                    mViewModel.name.set(if (it.nickname.isEmpty()) it.username else it.nickname)
                    requestMeViewModel.getIntegral()
                }, {
                    mViewModel.name.set("请先登录~")
                    mViewModel.info.set("id：--　排名：--")
                    mViewModel.integral.set(0)
                })
            })
        }
    }

    inner class ProxyClick {

        /** 登录 */
        fun login() {
            nav().jumpByLogin {}
        }

        /** 收藏 */
        fun collect() {
            nav().jumpByLogin {
                it.navigateAction(R.id.action_mainfragment_to_collectFragment)
            }
        }

        /** 积分 */
        fun integral() {
            nav().jumpByLogin {
                it.navigateAction(R.id.action_mainfragment_to_integralFragment,
                    Bundle().apply {
                        putParcelable("rank", rank)
                    }
                )
            }
        }

        /** 文章 */
        fun ariticle() {
//            nav().jumpByLogin {
//                it.navigateAction(R.id.action_mainfragment_to_ariticleFragment)
//            }
            Toast.makeText(activity, "sdlfjsldfj", Toast.LENGTH_SHORT).show()
            requestMeViewModel.sendMQTT("666888", "123123")


        }

        fun rebboot() {
            requestMeViewModel.sendMQTT(sn, "reboot")
        }

        fun gotoSetting() {
            requestMeViewModel.sendMQTT(sn, "am start -n com.android.settings/.Settings")
        }

        fun gotoWifiHot() {
            requestMeViewModel.sendMQTT(
                sn,
                "am start -a android.intent.action.MAIN -n com.android.settings/.TetherSettings"
            )
        }

        fun gotoYunWei() {
            requestMeViewModel.sendMQTT(
                sn,
                "am start -a android.intent.action.MAIN -n com.yundashi.sellrobot.shop/com.yundashi.sellrobot.shop.ui.activity.debug.AdminActivity"
            )
        }

        fun gotoWebBrowser() {
            requestMeViewModel.sendMQTT(
                sn,
                "am start -a android.intent.action.MAIN -n acr.browser.barebones/acr.browser.lightning.activity.MainActivity"
            )
        }

        fun gotoFileBrowser() {
            requestMeViewModel.sendMQTT(
                sn,
                "am start -a android.intent.action.MAIN -n com.android.rk/com.android.rk.RockExplorer"
            )
        }

        fun goBack() {
            requestMeViewModel.sendMQTT(
                sn,
                "input keyevent BACK"
            )
        }

        fun restartShopAPP() {
            requestMeViewModel.sendMQTT(
                sn,
                " am start -n com.yundashi.sellrobot.shop/com.yundashi.sellrobot.shop.ui.activity.SplashActivity"
            )
        }


        fun todo() {
            nav().jumpByLogin {
                it.navigateAction(R.id.action_mainfragment_to_todoListFragment)
            }
        }

        /** 玩Android开源网站 */
        fun about() {
            nav().navigateAction(R.id.action_to_webFragment, Bundle().apply {
                putParcelable(
                    "bannerdata",
                    BannerResponse(
                        title = "玩Android网站",
                        url = "https://www.wanandroid.com/"
                    )
                )
            })
        }

        /** 加入我们 */
        fun join() {
            joinQQGroup("9n4i5sHt4189d4DvbotKiCHy-5jZtD4D")
        }

        /** 设置*/
        fun setting() {
            nav().navigateAction(R.id.action_mainfragment_to_settingFragment)
        }

    }
}