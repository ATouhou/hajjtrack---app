package com.maxxsol.hajjtrack.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.maxxsol.hajjtrack.AppConstants;
import com.maxxsol.hajjtrack.R;

public class Destination extends DialogFragment {

	private Button btn_travelByCar, btn_travelByFoot;
	private Destination _dialog;

	public interface FinishDestinationDialogListener {
		public void onFinishDestinationDialogListener(String travelBy);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = (LinearLayout) inflater.inflate(
				R.layout.dialogfragment_destination, container, false);

		btn_travelByCar = (Button) view.findViewById(R.id.btn_travelByCar);
		btn_travelByFoot = (Button) view.findViewById(R.id.btn_travelByFoot);

		_dialog = this;
		getDialog().setTitle(getResources().getString(R.string.destination));

		btn_travelByCar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FinishDestinationDialogListener activity = (FinishDestinationDialogListener) getActivity();
				String travelBy = AppConstants.DIRECTION_MODE_DRIVING;
				activity.onFinishDestinationDialogListener(travelBy);
				_dialog.dismiss();
			}
		});

		btn_travelByFoot.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FinishDestinationDialogListener activity = (FinishDestinationDialogListener) getActivity();
				String travelBy = AppConstants.DIRECTION_MODE_WALKING;
				activity.onFinishDestinationDialogListener(travelBy);
				_dialog.dismiss();
			}
		});

		return view;
	}

}
