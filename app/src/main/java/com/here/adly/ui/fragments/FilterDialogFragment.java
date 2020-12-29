package com.here.adly.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.here.adly.R;
import com.here.adly.adapters.FilterAdapter;
import com.here.adly.models.Filter;
import com.here.adly.preferences.FilterPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FilterDialogFragment extends DialogFragment {
    RecyclerView filterRV;
    RecyclerView filterValuesRV;
    FilterAdapter filterAdapter;


    public FilterDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static FilterDialogFragment newInstance(String title) {
        FilterDialogFragment frag = new FilterDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        filterRV = view.findViewById(R.id.filterRV);
        filterValuesRV = view.findViewById(R.id.filterValuesRV);
        Button clearB = view.findViewById(R.id.clearB);
        initControls();

        clearB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterPreferences.filters.get(Filter.INDEX_COLOR).setSelected(new ArrayList());
                FilterPreferences.filters.get(Filter.INDEX_SIZE).setSelected(new ArrayList());
                FilterPreferences.filters.get(Filter.INDEX_PRICE).setSelected(new ArrayList());
            }
        });

        Button applyB = view.findViewById(R.id.applyB);
        applyB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        filterRV.setLayoutManager(new LinearLayoutManager(getContext()));
        filterValuesRV.setLayoutManager(new LinearLayoutManager(getContext()));

        getDialog().setTitle("Filter options");

    }

    public static String TAG = "PurchaseConfirmationDialog";

    private void initControls() {


        List<String> colors = Arrays.asList(new String[]{"Red", "Green", "Blue", "White"});
        if (!FilterPreferences.filters.containsKey(Filter.INDEX_COLOR)) {
            FilterPreferences.filters.put(Filter.INDEX_COLOR, new Filter("Color", colors, new ArrayList()));
        }
        List<String> sizes = Arrays.asList(new String[]{"10", "12", "14", "16", "18", "20"});
        if (!FilterPreferences.filters.containsKey(Filter.INDEX_SIZE)) {
            FilterPreferences.filters.put(Filter.INDEX_SIZE, new Filter("Size", sizes, new ArrayList()));
        }
        List<String> prices = Arrays.asList(new String[]{"0-100", "101-200", "201-300"});
        if (!FilterPreferences.filters.containsKey(Filter.INDEX_PRICE)) {
            FilterPreferences.filters.put(Filter.INDEX_PRICE, new Filter("Price", prices, new ArrayList()));
        }

        filterAdapter = new FilterAdapter(getContext(), FilterPreferences.filters, filterValuesRV);
        filterRV.setAdapter(filterAdapter);





    }

}
