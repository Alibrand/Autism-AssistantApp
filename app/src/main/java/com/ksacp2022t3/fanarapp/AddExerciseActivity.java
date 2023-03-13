package com.ksacp2022t3.fanarapp;

import static android.widget.Toast.*;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ksacp2022t3.fanarapp.models.Exercise;
import com.ksacp2022t3.fanarapp.models.ExerciseFile;
import com.ksacp2022t3.fanarapp.models.GlideApp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddExerciseActivity extends AppCompatActivity {

    EditText txt_exercise_text,txt_option1_text,txt_option2_text,
    txt_option3_text;
    RadioGroup group_correct;
    ImageView option1_image,option2_image,option3_image,btn_back,btn_record,btn_delete_record;
    TextView txt_time, btn_upload_audio;
    AppCompatButton btn_save;

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    FirebaseStorage storage;
    ProgressDialog progressDialog;


    MediaRecorder recorder;
    MediaPlayer player;
    String state="IDLE";
    String outputFile = Environment.getExternalStorageDirectory().
            getAbsolutePath() + "/fanar/recording.m4a";

    int seconds=0;

    Handler mHandler;
    Runnable mRunnable;
    int max_audio_length=20;

    Uri selected_audio_file;
    Uri option1_image_uri,option2_image_uri,option3_image_uri;
    int counter=0;

    String category_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);
        txt_exercise_text = findViewById(R.id.txt_exercise_text);
        txt_option1_text = findViewById(R.id.txt_option1_text);
        txt_option2_text = findViewById(R.id.txt_option2_text);
        txt_option3_text = findViewById(R.id.txt_option3_text);
        group_correct = findViewById(R.id.group_correct);
        option1_image = findViewById(R.id.option1_image);
        option2_image = findViewById(R.id.option2_image);
        option3_image = findViewById(R.id.option3_image);
        btn_back = findViewById(R.id.btn_back);
        btn_record = findViewById(R.id.btn_record);
        txt_time = findViewById(R.id.txt_time);
        btn_save = findViewById(R.id.btn_save);
        btn_delete_record = findViewById(R.id.btn_delete_record);
        btn_upload_audio = findViewById(R.id.btn_upload);





        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();


        category_id=getIntent().getStringExtra("category_id");


        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle(R.string.processing);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        createFolder();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        btn_upload_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(AddExerciseActivity.this)
                        .setMessage(R.string.confirm_selecting)
                        .setTitle(R.string.confirm)
                        .setNegativeButton(R.string.cancel,null)
                        .setPositiveButton(R.string.proceed, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent_upload = new Intent();
                                intent_upload.setType("audio/*");
                                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intent_upload,100);
                            }
                        }).show();



            }
        });

        option1_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_upload = new Intent();
                intent_upload.setType("image/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload,101);
            }
        });
        option2_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_upload = new Intent();
                intent_upload.setType("image/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload,102);
            }
        });
        option3_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_upload = new Intent();
                intent_upload.setType("image/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload,103);
            }
        });




        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(state.equals("IDLE"))
                {
                    if(!checkRecordPermission()||!checkWriteExternalPermission())
                    {
                        ActivityCompat.requestPermissions(AddExerciseActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO},100);
                         return;
                    }

                    startRecording();
                    startCounter();

                }
                else if(state.equals("RECORDING"))
                {
                    stopRecording();


                 }
                else if(state.equals("STOP"))
                {
                   startPlaying();
                    ;


                }
                else if(state.equals("PLAYING"))
                {
                    stopPlaying();

                  }

            }
        });

        btn_delete_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlaying();
                state="IDLE";
                btn_record.setImageResource(R.drawable.ic_baseline_fiber_manual_record_24);
                btn_delete_record.setVisibility(View.GONE);
                seconds=0;
                txt_time.setText("00:00");
                deleteRecording();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String str_txt_exercise_text =txt_exercise_text.getText().toString();
                String str_txt_option1_text =txt_option1_text.getText().toString();
                String str_txt_option2_text =txt_option2_text.getText().toString();
                String str_txt_option3_text =txt_option3_text.getText().toString();

                if(str_txt_exercise_text.isEmpty())
                {
                     txt_exercise_text.setError(getString(R.string.required_field) );
                     return;
                }
                if(str_txt_option1_text.isEmpty())
                {
                     txt_option1_text.setError(getString(R.string.required_field));
                     return;
                }
                if(str_txt_option2_text.isEmpty())
                {
                     txt_option2_text.setError(getString(R.string.required_field));
                     return;
                }
                if(str_txt_option3_text.isEmpty())
                {
                     txt_option3_text.setError(getString(R.string.required_field));
                     return;
                }

                if(selected_audio_file==null && state.equals("IDLE"))
                {
                    makeText(AddExerciseActivity.this, R.string.you_should_upload , LENGTH_LONG).show();
                    return;
                }


                progressDialog.show();
                if(selected_audio_file==null)
                {
                    selected_audio_file=Uri.fromFile(new File(outputFile));
                }


                List<ExerciseFile> files_to_upload=new ArrayList<>();
                Exercise exercise=new Exercise();


                StorageReference reference=storage.getReference();
                String audio_name= UUID.randomUUID().toString();
                StorageReference audio_file=reference.child("exercises_audios/"+audio_name);
                files_to_upload.add(new ExerciseFile(audio_name,audio_file,selected_audio_file));
                exercise.setAudio_name(audio_name);


                String option1_image= UUID.randomUUID().toString();
                StorageReference image1_file=reference.child("exercises_images/"+option1_image);
                files_to_upload.add(new ExerciseFile(option1_image,image1_file,option1_image_uri));
                exercise.setOption1_image(option1_image);
                exercise.setOption1_text(str_txt_option1_text);


                String option2_image= UUID.randomUUID().toString();
                StorageReference image2_file=reference.child("exercises_images/"+option2_image);
                files_to_upload.add(new ExerciseFile(option2_image,image2_file,option2_image_uri));
                exercise.setOption2_image(option2_image);
                exercise.setOption2_text(str_txt_option2_text);


                String option3_image= UUID.randomUUID().toString();
                StorageReference image3_file=reference.child("exercises_images/"+option3_image);
                files_to_upload.add(new ExerciseFile(option3_image,image3_file,option3_image_uri));
                exercise.setOption3_image(option3_image);
                exercise.setOption3_text(str_txt_option3_text);


                exercise.setQuestion(str_txt_exercise_text);
                RadioButton selected=findViewById(group_correct.getCheckedRadioButtonId()) ;
                exercise.setCorrect_option(selected.getText().toString());


                counter=0;
                progressDialog.setTitle(getString(R.string.uploading));
                progressDialog.setMessage(getString(R.string.uploaded)+" "+counter+"/4");
                for (ExerciseFile file:files_to_upload)
                {

                    StorageReference ref=file.getReference();
                    ref.putFile(file.getPath())
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    counter++;
                                    progressDialog.setMessage(getString(R.string.uploaded)+" "+counter+"/4");

                                    if(counter==4)
                                    {
                                       //save exercise
                                        progressDialog.setTitle(R.string.processing);
                                        progressDialog.setMessage(getString(R.string.please_wait));
                                        DocumentReference documentReference=firestore
                                                .collection("accounts")
                                                .document(firebaseAuth.getUid())
                                                .collection("categories")
                                                .document(category_id)
                                                .collection("exercises")
                                                .document();
                                        exercise.setId(documentReference.getId());

                                        documentReference.set(exercise)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        progressDialog.dismiss();
                                                        makeText(AddExerciseActivity.this, R.string.exercise_added , LENGTH_LONG).show();
                                                        finish();
                                                        if(!state.equals("IDLE"))
                                                        {
                                                            deleteRecording();
                                                        }
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progressDialog.dismiss();
                                                        makeText(AddExerciseActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                                                    }
                                                });

                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    makeText(AddExerciseActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                                }
                            });

                }





            }
        });











    }

    private void startRecording(){

        recorder=new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFile(outputFile);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setAudioEncodingBitRate(16*22050);
        recorder.setAudioSamplingRate(11025);


        try {
            recorder.prepare();
        }
        catch (IOException e) {
            // handle error
        }
        catch (IllegalStateException e) {
            // handle error
        }


        recorder.start();

        btn_record.setImageResource(R.drawable.ic_baseline_stop_24);

        state="RECORDING";
    }

    private void stopRecording() {
        // stop recording and free up resources
        recorder.stop();
        recorder.release();

        recorder = null;
        state="STOP";
        btn_delete_record.setVisibility(View.VISIBLE);
        mHandler.removeCallbacks(mRunnable);

        btn_record.setImageResource(R.drawable.ic_baseline_play_arrow_24);

        selected_audio_file=null;

    }



    private boolean checkWriteExternalPermission()
    {
        String permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int res = AddExerciseActivity.this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private boolean checkRecordPermission()
    {
        String permission = Manifest.permission.RECORD_AUDIO;
        int res = AddExerciseActivity.this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(!checkRecordPermission()||!checkWriteExternalPermission())
        {
            makeText(AddExerciseActivity.this, R.string.you_cant_record_permission , LENGTH_LONG).show();
            return;
        }
        else
        {
            makeText(AddExerciseActivity.this, R.string.permission_granted , LENGTH_LONG).show();
        }
    }

    private void createFolder(){
        if(!checkRecordPermission()||!checkWriteExternalPermission())
        {
            ActivityCompat.requestPermissions(AddExerciseActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO},100);
            return;
        }
        File f = new File(Environment.getExternalStorageDirectory(), "fanar");
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    void initPlayer(){
        try {

            if(player!=null&&player.isPlaying())
                player.stop();
            player=new MediaPlayer();
            player.setDataSource(outputFile);
            player.prepare();
            player.setVolume(1,1);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void startPlaying(){
        initPlayer();
        player.start();
        state="PLAYING";
        btn_record.setImageResource(R.drawable.ic_baseline_stop_24);
        seconds=0;
        txt_time.setText("00:00");
        startCounter();

    }

    void stopPlaying(){
        if(player==null)
            return;
        if(player.isPlaying())
            player.stop();
        state="STOP";
        player.pause();
        player.seekTo(0);
        btn_record.setImageResource(R.drawable.ic_baseline_play_arrow_24);

    }

     private void startCounter(){
        mHandler=new Handler();
        mRunnable =new Runnable() {
            @Override
            public void run() {
                seconds++;
                if(seconds<10)
                {
                    txt_time.setText("00:0"+seconds);
                }
                else
                {
                    txt_time.setText("00:"+seconds);
                }
                if(seconds>=max_audio_length && state.equals("RECORDING"))
                {
                    stopRecording();
                    makeText(AddExerciseActivity.this, R.string.recording_stopped , LENGTH_LONG).show();
                    return;
                }
                else if (seconds>=max_audio_length && state.equals(state=="PLAYING")){
                    stopPlaying();
                    return;
                }
                if(state.equals("IDLE"))
                    return;
                if(player!=null&&!player.isPlaying()) {
                    stopPlaying();
                    return;
                }
                if(state.equals("STOP"))
                    return;
                mHandler.postDelayed(mRunnable,1000);

            }
        };
         mHandler.postDelayed(mRunnable,1000);
     }

     void deleteRecording(){
        File file=new File(outputFile);
        file.delete();
     }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            if(requestCode==100) {

                if(checkFileSize(data.getData())) {
                    makeText(AddExerciseActivity.this, R.string.smaller_size, LENGTH_LONG).show();
                    selected_audio_file=null;
                    return;
                }
                selected_audio_file = data.getData();
                makeText(AddExerciseActivity.this, R.string.audio_selecetd, LENGTH_LONG).show();

                stopPlaying();
                state="IDLE";
                btn_record.setImageResource(R.drawable.ic_baseline_fiber_manual_record_24);
                btn_delete_record.setVisibility(View.GONE);
                seconds=0;
                txt_time.setText("00:00");
                deleteRecording();


            }
            else  if(requestCode==101) {
                option1_image_uri = data.getData();
                GlideApp.with(AddExerciseActivity.this)
                        .load(option1_image_uri)
                        .centerCrop()
                        .into(option1_image);
            }
            else  if(requestCode==102) {
                option2_image_uri = data.getData();
                GlideApp.with(AddExerciseActivity.this)
                        .load(option2_image_uri)
                        .centerCrop()
                        .into(option2_image);
            }
            else  if(requestCode==103) {
                option3_image_uri = data.getData();
                GlideApp.with(AddExerciseActivity.this)
                        .load(option3_image_uri)
                        .centerCrop()
                        .into(option3_image);
            }
        }
    }

    boolean checkFileSize(Uri path)
    {
        Cursor returnCursor = getContentResolver().
                query(path, null, null, null, null);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();

        int size = returnCursor.getInt(sizeIndex) / 1024;

        returnCursor.close();

        return size>50;


    }
}