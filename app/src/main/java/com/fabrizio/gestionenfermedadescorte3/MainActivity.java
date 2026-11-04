package com.fabrizio.gestionenfermedadescorte3;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {

    private EditText inputUsername;
    private EditText inputPassword;

    private Button btnIngresar;
    private Button btnRegistrar;
    private Button btnRecuperar;

    private static final String URL_LOGIN =
            "https://gestion-enfermedades-api.onrender.com/api/auth/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        inputUsername =
                findViewById(R.id.inputUsername);

        inputPassword =
                findViewById(R.id.inputPassword);

        btnIngresar =
                findViewById(R.id.btnIngresar);

        btnRegistrar =
                findViewById(R.id.btnRegistrar);

        btnRecuperar =
                findViewById(R.id.btnRecuperar);


        // LOGIN
        btnIngresar.setOnClickListener(
                v -> iniciarSesion()
        );


        // REGISTRARSE
        btnRegistrar.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            UsuariosActivity.class
                    );

            startActivity(intent);
        });


        // RECUPERAR CONTRASEÑA
        btnRecuperar.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            RecuperarActivity.class
                    );

            startActivity(intent);
        });
    }


    private void iniciarSesion() {

        String username =
                inputUsername
                        .getText()
                        .toString()
                        .trim();

        String password =
                inputPassword
                        .getText()
                        .toString()
                        .trim();


        if (username.isEmpty() || password.isEmpty()) {

            Toast.makeText(
                    this,
                    "Digite usuario y contraseña",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        btnIngresar.setEnabled(false);


        new Thread(() -> {

            HttpURLConnection conexion = null;

            try {

                URL url =
                        new URL(URL_LOGIN);

                conexion =
                        (HttpURLConnection)
                                url.openConnection();


                conexion.setRequestMethod("POST");

                conexion.setRequestProperty(
                        "Content-Type",
                        "application/json"
                );

                conexion.setRequestProperty(
                        "Accept",
                        "application/json"
                );

                conexion.setDoOutput(true);

                conexion.setConnectTimeout(60000);
                conexion.setReadTimeout(60000);


                JSONObject datos =
                        new JSONObject();

                datos.put(
                        "username",
                        username
                );

                datos.put(
                        "password",
                        password
                );


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
                            leerRespuesta(
                                    entrada
                            );
                }


                if (codigo == 200) {

                    JSONObject jsonRespuesta =
                            new JSONObject(
                                    respuesta
                            );


                    String token =
                            jsonRespuesta.getString(
                                    "token"
                            );


                    getSharedPreferences(
                            "sesion",
                            MODE_PRIVATE
                    )
                            .edit()
                            .putString(
                                    "jwt_token",
                                    token
                            )
                            .putString(
                                    "usuario_actual",
                                    username
                            )
                            .apply();


                    runOnUiThread(() -> {

                        btnIngresar.setEnabled(
                                true
                        );


                        Toast.makeText(
                                MainActivity.this,
                                "Inicio de sesión correcto",
                                Toast.LENGTH_SHORT
                        ).show();


                        Intent intent =
                                new Intent(
                                        MainActivity.this,
                                        MenuActivity.class
                                );


                        startActivity(intent);

                        finish();
                    });


                } else {

                    runOnUiThread(() -> {

                        btnIngresar.setEnabled(
                                true
                        );


                        Toast.makeText(
                                MainActivity.this,
                                "Usuario o contraseña incorrectos",
                                Toast.LENGTH_LONG
                        ).show();
                    });
                }


            } catch (Exception e) {

                runOnUiThread(() -> {

                    btnIngresar.setEnabled(
                            true
                    );


                    Toast.makeText(
                            MainActivity.this,
                            "Error de conexión: "
                                    + e.getMessage(),
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

            respuesta.append(
                    linea
            );
        }


        lector.close();


        return respuesta.toString();
    }
}