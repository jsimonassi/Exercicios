/*
 * Classe: ApiAcess
 *
 * Descrição: Responsável pelo acesso aos dados da Api do Firebase.
 * Adicionar usuários, exercícios, atualizar fotos e demais recursos.
 */


package com.simonassi.exercicios;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


public class ApiAcess {

    //GET Users/id
    public static void searchUser(String email, final SearchUserCallback callback){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){

                        User response = new User();
                        response.setName(document.get("name").toString());
                        response.setEmail(document.get("email").toString());
                        response.setPassword(document.get("password").toString());
                        response.setPhoto(document.get("photo").toString());
                        response.setWeight(Float.parseFloat(document.get("weight").toString()));

                        callback.onSuccess(response);
                    }
                    else
                        callback.onFailure();
                }else
                    callback.onFailure();
            }
        });
    }

    public interface SearchUserCallback {
        void onSuccess(User response);
        void onFailure();
    }

    //PUT
    public static void addExercise(Exercise newExercise, final addExerciseCallback callback){
        FirebaseFirestore.getInstance().collection("users")
                .document(Logged.currentUser.getEmail()).collection("exercises")
                .document(newExercise.getRegistered_at().replaceAll("/", "-"))
                .set(newExercise).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    callback.onSuccess();
                }
                else {
                    callback.onFailure();
                }
            }
        });
    }

    public interface addExerciseCallback{
        void onSuccess();
        void onFailure();
    }

    //PUT
    public static void addUser(User newUser, final addUserCallback callback){
        FirebaseFirestore.getInstance().collection("users")
                .document(newUser.getEmail()).set(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    callback.onSuccess();
                }
                else {
                    callback.onFailure();
                }
            }
        });
    }

    public interface addUserCallback{
        void onSuccess();
        void onFailure();
    }


    public static void searchExercises(boolean filter0, boolean filter1, boolean filter2, boolean filter3, final SearchExercisesCallback callback){

        final ArrayList<Exercise> exercicios = new ArrayList<>();
        Query query = FirebaseFirestore.getInstance().collection("users").document(Logged.currentUser.getEmail())
                .collection("exercises").orderBy("registered_at", Query.Direction.ASCENDING);

        if(filter0) {
            query = FirebaseFirestore.getInstance().collection("users").document(Logged.currentUser.getEmail())
                    .collection("exercises").whereEqualTo("exercise_type", "0");
        }
        else if(filter1){
            query = FirebaseFirestore.getInstance().collection("users").document(Logged.currentUser.getEmail())
                    .collection("exercises").whereEqualTo("exercise_type", "1");
        }
        else if(filter2){
            query = FirebaseFirestore.getInstance().collection("users").document(Logged.currentUser.getEmail())
                    .collection("exercises").whereEqualTo("exercise_type", "2");
        }
        else if(filter3){
            query = FirebaseFirestore.getInstance().collection("users").document(Logged.currentUser.getEmail())
                    .collection("exercises").whereEqualTo("exercise_type", "3");
        }

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                List<DocumentChange> documentChanges = value.getDocumentChanges();
                if(documentChanges!=null){
                    for(DocumentChange doc: documentChanges){
                        if(doc.getType() == DocumentChange.Type.ADDED){
                            Exercise exercise = doc.getDocument().toObject(Exercise.class);
                            exercicios.add(exercise);
                        }
                    }
                    callback.onSuccess(exercicios);
                }else{
                    callback.onFailure();
                }
            }
        });
    }

    public interface SearchExercisesCallback {
        void onSuccess(ArrayList<Exercise> exercicios);
        void onFailure();
    }

    public static void deleteExercise(String timestamp){
        FirebaseFirestore.getInstance().collection("users")
                .document(Logged.currentUser.getEmail())
                .collection("exercises")
                .document(timestamp).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("DELETE", "DocumentSnapshot successfully deleted!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("DELETE", "Error deleting document", e);
                    }
                });
    }


    public static void updateProfilePicture(Bitmap bitmap){

        if(bitmap==null)
            return;

        final StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(Logged.currentUser.getEmail()+".jpg");


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        final UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }
        });

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return storageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Logged.currentUser.setPhoto(downloadUri.toString());
                    FirebaseFirestore.getInstance().collection("users")
                            .document(Logged.currentUser.getEmail()).set(Logged.currentUser);
                }
            }
        });

    }



}
