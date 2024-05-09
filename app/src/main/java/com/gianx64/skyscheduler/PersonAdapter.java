package com.gianx64.skyscheduler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PersonAdapter extends BaseAdapter {
    ArrayList<PersonClass> personnel;
    LayoutInflater inflater;
    Context context;
    PersonDB db;

    public PersonAdapter(@NonNull Context context, ArrayList<PersonClass> personnel, PersonDB db) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.personnel = personnel;
        this.db = db;
    }

    @Override
    public int getCount() {
        return personnel.size();
    }

    @Override
    public PersonClass getItem(int position) {
        PersonClass person = personnel.get(position);
        return person;
    }

    @Override
    public long getItemId(int position) {
        PersonClass person = personnel.get(position);
        return person.getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.person_list_item, null);

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView schedule = (TextView) convertView.findViewById(R.id.schedule);
        TextView load = (TextView) convertView.findViewById(R.id.load);
        name.setText("Nombre: "+personnel.get(position).getName());
        schedule.setText("Horario: "+personnel.get(position).getScheduleStart()+" - "+personnel.get(position).getScheduleEnd());
        load.setText("Carga: "+personnel.get(position).getLoad());
        Button edit = (Button) convertView.findViewById(R.id.edit);
        Button delete = (Button) convertView.findViewById(R.id.delete);
        edit.setTag(position);
        delete.setTag(position);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = Integer.parseInt(v.getTag().toString());
                final Dialog dialog = new Dialog(context);
                dialog.setTitle("Editar registro");
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.person);
                dialog.show();
                final EditText name = (EditText) dialog.findViewById(R.id.nombre);
                final EditText scheduleStart = (EditText) dialog.findViewById(R.id.horarioInicio);
                final EditText scheduleEnd = (EditText) dialog.findViewById(R.id.horarioFin);
                Button save = (Button) dialog.findViewById(R.id.save);
                Button cancel = (Button) dialog.findViewById(R.id.cancel);
                name.setText(personnel.get(position).getName());
                scheduleStart.setText(""+personnel.get(position).getScheduleStart());
                scheduleEnd.setText(""+personnel.get(position).getScheduleEnd());
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try{
                            PersonClass editedPerson = new PersonClass(personnel.get(position).getId(), name.getText().toString(), Integer.parseInt(scheduleStart.getText().toString()), Integer.parseInt(scheduleEnd.getText().toString()));
                            db.update(editedPerson);
                        } catch (Exception e) {
                            Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show();
                        }
                        personnel = db.readAll();
                        notifyDataSetChanged();
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
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = Integer.parseInt(v.getTag().toString());
                AlertDialog.Builder delete = new AlertDialog.Builder(context);
                delete.setMessage("¿Eliminar registro?");
                delete.setCancelable(false);
                delete.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.remove(personnel.get(position).getId());
                        personnel = db.readAll();
                        notifyDataSetChanged();
                    }
                });
                delete.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //No se hace nada.
                    }
                });
                delete.show();
            }
        });
        return convertView;
    }
}
