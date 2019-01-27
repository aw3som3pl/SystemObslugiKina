package com.wat.jannowakowski.systemobslugikina.activities.views;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.wat.jannowakowski.systemobslugikina.R;
import com.wat.jannowakowski.systemobslugikina.activities.presenters.TicketCheckerPresenter;

public class TicketChecker extends AppCompatActivity implements TicketCheckerPresenter.View{


    private Button scanButton;
    private Button backButton;

    private TicketCheckerPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_checker);

        presenter = new TicketCheckerPresenter(this);

        scanButton = findViewById(R.id.scan_button);
        backButton = findViewById(R.id.back_button);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQrCodeScanner();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToMenu();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                presenter.checkTicket(contents);
            } else if (resultCode == RESULT_CANCELED) {
                showToastMsg("Błąd oczytu QR!");
            }
        }
    }

    @Override
    public void startQrCodeScanner(){
        Intent intent = new Intent(getApplicationContext(),CaptureActivity.class);
        intent.setAction("com.google.zxing.client.android.SCAN");
        intent.putExtra("SAVE_HISTORY", false);
        startActivityForResult(intent, 0);
    }

    @Override
    public void showToastMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void navigateToMenu() {
        onBackPressed();
    }
}
