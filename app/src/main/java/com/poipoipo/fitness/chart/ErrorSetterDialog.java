package com.poipoipo.fitness.chart;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.poipoipo.fitness.R;

public class ErrorSetterDialog extends DialogFragment {
    View view;
    EditText inputLatError;
    EditText inputLngError;
    OnPositiveClickListener mListener;

    public static ErrorSetterDialog newInstance(LatLng latLng) {
        Bundle args = new Bundle();
        args.putDouble("lat", latLng.latitude);
        args.putDouble("lng", latLng.longitude);
        ErrorSetterDialog fragment = new ErrorSetterDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(getActivity(), R.layout.error_setter_dialog, null);
        inputLatError = (EditText) view.findViewById(R.id.input_lat_error);
        inputLngError = (EditText) view.findViewById(R.id.input_lng_error);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        inputLatError.setText(new StringBuilder().append(getArguments().getDouble("lat")));
        inputLngError.setText(new StringBuilder().append(getArguments().getDouble("lng")));
        AlertDialog dialog = builder.setTitle("Set Latitude And Longitude Error")
                .setView(view)
                .setPositiveButton("SAVE", null)
                .setNegativeButton("CANCEL", null).create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onPositiveClick(Double.parseDouble(inputLatError.getText().toString()),
                        Double.parseDouble(inputLngError.getText().toString()));
                dismiss();
            }
        });
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        mListener = (OnPositiveClickListener) context;
        super.onAttach(context);
    }

    public interface OnPositiveClickListener {
        void onPositiveClick(double lat_error, double lng_error);
    }
}
