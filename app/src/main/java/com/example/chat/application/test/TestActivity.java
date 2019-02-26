package com.example.chat.application.test;

import android.os.Bundle;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.fanap.podchat.example.R;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Toolbar toolbar =findViewById(R.id.toolbar_test);
        ViewPager viewPager = findViewById(R.id.viewPager);
        TabItem tabItChat = findViewById(R.id.tab_chat);
        TabItem tabItemFunction = findViewById(R.id.tab_function);
        TabItem tabItemLog = findViewById(R.id.tab_log);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        String[] titles =new String[]{"Chat","Function","Log"};
        tabLayout.setTabTextColors( ContextCompat.getColor(this, R.color.tabLayout_unselected_text_color),
                ContextCompat.getColor(this, R.color.tabLayout_selected_text_color));
        setSupportActionBar(toolbar);

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(),titles);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }
}
