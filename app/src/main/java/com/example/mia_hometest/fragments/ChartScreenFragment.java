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
        private LineChart mChart;
        ImageView mWeek = null;
        ImageView mMonth = null;
        TextView mLeft = null;
        TextView mRight = null;

        public ChartScreenFragment (Context context) {
            mContext = context;
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            Log.d(TAG, "ChartScreenFragment onAttach: ");
            mContext = context;
        }

        @SuppressLint("MissingInflatedId")
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Log.d(TAG, " ChartScreenFragment onCreateView: ");
            View view = inflater.inflate(R.layout.chart_screen, container, false);
            mChart = view.findViewById(R.id.chartLine); // 수정된 부분
            mWeek = view.findViewById(R.id.imgWeek);
            mMonth = view.findViewById(R.id.imgMonth);
            mLeft = view.findViewById(R.id.left);
            mRight = view.findViewById(R.id.right);

            createWeekData();
            mWeek.setImageResource(R.drawable.week_frame);
            mLeft.setText(R.string.left_week);
            mRight.setText(R.string.right_week);

            mWeek.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createWeekData();
                    mWeek.setImageResource(R.drawable.week_frame);
                    mMonth.setImageResource(R.drawable.month_unframe);
                    mLeft.setText(R.string.left_week);
                    mRight.setText(R.string.right_week);
                }
            });

            mMonth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createMonthData();
                    mWeek.setImageResource(R.drawable.week_unframe);
                    mMonth.setImageResource(R.drawable.month_frame);
                    mLeft.setText(R.string.week_title);
                    mRight.setText(R.string.month_title);
                }
            });

            return view;
        }

        @Override
        public void onDestroy() {
            Log.d(TAG, " ChartScreenFragment 끌게요");
            super.onDestroy();
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private void createMonthData() {
            ArrayList<Entry> weekList = new ArrayList<>();
            ArrayList<Entry> monthList = new ArrayList<>();

            try {
                // Week 데이터 읽기
                InputStream weekInputStream = mContext.getAssets().open("Data.json");
                BufferedReader weekReader = new BufferedReader(new InputStreamReader(weekInputStream));
                StringBuilder weekStringBuilder = new StringBuilder();
                String weekLine;

                while ((weekLine = weekReader.readLine()) != null) {
                    weekStringBuilder.append(weekLine);
                }

                JSONObject weekJsonObject = new JSONObject(weekStringBuilder.toString());
                JSONArray weekJsonArray = weekJsonObject.getJSONArray("Data");

                for (int i = 0; i < weekJsonArray.length(); i++) {
                    JSONObject item = weekJsonArray.getJSONObject(i);
                    float x = Float.parseFloat(item.getString("x"));
                    float y = Float.parseFloat(item.getString("y"));
                    weekList.add(new Entry(x, y));
                }

                weekInputStream.close();
                weekReader.close();

                // Month 데이터 읽기
                InputStream monthInputStream = mContext.getAssets().open("Data_Month.json");
                BufferedReader monthReader = new BufferedReader(new InputStreamReader(monthInputStream));
                StringBuilder monthStringBuilder = new StringBuilder();
                String monthLine;

                while ((monthLine = monthReader.readLine()) != null) {
                    monthStringBuilder.append(monthLine);
                }

                JSONObject monthJsonObject = new JSONObject(monthStringBuilder.toString());
                JSONArray monthJsonArray = monthJsonObject.getJSONArray("Data");

                for (int i = 0; i < monthJsonArray.length(); i++) {
                    JSONObject item = monthJsonArray.getJSONObject(i);
                    float x = Float.parseFloat(item.getString("x"));
                    float y = Float.parseFloat(item.getString("y"));
                    monthList.add(new Entry(x, y));
                }

                monthInputStream.close();
                monthReader.close();

            } catch (Exception e) {
                Log.d(TAG, "createDataSet: Error - " + e.getMessage());
            }

            LineDataSet weekDataSet = createLineDataSet(weekList, Color.parseColor("#363062"));
            LineDataSet monthDataSet = createLineDataSet(monthList, Color.parseColor("#5BDAA4"));

            LineData lineData = new LineData(weekDataSet, monthDataSet);
            mChart.setData(lineData);

            XAxis xAxis = mChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextColor(Color.WHITE);
            xAxis.setDrawGridLines(false); //격자 비활성
            xAxis.setDrawAxisLine(false); //x축 선 비활성

            YAxis yAxisLeft = mChart.getAxisLeft();
            yAxisLeft.setTextColor(Color.WHITE);
            yAxisLeft.setDrawGridLines(false);
            yAxisLeft.setDrawAxisLine(false); //y축 선 비활성

            YAxis yAxisRight = mChart.getAxisRight();
            yAxisRight.setEnabled(false);

            Legend legend = mChart.getLegend();
            legend.setEnabled(false);

            mChart.setTouchEnabled(false);
            mChart.setScaleEnabled(false);
            mChart.setPinchZoom(false);
            mChart.getDescription().setEnabled(false);

            // 차트의 border line 제거
            mChart.setDrawBorders(false);
            // 그림자 효과 추가
            weekDataSet.setDrawFilled(true);
            weekDataSet.setFillAlpha(50);
            weekDataSet.setFillDrawable(mContext.getDrawable(R.drawable.line_shadow));

            monthDataSet.setDrawFilled(true);
            monthDataSet.setFillAlpha(50);
            monthDataSet.setFillDrawable(mContext.getDrawable(R.drawable.line_shadow2));

            mChart.animateX(1500, Easing.EaseInCubic);
            mChart.invalidate();
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private void createWeekData() {
            ArrayList<Entry> weekList = new ArrayList<>();
            ArrayList<Entry> monthList = new ArrayList<>();

            try {
                // Week 데이터 읽기
                InputStream weekInputStream = mContext.getAssets().open("Data.json");
                BufferedReader weekReader = new BufferedReader(new InputStreamReader(weekInputStream));
                StringBuilder weekStringBuilder = new StringBuilder();
                String weekLine;

                while ((weekLine = weekReader.readLine()) != null) {
                    weekStringBuilder.append(weekLine);
                }

                JSONObject weekJsonObject = new JSONObject(weekStringBuilder.toString());
                JSONArray weekJsonArray = weekJsonObject.getJSONArray("Data");

                for (int i = 0; i < weekJsonArray.length(); i++) {
                    JSONObject item = weekJsonArray.getJSONObject(i);
                    float x = Float.parseFloat(item.getString("x"));
                    float y = Float.parseFloat(item.getString("y"));
                    weekList.add(new Entry(x, y));
                }

                weekInputStream.close();
                weekReader.close();

                // Month 데이터 읽기
                InputStream monthInputStream = mContext.getAssets().open("Data_Month.json");
                BufferedReader monthReader = new BufferedReader(new InputStreamReader(monthInputStream));
                StringBuilder monthStringBuilder = new StringBuilder();
                String monthLine;

                while ((monthLine = monthReader.readLine()) != null) {
                    monthStringBuilder.append(monthLine);
                }

                JSONObject monthJsonObject = new JSONObject(monthStringBuilder.toString());
                JSONArray monthJsonArray = monthJsonObject.getJSONArray("Data");

                for (int i = 0; i < monthJsonArray.length(); i++) {
                    JSONObject item = monthJsonArray.getJSONObject(i);
                    float x = Float.parseFloat(item.getString("x"));
                    float y = Float.parseFloat(item.getString("y"));
                    monthList.add(new Entry(x, y));
                }

                monthInputStream.close();
                monthReader.close();

            } catch (Exception e) {
                Log.d(TAG, "createDataSet: Error - " + e.getMessage());
            }

            LineDataSet weekDataSet = createLineDataSet(weekList, Color.parseColor("#363062"));
            LineDataSet monthDataSet = createLineDataSet(monthList, Color.parseColor("#5BDAA4"));

            LineData lineData = new LineData(weekDataSet, monthDataSet);
            mChart.setData(lineData);

            XAxis xAxis = mChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextColor(Color.WHITE);
            xAxis.setDrawGridLines(false); //격자 비활성
            xAxis.setDrawAxisLine(false); //x축 선 비활성

            YAxis yAxisLeft = mChart.getAxisLeft();
            yAxisLeft.setTextColor(Color.WHITE);
            yAxisLeft.setDrawGridLines(false);
            yAxisLeft.setDrawAxisLine(false); //y축 선 비활성

            YAxis yAxisRight = mChart.getAxisRight();
            yAxisRight.setEnabled(false);

            Legend legend = mChart.getLegend();
            legend.setEnabled(false);

            mChart.setTouchEnabled(false);
            mChart.setScaleEnabled(false);
            mChart.setPinchZoom(false);
            mChart.getDescription().setEnabled(false);

            // 차트의 border line 제거
            mChart.setDrawBorders(false);
            // 그림자 효과 추가
            weekDataSet.setDrawFilled(true);
            weekDataSet.setFillAlpha(50);
            weekDataSet.setFillDrawable(mContext.getDrawable(R.drawable.line_shadow));

            monthDataSet.setDrawFilled(true);
            monthDataSet.setFillAlpha(50);
            monthDataSet.setFillDrawable(mContext.getDrawable(R.drawable.line_shadow2));

            mChart.animateX(1500, Easing.EaseInCubic);
            mChart.invalidate();
        }

        private LineDataSet createLineDataSet(ArrayList<Entry> entries, int color) {
            LineDataSet dataSet = new LineDataSet(entries, "");
            dataSet.setColor(color);
            dataSet.setLineWidth(2f);
            dataSet.setDrawCircles(false);
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            dataSet.setDrawValues(false);
            return dataSet;
        }
    }


