package com.pshkrh.notes;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.pshkrh.notes.Helper.SnackbarHelper;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class LibActivity extends AppCompatActivity {

    private View parentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lib);

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Raleway-Medium.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        parentView = findViewById(R.id.lib_linear);

        String[] list = getResources().getStringArray(R.array.lib_array);

        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);

        ListView listView = findViewById(R.id.lib_listview);
        listView.setAdapter(itemsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i){
                    case 0:
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://developer.android.com/topic/libraries/support-library/"));
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/InflationX/Calligraphy"));
                        startActivity(intent);
                        break;

                    case 2:
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/afollestad/material-dialogs/"));
                        startActivity(intent);
                        break;

                    case 3:
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/rengwuxian/MaterialEditText"));
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}
