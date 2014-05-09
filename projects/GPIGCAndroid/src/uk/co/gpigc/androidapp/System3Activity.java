package uk.co.gpigc.androidapp;


import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import uk.co.gpigc.gpigcandroid.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RelativeLayout;


public class System3Activity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system2);

		// init example series data
		GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {
				new GraphViewData(1, 2.0d)
				, new GraphViewData(2, 1.5d)
				, new GraphViewData(3, 2.5d)
				, new GraphViewData(4, 1.0d)
		});

		GraphView graphView = new LineGraphView(
				this // context
				, "GraphViewDemo" // heading
				);
		graphView.addSeries(exampleSeries); // data

		RelativeLayout layout = (RelativeLayout) findViewById(R.id.contentLayout);
		layout.addView(graphView);
	}

	class GraphViewData implements GraphViewDataInterface {
		private double x,y;

		public GraphViewData(double x, double y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public double getX() {
			return this.x;
		}

		@Override
		public double getY() {
			return this.y;
		}
	}
}

