package com.millenial.stormy.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.millenial.stormy.R;
import com.millenial.stormy.databinding.HourlyListItemBinding;
import com.millenial.stormy.weather.Hour;

import java.util.List;

public class HourlyAdapter  extends RecyclerView.Adapter<HourlyAdapter.ViewHolder> {

    private List<Hour> hours;
    private Context context;

    public HourlyAdapter(List<Hour> hours, Context context) {
        this.hours = hours;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        HourlyListItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(viewGroup.getContext()),
                        R.layout.hourly_list_item,
                        viewGroup,
                        false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Hour hour = hours.get(i);
        viewHolder.hourlyListItemBinding.setHour(hour);

    }

    @Override
    public int getItemCount() {
        return hours.size();
    }








    public class ViewHolder extends RecyclerView.ViewHolder {

        // Binding variables
        public HourlyListItemBinding hourlyListItemBinding;

        // Constructors to do view lookups for each subview
        public ViewHolder(HourlyListItemBinding hourlyLayoutBinding) {
            super(hourlyLayoutBinding.getRoot());
            hourlyListItemBinding = hourlyLayoutBinding;
        }

    }
}







