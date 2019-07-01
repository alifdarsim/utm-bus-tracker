package com.codecraft.busutm;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codecraft.busutm.Model.BusHelper;
import com.codecraft.busutm.Model.BusModel;

import java.util.ArrayList;


public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {

    private final ArrayList<BusModel> sampleData = new ArrayList<>();
    private BusHelper busHelper = new BusHelper();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parentViewGroup, final int i) {
        final View rowView = LayoutInflater.from (parentViewGroup.getContext())
                .inflate(R.layout.bus_available_item, parentViewGroup, false);
        return new ViewHolder (rowView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        final BusModel rowData = sampleData.get(position);
        viewHolder.textBusName.setText(rowData.getPlate());
        viewHolder.textRouteName.setText(rowData.getRoute());
        viewHolder.imageBus.setImageResource(busHelper.getBusColorItem(rowData.getPlate()));
        viewHolder.itemView.setTag(rowData.getPlate());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("busClicked", rowData.getPlate());
                MainActivity.cameraFollowMarker(rowData.getPlate());
            }
        });
    }

    @Override
    public int getItemCount() {
        return sampleData.size();
    }

    public void removeData (int position) {
        sampleData.remove(position);
        notifyItemRemoved(position);
    }

    public void removeData (String busPlate) {
        for (int i = 0; i < sampleData.size(); i++){
            if (busPlate.equals(sampleData.get(i).getPlate())){
                sampleData.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    public void addItem(int positionToAdd, String busPlate) {
        if (positionToAdd != -1){
            sampleData.add(positionToAdd, new BusModel(busPlate, busHelper.getRoute(busPlate), "ASDASD"));
        } else {
            sampleData.add(new BusModel(busPlate, busHelper.getRoute(busPlate), "sadasd"));
        }
        notifyItemInserted(positionToAdd);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textBusName;
        private TextView textRouteName;
        private ImageView imageBus;
        public ViewHolder(View itemView) {
            super(itemView);
            textBusName = (TextView) itemView.findViewById(R.id.item_busName);
            textRouteName = (TextView) itemView.findViewById(R.id.item_routeName);
            imageBus = (ImageView) itemView.findViewById(R.id.item_busImage);
            textRouteName.setSelected(true);
        }
    }

}