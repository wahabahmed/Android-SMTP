package com.demo.com.contact;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class InfoActivity extends Activity implements OnClickListener {

    Session session = null;
    ProgressDialog pdialog = null;
    Context context = null;
    EditText userName, reciep, phone, address;
    String rec, subject, textMessage;
    private static ConnectivityManager connectivityManager;

    String category;
    private static final String e = BuildConfig.e.toString();
    private static final String k = BuildConfig.k.toString();
    private Location mLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        context = this;
        category = getIntent().getStringExtra("category");
        Button login = (Button) findViewById(R.id.btn_submit);
        userName = (EditText) findViewById(R.id.et_name);
        reciep = (EditText) findViewById(R.id.et_email);
        phone = (EditText) findViewById(R.id.et_phone);
        address = (EditText) findViewById(R.id.et_address);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        login.setOnClickListener(this);

        if (isInternetConnected()) {
            GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
            mLocation = gpsTracker.getLocation();
            //Log.i("test", "" + mLocation.getLongitude());
            if (mLocation != null) {
                address.setText(getAddress(mLocation));
            }
        }
    }

    private String getAddress(Location location) {

        Geocoder gcd = new Geocoder(getBaseContext(),
                Locale.getDefault());
        List<android.location.Address> addresses;
        String completeLocation = "";

        try {
            addresses = gcd.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);

            if (addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                /*String locality = addresses.get(0).getLocality();
                String subLocality = addresses.get(0).getSubLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();*/

                completeLocation = address;
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return completeLocation;
    }

    @Override
    public void onClick(View v) {
        rec = reciep.getText().toString();
        subject = "Email Inquiry from App";
        textMessage = "<HTML>" + "Category: " + category + " <BR> " +
                "Name: " + userName.getText().toString() + " <BR> " +
                "EMAIL: " + reciep.getText().toString() + " <BR> " +
                "Address: " + address.getText().toString() + " <BR>" +
                "Phone: " + phone.getText().toString() +
                "Phone: " + phone.getText().toString() +
                "<HTML>";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(e, k);
            }
        });

        pdialog = ProgressDialog.show(context, "", "Sending Mail...", true);

        RetreiveFeedTask task = new RetreiveFeedTask();
        task.execute();
    }

    public boolean isInternetConnected() {
        boolean connected = false;

        if (connectivityManager != null && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        } else {
            //We are not connected to network
            connected = false;
            Toast.makeText(this, "Please Check Your Internet Connecteion. ", Toast.LENGTH_LONG).show();
        }

        return connected;
    }

    class RetreiveFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.i("test", e);
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(rec));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(e));
                message.setSubject(subject);
                message.setContent(textMessage, "text/html; charset=utf-8");
                Transport.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            pdialog.dismiss();
            reciep.setText("");
            address.setText("");
            phone.setText("");
            Toast.makeText(getApplicationContext(), "Message sent", Toast.LENGTH_LONG).show();
        }
    }
}
