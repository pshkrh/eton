package com.pshkrh.eton;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pshkrh.eton.Helper.SnackbarHelper;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class AboutActivity extends AppCompatActivity {

    Context mContext = this;
    View parentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Raleway-Medium.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        TextView version = findViewById(R.id.version);
        String ver = "Version " + BuildConfig.VERSION_NAME;
        version.setText(ver);

        parentView = findViewById(R.id.about_coordinator);

        ImageView github = findViewById(R.id.github_button);
        ImageView linkedin = findViewById(R.id.linkedin_button);

        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String link = getResources().getString(R.string.github_link);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(intent);
            }
        });

        linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String link = getResources().getString(R.string.linkedin_link);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(intent);
            }
        });

        String[] list = getResources().getStringArray(R.array.about_array);

        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);

        ListView listView = findViewById(R.id.about_listview);
        listView.setAdapter(itemsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i){
                    case 0:
                        Intent intent = new Intent(AboutActivity.this,CodeLicenseActivity.class);
                        startActivity(intent);
                        break;

                    case 1:
                        intent = new Intent(AboutActivity.this,LibActivity.class);
                        startActivity(intent);
                        break;

                    case 2:
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.pshkrh.eton>"));
                        startActivity(intent);
                        break;

                    case 3:
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://pshkrh.com/"));
                        startActivity(intent);
                        break;

                    case 4:
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Pushkar+Kurhekar"));
                        startActivity(intent);
                        break;

                    case 5:
                        SnackbarHelper.snackLong(parentView,"Special Thanks to Aniruddh Iyer for the Logo Design!");
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
