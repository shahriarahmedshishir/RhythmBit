package com.diydiscovery.rhythmbit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView noMusicTextView;
    ArrayList<AudioModel> songlist=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        recyclerView = findViewById(R.id.recyclerview);
        noMusicTextView = findViewById(R.id.nomusictext);

        if (checkPermission()==false) {
            requestPermission();
            return;
        }

        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };
        String selection = MediaStore.Audio.Media.IS_MUSIC + " !=0";

        try (Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null
        )) {
            while (cursor != null && cursor.moveToNext()) {
                AudioModel songData = new AudioModel(cursor.getString(1), cursor.getString(0), cursor.getString(2));
                if (new File(songData.getPath()).exists())
                    songlist.add(songData);
            }
        }

        if (songlist.size() > 0) {
            noMusicTextView.setVisibility(View.GONE);
            recyclerView.setAdapter(new MusicListAdapter(songlist, getApplicationContext()));
        } else {
            noMusicTextView.setVisibility(View.VISIBLE);
        }
    }

    boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity2.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity2.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity2.this, "Read permission is required. Please allow from settings.", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity2.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (recyclerView!=null){
            recyclerView.setAdapter(new MusicListAdapter(songlist,getApplicationContext()));
        }
    }
}