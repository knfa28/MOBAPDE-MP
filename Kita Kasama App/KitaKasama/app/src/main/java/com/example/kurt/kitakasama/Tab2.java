package com.example.kurt.kitakasama;

/**
 * Created by Seaver on 4/14/2016.
 */
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kurt.kitakasama.R;
import com.example.kurt.kitakasama.TrackerMapActivity;

/**
 * Created by hp1 on 21-01-2015.
 */
public class Tab2 extends Fragment {
    Button trackSession;
    EditText sessionCode;
    EditText trackerCode;
    int sCode;
    int tCode;
    boolean isTracker = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_2,container,false);
        trackSession = (Button) v.findViewById(R.id.btn_track);
        sessionCode = (EditText) v.findViewById(R.id.session_edit);
        trackerCode = (EditText) v.findViewById(R.id.tracker_edit);

        trackSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sCode = Integer.parseInt(sessionCode.getText().toString());
                tCode = Integer.parseInt(trackerCode.getText().toString());

                Intent intent = new Intent(v.getContext(),TrackerMapActivity.class);
                new InitSSH().execute();

                System.out.println("IM WAITING");

                if(isTracker){
                   intent.putExtra("sessionCode", sCode);
                   intent.putExtra("trackerCode", tCode);
                   startActivity(intent);
               }
            }
        });

        return v;
    }

    class InitSSH extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            if(MySQLSSHModel.getSession(sCode) != null) {
                isTracker = true;
                MySQLSSHModel.updateTrackerStatus(sCode, tCode, "Connected");
            }else{
                Toast.makeText(getContext(), "Session does not exist.", Toast.LENGTH_SHORT);
                isTracker = false;
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            Toast toast = Toast.makeText(getContext(),
                    "Error connecting to Server via SSH", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 400);
            toast.show();

        }
    }
}