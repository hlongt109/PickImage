package com.longthph30891.pickaimage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.longthph30891.pickaimage.Model.NhanVat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private ArrayList<NhanVat>list = new ArrayList<>();
    AdapterNv adapter;
    Context context = this;
    FloatingActionButton btnAdd;
    ImageView imgNv;
    RecyclerView rcv;
    FirebaseFirestore database;
    private String selectedImageUrl = "";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAdd = findViewById(R.id.btnAdd);
        rcv = findViewById(R.id.rcv);
        //
        database = FirebaseFirestore.getInstance();
        ListenerDb(); // lắng nghe real time
        //
        adapter = new AdapterNv(list,context,database);
        rcv.setLayoutManager(new LinearLayoutManager(context));
        rcv.setAdapter(adapter);
//        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getColor(R.color.blue)));
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenDiaLogAdd();
            }
        });
    }

    private void OpenDiaLogAdd() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = ((Activity)this).getLayoutInflater();
        View view = inflater.inflate(R.layout.them_nv,null);
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.show();
        //
        imgNv = view.findViewById(R.id.imageView);
        FloatingActionButton btnPick = view.findViewById(R.id.btnPickImg);
        EditText edten = view.findViewById(R.id.edTenNv);
        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        //
        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(MainActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }

        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String ten = edten.getText().toString();
               String id = UUID.randomUUID().toString();
               //
                NhanVat nv = new NhanVat(id,ten,selectedImageUrl);
                Log.d("url","url:"+selectedImageUrl);
                HashMap<String,Object> map = nv.convertHashMap();
                //
                database.collection("NhanVat").document(id).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        adapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Thất Bại", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.dismiss();
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();
            if(imgNv != null){
                imgNv.setImageURI(uri);
                selectedImageUrl = uri.toString();// Lưu URL vào biến
            }
        }
    }
    private void ListenerDb(){
        database.collection("NhanVat").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    return;
                }
                if(value != null){
                    for(DocumentChange dc : value.getDocumentChanges()){
                        switch (dc.getType()){
                            case ADDED: // thêm 1 document
                                NhanVat nv = dc.getDocument().toObject(NhanVat.class);
                                list.add(nv);
                                adapter.notifyItemInserted(list.size() - 1);
                                break;
                            case MODIFIED: // update 1 document
                                NhanVat nvUpdate = dc.getDocument().toObject(NhanVat.class);
                                if (dc.getOldIndex() == dc.getNewIndex()) {
                                    list.set(dc.getOldIndex(), nvUpdate);
                                    adapter.notifyItemChanged(dc.getOldIndex());
                                } else {
                                    list.remove(dc.getOldIndex());
                                    list.add(nvUpdate);
                                    adapter.notifyItemMoved(dc.getOldIndex(),dc.getNewIndex());
                                }
                                break;
                            case REMOVED: // xóa 1 document
                                dc.getDocument().toObject(NhanVat.class);
                                list.remove(dc.getOldIndex());
                                adapter.notifyItemRemoved(dc.getOldIndex());
                                break;
                        }
                    }
                }
            }

        });
    }
}