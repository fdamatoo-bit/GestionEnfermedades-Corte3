package com.fabrizio.gestionenfermedadescorte3;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RecuperarActivity extends AppCompatActivity {

    private EditText inputEmail;
    private Button btnEnviar;
    private Button btnVolver;

    private static final String URL_RECUPERAR =
            "https://gestion-enfermedades-api.onrender.com/api/auth/recuperar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar);

        inputEmail = findViewById(R.id.inputEmail);
        btnEnviar = findViewById(R.id.btnEnviar);
        btnVolver = findViewById(R.id.btnVolver);

        btnEnviar.setOnClickListener(v -> recuperarContrasena());

        btnVolver.setOnClickListener(v -> finish());
    }

    private void recuperarContrasena() {

        String email =
                inputEmail.getText().toString().trim();

        if (email.isEmpty()) {

            Toast.makeText(
                    this,
                    "Digite el correo electrónico",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        btnEnviar.setEnabled(false);

        new Thread(() -> {

            HttpURLConnection conexion = null;

            try {

                URL url =
                        new URL(URL_RECUPERAR);

                conexion =
                        (HttpURLConnection) url.openConnection();

                conexion.setRequestMethod("POST");

                conexion.setConnectTimeout(60000);
                conexion.setReadTimeout(60000);

                conexion.setRequestProperty(
                        "Content-Type",
                        "application/json"
                );

                conexion.setRequestProperty(
                        "Accept",
                        "application/json"
                );

                conexion.setDoOutput(true);

                JSONObject datos =
                        new JSONObject();

                datos.put("email", email);

                OutputStream salida =
                        conexion.getOutputStream();

                salida.write(
                        datos.toString()
                                .getBytes("UTF-8")
                );

                salida.flush();
                salida.close();

                int codigo =
                        conexion.getResponseCode();

                InputStream entrada;

                if (codigo >= 200 && codigo < 300) {

                    entrada =
                            conexion.getInputStream();

                } else {

                    entrada =
                            conexion.getErrorStream();
                }

                String respuesta = "";

                if (entrada != null) {

                    respuesta =
                            leerRespuesta(entrada);
                }

                String mensajeFinal =
                        respuesta;

                runOnUiThread(() -> {

                    btnEnviar.setEnabled(true);

                    if (codigo >= 200 && codigo < 300) {

                        Toast.makeText(
                                RecuperarActivity.this,
                                mensajeFinal.isEmpty()
                                        ? "Recordatorio enviado correctamente"
                                        : mensajeFinal,
                                Toast.LENGTH_LONG
                        ).show();

                    } else {

                        Toast.makeText(
                                RecuperarActivity.this,
                                "Error al recuperar contraseña. Código: "
                                        + codigo
                                        + "\n"
                                        + mensajeFinal,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });

            } catch (Exception e) {

                runOnUiThread(() -> {

                    btnEnviar.setEnabled(true);

                    Toast.makeText(
                            RecuperarActivity.this,
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });

            } finally {

                if (conexion != null) {
                    conexion.disconnect();
                }
            }

        }).start();
    }

    private String leerRespuesta(
            InputStream entrada
    ) throws Exception {

        BufferedReader lector =
                new BufferedReader(
                        new InputStreamReader(
                                entrada,
                                "UTF-8"
                        )
                );

        StringBuilder respuesta =
                new StringBuilder();

        String linea;

        while ((linea = lector.readLine()) != null) {
            respuesta.append(linea);
        }

        lector.close();

        return respuesta.toString();
    }
}