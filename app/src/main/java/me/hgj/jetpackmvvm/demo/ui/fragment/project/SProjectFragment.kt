package me.hgj.jetpackmvvm.demo.ui.fragment.project

import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.Settings.Global
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.LogUtils.A
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.fragment_sviewpager.iv_image
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.whileSelect
import me.hgj.jetpackmvvm.demo.R
import me.hgj.jetpackmvvm.demo.app.base.BaseFragment
import me.hgj.jetpackmvvm.demo.app.util.DatetimeUtil
import me.hgj.jetpackmvvm.demo.databinding.FragmentSviewpagerBinding
import me.hgj.jetpackmvvm.demo.viewmodel.request.RequestMeViewModel
import me.hgj.jetpackmvvm.demo.viewmodel.state.SProjectViewModel
import java.io.File
import java.lang.Thread.sleep


/**
 * 作者　: hegaojian
 * 时间　: 2019/12/28
 * 描述　:
 */
class SProjectFragment : BaseFragment<SProjectViewModel, FragmentSviewpagerBinding>() {

    val downloadpath =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString() + File.separator
    private var picName = "123123"

    /** */
    private val requestMeViewModel: RequestMeViewModel by viewModels()
    var clickable = true
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.vm = mViewModel
        mDatabind.click = ProxyClick()

        iv_image.setOnTouchListener { v, event ->
            var ivwidth = v.width
            var ivheight = v.height
            //获取图片的宽和高
            Log.i("TAG", "clickImg图片的宽ivwidth: ${v.width}")
            Log.i("TAG", "clickImg图片的高ivheight: ${v.height}")

            //获取图片的位置
            Log.i("TAG", "clickImg图片位置iv_image.x: ${iv_image.x}")
            Log.i("TAG", "clickImg图片位置iv_image.y: ${iv_image.y}")

            //获取屏幕的宽和高
            val dm = resources.displayMetrics
            val width = dm.widthPixels
            val height = dm.heightPixels

            Log.i("TAG", "clickImg屏幕宽: ${width}")
            Log.i("TAG", "clickImg屏幕高: ${height}")


            //获取点击的位置
            Log.i("TAG", "clickImg点击位置x: ${event.x}")
            Log.i("TAG", "clickImg点击位置y: ${event.y}")
//            "input tap ${ivwidth/event.x*1080} ${ivheight/event.y*1900}".let {
//                Log.i("TAG", "clickImg: $it")
//            }


            if (event.action == MotionEvent.ACTION_UP) {
                if (clickable) {
                    clickable = false
                    Handler().postDelayed({
                        requestMeViewModel.sendMQTT(
                            "",
                            "input tap ${event.x * 1.5} ${event.y * 1.5}"
                        )
                        clickable = true
                    }, 50)
                }
            }

            return@setOnTouchListener true
        }
    }


    inner class ProxyClick {

        fun screenCut() {
            picName = System.currentTimeMillis().toString()

            requestMeViewModel.sendMQTT("", "screencap -p ${downloadpath}${picName}.png")
        }


        fun uploadPic() {

            requestMeViewModel.sendMQTT(
                "",
                "custom:action:remote_upload_log_file-${downloadpath}${picName}.png", false
            )
        }
        fun copySettting(){
            iv_image.setImageResource(R.mipmap.bg_setting)
        }

        fun copyRedian(){
            iv_image.setImageResource(R.mipmap.bg_redian)
        }

        fun copyLiulanqi(){
            iv_image.setImageResource(R.mipmap.bg_liulanqi)
        }



        fun loadImage() {

            showLoading("加载中...")

            var i = 0

            GlobalScope.launch {
                Log.e("tag", "开始截图")
                screenCut()
                delay(2000L)
                Log.e("tag", "上传图片")
                uploadPic()
                delay(7000)
                Log.e("tag", "下载图片")
                download()
                delay(2000)
                //循环检查本地文件
                while (!File(downloadpath + "$picName.zip").exists()) {
                    i++
                    Log.e("tag", "等待下载")
                    download()
                    delay(2000)
                    if (i > 2) {
                        Handler(Looper.getMainLooper()).post {
                            dismissLoading()
                            Toast.makeText(activity, "下载超时", Toast.LENGTH_SHORT).show()
                        }
                        Log.e("tag", "下载超时")
                        return@launch
                    }
                }

                if (File(downloadpath + "$picName.zip").exists()) {
                    Log.e("tag", "解压图片")
                    unzip()
                    i=0
                    while (!File(downloadpath + "$picName.png").exists()) {
                        i++
                        Log.e("tag", "等待解压")
                        delay(500)
                        if (i > 5) {
                            Handler(Looper.getMainLooper()).post {
                                dismissLoading()
                                Toast.makeText(activity, "解压超时", Toast.LENGTH_SHORT).show()
                            }
                            Log.e("tag", "解压超时")
                            return@launch
                        }
                    }

                    when {
                        File(downloadpath + "$picName.png").exists() -> {
                            Log.e("tag", "图片存在")
                            Handler(Looper.getMainLooper()).post {
                                dismissLoading()

                                display()
                            }
                        }

                        else -> {
                            Log.e("tag", "图片不存在")
                            Handler(Looper.getMainLooper()).post {
                                dismissLoading()
                                Toast.makeText(activity, "图片不存在", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                }


            }

        }

        fun download() {
            val downloadUrl = "http://uyaoguishort-video.oss-cn-beijing.aliyuncs.com/${picName}.zip"


            val path = downloadpath
            DownloadFileTask().execute(downloadUrl, path, picName.toString() + ".zip")

        }

        fun unzip() {
            // 创建输出流，并指定保存路径
//            val fileName = "downloaded_file.zip" // 可以根据需要修改文件名

            val path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + File.separator + "$picName.zip"
            var file = File(path)
            if (file.exists()) {
                UnzipUtil.unzip(path, path.replace(".zip", "png"))
            }


        }

        fun display() {
            val path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + File.separator + "$picName.png"
            activity?.let {
                Glide.with(it).load(path)
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(iv_image)
            }
        }


    }


}