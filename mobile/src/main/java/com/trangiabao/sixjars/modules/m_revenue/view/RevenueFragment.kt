package com.trangiabao.sixjars.modules.m_revenue.view

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trangiabao.sixjars.R
import com.trangiabao.sixjars.data.model.Revenue
import com.trangiabao.sixjars.modules.m_revenue.adapter.RevenueAdapter
import com.trangiabao.sixjars.modules.m_revenue.presenter.RevenuePresenter
import com.trangiabao.sixjars.modules.m_revenue_update.view.UpdateRevenueActivity
import com.trangiabao.sixjars.utils.base.BaseFragment
import com.trangiabao.sixjars.utils.component.ScrollAwareFABBehavior
import com.trangiabao.sixjars.utils.component.dialog.CustomDialogConfirm
import com.trangiabao.sixjars.utils.component.toast.ToastHelper
import com.trangiabao.sixjars.utils.helper.DateTimeHelper
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.fragment_revenue.view.*
import kotlinx.android.synthetic.main.layout_date_filter.view.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter

class RevenueFragment : BaseFragment(), RevenueView {

    private var mView: View? = null
    private var _adapter: RevenueAdapter? = null
    private var presenter: RevenuePresenter? = null
    private var dateFormat: DateTimeFormatter? = null
    private var dateFrom: DateTime = DateTime()
    private var dateTo: DateTime = DateTime()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater!!.inflate(R.layout.fragment_revenue, container, false)
        presenter = RevenuePresenter(this)
        presenter!!.createView()
        presenter!!.filter(dateFrom, dateTo)
        return mView
    }

    override fun onInitControls() {
        mView!!.run {
            val p = fab.layoutParams as CoordinatorLayout.LayoutParams
            p.behavior = ScrollAwareFABBehavior()
            fab.layoutParams = p

            recyclerView!!.run {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                _adapter = RevenueAdapter(object : RevenueAdapter.ItemClickListener {
                    override fun onClickListener(type: Revenue, position: Int) {
                        val intent = Intent(context, UpdateRevenueActivity::class.java)
                        intent.putExtra("typeId", type.id)
                        startActivity(intent)
                    }

                    override fun onLongClickListener(type: Revenue, position: Int): Boolean {
                        CustomDialogConfirm.Builder(context)
                                .withTitle(R.string.confirm)
                                .withContent(R.string.deletion)
                                .withIcon(R.drawable.ic_delete)
                                .setOnConfirmClick(object : CustomDialogConfirm.OnConfirmListener {
                                    override fun onResult(dialog: CustomDialogConfirm, result: Boolean) {
                                        presenter!!.delete(type.id!!, position)
                                        dialog.dismiss()
                                    }
                                }).show()
                        return true
                    }
                })
                adapter = _adapter
            }
            dateFormat = DateTimeHelper.getDateFormat(context)
            dateFrom = DateTimeHelper.getDateFrom()
            dateTo = DateTimeHelper.getDateTo()
            txtDateFrom.text = DateTimeHelper.printDate(dateFormat!!, dateFrom)
            txtDateTo.text = DateTimeHelper.printDate(dateFormat!!, dateTo)
        }
    }

    override fun onInitEvents() {
        mView!!.run {
            txtDateFrom.setOnClickListener {
                val dpd = DatePickerDialog.newInstance({ _, year, monthOfYear, dayOfMonth ->
                    dateFrom = DateTime()
                            .withYear(year)
                            .withMonthOfYear(monthOfYear + 1)
                            .withDayOfMonth(dayOfMonth)
                    txtDateFrom.text = dateFormat!!.print(dateFrom)
                    presenter!!.filter(dateFrom, dateTo)
                }, dateFrom.year, dateFrom.monthOfYear - 1, dateFrom.dayOfMonth)
                dpd.setVersion(DatePickerDialog.Version.VERSION_1)
                dpd.show(activity.fragmentManager, "")
            }

            txtDateTo.setOnClickListener {
                val dpd = DatePickerDialog.newInstance({ _, year, monthOfYear, dayOfMonth ->
                    dateTo = DateTime()
                            .withYear(year)
                            .withMonthOfYear(monthOfYear + 1)
                            .withDayOfMonth(dayOfMonth)
                    txtDateTo.text = dateFormat!!.print(dateTo)
                    presenter!!.filter(dateFrom, dateTo)
                }, dateTo.year, dateTo.monthOfYear - 1, dateTo.dayOfMonth)
                dpd.setVersion(DatePickerDialog.Version.VERSION_1)
                dpd.show(activity.fragmentManager, "")
            }

            fab.setOnClickListener {
                val intent = Intent(context, UpdateRevenueActivity::class.java)
                intent.putExtra("typeId", "")
                startActivity(intent)
            }
        }
    }

    override fun onGetListResult(result: Boolean, msg: String, list: List<Revenue>) {
        _adapter!!.updateList(list.toMutableList())
    }

    override fun onDeleteResult(result: Boolean, msg: String, position: Int) {
        if (result) {
            _adapter!!.removeItem(position)
            ToastHelper(context).toastSuccess("Item has been remove")
        } else
            ToastHelper(context).toastError("Delete Error")
    }
}