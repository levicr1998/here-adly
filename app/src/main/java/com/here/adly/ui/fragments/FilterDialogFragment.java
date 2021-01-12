package com.here.adly.ui.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.here.adly.R;
import com.here.adly.models.AvailableFilter;
import com.here.adly.models.Filter;
import com.here.adly.models.LocationFilter;
import com.here.adly.models.PriceFilter;
import com.here.adly.models.TypeFilter;
import com.here.adly.preferences.FilterPreferences;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class FilterDialogFragment extends DialogFragment {

    CheckBox cbTypeEuropanel, cbTypeAbri, cbTypeTwoSign;
    RangeSlider rangeSliderPrice;
    Slider sliderLocation;
    TextInputLayout tilLocation;
    SwitchMaterial swAvailibility;
    ImageView ivClose;
    OnFilterMapListener mCallback;

    public interface OnFilterMapListener {
        void onFilterMapSubmit();
    }

    public FilterDialogFragment() {
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
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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
        swAvailibility = view.findViewById(R.id.sw_availability);
        sliderLocation = view.findViewById(R.id.s_filter_location);
        tilLocation = view.findViewById(R.id.til_filter_location_postal);

        sliderLocation.setLabelFormatter(value -> {
            DecimalFormat decimalFormat = new DecimalFormat("#");
            String distanceValue = (decimalFormat.format(value));
            String formattedValue =  distanceValue + "KM near me";
            return formattedValue;
        });
        rangeSliderPrice.setLabelFormatter(value -> {
            DecimalFormat decimalFormat = new DecimalFormat("#");
            String priceValue = (decimalFormat.format(value));
            String formattedValue = "€" + priceValue;
            return formattedValue;
        });
        cbTypeAbri = view.findViewById(R.id.cb_value_type_abri);
        cbTypeEuropanel = view.findViewById(R.id.cb_value_type_europanel);
        cbTypeTwoSign = view.findViewById(R.id.cb_value_type_twosign);
        ivClose = view.findViewById(R.id.iv_close_filter);
        initControls();

        clearB.setOnClickListener(v -> {

            FilterPreferences.filters.remove(Filter.INDEX_TYPE);
            FilterPreferences.filters.remove(Filter.INDEX_PRICE);
            FilterPreferences.filters.remove(Filter.INDEX_AVAILABLE);
            FilterPreferences.filters.remove(Filter.INDEX_LOCATION);
            initControls();
        });

        Button applyB = view.findViewById(R.id.applyB);
        applyB.setOnClickListener(v -> {
            checkIfTypeIsSelected(cbTypeAbri, "Abri");
            checkIfTypeIsSelected(cbTypeEuropanel, "Europanel");
            checkIfTypeIsSelected(cbTypeTwoSign, "2-Sign");
            setRangeSliderPrice();
            mCallback.onFilterMapSubmit();

            dismiss();
        });

        ivClose.setOnClickListener(view1 -> dismiss());


        getDialog().setTitle("Filter options");

    }

    @Override
    public void onDetach() {
        mCallback = null;
        super.onDetach();
    }


    private void setTypeCheckBox(CheckBox checkBox, String typeName) {
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

    private void setLocationFilterComponents(Slider sliderLocation){
        final LocationFilter filterData = (LocationFilter) FilterPreferences.filters.get(Filter.INDEX_LOCATION);
        String selectedPostalCode = filterData.getSelectedPostalCode();
        int selectedKiloMetersNearMe = filterData.getSelectedKiloMetersNearMe();

        if(selectedPostalCode.isEmpty()){
            tilLocation.getEditText().setText("");
        } else{
            tilLocation.getEditText().setText(selectedPostalCode);
        }

        sliderLocation.setValue(selectedKiloMetersNearMe);
    }

    private void setAvailableSwitch(SwitchMaterial switchAvailable) {
        final AvailableFilter filterData = (AvailableFilter) FilterPreferences.filters.get(Filter.INDEX_AVAILABLE);
        boolean isSelected = filterData.isSelected();
        if (isSelected) {
            switchAvailable.setChecked(true);
        } else {
            switchAvailable.setChecked(false);
        }
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
        resetFilters();
        PriceFilter filterData = (PriceFilter) FilterPreferences.filters.get(Filter.INDEX_PRICE);
        rangeSliderPrice.setValues(filterData.getSelected());
        setTypeCheckBox(cbTypeAbri, "Abri");
        setTypeCheckBox(cbTypeEuropanel, "Europanel");
        setTypeCheckBox(cbTypeTwoSign, "2-Sign");
        setAvailableSwitch(swAvailibility);
        setLocationFilterComponents(sliderLocation);

    }

    private void resetFilters() {
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

        if(!FilterPreferences.filters.containsKey(Filter.INDEX_AVAILABLE)){
            boolean isSelected = false;
            FilterPreferences.filters.put(Filter.INDEX_AVAILABLE, new AvailableFilter("Available",isSelected));
        }

        if (!FilterPreferences.filters.containsKey(Filter.INDEX_LOCATION)){
            String selectedPostalCode = "";
            int selectedKiloMetersNearMe = 500;
            FilterPreferences.filters.put(Filter.INDEX_LOCATION, new LocationFilter("Location", selectedPostalCode, selectedKiloMetersNearMe));
        }
    }

}
