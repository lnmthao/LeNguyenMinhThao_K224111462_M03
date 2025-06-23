package com.lnmt.adapters;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lnmt.R;
import com.lnmt.models.TaskDetail;

import java.util.ArrayList;

public class CallAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<TaskDetail> list;
    private final SQLiteDatabase db;
    private final int taskId;

    public CallAdapter(Context context, ArrayList<TaskDetail> list, SQLiteDatabase db, int taskId) {
        this.context = context;
        this.list = list;
        this.db = db;
        this.taskId = taskId;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return list.get(i).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_call_task, parent, false);
        TextView tvName = view.findViewById(R.id.tvCustomerName);
        TextView tvPhone = view.findViewById(R.id.tvPhoneNumber);

        TaskDetail item = list.get(position);
        tvName.setText(item.getCustomerName());
        tvPhone.setText(item.getPhoneNumber());

        if (item.getIsCalled() == 0) {
            view.setBackgroundColor(Color.YELLOW);
        }

        view.setOnClickListener(v -> {
            if (item.getIsCalled() == 0) {
                db.execSQL("UPDATE TaskForTeleSalesDetails SET IsCalled = 1 WHERE ID = ?", new Object[]{item.getId()});
                item.setIsCalled(1);
                notifyDataSetChanged();

                boolean allCalled = true;
                for (TaskDetail d : list) {
                    if (d.getIsCalled() == 0) {
                        allCalled = false;
                        break;
                    }
                }

                if (allCalled) {
                    db.execSQL("UPDATE TaskForTeleSales SET IsCompleted = 1 WHERE ID = ?", new Object[]{taskId});
                }
            }
        });

        return view;
    }
}

