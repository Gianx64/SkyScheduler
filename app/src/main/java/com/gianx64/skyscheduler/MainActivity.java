package com.gianx64.skyscheduler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
    ArrayList<PersonClass> personnel;
    PersonAdapter adapter;
    ListView personnel_list;
    String[] schedule = new String[39];
    PersonClass person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: Resetear cuando se elimine una persona
        Arrays.fill(schedule, "-");
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
                            if (!name.getText().toString().equals("") && !name.getText().toString().equals("-") && Integer.parseInt(scheduleStart.getText().toString()) < 2360 && Integer.parseInt(scheduleEnd.getText().toString()) < 2360 && Integer.parseInt(scheduleStart.getText().toString()) < Integer.parseInt(scheduleEnd.getText().toString())) {
                                db.insert(person);
                                Toast.makeText(getApplicationContext(), "Personal añadido exitosamente.", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                StringBuilder errors = new StringBuilder();
                                errors.append("Error:");
                                if (name.getText().toString().equals(""))
                                    errors.append("\nNombre no ingresado.");
                                if (name.getText().toString().equals("-"))
                                    errors.append("\nEse nombre causa problemas.");
                                if (!(Integer.parseInt(scheduleStart.getText().toString()) < 2360))
                                    errors.append("\nInicio de horario mayor que 2359.");
                                if (!(Integer.parseInt(scheduleEnd.getText().toString()) < 2360))
                                    errors.append("\nFin de horario mayor que 2359.");
                                if (Integer.parseInt(scheduleStart.getText().toString()) > Integer.parseInt(scheduleEnd.getText().toString()))
                                    errors.append("\nInicio de horario mayor que fin de horario.");
                                Toast.makeText(getApplicationContext(), errors, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            StringBuilder errors = new StringBuilder();
                            errors.append("Error: ");
                            if (name.getText().toString().equals(""))
                                errors.append("\nNombre no ingresado.");
                            if (name.getText().toString().equals("-"))
                                errors.append("\nEse nombre causa problemas.");
                            if (scheduleStart.getText().toString().equals(""))
                                errors.append("\nInicio de horario no ingresado.");
                            if (scheduleEnd.getText().toString().equals(""))
                                errors.append("\nFin de horario no ingresado.");
                            if (errors.length() == 7)
                                errors.append("\n"+e.getMessage());
                            Toast.makeText(getApplicationContext(), errors, Toast.LENGTH_SHORT).show();
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
                genL26(schedule, personnel);
                genL25(schedule, personnel);
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
        Button gen_l26 = findViewById(R.id.gen_l26);
        Button gen_l25 = findViewById(R.id.gen_l25);
        Button gen_tours = findViewById(R.id.gen_tours);
        gen_l26.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                genL26(schedule, personnel);
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
        gen_l25.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                genL25(schedule, personnel);
                for (int i=12; i<16; i++) {
                    if (!schedule[i * 2].equals(schedule[(i * 2) + 1]))
                        textViews[i].setText(schedule[i * 2] + " - " + schedule[(i * 2) + 1]);
                    else
                        textViews[i].setText(schedule[i*2]);
                }
                for (int i=16; i<textViews.length; i++) textViews[i].setText(schedule[i+16]);
                adapter.notifyDataSetChanged();
            }
        });
        gen_tours.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fillTours(schedule, personnel);
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
                info.setMessage("Desarrollado por:\nGiancarlo Anfossy Araneda\n\nContacto: +56 9 8578 2508\ngiancarlo.anfossy@gmail.com\n\nCréditos de ícono:\nRocío Palomino Araneda");
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

    public void balanceL26(String[] schedule) {
        Random random = new Random();   //Generador de numero aleatorio
        int chosen;                     //Persona elegida
        int loadmin = personnel.get(0).getLoad();
        int loadmax = loadmin;
        do {
            for (int i = 0; i < personnel.size(); i++)
                if (personnel.get(i).getLoad() < loadmin)
                    loadmin = personnel.get(i).getLoad();
            for (int i = 0; i < personnel.size(); i++)
                if (personnel.get(i).getLoad() > loadmax)
                    loadmax = personnel.get(i).getLoad();
            //TODO: balancear horario intercambiando nombres
        } while (loadmin+1<loadmax-1);
    }

    public void genL26(String[] schedule, ArrayList<PersonClass> personnel){
        Random random = new Random();   //Generador de numero aleatorio
        int chosen;                     //Persona elegida
        int[] present = new int[12];    //Cantidad de personal presente en cada hora
        for (int i = 0; i < 24; i++)
            schedule[i] = "-";
        if (personnel.size() == 0)
            for (int i = 0; i < 24; i++)
                schedule[i] = "Sin personal.";
        else {
            for (PersonClass person : personnel)
                person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
            for (int i=0; i<present.length; i++) {
                present[i] = 0;
                for (PersonClass person : personnel)
                    if (person.getScheduleStart()%100 > 0) {
                        if (person.getScheduleStart()/100 != i+6)
                            if (person.getScheduleStart() <= (10 + i) * 100 && person.getScheduleEnd() >= (11 + i) * 100)
                                present[i]++;
                    } else if (person.getScheduleStart()/100 != i+7)
                        if (person.getScheduleStart() <= (10 + i) * 100 && person.getScheduleEnd() >= (11 + i) * 100)
                            present[i]++;
            }
            for (int i = 0; i < 4; i++) {   //Llenar elevador principal desde las 10:00 hasta las 12:00
                if (present[i/2] > 0) {
                    while (true) {
                        chosen = random.nextInt(personnel.size());
                        //Log.d("info", "i = "+i+", chosen: "+personnel.get(chosen).getName());
                        if (personnel.get(chosen).getScheduleStart() <= (10 + (i / 2)) * 100 && personnel.get(chosen).getScheduleEnd() >= (11 + (i / 2)) * 100)
                            if (i > 1) {    //Topa con tour
                                if (present[i / 2] == 1) {
                                    schedule[i] = personnel.get(chosen).getName();
                                    if (!personnel.get(chosen).getName().equals(schedule[(i / 2) + 32]))
                                        personnel.get(chosen).addLoad();
                                    else schedule[(i / 2) + 32] = "-";
                                    break;
                                } else if (!schedule[i - (i % 2)].equals(personnel.get(chosen).getName()) && !schedule[(i - (i % 2)) + 1].equals(personnel.get(chosen).getName())) {
                                    schedule[i] = personnel.get(chosen).getName();
                                    if (!personnel.get(chosen).getName().equals(schedule[(i / 2) + 32]))
                                        personnel.get(chosen).addLoad();
                                    else schedule[(i / 2) + 32] = "-";
                                    break;
                                }
                            } else if (i == 0) {
                                schedule[i] = personnel.get(chosen).getName();
                                personnel.get(chosen).addLoad();
                                break;
                            } else {
                                if (present[i/2] == 1) {
                                    schedule[i] = personnel.get(chosen).getName();
                                    personnel.get(chosen).addLoad();
                                    break;
                                } else if (!schedule[i-(i%2)].equals(personnel.get(chosen).getName()) && !schedule[(i-(i%2))+1].equals(personnel.get(chosen).getName())) {
                                    schedule[i] = personnel.get(chosen).getName();
                                    personnel.get(chosen).addLoad();
                                    break;
                                }
                            }
                    }
                } else schedule[i] = "Sin personal.";
            }
            //Log.d("info", "Elevador principal lleno hasta las 12:00");
            for (int i = 23; i > 17; i--) { //Llenar elevador principal desde las 22:00 hasta las 20:00
                if (present[i/2] > 0) {
                    while (true) {
                        chosen = random.nextInt(personnel.size());
                        //Log.d("info", "i = "+i+", chosen: "+personnel.get(chosen).getName());
                        if (personnel.get(chosen).getScheduleStart() <= (10 + (i / 2)) * 100 && personnel.get(chosen).getScheduleEnd() >= (11 + (i / 2)) * 100)
                            if (i < 22) {   //Topa con elevador secundario
                                if (present[i / 2] == 1) {
                                    schedule[i] = personnel.get(chosen).getName();
                                    if (!personnel.get(chosen).getName().equals(schedule[(i-(i%2))+10]) && !personnel.get(chosen).getName().equals(schedule[(i-(i%2))+11]))
                                        personnel.get(chosen).addLoad();
                                    else {
                                        for (int j = 24; j < 32; j++)
                                            schedule[j] = "-";
                                        for (PersonClass person : personnel)
                                            person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
                                    }
                                    break;
                                } else if (!schedule[i - (i % 2)].equals(personnel.get(chosen).getName()) && !schedule[(i - (i % 2)) + 1].equals(personnel.get(chosen).getName())) {
                                    schedule[i] = personnel.get(chosen).getName();
                                    if (!personnel.get(chosen).getName().equals(schedule[(i-(i%2))+10]) && !personnel.get(chosen).getName().equals(schedule[(i-(i%2))+11]))
                                        personnel.get(chosen).addLoad();
                                    else {
                                        for (int j = 24; j < 32; j++)
                                            schedule[j] = "-";
                                        for (PersonClass person : personnel)
                                            person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
                                    }
                                    break;
                                }
                            } else {
                                if (present[i / 2] == 1) {
                                    schedule[i] = personnel.get(chosen).getName();
                                    personnel.get(chosen).addLoad();
                                    break;
                                } else if (!schedule[i - (i % 2)].equals(personnel.get(chosen).getName()) && !schedule[(i - (i % 2)) + 1].equals(personnel.get(chosen).getName())) {
                                    schedule[i] = personnel.get(chosen).getName();
                                    personnel.get(chosen).addLoad();
                                    break;
                                }
                            }
                    }
                } else schedule[i] = "Sin personal.";
            }
            //Log.d("info", "Elevador principal llenado desde las 20:00");
            for (int i = 17; i > 3; i--) {  //Llenar elevador principal desde las 20:00 hasta las 12:00
                //TODO: if (personnel.size() > 4) que no tengan elevadores seguidos
                if (present[i/2] > 0) {
                    while (true) {
                        chosen = random.nextInt(personnel.size());
                        Log.d("info", "i = "+i+", chosen: "+personnel.get(chosen).getName()+" "+personnel.get(chosen).getLoad());
                        if (personnel.get(chosen).getScheduleStart() <= (10 + (i / 2)) * 100 && personnel.get(chosen).getScheduleEnd() >= (11 + (i / 2)) * 100)
                            if (i == 17 || i == 16) {   //Topa con elevador secundario
                                if (present[i / 2] == 1) {
                                    schedule[i] = personnel.get(chosen).getName();
                                    personnel.get(chosen).addLoad();
                                    break;
                                } else if (!personnel.get(chosen).getName().equals(schedule[16]) && !personnel.get(chosen).getName().equals(schedule[17])) {
                                    if (!personnel.get(chosen).getName().equals(schedule[26]) && !personnel.get(chosen).getName().equals(schedule[27])) {
                                        schedule[i] = personnel.get(chosen).getName();
                                        personnel.get(chosen).addLoad();
                                        break;
                                    } else {
                                        schedule[i] = personnel.get(chosen).getName();
                                        for (int j = 24; j < 32; j++)
                                            schedule[j] = "-";
                                        for (PersonClass person : personnel)
                                            person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
                                        break;
                                    }
                                }
                            } else if (i == 15 || i == 14) {    //Topa con elevador secundario y tour
                                if (present[i / 2] == 1) {
                                    schedule[i] = personnel.get(chosen).getName();
                                    personnel.get(chosen).addLoad();
                                    break;
                                } else if (!personnel.get(chosen).getName().equals(schedule[i-(i%2)]) && !personnel.get(chosen).getName().equals(schedule[(i-(i%2))+1])) {
                                    if (!personnel.get(chosen).getName().equals(schedule[(i-(i%2))+10]) && !personnel.get(chosen).getName().equals(schedule[(i-(i%2))+11])) {
                                        if (!personnel.get(chosen).getName().equals(schedule[31 + (i / 2)])) {
                                            schedule[i] = personnel.get(chosen).getName();
                                            if (personnel.get(chosen).getName().equals(schedule[31 + (i / 2)]))
                                                schedule[31 + (i / 2)] = "-";
                                            else personnel.get(chosen).addLoad();
                                            break;
                                        } else {
                                            schedule[i] = personnel.get(chosen).getName();
                                            schedule[31+(i/2)] = "-";
                                            break;
                                        }
                                    } else {
                                        schedule[i] = personnel.get(chosen).getName();
                                        if (personnel.get(chosen).getName().equals(schedule[31+(i/2)])) //Caso que debería ser imposible
                                            schedule[31+(i/2)] = "-";
                                        for (int j = 24; j < 32; j++)
                                            schedule[j] = "-";
                                        for (PersonClass person : personnel)
                                            person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
                                        break;
                                    }
                                }
                            } else if (i < 12 && i > 5) {   //Topa con tour y horas de almuerzo
                                if (present[i / 2] == 1) {
                                    if (personnel.get(chosen).getScheduleStart()%100 > 0) {
                                        if (personnel.get(chosen).getScheduleStart()/100 != (i/2)+6) {
                                            schedule[i] = personnel.get(chosen).getName();
                                            personnel.get(chosen).addLoad();
                                            break;
                                        }
                                    } else if (personnel.get(chosen).getScheduleStart()/100 != (i/2)+7) {
                                        schedule[i] = personnel.get(chosen).getName();
                                        personnel.get(chosen).addLoad();
                                        break;
                                    }
                                } else if (!personnel.get(chosen).getName().equals(schedule[i-(i%2)]) && !personnel.get(chosen).getName().equals(schedule[(i-(i%2))+1])
                                        && !personnel.get(chosen).getName().equals(schedule[31+(i/2)])) {
                                    if (personnel.get(chosen).getScheduleStart() % 100 > 0) {
                                        if (personnel.get(chosen).getScheduleStart() / 100 != (i / 2) + 6) {
                                            schedule[i] = personnel.get(chosen).getName();
                                            personnel.get(chosen).addLoad();
                                            break;
                                        }
                                    } else if (personnel.get(chosen).getScheduleStart() / 100 != (i / 2) + 7) {
                                        schedule[i] = personnel.get(chosen).getName();
                                        personnel.get(chosen).addLoad();
                                        break;
                                    }
                                } else if (personnel.get(chosen).getName().equals(schedule[31+(i/2)])) {
                                    if (personnel.get(chosen).getScheduleStart()%100 > 0) {
                                        if (personnel.get(chosen).getScheduleStart()/100 != (i/2)+6) {
                                            schedule[i] = personnel.get(chosen).getName();
                                            schedule[31+(i/2)] = "-";
                                            break;
                                        }
                                    } else if (personnel.get(chosen).getScheduleStart()/100 != (i/2)+7) {
                                        schedule[i] = personnel.get(chosen).getName();
                                        schedule[31+(i/2)] = "-";
                                        break;
                                    }
                                }
                            } else {    //Topa con tour
                                if (present[i / 2] == 1) {
                                    schedule[i] = personnel.get(chosen).getName();
                                    if (personnel.get(chosen).getName().equals(schedule[31+(i/2)]))
                                        schedule[31+(i/2)] = "-";
                                    else personnel.get(chosen).addLoad();
                                    break;
                                } else if (!personnel.get(chosen).getName().equals(schedule[i-(i%2)]) && !personnel.get(chosen).getName().equals(schedule[(i-(i%2))+1])) {
                                    schedule[i] = personnel.get(chosen).getName();
                                    if (personnel.get(chosen).getName().equals(schedule[31 + (i / 2)]))
                                        schedule[31 + (i / 2)] = "-";
                                    else personnel.get(chosen).addLoad();
                                    break;
                                }
                            }
                    }
                } else schedule[i] = "Sin personal.";
            }
            //Log.d("info", "Elevador principal lleno desde las 12:00 hasta las 20:00");
        }
    }

    public void genL25(String[] schedule, ArrayList<PersonClass> personnel){
        Random random = new Random();   //Generador de numero aleatorio
        int chosen;                     //Persona elegida
        int[] present = new int[12];    //Cantidad de personal presente en cada hora
        for (int i = 24; i < 32; i++)
            schedule[i] = "-";
        for (PersonClass person : personnel)
            person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
        if (personnel.size() == 0)
            for (int i = 24; i < 32; i++)
                schedule[i] = "Sin personal.";
        else {
            for (PersonClass person : personnel)
                person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
            for (int i=0; i<present.length; i++) {
                present[i] = 0;
                for (PersonClass person : personnel)
                    if (person.getScheduleStart() <= (10 + i) * 100 && person.getScheduleEnd() >= (11 + i) * 100)
                        present[i]++;
            }
            for (int i = 31; i > 23; i--) {  //Llenar elevador de apoyo desde 21:00 hasta 17:00
                //TODO: Implementar loadmax (?)
                if (personnel.size() < 3)
                    schedule[i] = "Sin personal.";
                else if (present[7+((i-24)/2)] > 2) {
                    while (true) {
                        chosen = random.nextInt(personnel.size());
                        //Log.d("info", "i = "+i+", chosen: "+personnel.get(chosen).getName());
                        if (personnel.get(chosen).getScheduleStart() <= (17 + ((i-24)/2)) * 100 && personnel.get(chosen).getScheduleEnd() >= (18 + ((i-24)/2)) * 100)
                            if (present[7+((i-24)/2)] == 3) {
                                if (!schedule[i-(i%2)-10].equals(personnel.get(chosen).getName()) && !schedule[(i-(i%2))-9].equals(personnel.get(chosen).getName())) {
                                    schedule[i] = personnel.get(chosen).getName();
                                    if ((i == 24 || i == 25) && personnel.get(chosen).getName().equals(schedule[38]))
                                        schedule[38] = "-";
                                    else personnel.get(chosen).addLoad();
                                    break;
                                }
                            } else if (!schedule[i-(i%2)].equals(personnel.get(chosen).getName()) && !schedule[(i-(i%2))+1].equals(personnel.get(chosen).getName())
                            && !schedule[i-(i%2)-10].equals(personnel.get(chosen).getName()) && !schedule[(i-(i%2))-9].equals(personnel.get(chosen).getName())) {
                                schedule[i] = personnel.get(chosen).getName();
                                if ((i == 24 || i == 25) && personnel.get(chosen).getName().equals(schedule[38]))
                                    schedule[38] = "-";
                                else personnel.get(chosen).addLoad();
                                break;
                            }
                    }
                } else schedule[i] = "Sin personal.";
            }
            //Log.d("info", "Elevador de apoyo lleno desde 17:00 hasta 21:00");
        }
    }

    public void fillTours(String[] schedule, ArrayList<PersonClass> personnel){
        //TODO: actualizar verificacion de horarios y verificacion de almuerzo
        //TODO: arreglar problemas cuando son 3
        Random random = new Random();
        int chosen, tries;
        for (int i = 32; i < schedule.length; i++)
            schedule[i] = "-";
        for (PersonClass person : personnel)
            person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
        if (personnel.size() < 8) {
            if (personnel.size() == 0)
                for (int i = 32; i < schedule.length; i++)
                    schedule[i] = "Sin personal.";
            else for (PersonClass person : personnel) {
                tries = 0;
                while (true) {
                    chosen = random.nextInt(7);
                    if (schedule[32 + chosen].equals("-")) {
                        switch (chosen) {
                            case 0:
                                if (person.getScheduleStart() <= 1100 && !person.getName().equals(schedule[2]) && !person.getName().equals(schedule[3])) {
                                    schedule[32] = person.getName();
                                    person.addLoad();
                                }
                                tries++;
                                break;
                            case 1:
                                if (!person.getName().equals(schedule[4]) && !person.getName().equals(schedule[5])) {
                                    schedule[32 + chosen] = person.getName();
                                    person.addLoad();
                                }
                                tries++;
                                break;
                            case 2:
                                if (!person.getName().equals(schedule[6]) && !person.getName().equals(schedule[7])) {
                                    schedule[32 + chosen] = person.getName();
                                    person.addLoad();
                                }
                                tries++;
                                break;
                            case 3:
                                if (!person.getName().equals(schedule[8]) && !person.getName().equals(schedule[9])) {
                                    schedule[32 + chosen] = person.getName();
                                    person.addLoad();
                                }
                                tries++;
                                break;
                            case 4:
                                if (!person.getName().equals(schedule[10]) && !person.getName().equals(schedule[11])) {
                                    schedule[32 + chosen] = person.getName();
                                    person.addLoad();
                                }
                                tries++;
                                break;
                            case 5:
                                if (!person.getName().equals(schedule[12]) && !person.getName().equals(schedule[13])) {
                                    schedule[32 + chosen] = person.getName();
                                    person.addLoad();
                                }
                                tries++;
                                break;
                            case 6:
                                if (!person.getName().equals(schedule[14]) && !person.getName().equals(schedule[15]) && !person.getName().equals(schedule[24]) && !person.getName().equals(schedule[25])) {
                                    schedule[32 + chosen] = person.getName();
                                    person.addLoad();
                                }
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
                                        personnel.get(chosen).addLoad();
                                        picked[chosen] = true;
                                        ammount--;
                                    }
                                    tries++;
                                    break;
                                case 1:
                                    if (!personnel.get(chosen).getName().equals(schedule[4]) && !personnel.get(chosen).getName().equals(schedule[5])) {
                                        schedule[32 + i] = personnel.get(chosen).getName();
                                        personnel.get(chosen).addLoad();
                                        picked[chosen] = true;
                                        ammount--;
                                    }
                                    tries++;
                                    break;
                                case 2:
                                    if (!personnel.get(chosen).getName().equals(schedule[6]) && !personnel.get(chosen).getName().equals(schedule[7])) {
                                        schedule[32 + i] = personnel.get(chosen).getName();
                                        personnel.get(chosen).addLoad();
                                        picked[chosen] = true;
                                        ammount--;
                                    }
                                    tries++;
                                    break;
                                case 3:
                                    if (!personnel.get(chosen).getName().equals(schedule[8]) && !personnel.get(chosen).getName().equals(schedule[9])) {
                                        schedule[32 + i] = personnel.get(chosen).getName();
                                        personnel.get(chosen).addLoad();
                                        picked[chosen] = true;
                                        ammount--;
                                    }
                                    tries++;
                                    break;
                                case 4:
                                    if (!personnel.get(chosen).getName().equals(schedule[10]) && !personnel.get(chosen).getName().equals(schedule[11])) {
                                        schedule[32 + i] = personnel.get(chosen).getName();
                                        personnel.get(chosen).addLoad();
                                        picked[chosen] = true;
                                        ammount--;
                                    }
                                    tries++;
                                    break;
                                case 5:
                                    if (!personnel.get(chosen).getName().equals(schedule[12]) && !personnel.get(chosen).getName().equals(schedule[13])) {
                                        schedule[32 + i] = personnel.get(chosen).getName();
                                        personnel.get(chosen).addLoad();
                                        picked[chosen] = true;
                                        ammount--;
                                    }
                                    tries++;
                                    break;
                                case 6:
                                    if (!personnel.get(chosen).getName().equals(schedule[14]) && !personnel.get(chosen).getName().equals(schedule[15]) && !personnel.get(chosen).getName().equals(schedule[24]) && !personnel.get(chosen).getName().equals(schedule[25])) {
                                        schedule[32 + i] = personnel.get(chosen).getName();
                                        personnel.get(chosen).addLoad();
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
                                    personnel.get(chosen).addLoad();
                                    picked[chosen] = true;
                                }
                                tries++;
                                break;
                            case 1:
                                if (!personnel.get(chosen).getName().equals(schedule[4]) && !personnel.get(chosen).getName().equals(schedule[5])) {
                                    schedule[32 + i] = personnel.get(chosen).getName();
                                    personnel.get(chosen).addLoad();
                                    picked[chosen] = true;
                                }
                                tries++;
                                break;
                            case 2:
                                if (!personnel.get(chosen).getName().equals(schedule[6]) && !personnel.get(chosen).getName().equals(schedule[7])) {
                                    schedule[32 + i] = personnel.get(chosen).getName();
                                    personnel.get(chosen).addLoad();
                                    picked[chosen] = true;
                                }
                                tries++;
                                break;
                            case 3:
                                if (!personnel.get(chosen).getName().equals(schedule[8]) && !personnel.get(chosen).getName().equals(schedule[9])) {
                                    schedule[32 + i] = personnel.get(chosen).getName();
                                    personnel.get(chosen).addLoad();
                                    picked[chosen] = true;
                                }
                                tries++;
                                break;
                            case 4:
                                if (!personnel.get(chosen).getName().equals(schedule[10]) && !personnel.get(chosen).getName().equals(schedule[11])) {
                                    schedule[32 + i] = personnel.get(chosen).getName();
                                    personnel.get(chosen).addLoad();
                                    picked[chosen] = true;
                                }
                                tries++;
                                break;
                            case 5:
                                if (!personnel.get(chosen).getName().equals(schedule[12]) && !personnel.get(chosen).getName().equals(schedule[13])) {
                                    schedule[32 + i] = personnel.get(chosen).getName();
                                    personnel.get(chosen).addLoad();
                                    picked[chosen] = true;
                                }
                                tries++;
                                break;
                            case 6:
                                if (!personnel.get(chosen).getName().equals(schedule[14]) && !personnel.get(chosen).getName().equals(schedule[15]) && !personnel.get(chosen).getName().equals(schedule[24]) && !personnel.get(chosen).getName().equals(schedule[25])) {
                                    schedule[32 + i] = personnel.get(chosen).getName();
                                    personnel.get(chosen).addLoad();
                                    picked[chosen] = true;
                                }
                                tries++;
                                break;
                        }
                        if (!schedule[32 + i].equals("-") || tries > 6) break;
                    }
                }
            }
            for (int i = 32; i < schedule.length; i++)
                if (schedule[i] == "-") {
                    fillTours(schedule, personnel);
                    break;
                }
        }
    }
}