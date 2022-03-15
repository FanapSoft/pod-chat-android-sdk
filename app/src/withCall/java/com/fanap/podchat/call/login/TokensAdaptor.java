package com.fanap.podchat.call.login;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fanap.podchat.example.R;

import java.util.ArrayList;

class TokensAdaptor extends RecyclerView.Adapter<TokensAdaptor.ViewHolder> {


    public interface ITokenInterface {
        void onTokenSelected(TokenModel token);
    }

    ArrayList<TokenModel> tokensList;

    Context context;

    ITokenInterface iTokenInterface;

    public TokensAdaptor(ArrayList<TokenModel> tokens, Context context) {
        this.tokensList = tokensList;
        this.context = context;
    }

    public TokensAdaptor(ArrayList<TokenModel> tokensList, Context context, ITokenInterface iTokenInterface) {
        this.tokensList = tokensList;
        this.context = context;
        this.iTokenInterface = iTokenInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_token, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        if (!tokensList.isEmpty()) {

            TokenModel token = tokensList.get(viewHolder.getAdapterPosition());

            viewHolder.tvName.setText(token.getName());


            viewHolder.itemView.setOnClickListener(v -> {
                    if (iTokenInterface != null)
                        iTokenInterface.onTokenSelected(token);
            });
        }


    }

    @Override
    public int getItemCount() {
        return tokensList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvTokenName);
        }

    }
}
