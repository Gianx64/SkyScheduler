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
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    PersonDB db;
    static ArrayList<PersonClass> personnel;
    PersonAdapter adapter;
    ListView personnel_list;
    static TextView[] textViews;
    static String[] schedule = new String[39];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new PersonDB(this);
        personnel = db.readAll();
        sortPersonnel();
        adapter = new PersonAdapter(this, personnel, db);
        personnel_list = findViewById(R.id.person_list);
        personnel_list.setAdapter(adapter);
        textViews = new TextView[]{
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
                            PersonClass person = new PersonClass(name.getText().toString(), Integer.parseInt(scheduleStart.getText().toString()), Integer.parseInt(scheduleEnd.getText().toString()));
                            if (!name.getText().toString().equals("") && !name.getText().toString().equals("-") && Integer.parseInt(scheduleStart.getText().toString()) < 2360 && Integer.parseInt(scheduleEnd.getText().toString()) < 2360 && Integer.parseInt(scheduleStart.getText().toString()) < Integer.parseInt(scheduleEnd.getText().toString())) {
                                db.insert(person);
                                Toast.makeText(getApplicationContext(), "Personal añadido exitosamente.", Toast.LENGTH_SHORT).show();
                                wipeSchedule();
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
                                errors.append("\n").append(e.getMessage());
                            Toast.makeText(getApplicationContext(), errors, Toast.LENGTH_SHORT).show();
                        }
                        personnel = db.readAll();
                        sortPersonnel();
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
        Button generate_btn = findViewById(R.id.generate_btn);
        generate_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                genL26(schedule, personnel);
                genL25(schedule, personnel);
                genTours(schedule, personnel);

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
                genTours(schedule, personnel);
                for (int i=16; i<textViews.length; i++) textViews[i].setText(schedule[i+16]);
                adapter.notifyDataSetChanged();
            }
        });
        wipeSchedule();
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

    public static void wipeSchedule() {
        Arrays.fill(schedule, "-");
        for (int i=0; i<16; i++) {
            if (!schedule[i * 2].equals(schedule[(i * 2) + 1]))
                textViews[i].setText(schedule[i * 2] + " - " + schedule[(i * 2) + 1]);
            else
                textViews[i].setText(schedule[i*2]);
        }
        for (int i=16; i<textViews.length; i++) textViews[i].setText(schedule[i+16]);
    }

    public static void sortPersonnel() {
        Collections.sort(personnel, new Comparator<PersonClass>() {
            @Override
            public int compare(PersonClass o1, PersonClass o2) {
                return Integer.valueOf(o1.getScheduleStart()).compareTo(o2.getScheduleStart());
            }
        });
    }

    public void genL26(String[] schedule, ArrayList<PersonClass> personnel){
        ArrayList<ArrayList<PersonClass>> present = new ArrayList<ArrayList<PersonClass>>();
        for (int i = 0; i < 24; i++)
            schedule[i] = "-";
        if (personnel.size() == 0)
            for (int i = 0; i < 24; i++)
                schedule[i] = "Sin personal.";
        else {
            for (PersonClass person : personnel)
                person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
            for (int i=0; i<12; i++) {
                present.add(new ArrayList<PersonClass>());
                for (PersonClass person : personnel)
                    if (person.getScheduleStart() <= (10 + i) * 100 && person.getScheduleEnd() >= (11 + i) * 100)   //Si la persona está disponible en esa hora
                        if (person.getScheduleStart()%100 > 0) {    //Si la persona entra a una hora inexacta (Ej: 9:30)
                            if (person.getScheduleStart()/100 != i+6)   //Si no es hora de almuerzo de esa persona
                                present.get(i).add(person);
                        } else if (person.getScheduleStart()/100 != i+7)    //Si no es hora de almuerzo de esa persona
                            present.get(i).add(person);
            }
            /*for (int i=0; i<present.size(); i++) {
                Log.d("info", "Personas para las "+(i+10));
                for (int j=0; j<present.get(i).size(); j++)
                    Log.d("info", present.get(i).get(j).getName());
            }*/
            for (int i=0; i <= personnel.size(); i++)  //Repite la cantidad del personal
                for (int j=present.size()-1; j >= 0; j--)   //Recorre todos los horarios
                    if (present.get(j).size() == i) {
                        switch (j) {
                            case 0:
                            case 11:    //No topa
                                if (i == 0) {
                                    schedule[j * 2] = "Sin personal.";
                                    schedule[(j * 2) + 1] = "Sin personal.";
                                } else if (i == 1) {
                                    schedule[j * 2] = present.get(j).get(0).assign();
                                    schedule[(j * 2) + 1] = present.get(j).get(0).assign();
                                } else if (i == 2) {
                                    schedule[j * 2] = present.get(j).get(0).assign();
                                    schedule[(j * 2) + 1] = present.get(j).get(1).assign();
                                } else {
                                    Collections.shuffle(present.get(j));
                                    Collections.sort(present.get(j), new Comparator<PersonClass>() {
                                        @Override
                                        public int compare(PersonClass o1, PersonClass o2) {
                                            return Integer.valueOf(o1.getLoad()).compareTo(o2.getLoad());
                                        }
                                    });
                                    schedule[j * 2] = present.get(j).get(0).assign();
                                    schedule[(j * 2) + 1] = present.get(j).get(1).assign();
                                }
                                break;
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6: //Topa con tour
                                if (i == 0) {
                                    schedule[j * 2] = "Sin personal.";
                                    schedule[(j * 2) + 1] = "Sin personal.";
                                } else if (i == 1) {
                                    if (present.get(j).get(0).getName().equals(schedule[j + 32])) {
                                        for (int k = 32; k < schedule.length; k++)
                                            schedule[k] = "-";
                                        for (PersonClass person : personnel)
                                            person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
                                    }
                                    schedule[j * 2] = present.get(j).get(0).assign();
                                    schedule[(j * 2) + 1] = present.get(j).get(0).assign();
                                } else if (i == 2) {
                                    if (present.get(j).get(0).getName().equals(schedule[j + 32]) || present.get(j).get(1).getName().equals(schedule[j + 32])) {
                                        for (int k = 32; k < schedule.length; k++)
                                            schedule[k] = "-";
                                        for (PersonClass person : personnel)
                                            person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
                                    }
                                    schedule[j * 2] = present.get(j).get(0).assign();
                                    schedule[(j * 2) + 1] = present.get(j).get(1).assign();
                                } else {
                                    if (!schedule[(j * 2) + 2].equals("-"))
                                        if (present.get(j).size() > 3)
                                            for (int k = 0; k < 2; k++)
                                                for (PersonClass person : present.get(j))
                                                    if (person.getName().equals(schedule[(j * 2) + 2]) || person.getName().equals(schedule[(j * 2) + 3])) {
                                                        present.get(j).remove(person);
                                                        break;
                                                    }
                                    if (!schedule[(j * 2) - 2].equals("-"))
                                        if (present.get(j).size() > 3)
                                            for (int k = 0; k < 2; k++)
                                                for (PersonClass person : present.get(j))
                                                    if (person.getName().equals(schedule[(j * 2) - 2]) || person.getName().equals(schedule[(j * 2) - 1])) {
                                                        present.get(j).remove(person);
                                                        break;
                                                    }
                                    Collections.shuffle(present.get(j));
                                    Collections.sort(present.get(j), new Comparator<PersonClass>() {
                                        @Override
                                        public int compare(PersonClass o1, PersonClass o2) {
                                            return Integer.valueOf(o1.getLoad()).compareTo(o2.getLoad());
                                        }
                                    });
                                    if (present.get(j).get(0).getName().equals(schedule[j + 32]) || present.get(j).get(1).getName().equals(schedule[j + 32])) {
                                        for (int k = 32; k < schedule.length; k++)
                                            schedule[k] = "-";
                                        for (PersonClass person : personnel)
                                            person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
                                    }
                                    schedule[j * 2] = present.get(j).get(0).assign();
                                    schedule[(j * 2) + 1] = present.get(j).get(1).assign();
                                }
                                break;
                            case 7: //Topa con tour y elevador secundario
                                if (i == 0) {
                                    schedule[j * 2] = "Sin personal.";
                                    schedule[(j * 2) + 1] = "Sin personal.";
                                } else if (i == 1) {
                                    if (present.get(j).get(0).getName().equals(schedule[24]) || present.get(j).get(0).getName().equals(schedule[25]))
                                        for (int k = 24; k < 32; k++)
                                            schedule[k] = "-";
                                    if (present.get(j).get(0).getName().equals(schedule[38]))
                                        for (int k = 32; k < schedule.length; k++)
                                            schedule[k] = "-";
                                    for (PersonClass person : personnel)
                                        person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
                                    schedule[j * 2] = present.get(j).get(0).assign();
                                    schedule[(j * 2) + 1] = present.get(j).get(0).assign();
                                } else if (i == 2) {
                                    if (present.get(j).get(0).getName().equals(schedule[24]) || present.get(j).get(0).getName().equals(schedule[25]))
                                        for (int k = 24; k < 32; k++)
                                            schedule[k] = "-";
                                    if (present.get(j).get(0).getName().equals(schedule[38]))
                                        for (int k = 32; k < schedule.length; k++)
                                            schedule[k] = "-";
                                    for (PersonClass person : personnel)
                                        person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
                                    schedule[j * 2] = present.get(j).get(0).assign();
                                    schedule[(j * 2) + 1] = present.get(j).get(1).assign();
                                } else {
                                    if (!schedule[(j * 2) + 2].equals("-"))
                                        if (i > 3)
                                            for (int k = 0; k < 2; k++)
                                                for (PersonClass person : present.get(j))
                                                    if (person.getName().equals(schedule[(j * 2) + 2]) || person.getName().equals(schedule[(j * 2) + 3])) {
                                                        present.get(j).remove(person);
                                                        break;
                                                    }
                                    if (!schedule[(j * 2) - 2].equals("-"))
                                        if (i > 3)
                                            for (int k = 0; k < 2; k++)
                                                for (PersonClass person : present.get(j))
                                                    if (person.getName().equals(schedule[(j * 2) - 2]) || person.getName().equals(schedule[(j * 2) - 1])) {
                                                        present.get(j).remove(person);
                                                        break;
                                                    }
                                    Collections.shuffle(present.get(j));
                                    Collections.sort(present.get(j), new Comparator<PersonClass>() {
                                        @Override
                                        public int compare(PersonClass o1, PersonClass o2) {
                                            return Integer.valueOf(o1.getLoad()).compareTo(o2.getLoad());
                                        }
                                    });
                                    if (present.get(j).get(0).getName().equals(schedule[24]) || present.get(j).get(0).getName().equals(schedule[25]))
                                        for (int k = 24; k < 32; k++)
                                            schedule[k] = "-";
                                    if (present.get(j).get(0).getName().equals(schedule[38]))
                                        for (int k = 32; k < schedule.length; k++)
                                            schedule[k] = "-";
                                    for (PersonClass person : personnel)
                                        person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
                                    schedule[j * 2] = present.get(j).get(0).assign();
                                    schedule[(j * 2) + 1] = present.get(j).get(1).assign();
                                }
                                break;
                            case 8:
                            case 9:
                            case 10:    //Topa con elevador secundario
                                if (i == 0) {
                                    schedule[j * 2] = "Sin personal.";
                                    schedule[(j * 2) + 1] = "Sin personal.";
                                } else if (i == 1) {
                                    if (present.get(j).get(0).getName().equals(schedule[(j * 2) + 10]) || present.get(j).get(0).getName().equals(schedule[(j * 2) + 11])) {
                                        for (int k = 24; k < 32; k++)
                                            schedule[k] = "-";
                                        for (PersonClass person : personnel)
                                            person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
                                    }
                                    schedule[j * 2] = present.get(j).get(0).assign();
                                    schedule[(j * 2) + 1] = present.get(j).get(0).assign();
                                } else if (i == 2) {
                                    if (present.get(j).get(0).getName().equals(schedule[(j * 2) + 10]) || present.get(j).get(0).getName().equals(schedule[(j * 2) + 11])) {
                                        for (int k = 24; k < 32; k++)
                                            schedule[k] = "-";
                                        for (PersonClass person : personnel)
                                            person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
                                    }
                                    schedule[j * 2] = present.get(j).get(0).assign();
                                    schedule[(j * 2) + 1] = present.get(j).get(1).assign();
                                } else {
                                    if (!schedule[(j * 2) + 2].equals("-"))
                                        if (i > 3)
                                            for (int k = 0; k < 2; k++)
                                                for (PersonClass person : present.get(j))
                                                    if (person.getName().equals(schedule[(j * 2) + 2]) || person.getName().equals(schedule[(j * 2) + 3])) {
                                                        present.get(j).remove(person);
                                                        break;
                                                    }
                                    if (!schedule[(j * 2) - 2].equals("-"))
                                        if (i > 3)
                                            for (int k = 0; k < 2; k++)
                                                for (PersonClass person : present.get(j))
                                                    if (person.getName().equals(schedule[(j * 2) - 2]) || person.getName().equals(schedule[(j * 2) - 1])) {
                                                        present.get(j).remove(person);
                                                        break;
                                                    }
                                    Collections.shuffle(present.get(j));
                                    Collections.sort(present.get(j), new Comparator<PersonClass>() {
                                        @Override
                                        public int compare(PersonClass o1, PersonClass o2) {
                                            return Integer.valueOf(o1.getLoad()).compareTo(o2.getLoad());
                                        }
                                    });
                                    if (present.get(j).get(0).getName().equals(schedule[(j * 2) + 10]) || present.get(j).get(0).getName().equals(schedule[(j * 2) + 11])) {
                                        for (int k = 24; k < 32; k++)
                                            schedule[k] = "-";
                                        for (PersonClass person : personnel)
                                            person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
                                    }
                                    schedule[j * 2] = present.get(j).get(0).assign();
                                    schedule[(j * 2) + 1] = present.get(j).get(1).assign();
                                }
                                break;
                        }
                    }
        }
    }

    public void genL25(String[] schedule, ArrayList<PersonClass> personnel){
        ArrayList<ArrayList<PersonClass>> present = new ArrayList<ArrayList<PersonClass>>();
        for (int i = 24; i < 32; i++)
            schedule[i] = "-";
        if (personnel.size() == 0)
            for (int i = 24; i < 32; i++)
                schedule[i] = "Sin personal.";
        else {
            for (PersonClass person : personnel)
                person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
            for (int i=0; i<4; i++) {
                present.add(new ArrayList<PersonClass>());
                for (PersonClass person : personnel)
                    if (person.getScheduleStart() <= (17 + i) * 100 && person.getScheduleEnd() >= (18 + i) * 100)   //Si la persona está disponible en esa hora
                        if (!person.getName().equals(schedule[(i*2)+14]) && !person.getName().equals(schedule[(i*2)+15]))   //Si no topa con elevador principal
                            present.get(i).add(person);
            }
            /*for (int i=0; i<present.size(); i++) {
                Log.d("info", "Personas para las "+(i+17));
                for (int j=0; j<present.get(i).size(); j++)
                    Log.d("info", present.get(i).get(j).getName());
            }*/
            for (int i=0; i <= personnel.size(); i++)  //Repite la cantidad del personal
                for (int j=present.size()-1; j >= 0; j--)   //Recorre todos los horarios
                    if (present.get(j).size() == i) {
                        if (i == 0) {
                            schedule[(j*2) + 24] = "Sin personal.";
                            schedule[(j*2) + 25] = "Sin personal.";
                        } else if (i == 1) {
                            schedule[(j*2) + 24] = present.get(j).get(0).assign();
                            schedule[(j*2) + 25] = present.get(j).get(0).assign();
                            if (j == 0 && present.get(j).get(0).getName().equals(schedule[38])) {
                                for (int k = 32; k < schedule.length; k++)
                                    schedule[k] = "-";
                                for (PersonClass person : personnel)
                                    person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
                            }
                        } else if (i == 2) {
                            schedule[(j*2) + 24] = present.get(j).get(0).assign();
                            schedule[(j*2) + 25] = present.get(j).get(1).assign();
                            if (j == 0 && (present.get(j).get(0).getName().equals(schedule[38]) || present.get(j).get(1).getName().equals(schedule[38]))) {
                                for (int k = 32; k < schedule.length; k++)
                                    schedule[k] = "-";
                                for (PersonClass person : personnel)
                                    person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
                            }
                        } else {
                            switch (j) {
                                case 0:
                                    if (!schedule[(j*2) + 26].equals("-"))
                                        if (i > 3)
                                            for (int k = 0; k < 2; k++)
                                                for (PersonClass person : present.get(j))
                                                    if (person.getName().equals(schedule[(j*2) + 2]) || person.getName().equals(schedule[(j*2) + 3])) {
                                                        present.get(j).remove(person);
                                                        break;
                                                    }
                                    break;
                                case 1:
                                case 2:
                                    if (!schedule[(j*2) + 26].equals("-"))
                                        if (i > 3)
                                            for (int k = 0; k < 2; k++)
                                                for (PersonClass person : present.get(j))
                                                    if (person.getName().equals(schedule[(j*2) + 2]) || person.getName().equals(schedule[(j*2) + 3])) {
                                                        present.get(j).remove(person);
                                                        break;
                                                    }
                                case 3:
                                    if (!schedule[(j*2) + 22].equals("-"))
                                        if (i > 3)
                                            for (int k = 0; k < 2; k++)
                                                for (PersonClass person : present.get(j))
                                                    if (person.getName().equals(schedule[(j*2) - 2]) || person.getName().equals(schedule[(j*2) - 1])) {
                                                        present.get(j).remove(person);
                                                        break;
                                                    }
                                    break;
                            }
                            Collections.shuffle(present.get(j));
                            Collections.sort(present.get(j), new Comparator<PersonClass>() {
                                @Override
                                public int compare(PersonClass o1, PersonClass o2) {
                                    return Integer.valueOf(o1.getLoad()).compareTo(o2.getLoad());
                                }
                            });
                            schedule[(j*2) + 24] = present.get(j).get(0).assign();
                            schedule[(j*2) + 25] = present.get(j).get(1).assign();
                            if (j == 0 && (present.get(j).get(0).getName().equals(schedule[38]) || present.get(j).get(1).getName().equals(schedule[38]))) {
                                for (int k = 32; k < schedule.length; k++)
                                    schedule[k] = "-";
                                for (PersonClass person : personnel)
                                    person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
                            }
                        }
                    }
        }
    }

    public void genTours(String[] schedule, ArrayList<PersonClass> personnel){
        ArrayList<ArrayList<PersonClass>> present = new ArrayList<ArrayList<PersonClass>>();
        for (int i = 32; i < schedule.length; i++)
            schedule[i] = "-";
        if (personnel.size() == 0)
            for (int i = 32; i < schedule.length; i++)
                schedule[i] = "Sin personal.";
        else {
            for (PersonClass person : personnel)
                person.setLoad(Collections.frequency(Arrays.asList(schedule), person.getName()));
            for (int i=0; i<7; i++) {
                present.add(new ArrayList<PersonClass>());
                for (PersonClass person : personnel)
                    if (person.getScheduleStart() <= (11 + i) * 100 && person.getScheduleEnd() >= (12 + i) * 100)   //Si la persona está disponible en esa hora
                        if (!person.getName().equals(schedule[(i*2)+2]) && !person.getName().equals(schedule[(i*2)+3])) {   //Si no topa con elevador principal
                            if (person.getScheduleStart() % 100 > 0) {    //Si la persona entra a una hora inexacta (Ej: 9:30)
                                if (person.getScheduleStart() / 100 != i + 7)   //Si no es hora de almuerzo de esa persona
                                    present.get(i).add(person);
                            } else if (person.getScheduleStart() / 100 != i + 8) {  //Si no es hora de almuerzo de esa persona
                                present.get(i).add(person);
                            } else if (i == 6) {
                                if (!person.getName().equals(schedule[24]) && !person.getName().equals(schedule[25]))   //Si no topa con elevador secundario
                                    present.get(i).add(person);
                            } else present.get(i).add(person);
                        }
            }
            /*for (int i=0; i<present.size(); i++) {
                Log.d("info", "Personas para las "+(i+11));
                for (int j=0; j<present.get(i).size(); j++)
                    Log.d("info", present.get(i).get(j).getName());
            }*/
            for (int i=0; i <= personnel.size(); i++)  //Repite la cantidad del personal
                for (int j=present.size()-1; j >= 0; j--) { //Recorre todos los horarios
                    if (schedule[j+32].equals("-") && present.get(j).size() > 1) {
                        Collections.shuffle(present.get(j));
                        schedule[j+32] = present.get(j).get(0).assign();
                        for (int k=0; k<present.size(); k++)
                            for (int l = 0; l <present.get(k).size(); l++)
                                if (present.get(k).get(l).getName().equals(schedule[j+32])) {
                                    present.get(k).remove(l);
                                    break;
                                }
                    }
                    for (int k = j; k >= 0; k--)    //Revisa si en otros horarios queda un candidato
                        if (schedule[k + 32].equals("-") && present.get(k).size() == 1) { //Revisar si en otros horarios queda una persona
                            schedule[k + 32] = present.get(k).get(0).assign();
                            for (int l = 0; l < present.size(); l++)
                                for (int m = 0; m < present.get(l).size(); m++)
                                    if (present.get(l).get(m).getName().equals(schedule[k + 32])) {
                                        present.get(l).remove(m);   //Eliminar persona de otros horarios para tour
                                        break;
                                    }
                        }
                }
        }
    }
}