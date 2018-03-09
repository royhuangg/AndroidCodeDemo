package tw.yalan.cafeoffice.dialog

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatTextView
import android.view.View
import android.widget.TextView
import com.grasea.grandroid.mvp.UsingPresenter
import com.mcxiaoke.koi.ext.onClick
import kotlinx.android.synthetic.main.dialog_day_of_week_edit.*
import tw.yalan.cafeoffice.Config
import tw.yalan.cafeoffice.R
import tw.yalan.cafeoffice.api.model.*
import tw.yalan.cafeoffice.common.BaseDialogFragmentPresenter
import tw.yalan.cafeoffice.common.BaseMVPDialogFragment
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.reflect.jvm.internal.impl.protobuf.LazyStringArrayList

@UsingPresenter(value = DayOfWeekEditPresenter::class, singleton = false)
class DayOfWeekEditDialog : BaseMVPDialogFragment<DayOfWeekEditPresenter>() {

    override fun getDialogResourceId(): Int {
        return R.layout.dialog_day_of_week_edit
    }

    companion object {

        fun newInstance(id: String? = null, hour: BusinessHour? = null): DayOfWeekEditDialog {

            val args = Bundle()
            if (id != null)
                args.putString(Config.EXTRA_KEY, id)
            if (hour != null)
                args.putSerializable(Config.EXTRA_DATA, hour)
            val fragment = DayOfWeekEditDialog()
            fragment.arguments = args
            return fragment
        }

        val START = "Start"
        val END = "End"

    }

    var onClickSendBody: ((BusinessHour?) -> Unit)? = null
    val sdf = SimpleDateFormat("hh:mm", Locale.TAIWAN)
    val df = DecimalFormat("00")
    var onclick: View.OnClickListener = View.OnClickListener {
        if (it is TextView) {
            it.isEnabled = false
            var time = it.text.toString()
            var calendar = Calendar.getInstance()
            val calendarOther = Calendar.getInstance()
            calendar.time = sdf.parse(time)
            val split = (it.tag as String).split("_")
            when (split[0]) {
                "Monday" -> {
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                    if (split[1] == START) {
                        val timeOther = tvMondayEnd.text.toString()
                        calendarOther.time = sdf.parse(timeOther)
                    } else {
                        val timeOther = tvMondayStart.text.toString()
                        calendarOther.time = sdf.parse(timeOther)
                    }
                }
                "Tuesday" -> {
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY)
                    if (split[1] == START) {
                        val timeOther = tvTuesdayEnd.text.toString()
                        calendarOther.time = sdf.parse(timeOther)
                    } else {
                        val timeOther = tvTuesdayStart.text.toString()
                        calendarOther.time = sdf.parse(timeOther)
                    }
                }
                "Wednesday" -> {
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY)
                    if (split[1] == START) {
                        val timeOther = tvWednesdayEnd.text.toString()
                        calendarOther.time = sdf.parse(timeOther)
                    } else {
                        val timeOther = tvWednesdayStart.text.toString()
                        calendarOther.time = sdf.parse(timeOther)
                    }
                }
                "Thursday" -> {
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY)
                    if (split[1] == START) {
                        val timeOther = tvThursdayEnd.text.toString()
                        calendarOther.time = sdf.parse(timeOther)
                    } else {
                        val timeOther = tvThursdayStart.text.toString()
                        calendarOther.time = sdf.parse(timeOther)
                    }
                }
                "Friday" -> {
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
                    if (split[1] == START) {
                        val timeOther = tvFridayEnd.text.toString()
                        calendarOther.time = sdf.parse(timeOther)
                    } else {
                        val timeOther = tvFridayStart.text.toString()
                        calendarOther.time = sdf.parse(timeOther)
                    }
                }
                "Saturday" -> {
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
                    if (split[1] == START) {
                        val timeOther = tvSaturdayEnd.text.toString()
                        calendarOther.time = sdf.parse(timeOther)
                    } else {
                        val timeOther = tvSaturdayStart.text.toString()
                        calendarOther.time = sdf.parse(timeOther)
                    }
                }
                "Sunday" -> {
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                    if (split[1] == START) {
                        val timeOther = tvSundayEnd.text.toString()
                        calendarOther.time = sdf.parse(timeOther)
                    } else {
                        val timeOther = tvSundayStart.text.toString()
                        calendarOther.time = sdf.parse(timeOther)
                    }
                }
            }
            var dialog: OpenTimePickerDialog
            if (split[1] == START) {
                dialog = OpenTimePickerDialog.newInstance(dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK), startHour = calendar.get(Calendar.HOUR_OF_DAY), startMinute = calendar.get(Calendar.MINUTE),
                        endHour = calendarOther.get(Calendar.HOUR_OF_DAY), endMinute = calendarOther.get(Calendar.MINUTE), tabPosition = 0)
            } else {
                dialog = OpenTimePickerDialog.newInstance(dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK), startHour = calendarOther.get(Calendar.HOUR_OF_DAY), startMinute = calendarOther.get(Calendar.MINUTE),
                        endHour = calendar.get(Calendar.HOUR_OF_DAY), endMinute = calendar.get(Calendar.MINUTE), tabPosition = 1)
            }
            dialog.onClickSetButtonBody = { forAll, dayOfWeek, startHour, startMinute, endHour, endMinute ->
                //                val timeStart = "$startHour:$startMinute"
//                val timeEnd = "$endHour:$endMinute"

                val timeStart = df.format(startHour) + ":" + df.format(startMinute)
                val timeEnd = df.format(endHour) + ":" + df.format(endMinute)
                if (forAll) {
                    tvMondayStart.text = timeStart
                    tvTuesdayStart.text = timeStart
                    tvWednesdayStart.text = timeStart
                    tvThursdayStart.text = timeStart
                    tvFridayStart.text = timeStart
                    tvSaturdayStart.text = timeStart
                    tvSundayStart.text = timeStart

                    tvMondayEnd.text = timeEnd
                    tvTuesdayEnd.text = timeEnd
                    tvWednesdayEnd.text = timeEnd
                    tvThursdayEnd.text = timeEnd
                    tvFridayEnd.text = timeEnd
                    tvSaturdayEnd.text = timeEnd
                    tvSundayEnd.text = timeEnd

                    cbMonday.isChecked = true
                    cbTuesday.isChecked = true
                    cbWednesday.isChecked = true
                    cbThursday.isChecked = true
                    cbFriday.isChecked = true
                    cbSaturday.isChecked = true
                    cbSunday.isChecked = true

                } else {
                    when (dayOfWeek) {
                        Calendar.MONDAY -> {
                            cbMonday.isChecked = true
                            tvMondayStart.text = timeStart
                            tvMondayEnd.text = timeEnd
                        }
                        Calendar.TUESDAY -> {
                            cbTuesday.isChecked = true
                            tvTuesdayStart.text = timeStart
                            tvTuesdayEnd.text = timeEnd
                        }
                        Calendar.WEDNESDAY -> {
                            cbWednesday.isChecked = true
                            tvWednesdayStart.text = timeStart
                            tvWednesdayEnd.text = timeEnd
                        }
                        Calendar.THURSDAY -> {
                            cbThursday.isChecked = true
                            tvThursdayStart.text = timeStart
                            tvThursdayEnd.text = timeEnd
                        }
                        Calendar.FRIDAY -> {
                            cbFriday.isChecked = true
                            tvFridayStart.text = timeStart
                            tvFridayEnd.text = timeEnd
                        }
                        Calendar.SATURDAY -> {
                            cbSaturday.isChecked = true
                            tvSaturdayStart.text = timeStart
                            tvSaturdayEnd.text = timeEnd
                        }
                        Calendar.SUNDAY -> {
                            cbSunday.isChecked = true
                            tvSundayStart.text = timeStart
                            tvSundayEnd.text = timeEnd
                        }
                    }
                }
            }
            dialog.show(fragmentManager, "OpenTimePickerDialog")

            it.isEnabled = true

        }
    }

    override fun initView(savedInstanceState: Bundle?, vararg args: Any?) {
        btnClose.onClick { dismiss() }
        btnSent.onClick {
            for (i in 1..7) {
                when (i) {
                    1 -> if (cbSunday.isChecked) presenter?.saveTime(i, tvSundayStart.text.toString(), tvSundayEnd.text.toString())

                    2 -> if (cbMonday.isChecked) presenter?.saveTime(i, tvMondayStart.text.toString(), tvMondayEnd.text.toString())

                    3 -> if (cbTuesday.isChecked) presenter?.saveTime(i, tvTuesdayStart.text.toString(), tvTuesdayEnd.text.toString())

                    4 -> if (cbWednesday.isChecked) presenter?.saveTime(i, tvWednesdayStart.text.toString(), tvWednesdayEnd.text.toString())

                    5 -> if (cbThursday.isChecked) presenter?.saveTime(i, tvThursdayStart.text.toString(), tvThursdayEnd.text.toString())

                    6 -> if (cbFriday.isChecked) presenter?.saveTime(i, tvFridayStart.text.toString(), tvFridayEnd.text.toString())

                    7 -> if (cbSaturday.isChecked) presenter?.saveTime(i, tvSaturdayStart.text.toString(), tvSaturdayEnd.text.toString())
                }
            }

            presenter?.onClickSend()
        }
        btnRestore.onClick {

            presenter?.onClickRestore()
        }

        tvMondayEnd.tag = "Monday_End"
        tvMondayEnd.onClick { onclick.onClick(it) }
        tvMondayStart.tag = "Monday_Start"
        tvMondayStart.onClick { onclick.onClick(it) }
        cbMonday.setOnCheckedChangeListener { _, isChecked ->
            tvMondayEnd.alpha = if (isChecked) 1f else 0.5f
            tvMondayTo.alpha = if (isChecked) 1f else 0.5f
            tvMondayStart.alpha = if (isChecked) 1f else 0.5f
        }
        cbMonday.isChecked = true

        tvTuesdayEnd.tag = "Tuesday_End"
        tvTuesdayEnd.onClick { onclick.onClick(it) }
        tvTuesdayStart.tag = "Tuesday_Start"
        tvTuesdayStart.onClick { onclick.onClick(it) }
        cbTuesday.setOnCheckedChangeListener { _, isChecked ->
            tvTuesdayEnd.alpha = if (isChecked) 1f else 0.5f
            tvTuesdayTo.alpha = if (isChecked) 1f else 0.5f
            tvTuesdayStart.alpha = if (isChecked) 1f else 0.5f
        }
        cbTuesday.isChecked = true

        tvWednesdayEnd.tag = "Wednesday_End"
        tvWednesdayEnd.onClick { onclick.onClick(it) }
        tvWednesdayStart.tag = "Wednesday_Start"
        tvWednesdayStart.onClick { onclick.onClick(it) }
        cbWednesday.setOnCheckedChangeListener { _, isChecked ->
            tvWednesdayEnd.alpha = if (isChecked) 1f else 0.5f
            tvWednesdayTo.alpha = if (isChecked) 1f else 0.5f
            tvWednesdayStart.alpha = if (isChecked) 1f else 0.5f
        }
        cbWednesday.isChecked = true

        tvThursdayEnd.tag = "Thursday_End"
        tvThursdayEnd.onClick { onclick.onClick(it) }
        tvThursdayStart.tag = "Thursday_Start"
        tvThursdayStart.onClick { onclick.onClick(it) }
        cbThursday.setOnCheckedChangeListener { _, isChecked ->
            tvThursdayEnd.alpha = if (isChecked) 1f else 0.5f
            tvThursdayTo.alpha = if (isChecked) 1f else 0.5f
            tvThursdayStart.alpha = if (isChecked) 1f else 0.5f
        }
        cbThursday.isChecked = true

        tvFridayEnd.tag = "Friday_End"
        tvFridayEnd.onClick { onclick.onClick(it) }
        tvFridayStart.tag = "Friday_Start"
        tvFridayStart.onClick { onclick.onClick(it) }
        cbFriday.setOnCheckedChangeListener { _, isChecked ->
            tvFridayEnd.alpha = if (isChecked) 1f else 0.5f
            tvFridayTo.alpha = if (isChecked) 1f else 0.5f
            tvFridayStart.alpha = if (isChecked) 1f else 0.5f
        }
        cbFriday.isChecked = true

        tvSaturdayEnd.tag = "Saturday_End"
        tvSaturdayEnd.onClick { onclick.onClick(it) }
        tvSaturdayStart.tag = "Saturday_Start"
        tvSaturdayStart.onClick { onclick.onClick(it) }
        cbSaturday.setOnCheckedChangeListener { _, isChecked ->
            tvSaturdayEnd.alpha = if (isChecked) 1f else 0.5f
            tvSaturdayTo.alpha = if (isChecked) 1f else 0.5f
            tvSaturdayStart.alpha = if (isChecked) 1f else 0.5f
        }
        cbSaturday.isChecked = true

        tvSundayEnd.tag = "Sunday_End"
        tvSundayEnd.onClick { onclick.onClick(it) }
        tvSundayStart.tag = "Sunday_Start"
        tvSundayStart.onClick { onclick.onClick(it) }
        cbSunday.setOnCheckedChangeListener { _, isChecked ->
            tvSundayEnd.alpha = if (isChecked) 1f else 0.5f
            tvSundayTo.alpha = if (isChecked) 1f else 0.5f
            tvSundayStart.alpha = if (isChecked) 1f else 0.5f
        }
        cbSunday.isChecked = true

    }


    @SuppressLint("SetTextI18n")
    fun updateHourState(timeData: BusinessHour?) {
        for (i in 1..7) {
            val open = timeData?.getOpen(i)
            val close = timeData?.getClose(i)
            val emptyData: Boolean = open == null
            val hour = open?.get(Calendar.HOUR_OF_DAY) ?: 8
            val minute = open?.get(Calendar.MINUTE) ?: 30
            val hourClose = close?.get(Calendar.HOUR_OF_DAY) ?: 20
            val minuteClose = close?.get(Calendar.MINUTE) ?: 0
            val startTime = df.format(hour) + ":" + df.format(minute)
            val closeTime = df.format(hourClose) + ":" + df.format(minuteClose)

            when (i) {
                1 -> {
                    cbSunday.isChecked = !emptyData
                    tvSundayEnd.text = closeTime
                    tvSundayStart.text = startTime
                }
                2 -> {
                    cbMonday.isChecked = !emptyData
                    tvMondayEnd.text = closeTime
                    tvMondayStart.text = startTime
                }
                3 -> {
                    cbTuesday.isChecked = !emptyData
                    tvTuesdayEnd.text = closeTime
                    tvTuesdayStart.text = startTime
                }
                4 -> {
                    cbWednesday.isChecked = !emptyData
                    tvWednesdayEnd.text = closeTime
                    tvWednesdayStart.text = startTime
                }
                5 -> {
                    cbThursday.isChecked = !emptyData
                    tvThursdayEnd.text = closeTime
                    tvThursdayStart.text = startTime
                }
                6 -> {
                    cbFriday.isChecked = !emptyData
                    tvFridayEnd.text = closeTime
                    tvFridayStart.text = startTime
                }
                7 -> {
                    cbSaturday.isChecked = !emptyData
                    tvSaturdayEnd.text = closeTime
                    tvSaturdayStart.text = startTime
                }
            }

        }
    }

    fun back(timeData: BusinessHour?) {
        onClickSendBody?.invoke(timeData)
        dismiss()
    }


}

class DayOfWeekEditPresenter : BaseDialogFragmentPresenter<DayOfWeekEditDialog>() {
    var timeDataTemp: BusinessHour? = null
    var timeData: BusinessHour? = null

    override fun onDialogCreateView(args: Bundle?, savedInstanceState: Bundle?) {
        var timeDataTemp = args?.getSerializable(Config.EXTRA_DATA) as? BusinessHour ?: BusinessHour()
        timeData = timeDataTemp.copy()
        contract.initView(savedInstanceState)
        contract.updateHourState(timeData)
    }

    fun onClickRestore() {
        timeData = timeDataTemp?.copy()
        contract.updateHourState(timeData)
    }

    fun saveTime(dayOfWeek: Int, startTime: String, closeTime: String) {
        timeData?.putOpen(dayOfWeek, startTime)
        timeData?.putClose(dayOfWeek, closeTime)
    }

    fun onClickSend() {
        contract.back(timeData)
    }
}
