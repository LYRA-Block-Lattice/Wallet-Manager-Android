package com.lyrawallet.Ui.Dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.lyrawallet.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class Dashboard extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Dashboard() {
        // Required empty public constructor
    }

    public static Dashboard newInstance(String param1, String param2) {
        Dashboard fragment = new Dashboard();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
        @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dash, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Button toDashboardButton = getActivity().findViewById(R.id.toDashboard);
        toDashboardButton.setVisibility(View.INVISIBLE);
        Button toOpenWalletButton = getActivity().findViewById(R.id.toOpenWallet);
        toOpenWalletButton.setVisibility(View.INVISIBLE);
        Button toCloseWalletButton = getActivity().findViewById(R.id.toCloseWallet);
        toCloseWalletButton.setVisibility(View.VISIBLE);

        List<PieEntry> yaxisvalues = new ArrayList<PieEntry>();
        yaxisvalues.add(new PieEntry(8f, "Green"));
        yaxisvalues.add(new PieEntry(15f, "Yellow"));
        yaxisvalues.add(new PieEntry(12f, "Red"));
        yaxisvalues.add(new PieEntry(25f, "Blue"));
        yaxisvalues.add(new PieEntry(25f, "Magenta"));

        PieDataSet pieData = new PieDataSet(yaxisvalues, "");
        pieData.setHighlightEnabled(true);
        pieData.setSliceSpace(2f);
        pieData.setDrawValues(false);
        PieData data = new PieData(pieData);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(13f);
        data.setValueTextColor(Color.DKGRAY);

        PieChart pie_Chart = (PieChart) view.findViewById(R.id.pie_chart);
        pie_Chart.setData(data);
        pie_Chart.setUsePercentValues(false);
        pie_Chart.setHovered(true);
        pie_Chart.setHasTransientState(true);
        pie_Chart.setCenterText("Pie");
        pie_Chart.getDescription().setEnabled(false);
        pie_Chart.getLegend().setEnabled(false);
        pie_Chart.setRotationEnabled(false);
        pie_Chart.setDrawHoleEnabled(true);
        pie_Chart.setHoleRadius(30f);
        pie_Chart.setTransparentCircleRadius(60f);

        pieData.setColors(ColorTemplate.COLORFUL_COLORS);

        //pie_Chart.animateXY(1500, 1500);
    }
}
