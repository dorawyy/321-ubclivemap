package com.cpen321.ubclocationbroadcaster;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**HELPER CLASS FOR DisplaySortedList.java*/
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    public String activityNames[];
    public String actividyIds[];
    public Context context;
    public MyAdapter(Context ct, String s1[] , String s2[]) {
        context = ct;
        activityNames = s1;
        actividyIds = s2;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.myText1.setText(activityNames[position]);
        holder.myText2.setText(actividyIds[position]);
    }

    @Override
    public int getItemCount() {
        return (actividyIds.length);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView myText1;
        public TextView myText2;
        public Button seeMore2;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myText1 = itemView.findViewById(R.id.ActivityHeader);
            myText2 = itemView.findViewById(R.id.ActivityFooter);
            seeMore2 = itemView.findViewById(R.id.seeMoreButton);
            seeMore2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SortedlistclassUtil.activity_to_be_displayed = activityNames[getAdapterPosition()];
                    Log.d("tester", SortedlistclassUtil.activity_to_be_displayed);
                    Intent intent = new Intent(context,DisplayActivityDetails.class);
                    context.startActivity(intent);
                }
            });

        }
    }
}
