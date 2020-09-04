package com.shramik.india;

import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobsAdapter extends RecyclerView.Adapter<JobsAdapter.Holder> {
    List<String> id, name, mobile, state, industry;
    String from;
    Context context;
    JobsAdapter(String from, Context context, List<String> id, List<String> name, List<String> mobile, List<String> state , List<String> industry){
        this.context = context;
        this.name = name;
        this.mobile = mobile;
        this.state = state;
        this.industry = industry;
        this.from = from;
        this.id = id;
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.job_layout, parent, false);
        JobsAdapter.Holder holder = new JobsAdapter.Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {
        if(from.equals("Post")){
            holder.call.setImageResource(R.drawable.delete);
        }
         holder.name.setText(name.get(position));
         holder.state.setText(state.get(position));
         holder.industry.setText(industry.get(position));
         holder.call.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if(from.equals("Post")){
                     StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_DELETE_JOB, new Response.Listener<String>() {
                         @Override
                         public void onResponse(String response) {
                             try {
                                 JSONObject jsonObject = new JSONObject(response);
                                 if(jsonObject.getBoolean("error")){
                                     Toast.makeText(context, "Could not Delete the Job. Please retry later", Toast.LENGTH_SHORT).show();
                                 }else{
                                     name.remove(position);
                                     state.remove(position);
                                     industry.remove(position);
                                     mobile.remove(position);
                                     id.remove(position);
                                     notifyItemRemoved(position);
                                     notifyItemRangeChanged(position, name.size());
                                 }
                             } catch (JSONException e) {
                                 e.printStackTrace();
                             }
                         }
                     }, new Response.ErrorListener() {
                         @Override
                         public void onErrorResponse(VolleyError error) {

                         }
                     }){
                         @Override
                         protected Map<String, String> getParams() throws AuthFailureError {
                             Map<String, String> map = new HashMap<>();
                             map.put("id", id.get(holder.getAdapterPosition()));
                             return map;
                         }
                     };
                     RequestHanler.getInstance(context).addToRequestQueue(stringRequest);
                 }else {
                     Intent intent = new Intent(Intent.ACTION_CALL);
                     intent.setData(Uri.parse("tel:" + mobile.get(holder.getAdapterPosition())));
                     context.startActivity(intent);
                 }
             }
         });
    }

    @Override
    public int getItemCount() {
        return name.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        TextView name;
        TextView state;
        TextView industry;
        ImageView call;
        public Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            call = itemView.findViewById(R.id.call);
            industry = itemView.findViewById(R.id.industry);
            state = itemView.findViewById(R.id.state);
        }
    }
}
