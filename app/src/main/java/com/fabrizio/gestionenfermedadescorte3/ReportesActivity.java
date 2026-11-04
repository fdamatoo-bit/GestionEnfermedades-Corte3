package com.fabrizio.gestionenfermedadescorte3;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReportesActivity extends AppCompatActivity {

    private EditText inputNombreUsuario;
    private EditText inputEmailUsuario;
    private EditText inputGravedad;
    private EditText inputContagiosa;

    private Button btnReporteNombre;
    private Button btnReporteEmail;
    private Button btnReporteGravedad;
    private Button btnReporteContagiosa;
    private Button btnVolver;

    private TextView txtResultado;

    private static final String URL_USUARIOS =
            "https://gestion-enfermedades-api.onrender.com/api/usuarios/reportes";

    private static final String URL_ENFERMEDADES =
            "https://gestion-enfermedades-api.onrender.com/api/enfermedades/reportes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reportes);

        inputNombreUsuario =
                findViewById(R.id.inputNombreUsuario);

        inputEmailUsuario =
                findViewById(R.id.inputEmailUsuario);

        inputGravedad =
                findViewById(R.id.inputGravedad);

        inputContagiosa =
                findViewById(R.id.inputContagiosa);

        btnReporteNombre =
                findViewById(R.id.btnReporteNombre);

        btnReporteEmail =
                findViewById(R.id.btnReporteEmail);

        btnReporteGravedad =
                findViewById(R.id.btnReporteGravedad);

        btnReporteContagiosa =
                findViewById(R.id.btnReporteContagiosa);

        txtResultado =
                findViewById(R.id.txtResultado);

        btnVolver =
                findViewById(R.id.btnVolver);


        btnReporteNombre.setOnClickListener(
                v -> reporteUsuariosNombre()
        );

        btnReporteEmail.setOnClickListener(
                v -> reporteUsuariosEmail()
        );

        btnReporteGravedad.setOnClickListener(
                v -> reporteGravedad()
        );

        btnReporteContagiosa.setOnClickListener(
                v -> reporteContagiosa()
        );

        btnVolver.setOnClickListener(
                v -> finish()
        );
    }


    private String obtenerToken() {

        return getSharedPreferences(
                "sesion",
                MODE_PRIVATE
        ).getString(
                "jwt_token",
                ""
        );
    }


    // =====================================================
    // REPORTE USUARIOS POR NOMBRE
    // =====================================================

    private void reporteUsuariosNombre() {

        String nombre =
                inputNombreUsuario
                        .getText()
                        .toString()
                        .trim();


        if (nombre.isEmpty()) {

            Toast.makeText(
                    this,
                    "Digite un nombre",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        String url =
                URL_USUARIOS
                        + "/nombre?nombre="
                        + Uri.encode(nombre);


        ejecutarReporte(url);
    }


    // =====================================================
    // REPORTE USUARIOS POR EMAIL
    // =====================================================

    private void reporteUsuariosEmail() {

        String email =
                inputEmailUsuario
                        .getText()
                        .toString()
                        .trim();


        if (email.isEmpty()) {

            Toast.makeText(
                    this,
                    "Digite un email",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        String url =
                URL_USUARIOS
                        + "/email?email="
                        + Uri.encode(email);


        ejecutarReporte(url);
    }


    // =====================================================
    // REPORTE ENFERMEDADES POR GRAVEDAD
    // =====================================================

    private void reporteGravedad() {

        String gravedad =
                inputGravedad
                        .getText()
                        .toString()
                        .trim();


        if (gravedad.isEmpty()) {

            Toast.makeText(
                    this,
                    "Digite el nivel de gravedad",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        String url =
                URL_ENFERMEDADES
                        + "/gravedad?nivelGravedad="
                        + Uri.encode(gravedad);


        ejecutarReporte(url);
    }


    // =====================================================
    // REPORTE ENFERMEDADES POR CONTAGIOSA
    // =====================================================

    private void reporteContagiosa() {

        String valor =
                inputContagiosa
                        .getText()
                        .toString()
                        .trim()
                        .toLowerCase();


        if (
                !valor.equals("true")
                        &&
                        !valor.equals("false")
        ) {

            Toast.makeText(
                    this,
                    "Digite true o false",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        String url =
                URL_ENFERMEDADES
                        + "/contagiosa?valor="
                        + valor;


        ejecutarReporte(url);
    }


    // =====================================================
    // EJECUTAR REPORTE
    // =====================================================

    private void ejecutarReporte(
            String direccion
    ) {

        txtResultado.setText(
                "Consultando..."
        );


        new Thread(() -> {

            HttpURLConnection conexion = null;


            try {

                URL url =
                        new URL(
                                direccion
                        );


                conexion =
                        (HttpURLConnection)
                                url.openConnection();


                conexion.setRequestMethod(
                        "GET"
                );


                conexion.setConnectTimeout(
                        60000
                );

                conexion.setReadTimeout(
                        60000
                );


                conexion.setRequestProperty(
                        "Accept",
                        "application/json"
                );


                conexion.setRequestProperty(
                        "Authorization",
                        "Bearer " + obtenerToken()
                );


                int codigo =
                        conexion.getResponseCode();


                if (
                        codigo >= 200
                                &&
                                codigo < 300
                ) {

                    String respuesta =
                            leerRespuesta(
                                    conexion.getInputStream()
                            );


                    String resultado =
                            formatearJson(
                                    respuesta
                            );


                    runOnUiThread(() ->
                            txtResultado.setText(
                                    resultado
                            )
                    );


                } else {

                    String error = "";


                    if (
                            conexion.getErrorStream()
                                    != null
                    ) {

                        error =
                                leerRespuesta(
                                        conexion.getErrorStream()
                                );
                    }


                    String mensaje =
                            "Error "
                                    + codigo
                                    + "\n"
                                    + error;


                    runOnUiThread(() ->
                            txtResultado.setText(
                                    mensaje
                            )
                    );
                }


            } catch (Exception e) {


                runOnUiThread(() ->
                        txtResultado.setText(
                                "Error: "
                                        + e.getMessage()
                        )
                );


            } finally {


                if (conexion != null) {

                    conexion.disconnect();
                }
            }


        }).start();
    }


    // =====================================================
    // LEER RESPUESTA HTTP
    // =====================================================

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


        while (
                (linea = lector.readLine())
                        != null
        ) {

            respuesta.append(
                    linea
            );
        }


        lector.close();


        return respuesta.toString();
    }


    // =====================================================
    // FORMATEAR JSON Y OCULTAR PASSWORD
    // =====================================================

    private String formatearJson(
            String respuesta
    ) {


        try {


            // ---------------------------------------------
            // RESPUESTA TIPO ARRAY
            // ---------------------------------------------

            if (
                    respuesta
                            .trim()
                            .startsWith("[")
            ) {


                JSONArray array =
                        new JSONArray(
                                respuesta
                        );


                for (
                        int i = 0;
                        i < array.length();
                        i++
                ) {


                    JSONObject objeto =
                            array.optJSONObject(
                                    i
                            );


                    if (objeto != null) {

                        // No mostrar contraseña
                        objeto.remove(
                                "password"
                        );
                    }
                }


                return array.toString(
                        2
                );


            } else {


                // ---------------------------------------------
                // RESPUESTA TIPO OBJETO
                // ---------------------------------------------

                JSONObject objeto =
                        new JSONObject(
                                respuesta
                        );


                // No mostrar contraseña
                objeto.remove(
                        "password"
                );


                return objeto.toString(
                        2
                );
            }


        } catch (Exception e) {


            return respuesta;
        }
    }
}