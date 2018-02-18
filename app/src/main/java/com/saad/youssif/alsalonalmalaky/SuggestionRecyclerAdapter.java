package com.saad.youssif.alsalonalmalaky;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by youssif on 05/02/18.
 */

public class SuggestionRecyclerAdapter extends RecyclerView.Adapter<SuggestionRecyclerAdapter.SuggestionViewHolder> {

    private final List<Suggestion> suggestionListList;
    Context ctx;
    public static String req_id;
    ProgressDialog progressDialog;

    public SuggestionRecyclerAdapter(List<Suggestion> suggestionListList, Context context){
        this.suggestionListList = suggestionListList;
        this.ctx=context;
        progressDialog=new ProgressDialog(context);
    }

    @Override
    public SuggestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggest_recycler_items,parent,false);
        SuggestionRecyclerAdapter.SuggestionViewHolder viewHolder = new SuggestionRecyclerAdapter.SuggestionViewHolder(layoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SuggestionViewHolder holder, int position) {
        Suggestion suggestion=suggestionListList.get(position);
        holder.sug_usernameTv.setText(suggestion.getUsername());
        holder.sug_detailsTv.setText(suggestion.getDetails());
        holder.sug_timeTv.setText(suggestion.getTime());

    }

    @Override
    public int getItemCount() {
        return suggestionListList.size();
    }

    public class SuggestionViewHolder extends RecyclerView.ViewHolder {
    TextView sug_usernameTv;
    TextView sug_detailsTv,sug_timeTv;

    public SuggestionViewHolder(View item) {
        super(item);
        sug_usernameTv = (TextView) item.findViewById(R.id.sug_usernameTv);
        sug_detailsTv = (TextView) item.findViewById(R.id.sug_detailsTv);
        sug_timeTv = (TextView) item.findViewById(R.id.sug_timeTv);

    }

}
}
