package com.here.adly.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.google.android.material.slider.RangeSlider;
import com.here.adly.R;
import com.here.adly.models.Filter;
import com.here.adly.models.PriceFilter;
import com.here.adly.models.TypeFilter;
import com.here.adly.preferences.FilterPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class FilterDialogFragment extends DialogFragment {

    RangeSlider rangeSliderPrice;
    CheckBox cbTypeEuropanel, cbTypeAbri, cbTypeTwoSign;
    ImageView ivClose;
    OnFilterMapListener mCallback;

    public interface OnFilterMapListener {
        void onFilterMapSubmit();
    }

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

        try {
            mCallback = (OnFilterMapListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnFilterMapListener");
        }

        return inflater.inflate(R.layout.fragment_filter, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button clearB = view.findViewById(R.id.clearB);
        rangeSliderPrice = view.findViewById(R.id.rs_filter_price);
        cbTypeAbri = view.findViewById(R.id.cb_value_type_abri);
        cbTypeEuropanel = view.findViewById(R.id.cb_value_type_europanel);
        cbTypeTwoSign = view.findViewById(R.id.cb_value_type_twosign);
        ivClose = view.findViewById(R.id.iv_close_filter);
        initControls();

        clearB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TypeFilter typeFilter = (TypeFilter) FilterPreferences.filters.get(Filter.INDEX_TYPE);
                typeFilter.setSelected(new ArrayList());
                PriceFilter priceFilter = (PriceFilter) FilterPreferences.filters.get(Filter.INDEX_PRICE);
                priceFilter.setSelected(new ArrayList());
            }
        });

        Button applyB = view.findViewById(R.id.applyB);
        applyB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfTypeIsSelected(cbTypeAbri, "Abri");
                checkIfTypeIsSelected(cbTypeEuropanel, "Europanel");
                checkIfTypeIsSelected(cbTypeTwoSign, "2-Sign");
                setRangeSliderPrice();
                mCallback.onFilterMapSubmit();

                dismiss();
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        getDialog().setTitle("Filter options");

    }

    @Override
    public void onDetach() {
        mCallback = null; // => avoid leaking, thanks @Deepscorn
        super.onDetach();
    }

    public static String TAG = "PurchaseConfirmationDialog";

    private void setCheckedTypeCheckBox(CheckBox checkBox, String typeName) {
        final TypeFilter filterData = (TypeFilter) FilterPreferences.filters.get(Filter.INDEX_TYPE);
        List<String> selected = filterData.getSelected();
        if (selected.contains(typeName)) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }
    }

    private void setRangeSliderPrice() {
        final PriceFilter filterData = (PriceFilter) FilterPreferences.filters.get(Filter.INDEX_PRICE);
        List<Float> selected = rangeSliderPrice.getValues();
        filterData.setSelected(selected);
    }

    private void checkIfTypeIsSelected(CheckBox checkBox, String typeName) {
        final TypeFilter filterData = (TypeFilter) FilterPreferences.filters.get(Filter.INDEX_TYPE);
        List<String> selected = filterData.getSelected();
        if (checkBox.isChecked() && !selected.contains(typeName)) {
            selected.add(typeName);
            filterData.setSelected(selected);
        } else if (!checkBox.isChecked() && selected.contains(typeName)) {
            selected.remove(typeName);
            filterData.setSelected(selected);
        }
    }

    private void initControls() {


        if (!FilterPreferences.filters.containsKey(Filter.INDEX_TYPE)) {
            List<String> selectedTypes = new ArrayList<>();
            selectedTypes.add("Abri");
            selectedTypes.add("Europanel");
            selectedTypes.add("2-Sign");
            FilterPreferences.filters.put(Filter.INDEX_TYPE, new TypeFilter("Type", selectedTypes));
        }
        ;
        if (!FilterPreferences.filters.containsKey(Filter.INDEX_PRICE)) {
            List<Float> selectedPrices = new ArrayList<>();
            selectedPrices.add(0F);
            selectedPrices.add(500F);
            FilterPreferences.filters.put(Filter.INDEX_PRICE, new PriceFilter("Price", selectedPrices));
        }
        PriceFilter filterData = (PriceFilter) FilterPreferences.filters.get(Filter.INDEX_PRICE);
        rangeSliderPrice.setValues(filterData.getSelected());
        setCheckedTypeCheckBox(cbTypeAbri, "Abri");
        setCheckedTypeCheckBox(cbTypeEuropanel, "Europanel");
        setCheckedTypeCheckBox(cbTypeTwoSign, "2-Sign");

    }

}
