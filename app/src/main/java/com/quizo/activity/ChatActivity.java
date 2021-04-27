package com.quizo.activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.jaeger.library.StatusBarUtil;
import com.quizo.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
MaterialCardView lc1,lc2,rc1,rc2,rc3;
    TextView lb1,lb2,rb1,rb2,rb3;
Vibrator v;
    EditText editText;
    ExtendedFloatingActionButton addBtn;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    final public String ID = Objects.requireNonNull(currentUser).getDisplayName();
    final public String IDs = Objects.requireNonNull(Objects.requireNonNull(currentUser).getDisplayName()).replaceAll("[^a-zA-Z0-9]", "");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        lc1 = findViewById(R.id.lc1);
        lb1 = findViewById(R.id.lb1);
        lc2 = findViewById(R.id.lc2);
        lb2 = findViewById(R.id.lb2);
        rc1 = findViewById(R.id.rc1);
        rb1 = findViewById(R.id.rb1);
        rc2 = findViewById(R.id.rc2);
        rb2 = findViewById(R.id.rb2);
        rc3 = findViewById(R.id.rc3);
        rb3 = findViewById(R.id.rb3);

        StatusBarUtil.setTransparent(this);
        editText = findViewById(R.id.editText);
        addBtn = findViewById(R.id.addBtn);
        FirebaseDatabase firedb = FirebaseDatabase.getInstance();
        final DatabaseReference mRef = firedb.getReference("users");
        rc1.setVisibility(View.VISIBLE);
        rb1.setWidth(650);
        Vib();
        rb1.setText("Welcome " + ID+", To complete profile configuration you are just one step away.Please answer to the questions to complete profile setup.\n \nWhat is your nickname?");
        Animation fadein = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in);
        rc1.startAnimation(fadein);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.getText().toString().equals("")){
                    Toast.makeText(ChatActivity.this,"Please enter something",Toast.LENGTH_SHORT).show();
                }else{
lc1.setVisibility(View.VISIBLE);
lb1.setText(editText.getText().toString());
                    assert IDs != null;
                    mRef.child(IDs).child("message").setValue(editText.getText().toString());
                    Map map = new HashMap();
                    map.put("timestamp", ServerValue.TIMESTAMP);
                    mRef.child(IDs).updateChildren(map);
                    editText.setText("");
                    rc2.setVisibility(View.VISIBLE);
                    rb2.setMaxWidth(650);
                    Vib();
                    rb2.setText("What is your deepest desire? We will not disclose it anywhere(dont be personal)!");
                    Animation fadein = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in);
                    rc2.startAnimation(fadein);
                            addBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(editText.getText().toString().equals("")){
                                        Toast.makeText(ChatActivity.this,"Please enter something",Toast.LENGTH_SHORT).show();
                                    }else{


                                        lc2.setVisibility(View.VISIBLE);
                                        lb2.setText(editText.getText().toString());
                                        assert IDs != null;
                                        mRef.child(IDs).child("desire").setValue(editText.getText().toString());
                                        Map map = new HashMap();
                                        map.put("timestamp", ServerValue.TIMESTAMP);
                                        mRef.child("desire").updateChildren(map);
                                        editText.setText("");
                                        rc3.setVisibility(View.VISIBLE);
                                        rb3.setMaxWidth(650);
                                        getV();
                                        Animation fadein = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in);
                                        rc3.startAnimation(fadein);
                                        new CountDownTimer(1000,1000){

                                            @Override
                                            public void onTick(long l) {

                                            }

                                            @Override
                                            public void onFinish() {
                                                Intent intent = new Intent(ChatActivity.this,LoggedIn.class);
                                                ChatActivity.this.startActivity(intent);
                                                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                                                finish();
                                            }
                                        }.start();

                                    }
                                }
                            });
                }
            }
        });

    }

    public void getV(){
        FirebaseDatabase firedb = FirebaseDatabase.getInstance();
        final DatabaseReference mRef = firedb.getReference("users");
        mRef.child(IDs).child("desire").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              final String value = dataSnapshot.getValue(String.class);
              Vib();
                rb3.setText("Thank you for your support and patience.");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void Vib(){
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(50);
        }

    }

}