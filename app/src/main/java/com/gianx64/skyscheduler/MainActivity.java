package com.gianx64.skyscheduler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    //Gian code
    PersonDB db;
    PersonAdapter adapter;
    ArrayList<PersonClass> personnel;
    PersonClass person;
    String[] schedule = new String[39];
    ListView personnel_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Gian code
        db = new PersonDB(this);
        personnel = db.readAll();
        adapter = new PersonAdapter(this, personnel, db);
        personnel_list = (ListView) findViewById(R.id.person_list);
        personnel_list.setAdapter(adapter);
        Button add_btn = (Button) findViewById(R.id.add_btn);
        add_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setTitle("Nuevo registro");
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.person);
                dialog.show();
                final EditText name = (EditText) dialog.findViewById(R.id.nombre);
                final EditText scheduleStart = (EditText) dialog.findViewById(R.id.horarioInicio);
                final EditText scheduleEnd = (EditText) dialog.findViewById(R.id.horarioFin);
                Button save = (Button) dialog.findViewById(R.id.save);
                Button cancel = (Button) dialog.findViewById(R.id.cancel);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try{
                            person = new PersonClass(name.getText().toString(), Integer.valueOf(scheduleStart.getText().toString()), Integer.valueOf(scheduleEnd.getText().toString()));
                            db.insert(person);
                        } catch (Exception e) {
                            Toast.makeText(getApplication(), "ERROR", Toast.LENGTH_SHORT).show();
                        }
                        personnel = db.readAll();
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
        final TextView[] textViews = {
                findViewById(R.id.textView0),
                findViewById(R.id.textView1),
                findViewById(R.id.textView2),
                findViewById(R.id.textView3),
                findViewById(R.id.textView4),
                findViewById(R.id.textView5),
                findViewById(R.id.textView6),
                findViewById(R.id.textView7),
                findViewById(R.id.textView8),
                findViewById(R.id.textView9),
                findViewById(R.id.textView10),
                findViewById(R.id.textView11),
                findViewById(R.id.textView12),
                findViewById(R.id.textView13),
                findViewById(R.id.textView14),
                findViewById(R.id.textView15),
                findViewById(R.id.textView16),
                findViewById(R.id.textView17),
                findViewById(R.id.textView18),
                findViewById(R.id.textView19),
                findViewById(R.id.textView20),
                findViewById(R.id.textView21),
                findViewById(R.id.textView22)
        };
        Button generate_btn = (Button) findViewById(R.id.generate_btn);
        generate_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                for (PersonClass person : personnel) person.setLoad(0);
                for (int i=0; i<schedule.length; i++) schedule[i] = "-";
                fillSchedule(schedule);
                fillTours(schedule, personnel);

                for (int i=0; i<16; i++) textViews[i].setText(schedule[i*2]+" - "+schedule[(i*2)+1]);
                for (int i=16; i<textViews.length; i++) textViews[i].setText(schedule[i+16]);
                adapter.notifyDataSetChanged();
                //for (PersonClass person : personnel){ Log.d("info", "ID: "+person.getId()+", Name: "+person.getName()+", Schedule: "+person.getScheduleStart()+" - "+person.getScheduleEnd()+", Load: "+String.valueOf(person.getLoad())); };
            }
        });
    }

    public void showInfo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Información del desarrollador");
        builder.setMessage("Desarrollado por:\nGiancarlo Anfossy Araneda\n\nContacto: +56 9 8578 2508\ngiancarlo.anfossy@gmail.com");
        builder.show();
    }

    public void fillSchedule(String[] schedule){
        for (int i=0; i<schedule.length-7; i++) { schedule[i] = getSingle(personnel); }
    }

    public void fillTours(String[] schedule, ArrayList<PersonClass> personnel){
        Random random = new Random();
        int chosen;
        if (personnel.size() < 8)
            for (PersonClass person : personnel)
                while (true){
                    chosen = random.nextInt(7);
                    if (schedule[32+chosen] == "-") {
                        if (chosen == 0) {
                            if (person.getScheduleStart() >= 1100) {
                                schedule[32 + chosen] = person.getName();
                                person.setLoad(person.getLoad() + 1);
                                break;
                            }
                        }
                        else {
                            schedule[32+chosen] = person.getName();
                            person.setLoad(person.getLoad()+1);
                            break;
                        }
                    }
                }
        else {
            boolean[] picked = new boolean[personnel.size()];
            for (int i = 0; i < personnel.size(); i++) picked[i] = false;
            int loadmin = personnel.get(0).getLoad();
            for (int i=0; i<personnel.size(); i++)
                if (personnel.get(i).getLoad() < loadmin)
                    loadmin = personnel.get(i).getLoad();
            int ammount = 0;
            for (int i=0; i<personnel.size(); i++)
                if (personnel.get(i).getLoad() == loadmin)
                    ammount++;
            for (int i = 0; i < 7; i++){
                while (true){
                    chosen = random.nextInt(personnel.size());
                    if (ammount > 0) {
                        if (personnel.get(chosen).getLoad() == loadmin && !picked[chosen]) {
                            if (i == 0) {
                                if (personnel.get(chosen).getScheduleStart() >= 1100) {
                                    personnel.get(chosen).setLoad(personnel.get(chosen).getLoad() + 1);
                                    schedule[32 + i] = personnel.get(chosen).getName();
                                    picked[chosen] = true;
                                    ammount--;
                                    break;
                                }
                            } else {
                                personnel.get(chosen).setLoad(personnel.get(chosen).getLoad() + 1);
                                schedule[32 + i] = personnel.get(chosen).getName();
                                picked[chosen] = true;
                                ammount--;
                                break;
                            }
                        }
                    }
                    else
                        if (!picked[chosen]){
                            personnel.get(chosen).setLoad(personnel.get(chosen).getLoad() + 1);
                            schedule[32+i] = personnel.get(chosen).getName();
                            picked[chosen] = true;
                            break;
                        }
                }
            }
        }
    }

    public String getSingle(ArrayList<PersonClass> personnel){
        if (personnel.size() == 0)
            return "Error.";
        Random random = new Random();
        int chosen;
        int loadmin = personnel.get(0).getLoad();
        for (int i=0; i<personnel.size(); i++)
            if (personnel.get(i).getLoad() < loadmin)
                loadmin = personnel.get(i).getLoad();
        while (true){
            chosen = random.nextInt(personnel.size());
            if (personnel.get(chosen).getLoad() == loadmin){
                personnel.get(chosen).setLoad(personnel.get(chosen).getLoad() + 1);
                break;
            }
        }
        return personnel.get(chosen).getName();
    }

    public String getCouple(ArrayList<PersonClass> personnel){
        if (personnel.size() == 0)
            return "Error.";
        Random random = new Random();
        int[] chosen = new int[2];
        int loadmin = personnel.get(0).getLoad();
        int candidates = 0;
        for (int i=0; i<personnel.size(); i++)
            if (personnel.get(i).getLoad() < loadmin)
                loadmin = personnel.get(i).getLoad();
        for (int i=0; i<personnel.size(); i++)
            if (personnel.get(i).getLoad() == loadmin)
                candidates++;
        while (true){
            chosen[0] = random.nextInt(personnel.size());
            chosen[1] = random.nextInt(personnel.size());
            if (chosen[0] != chosen[1])
                if (candidates == 1)
                    if (personnel.get(chosen[0]).getLoad() < loadmin+1 || personnel.get(chosen[1]).getLoad() < loadmin+1){
                        personnel.get(chosen[0]).setLoad(personnel.get(chosen[0]).getLoad() + 1);
                        personnel.get(chosen[1]).setLoad(personnel.get(chosen[1]).getLoad() + 1);
                        break;
                    }
                else
                    if (personnel.get(chosen[0]).getLoad() < loadmin+1)
                        if (personnel.get(chosen[1]).getLoad() < loadmin+1){
                            personnel.get(chosen[0]).setLoad(personnel.get(chosen[0]).getLoad() + 1);
                            personnel.get(chosen[1]).setLoad(personnel.get(chosen[1]).getLoad() + 1);
                            break;
                        }
        }
        return personnel.get(chosen[0]).getName() + " - " + personnel.get(chosen[1]).getName();
    }
}