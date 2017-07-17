package jaipur.com.dthrillbox;

import android.Manifest;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static android.R.attr.value;
import static jaipur.com.dthrillbox.GlobalList.arrayartist;
import static jaipur.com.dthrillbox.GlobalList.arraypath;
import static jaipur.com.dthrillbox.GlobalList.arraypic;
import static jaipur.com.dthrillbox.GlobalList.arraytitle;

public class MainActivity extends AppCompatActivity {

    ImageButton prev,next,play,list;
    private static final int MY_PERMISSION_REQUEST=1;
    MediaPlayer mediaPlayer;
    Handler handler;
    Runnable runnable;
    MediaMetadataRetriever mmr;
    SeekBar seekBar;
    TextView starttime,remaintime;
    byte[] data;
    private double remain=0;
    int pos;
    Boolean value=false;
    TextView showtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#000000")));


        //mmr=new MediaMetadataRetriever();
        seekBar=(SeekBar)findViewById(R.id.seekBar);
        starttime=(TextView)findViewById(R.id.start);
        remaintime=(TextView)findViewById(R.id.end);
        list=(ImageButton)findViewById(R.id.stopbutton);
        play=(ImageButton)findViewById(R.id.pausebutton);
        prev=(ImageButton)findViewById(R.id.previousbuttton);
        next=(ImageButton)findViewById(R.id.nextbutton);
        showtext=(TextView)findViewById(R.id.showtext);

        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSION_REQUEST);
            }else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSION_REQUEST);
            }
        }else{
            dostuff();

        }


        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(),"Button Clicked",Toast.LENGTH_SHORT).show();
                if(value==false) {

                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.framecontainer, new songFragment())
                            .commit();
                    value=true;

                }
                else
                {
                    getSupportFragmentManager().beginTransaction().
                            remove(getSupportFragmentManager().findFragmentById(R.id.framecontainer)).commit();
                    value=false;
                }
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if(mediaPlayer!=null && mediaPlayer.isPlaying())
              {
                  mediaPlayer.pause();
                  play.setImageResource(R.drawable.playonee);
              }
                else if(mediaPlayer!=null)
              {
                  mediaPlayer.start();
                  play.setImageResource(R.drawable.pauseonee);
              }
            }
        });
         prev.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if(mediaPlayer!=null)
                 {
                     pos--;
                     if(pos<0)
                     pos=arraytitle.size()-1;
                     playapp(pos);
                 }
             }
         });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer!=null)
                {
                    pos++;
                    if(pos>arraytitle.size())
                        pos=0;
                    playapp(pos);
                }
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean input) {
                if(input)
                {
                    try{mediaPlayer.seekTo(progress);
                    playcycle();}
                    catch (Exception e){}
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    public void dostuff()
    {
        arraytitle=new ArrayList<>();
        arrayartist=new ArrayList<>();
        arraypath=new ArrayList<>();
        arraypic=new ArrayList<>();
        getMusic();

    }

    public void getMusic(){
        ContentResolver contentResolver=getContentResolver();
        Uri songuri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songcursor= contentResolver.query(songuri, null, null, null,null);
        if(songcursor!=null && songcursor.moveToFirst()){
            int songTitle=songcursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist=songcursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songLocation=songcursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            do{
                String cursortitle=songcursor.getString(songTitle);
                String cursorArtist=songcursor.getString(songArtist);
                String cursorlocation=songcursor.getString(songLocation);
                arraytitle.add(cursortitle+"\n");
                arrayartist.add(cursorArtist+"\n");
                arraypath.add(cursorlocation);
                //To add picture in Custom list view
                mmr=new MediaMetadataRetriever();
                mmr.setDataSource(cursorlocation);
                try{
                    arraypic.add(mmr.getEmbeddedPicture());}
                catch (Exception e){}

            }while (songcursor.moveToNext());
        }

    }


     public void playapp(int position)
     {
         try
         {
         pos=position;
         play.setImageResource(R.drawable.pauseonee);
        if(mediaPlayer!=null && mediaPlayer.isPlaying())
            mediaPlayer.release();
         handler=new Handler();
         mediaPlayer=new MediaPlayer();
         showtext.setText("NOW PLAYING:"+arraytitle.get(position));



             mediaPlayer.setDataSource(arraypath.get(position));
             mediaPlayer.prepare();
             mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
             seekBar.setMax(mediaPlayer.getDuration());

             starttime.setText(String.format("%02d:%02d",
                     TimeUnit.MILLISECONDS.toMinutes((long) mediaPlayer.getCurrentPosition()),
                     TimeUnit.MILLISECONDS.toSeconds((long) mediaPlayer.getCurrentPosition()) -
                             TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)mediaPlayer.getCurrentPosition()))
             ));
             remaintime.setText(String.format("-%02d:%02d",
                     TimeUnit.MILLISECONDS.toMinutes((long) mediaPlayer.getDuration()),
                     TimeUnit.MILLISECONDS.toSeconds((long) mediaPlayer.getDuration()) -
                             TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)mediaPlayer.getDuration()))
             ));

             playcycle();
             mediaPlayer.start();
         }
         catch(Exception e){e.printStackTrace();return ;}




     }


    public void playcycle()
    {
        try {
            //Toast.makeText(getApplicationContext(),"Playcycle Running1",Toast.LENGTH_SHORT).show();
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            //Toast.makeText(getApplicationContext(),"Playcycle Running2",Toast.LENGTH_SHORT).show();
            remain = mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition();
            if (remaintime.getText().toString().equals("-00:00")) {


                play.setImageResource(R.drawable.playonee);
                mediaPlayer.seekTo(0);
                mediaPlayer.pause();
            } else {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        playcycle();
                    }
                };
                handler.postDelayed(runnable, 1000);

                // Toast.makeText(getApplicationContext(),"Playcycle Running4",Toast.LENGTH_SHORT).show();
            }
            starttime.setText(String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes((long) mediaPlayer.getCurrentPosition()),
                    TimeUnit.MILLISECONDS.toSeconds((long) mediaPlayer.getCurrentPosition()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) mediaPlayer.getCurrentPosition()))
            ));
            remaintime.setText(String.format("-%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes((long) remain),
                    TimeUnit.MILLISECONDS.toSeconds((long) remain) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) remain))
            ));
        }
        catch (Exception e){}
        //Toast.makeText(getApplicationContext(),"Playcycle Running3",Toast.LENGTH_SHORT).show();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


            switch (item.getItemId()) {
                case R.id.action_search:
                    try{
                        mediaPlayer.pause();
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=" + arraytitle.get(pos))));
                    }
                    catch(Exception e){}
                    return true;
                case R.id.action_lyrics:
                     try {
                         startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.co.in/#q=+" + arraytitle.get(pos) + "lyrics")));
                     }
                     catch (Exception e){}
                     }


        return super.onOptionsItemSelected(item);

    }








    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION_REQUEST:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this,"Permission granted",Toast.LENGTH_SHORT).show();
                        dostuff();
                    }
                }
                else
                {
                    Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mediaPlayer.pause();
            mediaPlayer.stop();
            mediaPlayer.release();
            handler.removeCallbacks(runnable);
        }
        catch (Exception e){}
        finish();
    }

}
