package com.fanap.podchat.call.history;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fanap.podchat.call.CallStatus;
import com.fanap.podchat.call.model.CallVO;
import com.fanap.podchat.example.R;
import com.fanap.podchat.util.Util;

import java.util.ArrayList;

public class HistoryAdaptor extends RecyclerView.Adapter<HistoryAdaptor.ViewHolder> {


    public interface IHistoryInterface {
        void onAudioCallSelected(CallVO call);

        void onVideoCallSelected(CallVO call);
    }

    ArrayList<CallVO> historyVOS;

    Context context;

    IHistoryInterface iHistoryInterface;

    public HistoryAdaptor(ArrayList<CallVO> historyVOS, Context context) {
        this.historyVOS = historyVOS;
        this.context = context;
    }

    public HistoryAdaptor(ArrayList<CallVO> historyVOS, Context context, IHistoryInterface iHistoryInterface) {
        this.historyVOS = historyVOS;
        this.context = context;
        this.iHistoryInterface = iHistoryInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, viewGroup, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        if (!historyVOS.isEmpty()) {

            CallVO historyVO = historyVOS.get(viewHolder.getAdapterPosition());

            if (historyVO.getPartnerParticipantVO() != null) {

                if (historyVO.getPartnerParticipantVO().getFirstName() != null || historyVO.getPartnerParticipantVO().getLastName() != null)
                    viewHolder.tvName.setText(historyVO.getPartnerParticipantVO().getFirstName() + " " + historyVO.getPartnerParticipantVO().getLastName());
                else if (historyVO.getPartnerParticipantVO().getCellphoneNumber() != null) {
                    viewHolder.tvName.setText(historyVO.getPartnerParticipantVO().getCellphoneNumber());
                } else {
                    viewHolder.tvName.setText(historyVO.getPartnerParticipantVO().getContactName());
                }
                if (Util.isNotNullOrEmpty(historyVO.getPartnerParticipantVO().getImage()))
                    Glide.with(context)
                            .load(historyVO.getPartnerParticipantVO().getImage())
                            .apply(RequestOptions.circleCropTransform())
                            .into(viewHolder.imageViewProfile);
                else {
                    viewHolder.imageViewProfile.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_profile));
                }


            } else if(historyVO.getConversationVO()!=null){
                viewHolder.tvName.setText(historyVO.getConversationVO().getTitle());
                if (Util.isNotNullOrEmpty(historyVO.getConversationVO().getImage()))
                    Glide.with(context)
                            .load(historyVO.getConversationVO().getImage())
                            .apply(RequestOptions.circleCropTransform())
                            .into(viewHolder.imageViewProfile);
                else {
                    viewHolder.imageViewProfile.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_group));
                }
            }else {
                viewHolder.tvName.setText("Invalid name");
                viewHolder.imageViewProfile.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_profile));
                setImageStatus(viewHolder.imageStatus, historyVO.getStatus());
            }

            setImageStatus(viewHolder.imageStatus, historyVO.getStatus());

            viewHolder.imageButtonAudioCall.setOnClickListener(v -> {
                if (iHistoryInterface != null)
                    iHistoryInterface.onAudioCallSelected(historyVO);
            });
            viewHolder.imageButtonVideoCall.setOnClickListener(v -> {
                if (iHistoryInterface != null)
                    iHistoryInterface.onVideoCallSelected(historyVO);
            });

        }


    }

    private void setImageStatus(ImageView imageStatus, int status) {

        switch (status) {

            case CallStatus.Constants.ACCEPTED: {
                imageStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_call_end_green));
                break;
            }
            case CallStatus.Constants.REQUESTED: {
                imageStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_call_requested));
                break;
            }
            case CallStatus.Constants.CANCELED: {
                imageStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_call_requested));
                break;
            }
            case CallStatus.Constants.MISS: {
                imageStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_call_missed_red));
                break;
            }
            case CallStatus.Constants.DECLINED: {
                imageStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_call_declined));
                break;
            }
            case CallStatus.Constants.STARTED: {
                imageStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_call_requested));
                break;
            }
            case CallStatus.Constants.ENDED: {
                imageStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_call_end_green));
                break;
            }
            case CallStatus.Constants.LEAVE: {
                imageStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_call_end_green));
                break;
            }


        }


    }

    @Override
    public int getItemCount() {
        return historyVOS.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        ImageView imageViewProfile;
        ImageButton imageButtonAudioCall,imageButtonVideoCall;
        ImageView imageStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvContactName);
            imageViewProfile = itemView.findViewById(R.id.imageProfile);
            imageButtonAudioCall = itemView.findViewById(R.id.imgBtnCallContact);
            imageButtonVideoCall = itemView.findViewById(R.id.imgBtnVideoCallContact);
            imageStatus = itemView.findViewById(R.id.imageStatus);
        }

    }
}
