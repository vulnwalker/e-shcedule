package com.eschedule.eclient.settings;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.eschedule.eclient.App;
import com.eschedule.eclient.R;
import com.eschedule.eclient.database.DB;

/**
 * A simple {@link Fragment} subclass.
 */
public class PengaturanFragment extends Fragment {
    DB dbHelper;
    Context context;
   MediaPlayer mp ;
    App app2;
    public PengaturanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        View v =inflater.inflate(R.layout.fragment_pengaturan, container, false);
        getActivity().findViewById(R.id.add_info).setVisibility(View.INVISIBLE);
        RadioButton sound = (RadioButton) v.findViewById(R.id.sound);
        RadioButton sound2 = (RadioButton) v.findViewById(R.id.sound2);
        RadioButton sound3 = (RadioButton) v.findViewById(R.id.sound3);
        RadioButton sound4 = (RadioButton) v.findViewById(R.id.sound4);
        RadioButton sound5 = (RadioButton) v.findViewById(R.id.sound5);
        RadioButton sound6 = (RadioButton) v.findViewById(R.id.sound5);
        app2 = App.getInstance();
        if (app2.getDB().getUser().suara == null || app2.getDB().getUser().suara.equals("default")) {
            sound.setChecked(true);
        }else if (app2.getDB().getUser().suara.equals("sound1")){
            sound2.setChecked(true);
        }else if (app2.getDB().getUser().suara.equals("sound2")) {
            sound3.setChecked(true);
        }else if (app2.getDB().getUser().suara.equals("sound3")) {
            sound4.setChecked(true);
        }else if (app2.getDB().getUser().suara.equals("sound4")) {
            sound5.setChecked(true);
        }else if (app2.getDB().getUser().suara.equals("sound5")){
            sound6.setChecked(true);
        }
        RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb=(RadioButton) container.findViewById(checkedId);
               if (rb.getText().toString().equals("Car Alarm")){
                    stopPlaying();
                    mp = MediaPlayer.create(getActivity(), R.raw.sound);
                    mp.start();
                   set_sound("default");
               } else if (rb.getText().toString().equals("Pixie Dust")){
                   stopPlaying();
                   mp = MediaPlayer.create(getActivity(), R.raw.sound1);
                   mp.start();
                   set_sound("sound1");
               } else if (rb.getText().toString().equals("On The Hunt")){
                   stopPlaying();
                   mp = MediaPlayer.create(getActivity(), R.raw.sound2);
                   mp.start();
                   set_sound("sound2");
               } else if (rb.getText().toString().equals("BBM")){
                   stopPlaying();
                   mp = MediaPlayer.create(getActivity(), R.raw.sound3);
                   mp.start();
                   set_sound("sound3");
               } else if (rb.getText().toString().equals("Bird Loop")){
                   stopPlaying();
                   mp = MediaPlayer.create(getActivity(), R.raw.sound4);
                   if (mp.isPlaying()){
                       mp.stop();
                   }
                   mp.start();
                   set_sound("sound4");
               } else if (rb.getText().toString().equals("Piezo Alarm")){
                   stopPlaying();
                   mp = MediaPlayer.create(getActivity(), R.raw.sound5);
                   mp.start();
                   set_sound("sound5");
               }
            }
        });


        return v;

    }

    private void set_sound(String soundnya) {
        dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("update mob_user set suara = '" + soundnya + "'");
    }
    private void stopPlaying() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

}
