package com.ksacp2022t3.fanarapp.models;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.ksacp2022t3.fanarapp.R;

import java.util.ArrayList;
import java.util.List;

public class InitCategories {
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    Context context;
    String question;


    List<Category> categories=new ArrayList<>();






    List<Exercise> animals_exercises=new ArrayList<>();
    List<Exercise> nature_exercises=new ArrayList<>();
    List<Exercise> instruments_exercises=new ArrayList<>();
    List<Exercise> vehicles_exercises=new ArrayList<>();


    public InitCategories(Context context) {
        this.context = context;
        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
          question=context.getString(R.string.question_text);
        init_categories();
        init_exercises();

    }


    public  void insert_to_firestore(){
        WriteBatch batch= firestore.batch();
        for(Category category:categories)
        {
            DocumentReference cat_doc=firestore.collection("accounts")
                    .document(firebaseAuth.getUid())
                    .collection("categories")
                    .document();
            category.setId(cat_doc.getId());
            batch.set(cat_doc,category);
            List<Exercise> exercises=new ArrayList<>();
            if(category.getName().equals(context.getString(R.string.animals)))
                exercises=animals_exercises;
            if(category.getName().equals(context.getString(R.string.nature)))
                exercises=nature_exercises;
            if(category.getName().equals(context.getString(R.string.vehicles)))
                exercises=vehicles_exercises;
            if(category.getName().equals(context.getString(R.string.instruments)))
                exercises=instruments_exercises;

            for(Exercise exercise:exercises)
            {
                DocumentReference exe_doc=firestore.collection("accounts")
                        .document(firebaseAuth.getUid())
                        .collection("categories")
                        .document(category.getId())
                        .collection("exercises")
                        .document();
                exercise.setId(exe_doc.getId());
                batch.set(exe_doc,exercise);
            }
        }

        batch.commit().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(context,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
            }
        });

    }

    private void init_categories(){
        Category animals=new Category(context.getString(R.string.animals), "default/animals.jpg",true);
        Category nature=new Category(context.getString(R.string.nature),"default/nature.PNG",true);
        Category vehicles=new Category(context.getString(R.string.vehicles),"default/vehicles.jpg",true);
        Category instruments=new Category(context.getString(R.string.instruments),"default/instruments.PNG",true);
        categories.add(animals);
        categories.add(nature);
        categories.add(vehicles);
        categories.add(instruments);
    }

    private  void init_exercises(){
        animals_exercises.add(new Exercise(question,"default/cat.mp3","default/cat.png",context.getString(R.string.cat),"default/dog.png",context.getString(R.string.dog),"default/monkey.png",context.getString(R.string.monkey),"1",true));
        animals_exercises.add(new Exercise(question,"default/chick.mp3","default/chicken.png",context.getString(R.string.chicken),"default/chick.png",context.getString(R.string.chick),"default/duck.png",context.getString(R.string.duck),"2",true));
        animals_exercises.add(new Exercise(question,"default/chicken.mp3","default/cat.png",context.getString(R.string.cat),"default/chicken.png",context.getString(R.string.chicken),"default/monkey.png",context.getString(R.string.monkey),"2",true));
        animals_exercises.add(new Exercise(question,"default/cow.mp3","default/cow.png",context.getString(R.string.cow),"default/dog.png",context.getString(R.string.dog),"default/cat.png",context.getString(R.string.cat),"1",true));
        animals_exercises.add(new Exercise(question,"default/dog.mp3","default/cat.png",context.getString(R.string.cat),"default/dog.png",context.getString(R.string.dog),"default/elephant.png",context.getString(R.string.elephant),"2",true));
        animals_exercises.add(new Exercise(question,"default/duck.mp3","default/duck.png",context.getString(R.string.duck),"default/bird.jpg",context.getString(R.string.bird),"default/rooster.png",context.getString(R.string.rooster),"1",true));
        animals_exercises.add(new Exercise(question,"default/elephant.mp3","default/horse.png",context.getString(R.string.horse),"default/dog.png",context.getString(R.string.dog),"default/elephant.png",context.getString(R.string.elephant),"3",true));
        animals_exercises.add(new Exercise(question,"default/horse.mp3","default/horse.png",context.getString(R.string.horse),"default/dog.png",context.getString(R.string.dog),"default/monkey.png",context.getString(R.string.monkey),"1",true));
        animals_exercises.add(new Exercise(question,"default/rooster.mp3","default/chicken.png",context.getString(R.string.chicken),"default/rooster.png",context.getString(R.string.rooster),"default/chick.png",context.getString(R.string.chick),"2",true));


        nature_exercises.add(new Exercise(question,"default/rain.mp3","default/thunder.jpg",context.getString(R.string.thunder),"default/wind.png",context.getString(R.string.wind),"default/rain.jpg",context.getString(R.string.rain),"3",true));
        nature_exercises.add(new Exercise(question,"default/thunder.mp3","default/waterfall.jpg",context.getString(R.string.waterfall),"default/thunder.jpg",context.getString(R.string.thunder),"default/wind.png",context.getString(R.string.wind),"2",true));
        nature_exercises.add(new Exercise(question,"default/waterfall.mp3","default/waterfall.jpg",context.getString(R.string.waterfall),"default/wind.png",context.getString(R.string.wind),"default/rain.jpg",context.getString(R.string.rain),"1",true));
        nature_exercises.add(new Exercise(question,"default/wind.mp3","default/thunder.jpg",context.getString(R.string.thunder),"default/wind.png",context.getString(R.string.wind),"default/rain.jpg",context.getString(R.string.rain),"2",true));

        instruments_exercises.add(new Exercise(question,"default/accordion.mp3","default/accordion.jpg",context.getString(R.string.accordion),"default/piano.jpg",context.getString(R.string.piano),"default/violin.jpg",context.getString(R.string.violin),"1",true));
        instruments_exercises.add(new Exercise(question,"default/flute.mp3","default/drum.jpg",context.getString(R.string.drums),"default/piano.jpg",context.getString(R.string.piano),"default/flute.jpg",context.getString(R.string.flute),"3",true));
        instruments_exercises.add(new Exercise(question,"default/piano.mp3","default/flute.jpg",context.getString(R.string.flute),"default/accordion.jpg",context.getString(R.string.accordion),"default/piano.jpg",context.getString(R.string.piano),"3",true));
        instruments_exercises.add(new Exercise(question,"default/violin.mp3","default/guitar.jpg",context.getString(R.string.guitar),"default/violin.jpg",context.getString(R.string.violin),"default/flute.jpg",context.getString(R.string.flute),"2",true));
        instruments_exercises.add(new Exercise(question,"default/guitar.mp3","default/guitar.jpg",context.getString(R.string.guitar),"default/piano.jpg",context.getString(R.string.piano),"default/violin.jpg",context.getString(R.string.violin),"1",true));
        instruments_exercises.add(new Exercise(question,"default/drum.mp3","default/violin.jpg",context.getString(R.string.violin),"default/drum.jpg",context.getString(R.string.drums),"default/accordion.jpg",context.getString(R.string.accordion),"2",true));

        vehicles_exercises.add(new Exercise(question,"default/car.mp3","default/truck.png",context.getString(R.string.truck),"default/car.png",context.getString(R.string.car),"default/motocycle.png",context.getString(R.string.motorcycle),"2",true));
        vehicles_exercises.add(new Exercise(question,"default/helicopter.mp3","default/plane.png",context.getString(R.string.airplane),"default/ship.png",context.getString(R.string.ship),"default/helicopter.png",context.getString(R.string.helicopter),"3",true));
        vehicles_exercises.add(new Exercise(question,"default/motorcycle.mp3","default/motocycle.png",context.getString(R.string.motorcycle),"default/car.png",context.getString(R.string.car),"default/helicopter.png",context.getString(R.string.helicopter),"1",true));
        vehicles_exercises.add(new Exercise(question,"default/plane.mp3","default/ship.png",context.getString(R.string.ship),"default/truck.png",context.getString(R.string.truck),"default/plane.png",context.getString(R.string.airplane),"3",true));
        vehicles_exercises.add(new Exercise(question,"default/ship.mp3","default/helicopter.png",context.getString(R.string.helicopter),"default/ship.png",context.getString(R.string.ship),"default/truck.png",context.getString(R.string.truck),"2",true));
        vehicles_exercises.add(new Exercise(question,"default/truck.mp3","default/truck.png",context.getString(R.string.truck),"default/car.png",context.getString(R.string.car),"default/ship.png",context.getString(R.string.ship),"1",true));






    }

}
