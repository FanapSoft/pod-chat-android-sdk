package com.example.chat.application.chatexample.ui.custom;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chat.application.chatexample.model.Method;
import com.fanap.podchat.example.R;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

@Layout(R.layout.child_layout)
public class ChildView {
    private static String TAG = "ChildView";

    @View(R.id.child_name)
    TextView textViewChild;

    private Context mContext;
    private Method method;

    CallBack listener;

    public ChildView(Context mContext, Method method, CallBack listener) {
        this.mContext = mContext;
        this.method = method;
        this.listener = listener;
    }

    @Resolve
    private void onResolve() {
        textViewChild.setText(method.getName());
        textViewChild.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                listener.onClick(method.getName());
            }
        });
    }

    public interface CallBack {
        void onClick(String methosName);
    }
}


