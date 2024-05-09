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
import java.util.Arrays;
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
        personnel_list = findViewById(R.id.person_list);
        personnel_list.setAdapter(adapter);
        Button add_btn = findViewById(R.id.add_btn);
        add_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setTitle("Nuevo registro");
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.person);
                dialog.show();
                final EditText name = dialog.findViewById(R.id.nombre);
                final EditText scheduleStart = dialog.findViewById(R.id.horarioInicio);
                final EditText scheduleEnd = dialog.findViewById(R.id.horarioFin);
                Button save = dialog.findViewById(R.id.save);
                Button cancel = dialog.findViewById(R.id.cancel);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try{
                            person = new PersonClass(name.getText().toString(), Integer.parseInt(scheduleStart.getText().toString()), Integer.parseInt(scheduleEnd.getText().toString()));
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
        Button generate_btn = findViewById(R.id.generate_btn);
        generate_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                for (PersonClass person : personnel) person.setLoad(0);
                Arrays.fill(schedule, "-");
                fillSchedule(schedule);
                fillTours(schedule, personnel);

                for (int i=0; i<16; i++) {
                    if (!schedule[i * 2].equals(schedule[(i * 2) + 1]))
                        textViews[i].setText(schedule[i * 2] + " - " + schedule[(i * 2) + 1]);
                    else
                        textViews[i].setText(schedule[i*2]);
                }
                for (int i=16; i<textViews.length; i++) textViews[i].setText(schedule[i+16]);
                adapter.notifyDataSetChanged();
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
        Random random = new Random();
        int chosen;
        //TODO: hacer contadores de cuantos entran antes de las 9, de las 10, de las 11
        for (int i=0; i<4; i++) {   //Llenar elevador principal hasta las 12:00
            if (personnel.size() == 0)
                schedule[i] = "Sin personal.";
            while (true){
                chosen = random.nextInt(personnel.size());
                if (i < 2) {
                    if (i == 1) {
                        if (!schedule[i - 1].equals(personnel.get(chosen).getName()) && personnel.get(chosen).getScheduleStart() <= 1000) {
                            personnel.get(chosen).setLoad(personnel.get(chosen).getLoad() + 1);
                            schedule[i] = personnel.get(chosen).getName();
                            break;
                        }
                    }
                    else
                    if (personnel.get(chosen).getScheduleStart() <= 1000) {
                        personnel.get(chosen).setLoad(personnel.get(chosen).getLoad() + 1);
                        schedule[i] = personnel.get(chosen).getName();
                        break;
                    }
                }
                else
                if (!schedule[i - 1].equals(personnel.get(chosen).getName()) && personnel.get(chosen).getScheduleStart() <= 1100) {
                    personnel.get(chosen).setLoad(personnel.get(chosen).getLoad() + 1);
                    schedule[i] = personnel.get(chosen).getName();
                    break;
                }
            }
        }
        //Log.d("info", "Elevador principal lleno hasta las 12:00");
        //TODO: hacer contadores de cuantos salen despues de las 20, de las 21 y de las 22
        for (int i=18; i<schedule.length-7; i++) {
            if (personnel.size() == 0)
                schedule[i] = "Sin personal.";
            while (true){
                chosen = random.nextInt(personnel.size());
                if (i < 24) {   //Llenar elevador principal desde las 20:00
                    if (i == 18 || i == 19) {
                        if (personnel.get(chosen).getScheduleEnd() >= 2000)
                            if (i == 18) {
                                personnel.get(chosen).setLoad(personnel.get(chosen).getLoad() + 1);
                                schedule[i] = personnel.get(chosen).getName();
                                break;
                            }
                            else
                            if (!schedule[i - 1].equals(personnel.get(chosen).getName())) {
                                personnel.get(chosen).setLoad(personnel.get(chosen).getLoad() + 1);
                                schedule[i] = personnel.get(chosen).getName();
                                break;
                            }
                    }
                    else
                    if (i == 20 || i == 21) {
                        if (!schedule[i - 1].equals(personnel.get(chosen).getName()) && personnel.get(chosen).getScheduleEnd() >= 2100) {
                            personnel.get(chosen).setLoad(personnel.get(chosen).getLoad() + 1);
                            schedule[i] = personnel.get(chosen).getName();
                            break;
                        }
                    }
                    else
                    if (!schedule[i - 1].equals(personnel.get(chosen).getName()) && personnel.get(chosen).getScheduleEnd() >= 2200) {
                        personnel.get(chosen).setLoad(personnel.get(chosen).getLoad() + 1);
                        schedule[i] = personnel.get(chosen).getName();
                        break;
                    }
                }
                else    //Llenar elevador de apoyo
                if (i == 24 || i == 25) {
                    if (!schedule[i - 1].equals(personnel.get(chosen).getName())) {
                        personnel.get(chosen).setLoad(personnel.get(chosen).getLoad() + 1);
                        schedule[i] = personnel.get(chosen).getName();
                        break;
                    }
                }
                else
                if (i == 26 || i == 27) {
                    if (!schedule[i - 1].equals(personnel.get(chosen).getName())) {
                        personnel.get(chosen).setLoad(personnel.get(chosen).getLoad() + 1);
                        schedule[i] = personnel.get(chosen).getName();
                        break;
                    }
                }
                else
                if (i == 28 || i == 29) {
                    if (!schedule[18].equals(personnel.get(chosen).getName()) && !schedule[19].equals(personnel.get(chosen).getName()) && !schedule[i - 1].equals(personnel.get(chosen).getName())) {
                        personnel.get(chosen).setLoad(personnel.get(chosen).getLoad() + 1);
                        schedule[i] = personnel.get(chosen).getName();
                        break;
                    }
                }
                else
                if (i == 30 || i == 31)
                    if (!schedule[20].equals(personnel.get(chosen).getName()) && !schedule[21].equals(personnel.get(chosen).getName()) && !schedule[i - 1].equals(personnel.get(chosen).getName())) {
                        personnel.get(chosen).setLoad(personnel.get(chosen).getLoad() + 1);
                        schedule[i] = personnel.get(chosen).getName();
                        break;
                    }
            }
        }
        //Log.d("info", "Elevador secundario lleno y elevador principal llenado desde las 20:00");
        int retries = 0;
        for (int i=17; i>3; i--) {  //Llenar elevador principal desde las 12:00 hasta las 20:00
            if (retries > 22)
                break;
            int loadmin = personnel.get(0).getLoad();
            for (int j = 0; j < personnel.size(); j++)
                if (personnel.get(j).getLoad() < loadmin)
                    loadmin = personnel.get(j).getLoad();
            while (true) {
                if (retries > 22)
                    break;
                chosen = random.nextInt(personnel.size());
                if (i == 17 || i == 16) {
                    if (!schedule[27].equals(personnel.get(chosen).getName()) && !schedule[26].equals(personnel.get(chosen).getName()) && !schedule[i + 1].equals(personnel.get(chosen).getName())) {
                        personnel.get(chosen).setLoad(personnel.get(chosen).getLoad() + 1);
                        schedule[i] = personnel.get(chosen).getName();
                        break;
                    }
                }
                else
                if (i == 15 || i == 14) {
                    if (!schedule[25].equals(personnel.get(chosen).getName()) && !schedule[24].equals(personnel.get(chosen).getName()) && !schedule[i + 1].equals(personnel.get(chosen).getName())) {
                        personnel.get(chosen).setLoad(personnel.get(chosen).getLoad() + 1);
                        schedule[i] = personnel.get(chosen).getName();
                        break;
                    }
                }
                else
                //Problema: posible loop infinito parcheado
                if (personnel.get(chosen).getLoad() <= loadmin) {
                    retries++;
                    if (!schedule[i + 1].equals(personnel.get(chosen).getName())) {
                        personnel.get(chosen).setLoad(personnel.get(chosen).getLoad() + 1);
                        schedule[i] = personnel.get(chosen).getName();
                        break;
                    }
                    else {
                        i++;
                        for (PersonClass person : personnel)
                            if (person.getName().equals(schedule[i]))
                                person.setLoad(person.getLoad() - 1);
                        schedule[i] = "-";
                    }
                }
            }
        }
        //En el caso que se cumpla el loop infinito, se deshaga el horario actual y se haga otro nuevo
        if (retries > 22)
            fillSchedule(schedule);
        Log.d("info", "fillSchedule finalizado. Reintentos: "+retries);
    }

    public void fillTours(String[] schedule, ArrayList<PersonClass> personnel){
        Random random = new Random();
        int chosen;
        if (personnel.size() < 8) {
            if (personnel.size() == 0)
                for (int i = 32; i < schedule.length; i++)
                    schedule[i] = "Sin personal.";
            for (PersonClass person : personnel)
                while (true) {
                    chosen = random.nextInt(7);
                    if (schedule[32 + chosen].equals("-")) {
                        if (chosen == 0) {
                            if (person.getScheduleStart() >= 1100) {
                                schedule[32 + chosen] = person.getName();
                                person.setLoad(person.getLoad() + 1);
                                break;
                            }
                        } else {
                            schedule[32 + chosen] = person.getName();
                            person.setLoad(person.getLoad() + 1);
                            break;
                        }
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
}