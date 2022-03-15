package com.fanap.podchat.call.history;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fanap.podchat.call.CallStatus;
import com.fanap.podchat.example.R;
import com.fanap.podchat.util.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class HistoryAdaptor extends RecyclerView.Adapter<HistoryAdaptor.ViewHolder> {


    public interface IHistoryInterface {
        void onAudioCallSelected(CallWrapper call);

        void onVideoCallSelected(CallWrapper call);
    }

    ArrayList<CallWrapper> historyVOS;

    Context context;

    IHistoryInterface iHistoryInterface;

    public HistoryAdaptor(ArrayList<CallWrapper> historyVOS, Context context) {
        this.historyVOS = historyVOS;
        this.context = context;
    }

    public HistoryAdaptor(ArrayList<CallWrapper> historyVOS, Context context, IHistoryInterface iHistoryInterface) {
        this.historyVOS = historyVOS;
        this.context = context;
        this.iHistoryInterface = iHistoryInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == CallWrapper.CallItemType.ACTIVE) {
            view = LayoutInflater.from(context).inflate(R.layout.item_active_call, viewGroup, false);
            return new ViewHolder(view);
        }
        view = LayoutInflater.from(context).inflate(R.layout.item_history, viewGroup, false);
        return new ViewHolder(view);


    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (!historyVOS.isEmpty()) {

            CallWrapper historyVO = historyVOS.get(viewHolder.getAdapterPosition());

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
                    viewHolder.imageViewProfile.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_person));
                }


            } else if (historyVO.getConversationVO() != null) {
                viewHolder.tvName.setText(historyVO.getConversationVO().getTitle());
                if (Util.isNotNullOrEmpty(historyVO.getConversationVO().getImage()))
                    Glide.with(context)
                            .load(historyVO.getConversationVO().getImage())
                            .apply(RequestOptions.circleCropTransform())
                            .into(viewHolder.imageViewProfile);
                else {
                    viewHolder.imageViewProfile.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_groups));
                }
            } else {
                viewHolder.tvName.setText("Invalid name");
                viewHolder.imageViewProfile.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_person));

                if (viewHolder.getItemViewType() != CallWrapper.CallItemType.ACTIVE) {
                    setImageStatus(viewHolder.imageStatus, historyVO.getStatus());
                }
            }

            if (viewHolder.getItemViewType() != CallWrapper.CallItemType.ACTIVE) {
                setImageStatus(viewHolder.imageStatus, historyVO.getStatus());
                Date time = new Date(historyVO.getCreateTime());
                viewHolder.tvCallTime.setText(time.toString());
            }

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
                imageStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_successful_incoming_call));
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
                imageStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_successfull_outgoing_call));
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

    @Override
    public int getItemViewType(int position) {
        return Util.isNullOrEmpty(historyVOS) ? super.getItemViewType(position)
                : position >= historyVOS.size() ? super.getItemViewType(position)
                : historyVOS.get(position).getCallItemType();

    }

    @SuppressLint("NotifyDataSetChanged")
    public void update(List<CallWrapper> calls) {
        historyVOS = new ArrayList<>(calls);
        notifyDataSetChanged();
    }

    public void addToFirst(List<CallWrapper> calls) {
        if (historyVOS == null) {
            historyVOS = new ArrayList<>();
        }
        historyVOS.addAll(0, calls);
        notifyItemChanged(0);
    }

    public void add(List<CallWrapper> calls) {
        if (historyVOS == null) {
            historyVOS = new ArrayList<>();
        }
        historyVOS.addAll(calls);
        Collections.sort(historyVOS, (o1, o2) -> Long.compare(o2.getCreateTime(),o1.getCreateTime()));
        notifyItemRangeChanged(historyVOS.size(), calls.size());
    }

    public void removeItem(CallWrapper call) {
        if (historyVOS != null) {
            if(historyVOS.contains(call)){
                int pos = historyVOS.indexOf(call);
                historyVOS.remove(call);
                notifyItemRemoved(pos);
            }
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvCallTime;
        ImageView imageViewProfile;
        ImageButton imageButtonAudioCall, imageButtonVideoCall;
        ImageView imageStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvContactName);
            imageViewProfile = itemView.findViewById(R.id.imageProfile);
            imageButtonAudioCall = itemView.findViewById(R.id.imgBtnCallContact);
            imageButtonVideoCall = itemView.findViewById(R.id.imgBtnVideoCallContact);
            imageStatus = itemView.findViewById(R.id.imageStatus);
            tvCallTime = itemView.findViewById(R.id.tvTime);
        }

    }


    public static class ViewHolderActiveCall extends RecyclerView.ViewHolder {

        TextView tvName;
        ImageView imageViewProfile;
        ImageButton imageButtonAudioCall, imageButtonVideoCall;

        public ViewHolderActiveCall(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvContactName);
            imageViewProfile = itemView.findViewById(R.id.imageProfile);
            imageButtonAudioCall = itemView.findViewById(R.id.imgBtnCallContact);
            imageButtonVideoCall = itemView.findViewById(R.id.imgBtnVideoCallContact);
        }

    }

}
