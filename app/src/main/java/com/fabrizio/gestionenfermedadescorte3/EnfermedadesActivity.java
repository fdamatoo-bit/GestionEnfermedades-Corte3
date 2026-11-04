package com.fabrizio.gestionenfermedadescorte3;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
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

public class EnfermedadesActivity extends AppCompatActivity {

    private EditText inputId;
    private EditText inputNombre;
    private EditText inputNombreCientifico;
    private EditText inputNivelGravedad;
    private EditText inputSintomas;
    private EditText inputMedicamentos;

    private CheckBox checkContagiosa;
    private CheckBox checkCubiertaPost;
    private CheckBox checkIncapacidad;

    private Button btnConsultar;
    private Button btnCrear;
    private Button btnActualizar;
    private Button btnEliminar;
    private Button btnVolver;

    private static final String URL_BASE =
            "https://gestion-enfermedades-api.onrender.com/api/enfermedades";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enfermedades);

        inputId = findViewById(R.id.inputId);
        inputNombre = findViewById(R.id.inputNombre);
        inputNombreCientifico = findViewById(R.id.inputNombreCientifico);
        inputNivelGravedad = findViewById(R.id.inputNivelGravedad);
        inputSintomas = findViewById(R.id.inputSintomas);
        inputMedicamentos = findViewById(R.id.inputMedicamentos);

        checkContagiosa = findViewById(R.id.checkContagiosa);
        checkCubiertaPost = findViewById(R.id.checkCubiertaPost);
        checkIncapacidad = findViewById(R.id.checkIncapacidad);

        btnConsultar = findViewById(R.id.btnConsultar);
        btnCrear = findViewById(R.id.btnCrear);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnVolver = findViewById(R.id.btnVolver);

        btnConsultar.setOnClickListener(v -> consultar());
        btnCrear.setOnClickListener(v -> crear());
        btnActualizar.setOnClickListener(v -> actualizar());
        btnEliminar.setOnClickListener(v -> eliminar());
        btnVolver.setOnClickListener(v -> finish());
    }

    private String obtenerToken() {
        return getSharedPreferences("sesion", MODE_PRIVATE)
                .getString("jwt_token", "");
    }

    private JSONObject construirJson() throws Exception {

        JSONObject datos = new JSONObject();

        String id = inputId.getText().toString().trim();

        if (!id.isEmpty()) {
            datos.put("id", Integer.parseInt(id));
        }

        datos.put(
                "nombre",
                inputNombre.getText().toString().trim()
        );

        datos.put(
                "nombreCientifico",
                inputNombreCientifico.getText().toString().trim()
        );

        datos.put(
                "nivelGravedad",
                inputNivelGravedad.getText().toString().trim()
        );

        datos.put(
                "sintomas",
                inputSintomas.getText().toString().trim()
        );

        datos.put(
                "medicamentos",
                inputMedicamentos.getText().toString().trim()
        );

        datos.put(
                "esContagiosa",
                checkContagiosa.isChecked()
        );

        datos.put(
                "esCubiertaPorPost",
                checkCubiertaPost.isChecked()
        );

        datos.put(
                "requiereIncapacidad",
                checkIncapacidad.isChecked()
        );

        return datos;
    }

    private boolean validarCampos() {

        if (inputId.getText().toString().trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Digite un ID",
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }

        if (inputNombre.getText().toString().trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Digite el nombre",
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }

        return true;
    }

    private void crear() {

        if (!validarCampos()) {
            return;
        }

        new Thread(() -> {

            HttpURLConnection conexion = null;

            try {

                conexion = prepararConexion(
                        URL_BASE,
                        "POST"
                );

                JSONObject datos = construirJson();

                enviarJson(conexion, datos);

                int codigo = conexion.getResponseCode();

                if (codigo >= 200 && codigo < 300) {

                    runOnUiThread(() -> {

                        Toast.makeText(
                                EnfermedadesActivity.this,
                                "Enfermedad creada correctamente",
                                Toast.LENGTH_LONG
                        ).show();

                        limpiarCampos();
                    });

                } else {

                    String error = leerError(conexion);

                    runOnUiThread(() ->
                            Toast.makeText(
                                    EnfermedadesActivity.this,
                                    "Error al crear. Código: "
                                            + codigo
                                            + "\n"
                                            + error,
                                    Toast.LENGTH_LONG
                            ).show()
                    );
                }

            } catch (Exception e) {

                mostrarError(e);

            } finally {

                if (conexion != null) {
                    conexion.disconnect();
                }
            }

        }).start();
    }

    private void consultar() {

        String id =
                inputId.getText().toString().trim();

        if (id.isEmpty()) {

            Toast.makeText(
                    this,
                    "Digite el ID de la enfermedad",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        new Thread(() -> {

            HttpURLConnection conexion = null;

            try {

                conexion = prepararConexion(
                        URL_BASE + "/" + id,
                        "GET"
                );

                int codigo =
                        conexion.getResponseCode();

                if (codigo >= 200 && codigo < 300) {

                    String respuesta =
                            leerRespuesta(
                                    conexion.getInputStream()
                            );

                    JSONObject json =
                            new JSONObject(respuesta);

                    runOnUiThread(() -> {

                        inputNombre.setText(
                                json.optString("nombre")
                        );

                        inputNombreCientifico.setText(
                                json.optString("nombreCientifico")
                        );

                        inputNivelGravedad.setText(
                                json.optString("nivelGravedad")
                        );

                        inputSintomas.setText(
                                json.optString("sintomas")
                        );

                        inputMedicamentos.setText(
                                json.optString("medicamentos")
                        );

                        checkContagiosa.setChecked(
                                json.optBoolean("esContagiosa")
                        );

                        checkCubiertaPost.setChecked(
                                json.optBoolean("esCubiertaPorPost")
                        );

                        checkIncapacidad.setChecked(
                                json.optBoolean("requiereIncapacidad")
                        );

                        Toast.makeText(
                                EnfermedadesActivity.this,
                                "Enfermedad encontrada",
                                Toast.LENGTH_SHORT
                        ).show();
                    });

                } else {

                    String error = leerError(conexion);

                    runOnUiThread(() ->
                            Toast.makeText(
                                    EnfermedadesActivity.this,
                                    "No encontrada. Código: "
                                            + codigo
                                            + "\n"
                                            + error,
                                    Toast.LENGTH_LONG
                            ).show()
                    );
                }

            } catch (Exception e) {

                mostrarError(e);

            } finally {

                if (conexion != null) {
                    conexion.disconnect();
                }
            }

        }).start();
    }

    private void actualizar() {

        if (!validarCampos()) {
            return;
        }

        String id =
                inputId.getText().toString().trim();

        new Thread(() -> {

            HttpURLConnection conexion = null;

            try {

                conexion = prepararConexion(
                        URL_BASE + "/" + id,
                        "PUT"
                );

                JSONObject datos =
                        construirJson();

                enviarJson(conexion, datos);

                int codigo =
                        conexion.getResponseCode();

                if (codigo >= 200 && codigo < 300) {

                    runOnUiThread(() ->
                            Toast.makeText(
                                    EnfermedadesActivity.this,
                                    "Enfermedad actualizada correctamente",
                                    Toast.LENGTH_LONG
                            ).show()
                    );

                } else {

                    String error =
                            leerError(conexion);

                    runOnUiThread(() ->
                            Toast.makeText(
                                    EnfermedadesActivity.this,
                                    "Error al actualizar. Código: "
                                            + codigo
                                            + "\n"
                                            + error,
                                    Toast.LENGTH_LONG
                            ).show()
                    );
                }

            } catch (Exception e) {

                mostrarError(e);

            } finally {

                if (conexion != null) {
                    conexion.disconnect();
                }
            }

        }).start();
    }

    private void eliminar() {

        String id =
                inputId.getText().toString().trim();

        if (id.isEmpty()) {

            Toast.makeText(
                    this,
                    "Digite el ID de la enfermedad",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        new Thread(() -> {

            HttpURLConnection conexion = null;

            try {

                conexion = prepararConexion(
                        URL_BASE + "/" + id,
                        "DELETE"
                );

                int codigo =
                        conexion.getResponseCode();

                if (codigo >= 200 && codigo < 300) {

                    runOnUiThread(() -> {

                        Toast.makeText(
                                EnfermedadesActivity.this,
                                "Enfermedad eliminada correctamente",
                                Toast.LENGTH_LONG
                        ).show();

                        limpiarCampos();
                    });

                } else {

                    String error =
                            leerError(conexion);

                    runOnUiThread(() ->
                            Toast.makeText(
                                    EnfermedadesActivity.this,
                                    "Error al eliminar. Código: "
                                            + codigo
                                            + "\n"
                                            + error,
                                    Toast.LENGTH_LONG
                            ).show()
                    );
                }

            } catch (Exception e) {

                mostrarError(e);

            } finally {

                if (conexion != null) {
                    conexion.disconnect();
                }
            }

        }).start();
    }

    private HttpURLConnection prepararConexion(
            String direccion,
            String metodo
    ) throws Exception {

        URL url =
                new URL(direccion);

        HttpURLConnection conexion =
                (HttpURLConnection) url.openConnection();

        conexion.setRequestMethod(metodo);

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

        conexion.setRequestProperty(
                "Authorization",
                "Bearer " + obtenerToken()
        );

        if (
                metodo.equals("POST")
                        || metodo.equals("PUT")
        ) {
            conexion.setDoOutput(true);
        }

        return conexion;
    }

    private void enviarJson(
            HttpURLConnection conexion,
            JSONObject datos
    ) throws Exception {

        OutputStream salida =
                conexion.getOutputStream();

        salida.write(
                datos.toString().getBytes("UTF-8")
        );

        salida.flush();
        salida.close();
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

        while (
                (linea = lector.readLine()) != null
        ) {
            respuesta.append(linea);
        }

        lector.close();

        return respuesta.toString();
    }

    private String leerError(
            HttpURLConnection conexion
    ) {

        try {

            InputStream entrada =
                    conexion.getErrorStream();

            if (entrada == null) {
                return "";
            }

            String respuesta =
                    leerRespuesta(entrada);

            if (respuesta.length() > 180) {
                return respuesta.substring(0, 180);
            }

            return respuesta;

        } catch (Exception e) {

            return "";
        }
    }

    private void limpiarCampos() {

        inputId.setText("");
        inputNombre.setText("");
        inputNombreCientifico.setText("");
        inputNivelGravedad.setText("");
        inputSintomas.setText("");
        inputMedicamentos.setText("");

        checkContagiosa.setChecked(false);
        checkCubiertaPost.setChecked(false);
        checkIncapacidad.setChecked(false);
    }

    private void mostrarError(Exception e) {

        runOnUiThread(() ->
                Toast.makeText(
                        EnfermedadesActivity.this,
                        "Error: " + e.getMessage(),
                        Toast.LENGTH_LONG
                ).show()
        );
    }
}