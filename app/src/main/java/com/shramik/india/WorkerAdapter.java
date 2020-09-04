package com.shramik.india;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WorkerAdapter extends RecyclerView.Adapter<WorkerAdapter.Holder> {
    Context context;
    List<String> name, mobile, age, workers;
    WorkerAdapter(Context context, List<String> name, List<String> mobile, List<String> age, List<String> workers){
        this.context = context;
        this.name = name;
        this.age = age;
        this.mobile = mobile;
        this.workers = workers;
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.worker_layout, parent, false);
        WorkerAdapter.Holder holder = new WorkerAdapter.Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {
        holder.name.setText(name.get(position));
        holder.age.setText(age.get(position));
        holder.workers.setText(workers.get(position));
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:"+mobile.get(holder.getAdapterPosition())));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return name.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        TextView name, age, workers;
        ImageView call;
        public Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            age = itemView.findViewById(R.id.age);
            workers = itemView.findViewById(R.id.workers);
            call = itemView.findViewById(R.id.call);
        }
    }
}
