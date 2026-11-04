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

public class UsuariosActivity extends AppCompatActivity {

    private EditText inputUsername;
    private EditText inputPassword;
    private EditText inputNombre;
    private EditText inputEmail;

    private Button btnConsultar;
    private Button btnCrear;
    private Button btnActualizar;
    private Button btnEliminar;
    private Button btnVolver;

    private static final String URL_BASE =
            "https://gestion-enfermedades-api.onrender.com/api/usuarios";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios);

        inputUsername = findViewById(R.id.inputUsername);
        inputPassword = findViewById(R.id.inputPassword);
        inputNombre = findViewById(R.id.inputNombre);
        inputEmail = findViewById(R.id.inputEmail);

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

        datos.put(
                "username",
                inputUsername.getText().toString().trim()
        );

        datos.put(
                "password",
                inputPassword.getText().toString().trim()
        );

        datos.put(
                "nombre",
                inputNombre.getText().toString().trim()
        );

        datos.put(
                "email",
                inputEmail.getText().toString().trim()
        );

        return datos;
    }

    private boolean validarCompleto() {

        if (inputUsername.getText().toString().trim().isEmpty()) {
            Toast.makeText(
                    this,
                    "Digite el nombre de usuario",
                    Toast.LENGTH_SHORT
            ).show();
            return false;
        }

        if (inputPassword.getText().toString().trim().isEmpty()) {
            Toast.makeText(
                    this,
                    "Digite la contraseña",
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

        if (inputEmail.getText().toString().trim().isEmpty()) {
            Toast.makeText(
                    this,
                    "Digite el correo electrónico",
                    Toast.LENGTH_SHORT
            ).show();
            return false;
        }

        return true;
    }

    private void crear() {

        if (!validarCompleto()) {
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
                                UsuariosActivity.this,
                                "Usuario creado correctamente",
                                Toast.LENGTH_LONG
                        ).show();

                        limpiarCampos();
                    });

                } else {

                    String error = leerError(conexion);

                    runOnUiThread(() ->
                            Toast.makeText(
                                    UsuariosActivity.this,
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

        String username =
                inputUsername.getText().toString().trim();

        if (username.isEmpty()) {

            Toast.makeText(
                    this,
                    "Digite el nombre de usuario",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        new Thread(() -> {

            HttpURLConnection conexion = null;

            try {

                conexion = prepararConexion(
                        URL_BASE + "/" + username,
                        "GET"
                );

                int codigo = conexion.getResponseCode();

                if (codigo >= 200 && codigo < 300) {

                    String respuesta =
                            leerRespuesta(
                                    conexion.getInputStream()
                            );

                    JSONObject json =
                            new JSONObject(respuesta);

                    runOnUiThread(() -> {

                        inputPassword.setText(
                                json.optString("password")
                        );

                        inputNombre.setText(
                                json.optString("nombre")
                        );

                        inputEmail.setText(
                                json.optString("email")
                        );

                        Toast.makeText(
                                UsuariosActivity.this,
                                "Usuario encontrado",
                                Toast.LENGTH_SHORT
                        ).show();
                    });

                } else {

                    String error = leerError(conexion);

                    runOnUiThread(() ->
                            Toast.makeText(
                                    UsuariosActivity.this,
                                    "Usuario no encontrado. Código: "
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

        if (!validarCompleto()) {
            return;
        }

        String username =
                inputUsername.getText().toString().trim();

        new Thread(() -> {

            HttpURLConnection conexion = null;

            try {

                conexion = prepararConexion(
                        URL_BASE + "/" + username,
                        "PUT"
                );

                JSONObject datos = construirJson();

                enviarJson(conexion, datos);

                int codigo = conexion.getResponseCode();

                if (codigo >= 200 && codigo < 300) {

                    runOnUiThread(() ->
                            Toast.makeText(
                                    UsuariosActivity.this,
                                    "Usuario actualizado correctamente",
                                    Toast.LENGTH_LONG
                            ).show()
                    );

                } else {

                    String error = leerError(conexion);

                    runOnUiThread(() ->
                            Toast.makeText(
                                    UsuariosActivity.this,
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

        String username =
                inputUsername.getText().toString().trim();

        if (username.isEmpty()) {

            Toast.makeText(
                    this,
                    "Digite el nombre de usuario",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        new Thread(() -> {

            HttpURLConnection conexion = null;

            try {

                conexion = prepararConexion(
                        URL_BASE + "/" + username,
                        "DELETE"
                );

                int codigo = conexion.getResponseCode();

                if (codigo >= 200 && codigo < 300) {

                    runOnUiThread(() -> {

                        Toast.makeText(
                                UsuariosActivity.this,
                                "Usuario eliminado correctamente",
                                Toast.LENGTH_LONG
                        ).show();

                        limpiarCampos();
                    });

                } else {

                    String error = leerError(conexion);

                    runOnUiThread(() ->
                            Toast.makeText(
                                    UsuariosActivity.this,
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

        URL url = new URL(direccion);

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

        String token = obtenerToken();

        if (!token.isEmpty()) {
            conexion.setRequestProperty(
                    "Authorization",
                    "Bearer " + token
            );
        }

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

        inputUsername.setText("");
        inputPassword.setText("");
        inputNombre.setText("");
        inputEmail.setText("");
    }

    private void mostrarError(Exception e) {

        runOnUiThread(() ->
                Toast.makeText(
                        UsuariosActivity.this,
                        "Error: " + e.getMessage(),
                        Toast.LENGTH_LONG
                ).show()
        );
    }
}