package com.gianx64.skyscheduler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
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
                        try {
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
                Arrays.fill(schedule, "-");
                fillSchedule(schedule, personnel);
                if (personnel.size() > 0)
                    for (PersonClass person : personnel)
                        person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_info:
                AlertDialog.Builder info = new AlertDialog.Builder(MainActivity.this);
                info.setCancelable(true);
                info.setTitle("Información del desarrollador");
                info.setMessage("Desarrollado por:\nGiancarlo Anfossy Araneda\n\nContacto: +56 9 8578 2508\ngiancarlo.anfossy@gmail.com");
                info.show();
                break;
            case R.id.action_use:
                AlertDialog.Builder use = new AlertDialog.Builder(MainActivity.this);
                use.setCancelable(true);
                use.setTitle("Diccionario");
                use.setMessage("Horario: escrito en formato militar.\n(930 = 0930 = 9:30 AM)\n\nCarga: cantidad de espacios asignados en el horario.\n1 elevador acompañado = 1 carga, 1 elevador solo = 2 cargas, 1 tour = 1 carga.");
                use.show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void fillSchedule(String[] schedule, ArrayList<PersonClass> personnel){
        Random random = new Random();
        int chosen;
        int[] present = new int[5];
        if (personnel.size() > 0)
            for (PersonClass person : personnel) {
                person.setLoad(0);
                if (person.getScheduleStart() <= 1000)
                    present[0]++;   //Anfitriones que entran antes de las 10:00
                if (person.getScheduleStart() <= 1100)
                    present[1]++;   //Anfitriones que entran antes de las 11:00
                if (person.getScheduleEnd() >= 2200)
                    present[4]++;   //Anfitriones que a las 22:00
                if (person.getScheduleEnd() >= 2100)
                    present[3]++;   //Anfitriones que salen a las 21:00
                if (person.getScheduleEnd() >= 2000)
                    present[2]++;   //Anfitriones que salen a las 20:00
            }
        for (int i=0; i<4; i++) {   //Llenar elevador principal hasta las 12:00
            if (personnel.size() == 0)
                schedule[i] = "Sin personal.";
            else
            while (true){
                chosen = random.nextInt(personnel.size());
                if (i < 2) {
                    if (present[0] == 0) {
                        schedule[i] = "Sin personal.";
                        break;
                    }
                    else
                    if (personnel.get(chosen).getScheduleStart() <= 1000){
                        if (i == 1) {
                            if (present[0] == 1) {
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
                        else {
                            personnel.get(chosen).setLoad(personnel.get(chosen).getLoad() + 1);
                            schedule[i] = personnel.get(chosen).getName();
                            break;
                        }
                    }
                }
                else
                if (present[1] == 0) {
                    schedule[i] = "Sin personal.";
                    break;
                }
                else
                if (personnel.get(chosen).getScheduleStart() <= 1100) {
                    if (present[1] == 1) {
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
            }
        }
        //Log.d("info", "Elevador principal lleno hasta las 12:00");
        for (int i=18; i<24; i++) { //Llenar elevador principal desde las 20:00
            if (personnel.size() == 0)
                schedule[i] = "Sin personal.";
            else
            while (true){
                chosen = random.nextInt(personnel.size());
                if (i == 18 || i == 19) {
                    if (present[2] == 0) {
                        schedule[i] = "Sin personal.";
                        break;
                    }
                    else
                    if (personnel.get(chosen).getScheduleEnd() >= 2000)
                        if (present[2] == 1 || i == 18) {
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
                    if (present[3] == 0) {
                        schedule[i] = "Sin personal.";
                        break;
                    }
                    else
                    if (personnel.get(chosen).getScheduleEnd() >= 2100) {
                        if (present[3] == 1) {
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
                }
                else
                if (present[4] == 0) {
                    schedule[i] = "Sin personal.";
                    break;
                }
                else
                if (personnel.get(chosen).getScheduleEnd() >= 2200) {
                    if (present[4] == 1) {
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
            }
        }
        //Log.d("info", "Elevador principal llenado desde las 20:00");
        int tries = 0;
        for (int i=17; i>3; i--) {  //Llenar elevador principal desde las 12:00 hasta las 20:00
            if (tries > 22)
                break;
            if (personnel.size() == 0)
                schedule[i] = "Sin personal.";
            else
            if (personnel.size() == 1) {
                personnel.get(0).setLoad(personnel.get(0).getLoad() + 1);
                schedule[i] = personnel.get(0).getName();
            }
            else
            if (personnel.size() == 2) {
                personnel.get(i%2).setLoad(personnel.get(i%2).getLoad() + 1);
                schedule[i] = personnel.get(i%2).getName();
            }
            else {
                int loadmin = personnel.get(0).getLoad();
                for (int j = 0; j < personnel.size(); j++)
                    if (personnel.get(j).getLoad() < loadmin)
                        loadmin = personnel.get(j).getLoad();
                while (true) {
                    if (tries > 22)
                        break;
                    chosen = random.nextInt(personnel.size());
                    if (i == 17 || i == 16) {
                        if (!schedule[i + 1].equals(personnel.get(chosen).getName())) {
                            personnel.get(chosen).setLoad(personnel.get(chosen).getLoad() + 1);
                            schedule[i] = personnel.get(chosen).getName();
                            break;
                        }
                    }
                    else
                    if (i == 15 || i == 14) {
                        if (!schedule[i + 1].equals(personnel.get(chosen).getName())) {
                            personnel.get(chosen).setLoad(personnel.get(chosen).getLoad() + 1);
                            schedule[i] = personnel.get(chosen).getName();
                            break;
                        }
                    }
                    else
                    //Problema: posible loop infinito (mal parcheado) (resta mal la carga)
                    if (personnel.get(chosen).getLoad() <= loadmin) {
                        tries++;
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
                            i++;
                        }
                    }
                }
            }
        }
        //Log.d("info", "Elevador principal lleno desde las 12:00 hasta las 20:00");
        //En el caso que se cumpla el loop infinito, se deshaga el horario actual y se haga otro nuevo
        if (tries > 22)
            fillSchedule(schedule, personnel);
        for (int i=24; i<schedule.length-7; i++) {  //Llenar elevador de apoyo
            //Log.d("info", "i = "+i);
            if (personnel.size() < 3)
                schedule[i] = "Sin personal.";
            else
            while (true) {
                chosen = random.nextInt(personnel.size());
                if (i == 24 || i == 25) {
                    if (!schedule[14].equals(personnel.get(chosen).getName()) && !schedule[15].equals(personnel.get(chosen).getName())) {
                        if (personnel.size() == 3) {
                            personnel.get(chosen).setLoad(personnel.get(chosen).getLoad() + 1);
                            schedule[i] = personnel.get(chosen).getName();
                            break;
                        } else if (!schedule[i - 1].equals(personnel.get(chosen).getName())) {
                            personnel.get(chosen).setLoad(personnel.get(chosen).getLoad() + 1);
                            schedule[i] = personnel.get(chosen).getName();
                            break;
                        }
                    }
                }
                else
                if (i == 26 || i == 27) {
                    if (!schedule[16].equals(personnel.get(chosen).getName()) && !schedule[17].equals(personnel.get(chosen).getName()))
                        if (personnel.size() == 3) {
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
                if (i == 28 || i == 29) {
                    if (present[2] < 3) {
                        schedule[i] = "Sin personal.";
                        break;
                    }
                    else
                    if (personnel.get(chosen).getScheduleEnd() >= 2000)
                        if (!schedule[18].equals(personnel.get(chosen).getName()) && !schedule[19].equals(personnel.get(chosen).getName())) {
                            if (present[2] == 3) {
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
                }
                else
                if (i == 30 || i == 31)
                    if (present[3] < 3) {
                        schedule[i] = "Sin personal.";
                        break;
                    }
                    else
                    if (personnel.get(chosen).getScheduleEnd() >= 2100)
                        if (!schedule[20].equals(personnel.get(chosen).getName()) && !schedule[21].equals(personnel.get(chosen).getName())) {
                            if (present[3] == 3) {
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
            }
        }
        //Log.d("info", "Elevador de apoyo lleno");
    }

    public void fillTours(String[] schedule, ArrayList<PersonClass> personnel){
        Random random = new Random();
        int chosen, tries;
        if (personnel.size() < 8) {
            if (personnel.size() == 0)
                for (int i = 32; i < schedule.length; i++)
                    schedule[i] = "Sin personal.";
            for (PersonClass person : personnel) {
                tries = 0;
                while (true) {
                    chosen = random.nextInt(7);
                    if (schedule[32 + chosen].equals("-")) {
                        switch (chosen) {
                            case 0:
                                if (person.getScheduleStart() <= 1100 && !person.getName().equals(schedule[2]) && !person.getName().equals(schedule[3]))
                                    schedule[32] = person.getName();
                                tries++;
                                break;
                            case 1:
                                if (!person.getName().equals(schedule[4]) && !person.getName().equals(schedule[5]))
                                    schedule[32 + chosen] = person.getName();
                                tries++;
                                break;
                            case 2:
                                if (!person.getName().equals(schedule[6]) && !person.getName().equals(schedule[7]))
                                    schedule[32 + chosen] = person.getName();
                                tries++;
                                break;
                            case 3:
                                if (!person.getName().equals(schedule[8]) && !person.getName().equals(schedule[9]))
                                    schedule[32 + chosen] = person.getName();
                                tries++;
                                break;
                            case 4:
                                if (!person.getName().equals(schedule[10]) && !person.getName().equals(schedule[11]))
                                    schedule[32 + chosen] = person.getName();
                                tries++;
                                break;
                            case 5:
                                if (!person.getName().equals(schedule[12]) && !person.getName().equals(schedule[13]))
                                    schedule[32 + chosen] = person.getName();
                                tries++;
                                break;
                            case 6:
                                if (!person.getName().equals(schedule[14]) && !person.getName().equals(schedule[15]) && !person.getName().equals(schedule[24]) && !person.getName().equals(schedule[25]))
                                    schedule[32 + chosen] = person.getName();
                                tries++;
                                break;
                        }
                        if (!schedule[32 + chosen].equals("-") || tries > 6) break;
                    }
                }
            }
        }
        else {
            boolean[] picked = new boolean[personnel.size()];
            for (int i = 0; i < personnel.size(); i++) picked[i] = false;
            int loadmax = personnel.get(0).getLoad();
            for (int i=0; i<personnel.size(); i++)
                if (personnel.get(i).getLoad() > loadmax)
                    loadmax = personnel.get(i).getLoad();
            int ammount = 0;
            for (int i=0; i<personnel.size(); i++)
                if (personnel.get(i).getLoad() < loadmax)
                    ammount++;
            for (int i = 0; i < 7; i++) {
                tries = 0;
                while (true){
                    chosen = random.nextInt(personnel.size());
                    if (ammount > 0) {
                        if (personnel.get(chosen).getLoad() < loadmax && !picked[chosen]) {
                            switch (i) {
                                case 0:
                                    if (personnel.get(chosen).getScheduleStart() <= 1100 && !personnel.get(chosen).getName().equals(schedule[2]) && !personnel.get(chosen).getName().equals(schedule[3])) {
                                        schedule[32 + i] = personnel.get(chosen).getName();
                                        picked[chosen] = true;
                                        ammount--;
                                    }
                                    tries++;
                                    break;
                                case 1:
                                    if (!personnel.get(chosen).getName().equals(schedule[4]) && !personnel.get(chosen).getName().equals(schedule[5])) {
                                        schedule[32 + i] = personnel.get(chosen).getName();
                                        picked[chosen] = true;
                                        ammount--;
                                    }
                                    tries++;
                                    break;
                                case 2:
                                    if (!personnel.get(chosen).getName().equals(schedule[6]) && !personnel.get(chosen).getName().equals(schedule[7])) {
                                        schedule[32 + i] = personnel.get(chosen).getName();
                                        picked[chosen] = true;
                                        ammount--;
                                    }
                                    tries++;
                                    break;
                                case 3:
                                    if (!personnel.get(chosen).getName().equals(schedule[8]) && !personnel.get(chosen).getName().equals(schedule[9])) {
                                        schedule[32 + i] = personnel.get(chosen).getName();
                                        picked[chosen] = true;
                                        ammount--;
                                    }
                                    tries++;
                                    break;
                                case 4:
                                    if (!personnel.get(chosen).getName().equals(schedule[10]) && !personnel.get(chosen).getName().equals(schedule[11])) {
                                        schedule[32 + i] = personnel.get(chosen).getName();
                                        picked[chosen] = true;
                                        ammount--;
                                    }
                                    tries++;
                                    break;
                                case 5:
                                    if (!personnel.get(chosen).getName().equals(schedule[12]) && !personnel.get(chosen).getName().equals(schedule[13])) {
                                        schedule[32 + i] = personnel.get(chosen).getName();
                                        picked[chosen] = true;
                                        ammount--;
                                    }
                                    tries++;
                                    break;
                                case 6:
                                    if (!personnel.get(chosen).getName().equals(schedule[14]) && !personnel.get(chosen).getName().equals(schedule[15]) && !personnel.get(chosen).getName().equals(schedule[24]) && !personnel.get(chosen).getName().equals(schedule[25])) {
                                        schedule[32 + i] = personnel.get(chosen).getName();
                                        picked[chosen] = true;
                                        ammount--;
                                    }
                                    tries++;
                                    break;
                            }
                            if (!schedule[32 + i].equals("-") || tries > 6) break;
                        }
                    }
                    else
                        if (!picked[chosen]) {
                            switch (i) {
                                case 0:
                                    if (personnel.get(chosen).getScheduleStart() <= 1100 && !personnel.get(chosen).getName().equals(schedule[2]) && !personnel.get(chosen).getName().equals(schedule[3])) {
                                        schedule[32 + i] = personnel.get(chosen).getName();
                                        picked[chosen] = true;
                                    }
                                    tries++;
                                    break;
                                case 1:
                                    if (!personnel.get(chosen).getName().equals(schedule[4]) && !personnel.get(chosen).getName().equals(schedule[5])) {
                                        schedule[32 + i] = personnel.get(chosen).getName();
                                        picked[chosen] = true;
                                    }
                                    tries++;
                                    break;
                                case 2:
                                    if (!personnel.get(chosen).getName().equals(schedule[6]) && !personnel.get(chosen).getName().equals(schedule[7])) {
                                        schedule[32 + i] = personnel.get(chosen).getName();
                                        picked[chosen] = true;
                                    }
                                    tries++;
                                    break;
                                case 3:
                                    if (!personnel.get(chosen).getName().equals(schedule[8]) && !personnel.get(chosen).getName().equals(schedule[9])) {
                                        schedule[32 + i] = personnel.get(chosen).getName();
                                        picked[chosen] = true;
                                    }
                                    tries++;
                                    break;
                                case 4:
                                    if (!personnel.get(chosen).getName().equals(schedule[10]) && !personnel.get(chosen).getName().equals(schedule[11])) {
                                        schedule[32 + i] = personnel.get(chosen).getName();
                                        picked[chosen] = true;
                                    }
                                    tries++;
                                    break;
                                case 5:
                                    if (!personnel.get(chosen).getName().equals(schedule[12]) && !personnel.get(chosen).getName().equals(schedule[13])) {
                                        schedule[32 + i] = personnel.get(chosen).getName();
                                        picked[chosen] = true;
                                    }
                                    tries++;
                                    break;
                                case 6:
                                    if (!personnel.get(chosen).getName().equals(schedule[14]) && !personnel.get(chosen).getName().equals(schedule[15]) && !personnel.get(chosen).getName().equals(schedule[24]) && !personnel.get(chosen).getName().equals(schedule[25])) {
                                        schedule[32 + i] = personnel.get(chosen).getName();
                                        picked[chosen] = true;
                                    }
                                    tries++;
                                    break;
                            }
                            if (!schedule[32 + i].equals("-") || tries > 6) break;
                        }
                }
            }
        }
    }
}