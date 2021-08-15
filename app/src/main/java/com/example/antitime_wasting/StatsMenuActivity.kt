package com.example.antitime_wasting

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.SpinnerAdapter
import androidx.appcompat.app.AppCompatActivity
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import android.content.res.Resources
import com.jjoe64.graphview.helper.StaticLabelsFormatter
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.GridLabelRenderer.GridStyle
/**
 * Page to display statistics stored in the database
 *
 * TODO: change to displaying in either minutes or hours
 * TODO: Add labels to spinners
 *
 * @author David Black
 * @author Sam Fern
 */
class StatsMenuActivity : AppCompatActivity() {
    var graphView: GraphView? = null
    var Ymin: Double = 10.0



    /**
     * Creates the basis of a graph, two spinners for selecting the type of data to display, along
     * with a button to apply the changes from the spinners
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats_menu)

        val typeSpinner : Spinner = findViewById(R.id.sessionTypeSelector)
        val typeAdapter: SpinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.SessionTypes,
            R.layout.spinner_item
        )
        typeSpinner.setAdapter(typeAdapter)

        val scopeSpinner : Spinner = findViewById(R.id.scopeSelector)
        val scopeAdapter:SpinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.ScopeTypes,
            R.layout.spinner_item
        )
        scopeSpinner.setAdapter(scopeAdapter)

        graphView = findViewById(R.id.idGraphView)
        //graphView?.setTitle("My Graph View")

        //graphView?.getGridLabelRenderer()?.setVerticalAxisTitle("Time spent")
        graphView?.getGridLabelRenderer()?.setVerticalAxisTitleTextSize(30f)
        //graphView?.getGridLabelRenderer()?.setHorizontalAxisTitle("Day")
        graphView?.getGridLabelRenderer()?.setHorizontalAxisTitleTextSize(30f)
        graphView?.getGridLabelRenderer()?.setPadding(8)

        //graphView?.setTitleColor(R.color.purple_200)
        //graphView?.setTitleColor(R.color.black)

        graphView?.setTitleTextSize(50f)

        graphView?.getGridLabelRenderer()?.setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);

        //graphView?.getViewport()?.setYAxisBoundsManual(true)
        //graphView?.getViewport()?.setXAxisBoundsManual(true)


        //graphView?.getViewport()?.setMinX(0.0)
        /*graphView?.getViewport()?.setMaxX(31.0)
        graphView?.getViewport()?.setMinY(0.0)
        graphView?.getViewport()?.setMaxY(Ymin)
*/

        val dayLabels = StaticLabelsFormatter(graphView)
        var dayArray = arrayOf("", "05", "10", "15", "20", "25", "30")
        var i: Int = 0
        /*for (i: Int in 0..30){
            dayArray[i] = (i+1).toString() + "  "
        }*/
        dayLabels.setHorizontalLabels(dayArray)

        val res: Resources = resources
        val monthLabels = StaticLabelsFormatter(graphView)
        val monthArray = arrayOfNulls<String>(12)
        i = 0
        for(month in res.getStringArray(R.array.months)) {
            monthArray[i] = month.substring(0, 3) + "  "
            i += 1
        }
        monthLabels.setHorizontalLabels(monthArray)


        update_graph("Study", Scope.BY_DAY, dayLabels)


        val applybtn = findViewById<Button>(R.id.applyBtn)
        applybtn.setOnClickListener {
            when (scopeSpinner.selectedItem.toString()){
                "Month View" -> update_graph(typeSpinner.selectedItem.toString(), Scope.BY_DAY, dayLabels)
                "Year View" -> update_graph(typeSpinner.selectedItem.toString(), Scope.BY_MONTH, monthLabels)
            }

        }

        /* TESTING
        val data: ArrayList<Point> = DataPointFinder.findDataPoints("Study", Scope.BY_DAY, this)
        for (point in data) {
            Log.i("datapoints", "x: ${point.x}  y: ${point.y}")
        }
        */
    }

    /**
     * Redraws the graph with data from a given session type and a scope.
     *
     * @param sessionType the type of session the graph is to display.
     * @param scope the length of time the graph covers.
     */
    fun update_graph(sessionType: String, scope: Scope, YLabels: StaticLabelsFormatter){
        graphView?.removeAllSeries()
        val points: ArrayList<Point> = DataPointFinder.findDataPoints(sessionType, scope, this)
        var dataPoints = arrayOfNulls<DataPoint>(points.size)
        for (point in points) {
            dataPoints[point.x - 1] = DataPoint((point.x).toDouble(), (point.y).toDouble() / (1000))
        }
        //val series = LineGraphSeries(dataPoints)
        val series = BarGraphSeries(dataPoints)

        //graphView?.getGridLabelRenderer()?.setVerticalAxisTitle("Time spent")

        when (scope){
            Scope.BY_DAY -> {
                graphView?.setTitle("$sessionType This Month")
                graphView?.getGridLabelRenderer()?.setHorizontalAxisTitle("Day")
                graphView?.getGridLabelRenderer()?.setLabelFormatter(YLabels)
                graphView?.getGridLabelRenderer()?.setHorizontalLabelsAngle(135)
            }
            Scope.BY_MONTH -> {
                graphView?.setTitle("$sessionType This Year")
                graphView?.getGridLabelRenderer()?.setHorizontalAxisTitle("Month")
                graphView?.getGridLabelRenderer()?.setLabelFormatter(YLabels)
                graphView?.getGridLabelRenderer()?.setHorizontalLabelsAngle(135)
            }
        }



        val numPoints: Double = points.size.toDouble()
        val maxValue: Double = DataPointFinder.getMaxY(points)

        graphView?.getViewport()?.setMaxX(numPoints)
        if (maxValue > Ymin) {
            graphView?.getViewport()?.setMaxY(maxValue + 1)
        } else {
            graphView?.getViewport()?.setMaxY(Ymin)
        }

        graphView?.addSeries(series)

    }
}