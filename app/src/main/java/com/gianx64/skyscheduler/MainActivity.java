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
                                    if (!personnel.get(chosen).getName().equals(schedule[(i / 2) + 31]))
                                        personnel.get(chosen).addLoad();
                                    else {
                                        for (int j = 32; j < schedule.length; j++)
                                            schedule[j] = "-";
                                        for (PersonClass person : personnel)
                                            person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
                                    }
                                    break;
                                } else if (!schedule[i - (i % 2)].equals(personnel.get(chosen).getName()) && !schedule[(i - (i % 2)) + 1].equals(personnel.get(chosen).getName())) {
                                    schedule[i] = personnel.get(chosen).getName();
                                    if (!personnel.get(chosen).getName().equals(schedule[(i / 2) + 31]))
                                        personnel.get(chosen).addLoad();
                                    else {
                                        for (int j = 32; j < schedule.length; j++)
                                            schedule[j] = "-";
                                        for (PersonClass person : personnel)
                                            person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
                                    }
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
            //Log.d("info", "Elevador principal lleno desde las 10:00 hasta las 12:00");
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
            //Log.d("info", "Elevador principal lleno desde las 22:00 hasta las 20:00");
            for (int i = 17; i > 3; i--) {  //Llenar elevador principal desde las 20:00 hasta las 12:00
                //TODO: if (personnel.size() > 4) que no tengan elevadores seguidos
                if (present[i/2] > 0) {
                    while (true) {
                        chosen = random.nextInt(personnel.size());
                        //Log.d("info", "i = "+i+", chosen: "+personnel.get(chosen).getName()+" "+personnel.get(chosen).getLoad());
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
                                            if (personnel.get(chosen).getName().equals(schedule[31 + (i / 2)])) {
                                                for (int j = 32; j < schedule.length; j++)
                                                    schedule[j] = "-";
                                                for (PersonClass person : personnel)
                                                    person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
                                            }
                                            else personnel.get(chosen).addLoad();
                                            break;
                                        } else {
                                            schedule[i] = personnel.get(chosen).getName();
                                            for (int j = 32; j < schedule.length; j++)
                                                schedule[j] = "-";
                                            for (PersonClass person : personnel)
                                                person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
                                            break;
                                        }
                                    } else {
                                        schedule[i] = personnel.get(chosen).getName();
                                        if (personnel.get(chosen).getName().equals(schedule[31+(i/2)])) //Caso que debería ser imposible
                                            for (int j = 32; j < schedule.length; j++)
                                                schedule[j] = "-";
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
                                            for (int j = 32; j < schedule.length; j++)
                                                schedule[j] = "-";
                                            for (PersonClass person : personnel)
                                                person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
                                            break;
                                        }
                                    } else if (personnel.get(chosen).getScheduleStart()/100 != (i/2)+7) {
                                        schedule[i] = personnel.get(chosen).getName();
                                        for (int j = 32; j < schedule.length; j++)
                                            schedule[j] = "-";
                                        for (PersonClass person : personnel)
                                            person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
                                        break;
                                    }
                                }
                            } else {    //Topa con tour
                                if (present[i / 2] == 1) {
                                    schedule[i] = personnel.get(chosen).getName();
                                    if (personnel.get(chosen).getName().equals(schedule[31+(i/2)])) {
                                        for (int j = 32; j < schedule.length; j++)
                                            schedule[j] = "-";
                                        for (PersonClass person : personnel)
                                            person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
                                    }
                                    else personnel.get(chosen).addLoad();
                                    break;
                                } else if (!personnel.get(chosen).getName().equals(schedule[i-(i%2)]) && !personnel.get(chosen).getName().equals(schedule[(i-(i%2))+1])) {
                                    schedule[i] = personnel.get(chosen).getName();
                                    if (personnel.get(chosen).getName().equals(schedule[31 + (i / 2)])) {
                                        for (int j = 32; j < schedule.length; j++)
                                            schedule[j] = "-";
                                        for (PersonClass person : personnel)
                                            person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
                                    }
                                    else personnel.get(chosen).addLoad();
                                    break;
                                }
                            }
                    }
                } else schedule[i] = "Sin personal.";
            }
            //Log.d("info", "Elevador principal lleno desde las 20:00 hasta las 12:00");
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
                                    if ((i == 24 || i == 25) && personnel.get(chosen).getName().equals(schedule[38])) {
                                        for (int j = 32; j < schedule.length; j++)
                                            schedule[j] = "-";
                                        for (PersonClass person : personnel)
                                            person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
                                    }
                                    else personnel.get(chosen).addLoad();
                                    break;
                                }
                            } else if (!schedule[i-(i%2)].equals(personnel.get(chosen).getName()) && !schedule[(i-(i%2))+1].equals(personnel.get(chosen).getName())
                            && !schedule[i-(i%2)-10].equals(personnel.get(chosen).getName()) && !schedule[(i-(i%2))-9].equals(personnel.get(chosen).getName())) {
                                schedule[i] = personnel.get(chosen).getName();
                                if ((i == 24 || i == 25) && personnel.get(chosen).getName().equals(schedule[38])) {
                                    for (int j = 32; j < schedule.length; j++)
                                        schedule[j] = "-";
                                    for (PersonClass person : personnel)
                                        person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
                                }
                                else personnel.get(chosen).addLoad();
                                break;
                            }
                    }
                } else schedule[i] = "Sin personal.";
            }
            //Log.d("info", "Elevador de apoyo lleno desde 21:00 hasta 17:00");
        }
    }

    public void fillTours(String[] schedule, ArrayList<PersonClass> personnel) {
        //TODO: implementar sistema de present (si son 3 y no topa con elevador secundario) (si son 3 y topa con elevador secundario?)
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
                do {
                    chosen = random.nextInt(7);
                    switch (chosen) {
                        case 0:
                        case 1:
                        case 5:
                            if (person.getScheduleStart() <= (11 + chosen) * 100 && person.getScheduleEnd() >= (12 + chosen) * 100)
                                if (!person.getName().equals(schedule[(chosen*2)+2]) && !person.getName().equals(schedule[(chosen*2)+3])) {
                                    schedule[32+chosen] = person.getName();
                                    person.addLoad();
                                }
                            tries++;
                            break;
                        case 2: //Hora de almuerzo
                        case 3:
                        case 4:
                            if (person.getScheduleStart() <= (11 + chosen) * 100 && person.getScheduleEnd() >= (12 + chosen) * 100)
                                if (!person.getName().equals(schedule[(chosen*2)+2]) && !person.getName().equals(schedule[(chosen*2)+3])) {
                                    if (person.getScheduleStart()%100 > 0) {
                                        if (person.getScheduleStart()/100 != chosen+7) {
                                            schedule[32+chosen] = person.getName();
                                            person.addLoad();
                                        }
                                    } else if (person.getScheduleStart()/100 != chosen+8) {
                                        schedule[32+chosen] = person.getName();
                                        person.addLoad();
                                    }
                                }
                            tries++;
                            break;
                        case 6: //Topa con elevador primario y secundario
                            if (person.getScheduleStart() <= (11 + chosen) * 100 && person.getScheduleEnd() >= (12 + chosen) * 100)
                                if (!person.getName().equals(schedule[(chosen*2)+2]) && !person.getName().equals(schedule[(chosen*2)+3]) && !person.getName().equals(schedule[(chosen*2)+12]) && !person.getName().equals(schedule[(chosen*2)+13])) {
                                    schedule[32+chosen] = person.getName();
                                    person.addLoad();
                                }
                            tries++;
                            break;
                    }
                } while (schedule[32 + chosen].equals("-") && tries < 7);
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
                do {
                    chosen = random.nextInt(personnel.size());
                    switch (i) {
                        case 0:
                        case 1:
                        case 5:
                            if (personnel.get(chosen).getScheduleStart() <= (11 + i) * 100 && personnel.get(chosen).getScheduleEnd() >= (12 + i) * 100)
                                if (!personnel.get(chosen).getName().equals(schedule[(i * 2) + 2]) && !personnel.get(chosen).getName().equals(schedule[(i * 2) + 3]))
                                    if (!picked[chosen])
                                        if (ammount > 0) {
                                            if (personnel.get(chosen).getLoad() < loadmax) {
                                                schedule[32 + i] = personnel.get(chosen).getName();
                                                personnel.get(chosen).addLoad();
                                                picked[chosen] = true;
                                                ammount--;
                                            }
                                        } else {
                                            schedule[32 + i] = personnel.get(chosen).getName();
                                            personnel.get(chosen).addLoad();
                                            picked[chosen] = true;
                                        }
                            tries++;
                            break;
                        case 2: //Hora de almuerzo
                        case 3:
                        case 4:
                            if (personnel.get(chosen).getScheduleStart() <= (11 + i) * 100 && personnel.get(chosen).getScheduleEnd() >= (12 + i) * 100)
                                if (!personnel.get(chosen).getName().equals(schedule[(i * 2) + 2]) && !personnel.get(chosen).getName().equals(schedule[(i * 2) + 3])) {
                                    if (personnel.get(chosen).getScheduleStart() % 100 > 0) {
                                        if (personnel.get(chosen).getScheduleStart() / 100 != i + 7) {
                                            if (!picked[chosen])
                                                if (ammount > 0) {
                                                    if (personnel.get(chosen).getLoad() < loadmax) {
                                                        schedule[32 + i] = personnel.get(chosen).getName();
                                                        personnel.get(chosen).addLoad();
                                                        picked[chosen] = true;
                                                        ammount--;
                                                    }
                                                } else {
                                                    schedule[32 + i] = personnel.get(chosen).getName();
                                                    personnel.get(chosen).addLoad();
                                                    picked[chosen] = true;
                                                }
                                        }
                                    } else if (personnel.get(chosen).getScheduleStart() / 100 != i + 8) {
                                        if (!picked[chosen])
                                            if (ammount > 0) {
                                                if (personnel.get(chosen).getLoad() < loadmax) {
                                                    schedule[32 + i] = personnel.get(chosen).getName();
                                                    personnel.get(chosen).addLoad();
                                                    picked[chosen] = true;
                                                    ammount--;
                                                }
                                            } else {
                                                schedule[32 + i] = personnel.get(chosen).getName();
                                                personnel.get(chosen).addLoad();
                                                picked[chosen] = true;
                                            }
                                    }
                                }
                            tries++;
                            break;
                        case 6: //Topa con elevador primario y secundario
                            if (personnel.get(chosen).getScheduleStart() <= (11 + i) * 100 && personnel.get(chosen).getScheduleEnd() >= (12 + i) * 100)
                                if (!personnel.get(chosen).getName().equals(schedule[(i * 2) + 2]) && !personnel.get(chosen).getName().equals(schedule[(i * 2) + 3]) && !personnel.get(chosen).getName().equals(schedule[(i * 2) + 12]) && !personnel.get(chosen).getName().equals(schedule[(i * 2) + 13])) {
                                    if (!picked[chosen])
                                        if (ammount > 0) {
                                            if (personnel.get(chosen).getLoad() < loadmax) {
                                                schedule[32 + i] = personnel.get(chosen).getName();
                                                personnel.get(chosen).addLoad();
                                                picked[chosen] = true;
                                                ammount--;
                                            }
                                        } else {
                                            schedule[32 + i] = personnel.get(chosen).getName();
                                            personnel.get(chosen).addLoad();
                                            picked[chosen] = true;
                                        }
                                }
                            tries++;
                            break;
                    }
                } while (schedule[32 + i].equals("-") && tries < 7);
            }
            /*for (int i = 32; i < schedule.length; i++)
                if (schedule[i].equals("-")) {
                    fillTours(schedule, personnel);
                    break;
                }*/
        }
        //Log.d("info", "Tours lleno desde 11:00 hasta 17:00");
    }
}