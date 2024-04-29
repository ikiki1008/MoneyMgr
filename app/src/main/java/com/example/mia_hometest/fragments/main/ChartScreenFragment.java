    package com.example.mia_hometest.fragments.main;

    import android.content.Context;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;

    import androidx.fragment.app.Fragment;

    import com.example.mia_hometest.R;

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


