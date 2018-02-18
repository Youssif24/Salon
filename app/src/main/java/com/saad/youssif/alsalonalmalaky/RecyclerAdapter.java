package com.saad.youssif.alsalonalmalaky;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.util.Date;
import java.util.List;

/**
 * Created by youssif on 17/01/18.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RequestViewHolder> {
    private final List<Request> requestsList;
    MainActivity mainActivity;
    Context ctx;
    public static String req_id;
    ProgressDialog progressDialog;

    public RecyclerAdapter(List<Request> requestsList, Context context){
        this.requestsList = requestsList;
        this.ctx=context;
        progressDialog=new ProgressDialog(context);
    }
    @Override
    public RecyclerAdapter.RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item,parent,false);
        RequestViewHolder viewHolder = new RequestViewHolder(layoutView,mainActivity);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerAdapter.RequestViewHolder holder, final int position) {
        final Request request = requestsList.get(position);
        //holder.tvName.setText("اسم العميل : "+request.getUsername());
        holder.tvDesc.setText(request.getDesc());
        holder.tvDate.setText(request.getDate());
        String time=request.getTime();
        time=time.replace(":00.000000","");
        time=time.replace(".000000","");
        time=time.replace("00:","");
        holder.tvTime.setText(time);
        String response=request.getResponse();
        if(response.equals(""))
        {
            holder.tvResponse.setText(" لم يتم الرد ");
            request.setResponse(" لم يتم الرد ");
        }



        if(mainActivity.type.equals("user"))
        {
            //holder.tvName.setVisibility(View.GONE);
            holder.replyImg.setVisibility(View.GONE);
            holder.canceImg.setVisibility(View.GONE);
            holder.textViewOption.setVisibility(View.GONE);
            //holder.itemControlLayout.setVisibility(View.GONE);
        }
        else
        {
            holder.replyShowLayout.setVisibility(View.GONE);
        }
        holder.setClickListener(new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                req_id=requestsList.get(position).getId();
            }
        });
        holder.replyImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent=new Intent(ctx,RequestReply.class);
                mIntent.putExtra("req_id",requestsList.get(position).getId());
                mIntent.putExtra("req_user_id",requestsList.get(position).getUser_id());
                v.getContext().startActivity(mIntent);
            }
        });
        holder.canceImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ctx);

                builder.setCancelable(false).setMessage("هل تريد مسح الطلب ؟")
                        .setIcon(android.R.drawable.ic_delete)

                        .setTitle("مسح طلب")
                        .setPositiveButton("مسح", new DialogInterface.OnClickListener() {

                            @Override

                            public void onClick(DialogInterface dialogInterface, int i) {
                                progressDialog.setMessage("جازي الحذف...");
                                progressDialog.show();
                                mainActivity=new MainActivity();
                                mainActivity.delete_request(requestsList.get(position).getId(),ctx);
                                v.getContext().startActivity(new Intent(ctx,MainActivity.class));
                                ((Activity)ctx).finish();


                                progressDialog.dismiss();

                            }
                        }).setNegativeButton("إلغاء", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();


            }
        });


        holder.textViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(ctx, holder.textViewOption);
                //inflating menu from xml resource
                popup.inflate(R.menu.recyler_item_options);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.reply_option:
                                Intent reply_intent=new Intent(ctx,InformationShow.class);
                                reply_intent.putExtra("option","reply");
                                reply_intent.putExtra("info_reply",request.getResponse());
                                view.getContext().startActivity(reply_intent);
                                //handle menu1 click
                                break;
                            case R.id.information_option:
                                //handle menu2 click
                                Intent info_intent=new Intent(ctx,InformationShow.class);
                                info_intent.putExtra("option","info");
                                info_intent.putExtra("info_name",request.getUsername());
                                info_intent.putExtra("info_phone",request.getPhone());
                                view.getContext().startActivity(info_intent);
                                //Toast.makeText(ctx,request.getUsername(),Toast.LENGTH_LONG).show();
                                break;

                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();

            }
        });





    }


    @Override
    public int getItemCount() {
        return requestsList.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private RecyclerViewClickListener clickListener;
        TextView tvName;
        TextView tvDesc,tvDate,tvTime,tvResponse,textViewOption;
        LinearLayout itemControlLayout,replyShowLayout;
        MainActivity mainActivity;
        ImageView replyImg,canceImg;
        public RequestViewHolder(View item,MainActivity mainActivity) {
            super(item);
            item.setOnClickListener(this);
            this.mainActivity=mainActivity;
           tvDesc = (TextView) item.findViewById(R.id.tvDetails);
            tvDate = (TextView) item.findViewById(R.id.dateTv);
            tvTime=(TextView)item.findViewById(R.id.timeTv);
            textViewOption=(TextView)item.findViewById(R.id.textViewOptions);
            tvResponse=(TextView)item.findViewById(R.id.reply_showTv);
            itemControlLayout=(LinearLayout)item.findViewById(R.id.adminControlsLayout);
            replyShowLayout=(LinearLayout)item.findViewById(R.id.user_response_show_layout);
            replyImg=(ImageView)item.findViewById(R.id.request_reply_imgView);
            canceImg=(ImageView)item.findViewById(R.id.request_cancel_imgView);
            itemControlLayout.setOnClickListener(this);
            replyImg.setOnClickListener(this);
            canceImg.setOnClickListener(this);

        }

        public void setClickListener(RecyclerViewClickListener itemClickListener) {
            this.clickListener = itemClickListener;

        }

        @Override
        public void onClick(View v) {

            clickListener.onClick(v, getPosition());

        }
    }
}
