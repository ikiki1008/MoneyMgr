    package com.example.mia_hometest.fragments;

    import android.annotation.SuppressLint;
    import android.content.Context;
    import android.content.res.AssetManager;
    import android.graphics.Color;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ImageView;
    import android.widget.TextView;

    import androidx.fragment.app.Fragment;

    import com.example.mia_hometest.R;
    import com.github.mikephil.charting.animation.Easing;
    import com.github.mikephil.charting.charts.LineChart;
    import com.github.mikephil.charting.components.Legend;
    import com.github.mikephil.charting.components.XAxis;
    import com.github.mikephil.charting.components.YAxis;
    import com.github.mikephil.charting.data.Entry;
    import com.github.mikephil.charting.data.LineData;
    import com.github.mikephil.charting.data.LineDataSet;

    import org.json.JSONArray;
    import org.json.JSONObject;

    import java.io.BufferedReader;
    import java.io.InputStream;
    import java.io.InputStreamReader;
    import java.util.ArrayList;

    public class ChartScreenFragment extends Fragment {
        private final String TAG = ChartScreenFragment.class.getSimpleName();
        private Context mContext = null;

        public ChartScreenFragment (Context context) {
            mContext = context;
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            Log.d(TAG, "ChartScreenFragment onAttach: ");
            mContext = context;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Log.d(TAG, " ChartScreenFragment onCreateView: ");
            View view = inflater.inflate(R.layout.chart_screen, container, false);
            return view;
        }

        @Override
        public void onDestroy() {
            Log.d(TAG, " ChartScreenFragment 끌게요");
            super.onDestroy();
        }
    }


