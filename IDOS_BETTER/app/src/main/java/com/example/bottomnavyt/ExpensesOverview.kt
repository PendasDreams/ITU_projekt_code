package com.example.bottomnavyt

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class ExpensesOverview : Fragment() {

    private lateinit var lineChart: LineChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_expensesoverview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize your chart here
        lineChart = view.findViewById(R.id.chart)

        setupLineChart()
    }

    private fun setupLineChart() {
        val values = arrayOf(700f, 400f, 800f, 500f, 650f, 300f)
        val entries = ArrayList<Entry>()
        for (i in values.indices) {
            entries.add(Entry(i.toFloat(), values[i]))
        }

        val dataSet = LineDataSet(entries, "Expenses")
        with(dataSet) {
            setColors(ColorTemplate.getHoloBlue())
            valueTextColor = Color.WHITE
            lineWidth = 2f
            setCircleColor(Color.WHITE)
            circleRadius = 5f
            setDrawCircleHole(false)
            valueTextSize = 10f
            setDrawFilled(true)
            fillColor = ColorTemplate.getHoloBlue()
        }

        val lineData = LineData(dataSet)
        lineChart.data = lineData

        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.textColor = Color.WHITE
        xAxis.granularity = 1f
        xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("Čer", "Čvc", "Srp", "Zář", "Říj", "Lis"))

        val yAxisLeft = lineChart.axisLeft
        yAxisLeft.textColor = Color.WHITE

        val yAxisRight = lineChart.axisRight
        yAxisRight.setDrawLabels(false)

        lineChart.description.isEnabled = false
        lineChart.legend.isEnabled = false

        lineChart.invalidate()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ExpensesOverview().apply {
                arguments = Bundle().apply {
                    // Add any required arguments here
                }
            }
    }
}
