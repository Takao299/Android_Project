package com.example.spbtex.ui.history;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.example.spbtex.MenuActivity;
import com.example.spbtex.R;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;

import android.widget.TextView;

import com.example.spbtex.databinding.ActivityHistoryBinding;

public class HistoryActivity extends MenuActivity {

    private ActivityHistoryBinding binding;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter =
                new SectionsPagerAdapter(this, getSupportFragmentManager(), this.user_id, this.session_id); //セッション追加
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);

        //追加　表の頭
        TextView numberTop = findViewById(R.id.number_top);
        numberTop.setText(R.string.tab_list_text_1);
        TextView contentTop = findViewById(R.id.content_top);
        contentTop.setText(R.string.tab_list_text_2);
        TextView contentTop2 = findViewById(R.id.content2_top);
        contentTop2.setText(R.string.tab_list_text_3);

        /*
        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });
         */
    }
}