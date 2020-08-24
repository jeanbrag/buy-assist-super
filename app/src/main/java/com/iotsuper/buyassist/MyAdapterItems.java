package com.iotsuper.buyassist;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;

public class MyAdapterItems extends RecyclerView.Adapter<MyAdapterItems.MyViewHolder> {

    public ArrayList<Produto> getmDataset() {
        return mDataset;
    }

    private ArrayList<Produto> mDataset;


    private static ClickOnRecyclerView clickOnRecyclerView;
    public void setClickOnRecyclerView(ClickOnRecyclerView clickOnRecyclerView) {
        MyAdapterItems.clickOnRecyclerView = clickOnRecyclerView;
    }


    public void setmDataset(ArrayList<Produto> mDataset) {
        this.mDataset = mDataset;
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public MyViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapterItems(ArrayList<Produto> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapterItems.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.view.checkBoxProduct.text.setText(mDataset[position]);
        final CheckBox checkBox = (CheckBox) holder.view.findViewById(R.id.checkBoxProduct);
        checkBox.setText(mDataset.get(position).getNome());
        checkBox.setChecked(mDataset.get(position).getCheckbox());
        checkBox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                clickOnRecyclerView.onLongClick(holder.view);
                return true;
            }
        });

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickOnRecyclerView.onCustomClick(holder.view);
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
