package com.fanap.podchat.call.login;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.chat.application.chatexample.token.TokenHandler;
import com.fanap.podchat.example.R;
import com.fanap.podchat.util.ChatStateType;

import java.util.ArrayList;
import java.util.Objects;


public class LoginFragment extends Fragment {

    public interface ILoginInterface {
        void onTokenReceived(String token);
    }

    private ILoginInterface callback;

    private RecyclerView recyclerView;
    private TokenHandler tokenHandler;


    private EditText etNumber, etOtp;
    private Button btnLogin, btnVerify;
    private ViewSwitcher viewSwitcher;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (ILoginInterface) context;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Attach failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);


        viewSwitcher = view.findViewById(R.id.viewSwitcherLogin);

        etNumber = view.findViewById(R.id.etNumber);
        etOtp = view.findViewById(R.id.etOtp);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnVerify = view.findViewById(R.id.btnVerifyCode);
        recyclerView = view.findViewById(R.id.recyclerViewTokens);


        setupTokenHandler(view);
        setupTokenList(view);
        setListeners();

        return view;
    }

    private void setListeners() {


        btnLogin.setOnClickListener(v -> {

            if (etNumber.getText().toString().isEmpty()) {
                Toast.makeText(v.getContext(), "I need some input :')", Toast.LENGTH_SHORT).show();
                return;
            }
            viewSwitcher.showNext();
            tokenHandler.handshake(etNumber.getText().toString());

        });

        btnVerify.setOnClickListener(v -> {

            if (etOtp.getText().toString().isEmpty()) {
                viewSwitcher.showPrevious();
                return;
            }
            tokenHandler.verifyNumber(etOtp.getText().toString());

        });

    }

    private void setupTokenHandler(View view) {

        tokenHandler = new TokenHandler(view.getContext().getApplicationContext());

        tokenHandler.addListener(new TokenHandler.ITokenHandler() {
            @Override
            public void onGetToken(String token) {

                if (callback != null)
                    callback.onTokenReceived(token);
            }

            @Override
            public void onTokenRefreshed(String token) {

//                if (state.equals(ChatStateType.ChatSateConstant.ASYNC_READY))
//                    chat.setToken(token);
//                else view.onGetToken(token);

            }

            @Override
            public void onError(String message) {
//                view.onError(message);
            }
        });
    }

    private void setupTokenList(View view) {

        ArrayList<TokenModel> tokenModels = new ArrayList<>();

        TokenModel tokenModel = new TokenModel("Farhad Kheirkhah", Objects.requireNonNull(getContext()).getResources().getString(R.string.Farhad_Kheirkhah));
        tokenModels.add(tokenModel);
        tokenModel = new TokenModel("Masoud Amjadi", Objects.requireNonNull(getContext()).getResources().getString(R.string.Farhad_Kheirkhah));
        tokenModels.add(tokenModel);

        TokensAdaptor adaptor = new TokensAdaptor(tokenModels, view.getContext(), new TokensAdaptor.ITokenInterface() {
            @Override
            public void onTokenSelected(TokenModel token) {
                Log.i("LOGIN", "SELECTED TOKEN: " + token.getToken());
                if (callback != null) {
                    callback.onTokenReceived(token.getToken());
                }
            }
        });

        recyclerView.setAdapter(adaptor);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));

    }
}