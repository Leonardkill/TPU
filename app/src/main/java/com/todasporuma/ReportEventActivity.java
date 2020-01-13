package com.todasporuma;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Calendar;

public class ReportEventActivity extends AppCompatActivity  implements DatePickerFragment.DatePickerListener, AdapterView.OnItemSelectedListener {



    private EditText editNome_oco,editDescOcorrencia,editLocalOcorrencia;
    private Button btn_salva_oco,btn_data;
    private TextView tvDisplayDate;
    private Spinner spinner_tipo;
    private DatabaseReference mDatabase;

    public static Intent createIntent(Context context) {
        return new Intent(context, ReportEventActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_event);

        editNome_oco = findViewById(R.id.editText8);
        editDescOcorrencia = findViewById(R.id.editText3);
        editLocalOcorrencia = findViewById(R.id.editText9);
        btn_salva_oco = findViewById(R.id.save_oco);
        spinner_tipo = findViewById(R.id.spinner);
        tvDisplayDate = findViewById(R.id.tvDisplaydate);
        btn_data = findViewById(R.id.btnShowDatePicker);

        mDatabase = FirebaseDatabase.getInstance().getReference();


        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        Button btnShowDatePicker = findViewById(R.id.btnShowDatePicker);
        btnShowDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.setCancelable(false);
                datePickerFragment.show(getSupportFragmentManager(),"date Picker");
            }
        });

        btn_salva_oco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = editNome_oco.getText().toString();
                String descricao = editDescOcorrencia.getText().toString();
                String local = editLocalOcorrencia.getText().toString();
                String tipo_spinner = spinner_tipo.getSelectedItem().toString();
                String data = tvDisplayDate.getText().toString();

                if(nome.isEmpty()){
                    editNome_oco.setError("Digite seu nome");
                    editNome_oco.requestFocus();
                }

                else if (descricao.isEmpty()){
                    editDescOcorrencia.setError("Informe uma descrição para a ocorrência");
                    editDescOcorrencia.requestFocus();
                }

                else if (local.isEmpty()){
                    editLocalOcorrencia.setError("Informe o local da ocorrência");
                    editLocalOcorrencia.requestFocus();
                }

                else if  (tipo_spinner.isEmpty()){
                    TextView errorText = (TextView)spinner_tipo.getSelectedView();
                    errorText.setError("Erro");
                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
                    errorText.setText("Seleciona tipo ocorrencia");//changes the selected item text to this
                    spinner_tipo.requestFocus();
                }

                else
                    {
                mDatabase.child("Ocorrencias").child("nameReciver").setValue(nome);
                mDatabase.child("Ocorrencias").child("descOcorrencia").setValue(descricao);
                mDatabase.child("Ocorrencias").child("localOcorrencia").setValue(local);
                mDatabase.child("Ocorrencias").child("tipoOcorrencia").setValue(tipo_spinner);
                mDatabase.child("Ocorrencias").child("dataOcorrencia").setValue(data);

                Toast.makeText(ReportEventActivity.this,"Ocorrência registrada com sucesso",Toast.LENGTH_LONG).show();

                }

            }
        });



    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH,month);
        cal.set(Calendar.DAY_OF_MONTH,day);
        String date = DateFormat.getDateInstance().format(cal.getTime());


        if(tvDisplayDate.getText() == "") {

            btn_data.requestFocus();
            tvDisplayDate.setError("Selecione uma data");

        }
        else {
            tvDisplayDate.setText(date);

        }


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
