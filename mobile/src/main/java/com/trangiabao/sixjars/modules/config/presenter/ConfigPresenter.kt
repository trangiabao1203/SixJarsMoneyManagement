package com.trangiabao.sixjars.modules.config.presenter

import android.content.Context
import com.trangiabao.sixjars.data.database.JarDB
import com.trangiabao.sixjars.data.model.Jar
import com.trangiabao.sixjars.modules.config.view.ConfigView

class ConfigPresenter(var context: Context, var view: ConfigView) : ConfigPresenterImpl {

    override fun createView() {
        view.onInitControls()
        view.onInitEvents()
    }

    override fun getAll() {
        val list = JarDB.getAll()
        val result = list.isNotEmpty()
        var msg: String = ""
        if (!result) {
            msg = "Load that bai"
        }
        view.onListLoaded(result, msg, list)
    }

    override fun update(list: List<Jar>) {
        var newList = list
        val sum = newList.sumBy { x -> x.percent!! }
        var result: Boolean = false
        val msg: String
        if (sum != 100) {
            msg = "Khong du 100%"
        } else {
            result = true
            msg = "Update Successed"
            newList = JarDB.update(list)
        }
        view.onUpdateResult(result, msg, newList)
    }
}