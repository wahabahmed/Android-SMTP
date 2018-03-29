package com.demo.com.contact;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        RadioButton radioButton = (RadioButton) view;
        boolean checked = radioButton.isChecked();
        String value = radioButton.getText().toString();

        Toast.makeText(this, "Value: " + value, Toast.LENGTH_LONG).show();
        // Check which radio button was clicked
        /*switch (view.getId()) {
            case R.id.radio_dept3:
                value = "Plumbing";
                break;
            case R.id.radio_dept2:
                value = "Electricion";
                break;
            case R.id.technician:
                value = "Electricion";
                break;
        }*/

        Intent intent = new Intent(this, InfoActivity.class);
        intent.putExtra("category", value);
        startActivity(intent);
    }
}
