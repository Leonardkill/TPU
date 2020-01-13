package com.todasporuma;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.razerdp.widget.animatedpieview.AnimatedPieView;
import com.razerdp.widget.animatedpieview.AnimatedPieViewConfig;
import com.razerdp.widget.animatedpieview.data.SimplePieInfo;

public class GraficActivity extends AppCompatActivity {

    public static Intent createIntent(Context context) {
        return new Intent(context, GraficActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafic);

        Button btnVoltar = findViewById(R.id.btnVoltar);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(ReportEventActivity.createIntent(GraficActivity.this));
                finish();
            }
        });


        drawPie();
    }

    public void drawPie ()
    {
        AnimatedPieView mAnimatedPieView = findViewById(R.id.animatedPieView);
        AnimatedPieViewConfig config = new AnimatedPieViewConfig();
        config.startAngle(-90)// Starting angle offset
                .addData(new SimplePieInfo(20.0, Color.parseColor("#00A8BD"), "Ass: nas ruas"))
                .addData(new SimplePieInfo(15.0, Color.parseColor("#D5ADED"), "Agre: doméstico"))
                .addData(new SimplePieInfo(30.0, Color.parseColor("#CE2D4F"), "Ass: Moral"))//Data (bean that implements the IPieInfo interface)
                .addData(new SimplePieInfo(18.0, Color.parseColor("#FFC983"), "Ass: psicológico")).drawText(true).strokeMode(false)

      .duration(2000).textSize(30);// draw pie animation duration

// The following two sentences can be replace directly 'mAnimatedPieView.start (config); '
        mAnimatedPieView.applyConfig(config);
        mAnimatedPieView.start();
    }

}
