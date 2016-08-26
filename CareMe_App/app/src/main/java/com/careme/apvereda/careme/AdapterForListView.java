package com.careme.apvereda.careme;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import db.RoutineDB;
import entities.Routine;
import entities.RoutinePlace;

/**
 * Esta obra está sujeta a la licencia Reconocimiento-CompartirIgual 4.0 Internacional de
 * Creative Commons. Para ver una copia de esta licencia,
 * visite http://creativecommons.org/licenses/by-sa/4.0/.
 *
 * CareMe, creado por Alejandro Perez Vereda el 29/7/15.
 *
 * This work is licensed under the Creative Commons Attribution-ShareAlike 4.0 International
 * License. To view a copy of this license,
 * visit http://creativecommons.org/licenses/by-sa/4.0/.
 *
 * CareMe, created by Alejandro Perez Vereda on 29/7/15.
 *
 * Contact: aperezvereda@gmail.com
 */

public class AdapterForListView extends BaseAdapter {
	Activity context;
	List<Routine> data;
	RoutineDB db;

	public AdapterForListView(Activity context, List<Routine> data) {
		super();
		this.context = context;
		this.data = data;
		db = RoutineDB.getInstance();
		db.init(context);
	}

	public void setData(List<Routine> data) {
		this.data = data;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.listitem_routine, null);

		List<RoutinePlace> places = db.getRoutinePlaces(data.get(position).getId());
		TextView lblAddress = (TextView) convertView.findViewById(R.id.lbladdress);
		lblAddress.setText(places.get(1).getPlace().getDescription());
		//lblAddress.setText(data.get(position).getLatitude()+"");

		TextView lblHour = (TextView) convertView.findViewById(R.id.lblhour);
		lblHour.setText(getDateTime(data.get(position).getStart()));
		//lblHour.setText(getDateTime(data.get(position).getDate()));

		return (convertView);
	}

	private String getDateTime(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
		return dateFormat.format(date);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
}
