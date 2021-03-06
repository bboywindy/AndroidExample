package com.windy.libralive.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import com.blankj.utilcode.util.ToastUtils

import com.windy.libralive.base.viewmodel.BaseViewModel
import com.windy.libralive.common.BaseResult
import com.windy.libralive.common.fold
import com.windy.libralive.data.model.BaseListResponse
import com.windy.libralive.data.model.home.HomeBean
import com.windy.libralive.data.model.home.LoginBean
import com.windy.libralive.net.RetrofitFactory

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeViewModel : BaseViewModel() {

    override var modelClassName: String = HomeViewModel::class.java.simpleName

    val text: LiveData<String> = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }

    val mHomeBeans = arrayListOf<HomeBean>()
    val mDataBeans = MutableLiveData<BaseListResponse<MutableList<HomeBean>>>()

    val mLoginRegisterBean = MutableLiveData<LoginBean>()

    val state = MutableLiveData<Int>().apply {
        this.value = 0
    }

    fun getArticleJson(page: Int) = launchUI {
        val baseResponse = withContext(Dispatchers.IO) {
            val articleJson = RetrofitFactory.getInstance().service.getArticleJson(page)
            if (articleJson.errCode == 0) BaseResult.success(articleJson.data) else BaseResult.failure(
                Throwable("Failed to getArticleJson ${articleJson.errMsg}")
            )
        }

        baseResponse.fold({
            Log.d(modelClassName, "getArticleJson: $it")
            mHomeBeans.addAll(it.datas)
            it.datas.clear()
            it.datas.addAll(mHomeBeans)
            mDataBeans.value = it
        }, {
            ToastUtils.showShort(it.message)
        })
    }

    fun login(username: String, password: String) = launchUI {
        val baseResponse = withContext(Dispatchers.IO) {
            val loginJson = RetrofitFactory.getInstance().service.login(username, password)
            if (loginJson.errCode == 0) BaseResult.success(loginJson.data) else BaseResult.failure(
                Throwable("Failed to login ${loginJson.errMsg}")
            )
        }

        baseResponse.fold({
            Log.d(modelClassName, "login: $it")
            mLoginRegisterBean.value = it
        }, {
            Log.d(modelClassName, "login: $it")
            ToastUtils.showShort(it.message)
        })
    }

}