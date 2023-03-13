package com.ksacp2022t3.fanarapp;

import static android.widget.Toast.*;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.slider.Slider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ksacp2022t3.fanarapp.models.Exercise;
import com.ksacp2022t3.fanarapp.models.GlideApp;

import java.io.IOException;
import java.util.List;


public class PlayExerciseActivity extends AppCompatActivity {
    ImageView btn_back,option1_image,option2_image,option3_image,btn_play,btn_next,btn_previous;
    TextView txt_exercise_text,txt_option1_text,txt_option2_text,txt_option3_text,txt_time;
    SeekBar seekbar;
    LinearLayoutCompat option1,option2,option3;
    ProgressBar loading_audio;
    AppCompatButton btn_check;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;
    FirebaseStorage storage;
    FirebaseAuth firebaseAuth;
    MediaPlayer player,result_player;


    Handler mHandler;
    Runnable mRunnable;
    int seconds=0;

    String highlighted="1" ;
    View highlighted_option;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_exercise);
        btn_back = findViewById(R.id.btn_back);
        option1_image = findViewById(R.id.option1_image);
        option2_image = findViewById(R.id.option2_image);
        option3_image = findViewById(R.id.option3_image);
        btn_play = findViewById(R.id.btn_play);
        txt_exercise_text = findViewById(R.id.txt_exercise_text);
        txt_option1_text = findViewById(R.id.txt_option1_text);
        txt_option2_text = findViewById(R.id.txt_option2_text);
        txt_option3_text = findViewById(R.id.txt_option3_text);
        txt_time = findViewById(R.id.txt_time);
        seekbar = findViewById(R.id.seekbar);
        btn_check = findViewById(R.id.btn_check);
        loading_audio = findViewById(R.id.loading_audio);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        btn_next = findViewById(R.id.btn_next);
        btn_previous = findViewById(R.id.btn_previous);




        storage=FirebaseStorage.getInstance();
        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle(R.string.loading);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);



        String exercise_id=getIntent().getStringExtra("exercise_id");
        String category_id=getIntent().getStringExtra("category_id");
        List<Exercise> exercises= (List<Exercise>) getIntent().getSerializableExtra("exercises");
        int index=getIntent().getIntExtra("index",0);




        progressDialog.show();
        firestore.collection("accounts")
                .document(firebaseAuth.getUid())
                .collection("categories")
                .document(category_id)
                .collection("exercises")
                .document(exercise_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progressDialog.dismiss();
                        Exercise exercise=documentSnapshot.toObject(Exercise.class);
                        txt_exercise_text.setText(exercise.getQuestion());
                        txt_option1_text.setText(exercise.getOption1_text());
                        txt_option2_text.setText(exercise.getOption2_text());
                        txt_option3_text.setText(exercise.getOption3_text());

                        StorageReference reference=storage.getReference();

                        StorageReference image1=reference.child("exercises_images/"+exercise.getOption1_image());
                        StorageReference image2=reference.child("exercises_images/"+exercise.getOption2_image());
                        StorageReference image3=reference.child("exercises_images/"+exercise.getOption3_image());
                        StorageReference audio=reference.child("exercises_audios/"+exercise.getAudio_name());

                        GlideApp.with(PlayExerciseActivity.this)
                                .load(image1)
                                .fitCenter()
                                .into(option1_image);

                        GlideApp.with(PlayExerciseActivity.this)
                                .load(image2)
                                .fitCenter()
                                .into(option2_image);

                        GlideApp.with(PlayExerciseActivity.this)
                                .load(image3)
                                .fitCenter()
                                .into(option3_image);

                        audio.getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                initPlayer(uri.toString());
                                                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                                    @Override
                                                    public void onPrepared(MediaPlayer mediaPlayer) {
                                                        loading_audio.setVisibility(View.GONE);
                                                        btn_play.setVisibility(View.VISIBLE);
                                                    }
                                                });

                                                btn_play.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        if(!player.isPlaying()) {
                                                            player.start();
                                                            final AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                                                            int volume=mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                                            if(volume==0)
                                                            {
                                                                makeText(PlayExerciseActivity.this, R.string.muted_volume , LENGTH_LONG).show();
                                                            }
                                                            btn_play.setImageResource(R.drawable.ic_baseline_pause_24);
                                                            seekbar.setMax(player.getDuration());
                                                            seconds=0;
                                                            mHandler=new Handler();
                                                            mRunnable=new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    int currentPosition = player.getCurrentPosition();
                                                                    int total = player.getDuration();
                                                                    if(player != null && player.isPlaying() && currentPosition < total)
                                                                    {
                                                                        currentPosition = player.getCurrentPosition();
                                                                        seekbar.setProgress(currentPosition);

                                                                        seconds++;
                                                                        if(seconds<10)
                                                                        {
                                                                            txt_time.setText("00:0"+seconds);
                                                                        }
                                                                        else
                                                                        {
                                                                            txt_time.setText("00:"+seconds);
                                                                        }

                                                                        mHandler.postDelayed(mRunnable,1000);
                                                                    }
                                                                    else {
                                                                        stopPlaying();
                                                                        btn_play.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                                                                    }




                                                                }
                                                            };
                                                            mHandler.post(mRunnable);
                                                        }
                                                        else {
                                                            stopPlaying();
                                                            btn_play.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                                                            seconds=0;
                                                            txt_time.setText("00:00");
                                                        }

                                                    }
                                                });
                                            }
                                        });

                        btn_check.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(highlighted.equals(exercise.getCorrect_option()))
                                {
                                    highlighted_option.setBackgroundColor(Color.GREEN);
                                    makeText(PlayExerciseActivity.this, R.string.correct , LENGTH_LONG).show();
                                    playResult("correct");
                                }
                                else
                                {
                                    highlighted_option.setBackgroundColor(Color.RED);
                                    makeText(PlayExerciseActivity.this, R.string.wrong , LENGTH_LONG).show();
                                    int correct_option_id=getResources().getIdentifier("option"+exercise.getCorrect_option(),"id",getPackageName());
                                    View correct=findViewById(correct_option_id);
                                    correct.setBackgroundColor(Color.GREEN);
                                    playResult("wrong");
                                }

                            }
                        });

                        btn_next.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                               int next_index=index+1;
                                if(index==exercises.size()-1)
                                {
                                    next_index=0;
                                }
                                 Intent intent=getIntent();
                                intent.putExtra("exercise_id",exercises.get(next_index).getId());
                                intent.putExtra("index",next_index);
                                startActivity(intent);
                                finish();


                            }
                        });

                        btn_previous.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int prev_index=index-1;
                                if(index==0)
                                {
                                    prev_index=exercises.size()-1;
                                }
                                Intent intent=getIntent();
                                intent.putExtra("exercise_id",exercises.get(prev_index).getId());
                                intent.putExtra("index",prev_index);
                                startActivity(intent);
                                finish();
                            }
                        });





                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        makeText(PlayExerciseActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                        finish();
                    }
                });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });







    }

    void initPlayer(String url){
        try {

            if(player!=null&&player.isPlaying())
                player.stop();
            player=new MediaPlayer();
            player.setDataSource(url);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.prepareAsync();
            player.setVolume(1,1);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    void stopPlaying(){
        if(player==null)
            return;

        player.pause();
        //player.seekTo(0);

    }

    private void clearMediaPlayer() {
        player.stop();
        player.release();
        player = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearMediaPlayer();
    }

    public void highlight(View view) {
        option1.setBackground(null);
        option2.setBackground(null);
        option3.setBackground(null);
        view.setBackgroundColor(Color.YELLOW);
        highlighted=view.getTag().toString();
        highlighted_option=view;
    }

    void playResult(String file){
        try {
            final AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
            if(result_player!=null&&result_player.isPlaying())
                result_player.stop();
            result_player=new MediaPlayer();
            AssetFileDescriptor afd = getAssets().openFd(file+".mp3");
            result_player.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            afd.close();
            result_player.prepare();
            result_player.setVolume(1,1);
            result_player.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}