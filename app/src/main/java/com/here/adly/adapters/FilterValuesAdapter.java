package com.here.adly.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.here.adly.R;
import com.here.adly.models.Filter;
import com.here.adly.preferences.FilterPreferences;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FilterValuesAdapter extends RecyclerView.Adapter<FilterValuesAdapter.MyViewHolder> {

    private Context context;
    private Integer filterIndex;

    public FilterValuesAdapter(Context context, Integer filterIndex) {
        this.context = context;
        this.filterIndex = filterIndex;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.filter_value_item, viewGroup, false);
        MyViewHolder mvh = new MyViewHolder(view);
        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int position) {
        final Filter tmpFilter = FilterPreferences.filters.get(filterIndex);
        myViewHolder.value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> selected = tmpFilter.getSelected();
                if(myViewHolder.value.isChecked()) {
                    selected.add(tmpFilter.getValues().get(position));
                    tmpFilter.setSelected(selected);
                } else {
                    selected.remove(tmpFilter.getValues().get(position));
                    tmpFilter.setSelected(selected);
                }
                FilterPreferences.filters.put(filterIndex, tmpFilter);
            }
        });
        myViewHolder.value.setText(tmpFilter.getValues().get(position));
        if(tmpFilter.getSelected().contains(tmpFilter.getValues().get(position))) {
            myViewHolder.value.setChecked(true);
        }
    }

    @Override
    public int getItemCount() {
        return FilterPreferences.filters.get(filterIndex).getValues().size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View container;
        CheckBox value;

        public MyViewHolder(View view) {
            super(view);
            container = view;
            value = view.findViewById(R.id.value);
        }
    }

}
