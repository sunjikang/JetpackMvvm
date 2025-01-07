package me.hgj.jetpackmvvm.demo.ui.fragment.home

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_shome.input_button
import kotlinx.android.synthetic.main.fragment_shome.input_button2
import kotlinx.android.synthetic.main.fragment_shome.input_edittext
import kotlinx.android.synthetic.main.fragment_shome.input_shopname
import kotlinx.android.synthetic.main.fragment_shome.is_official
import kotlinx.android.synthetic.main.include_list.*
import kotlinx.android.synthetic.main.include_recyclerview.*
import kotlinx.android.synthetic.main.include_toolbar.*
import me.hgj.jetpackmvvm.demo.R
import me.hgj.jetpackmvvm.demo.app.base.BaseFragment1
import me.hgj.jetpackmvvm.demo.app.ext.*
import me.hgj.jetpackmvvm.demo.databinding.FragmentShomeBinding
import me.hgj.jetpackmvvm.demo.viewmodel.state.HomeViewModel
import me.hgj.jetpackmvvm.ext.util.toJson
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


/**
 * 作者　: hegaojian
 * 时间　: 2019/12/27
 * 描述　:
 */
class SHomeFragment : BaseFragment1<HomeViewModel, FragmentShomeBinding>() {


     override fun initView(savedInstanceState: Bundle?) {
         input_button.setOnClickListener {

            SPUtils.getInstance().put("hj", is_official.isChecked)

            input_edittext.text.toString().let {
                SPUtils.getInstance().put("sn", it)
            }
             input_shopname.text.toString().let {
                 SPUtils.getInstance().put("shopname", it)
             }

        }

        input_button2.setOnClickListener {
            input_shopname.text.toString().let {
                for (machineBean in listbean) {
                    if (machineBean?.merchantName?.contains(input_shopname.text.toString()) == true) {
                        Log.i("TAG", "readJsonData: ${machineBean?.merchantName}")
                        temListbean.add(machineBean?.merchantName)
                        Log.i("TAG", "readJsonData: ${temListbean.size}")
                    }
                }
            }
            val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
            builder.setTitle("门店名称")
            builder.setItems(temListbean.toTypedArray(),
                DialogInterface.OnClickListener { dialog, which ->

                    listbean.forEach({
                        if (it?.merchantName == temListbean[which]) {
                            selectmachine = it
                        }
                    })
                    Toast.makeText(
                        activity,
                        "门店编码：${selectmachine?.code}\n门店名称：${selectmachine?.merchantName}",
                        Toast.LENGTH_SHORT
                    ).show()
                    input_edittext.text.toString().let {
                        SPUtils.getInstance().put("sn", selectmachine?.sn)
                    }
                    input_edittext.text.clear()
                    input_shopname?.setText(selectmachine?.merchantName)
                    input_edittext?.setText(selectmachine?.sn)

                })
            builder.create().show()
        }

        //解析json实体
        readJsonData()
    }

    var selectmachine: MachineBean? = null
    var temListbean = ArrayList<String?>()

    var listbean = ArrayList<MachineBean?>()
    fun readJsonData() {
        val json = readRawTextFile(R.raw.machine)


        val type = object : TypeToken<ArrayList<MachineBean?>?>() {}.type
        var gson = Gson()
        listbean = gson.fromJson<ArrayList<MachineBean?>>(json, type)
        listbean.forEach {
            Log.i("TAG", "readJsonData: ${it?.sn.toString()}")
        }
        for (i in 0..20) {
            if (i + 1 > listbean.size / 30) {
                listbean.subList((i * 30), listbean.size).toJson().let {
                    Log.i("TAG", "readJsonData: $it")
                }
                break
            } else {
                listbean.subList((i * 30), (i + 1) * 30).toJson().let {
                    Log.i("TAG", "readJsonData: $it")
                }
            }

        }


    }

    private fun readRawTextFile(resId: Int): String {
        val resources = resources
        val inputStream = resources.openRawResource(resId)
        val reader = BufferedReader(InputStreamReader(inputStream))

        val sb = StringBuilder()
        var line: String?
        try {
            while ((reader.readLine().also { line = it }) != null) {
                sb.append(line).append("\n")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return sb.toString()
    }


}


data class MachineBean(
    val sn: String, //sn
    val merchantName: String, //门店名称
    val code: String //设备编码

)