package com.fabrizio.gestionenfermedadescorte3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MenuActivity extends AppCompatActivity
        implements SensorEventListener {

    private Button btnUsuarios;
    private Button btnEnfermedades;
    private Button btnReportes;
    private Button btnCerrarSesion;

    private TextView txtLatitud;
    private TextView txtLongitud;

    // =========================
    // SENSOR 1: ACELERÓMETRO
    // =========================

    private SensorManager sensorManager;
    private Sensor acelerometro;

    private long ultimoMovimiento = 0;

    private float ultimoX = 0;
    private float ultimoY = 0;
    private float ultimoZ = 0;

    private boolean primeraLectura = true;

    // =========================
    // SENSOR 2: UBICACIÓN
    // =========================

    private LocationManager locationManager;
    private LocationListener locationListener;

    private static final int PERMISO_UBICACION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);

        btnUsuarios =
                findViewById(R.id.btnUsuarios);

        btnEnfermedades =
                findViewById(R.id.btnEnfermedades);

        btnReportes =
                findViewById(R.id.btnReportes);

        btnCerrarSesion =
                findViewById(R.id.btnCerrarSesion);

        txtLatitud =
                findViewById(R.id.txtLatitud);

        txtLongitud =
                findViewById(R.id.txtLongitud);


        // ============================================
        // NAVEGACIÓN
        // ============================================

        btnUsuarios.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MenuActivity.this,
                            UsuariosActivity.class
                    );

            startActivity(intent);
        });


        btnEnfermedades.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MenuActivity.this,
                            EnfermedadesActivity.class
                    );

            startActivity(intent);
        });


        btnReportes.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MenuActivity.this,
                            ReportesActivity.class
                    );

            startActivity(intent);
        });


        btnCerrarSesion.setOnClickListener(v -> {

            getSharedPreferences(
                    "sesion",
                    MODE_PRIVATE
            )
                    .edit()
                    .clear()
                    .apply();

            Intent intent =
                    new Intent(
                            MenuActivity.this,
                            MainActivity.class
                    );

            startActivity(intent);

            finish();
        });


        // ============================================
        // SENSOR 1: ACELERÓMETRO
        // ============================================

        sensorManager =
                (SensorManager)
                        getSystemService(
                                SENSOR_SERVICE
                        );

        if (sensorManager != null) {

            acelerometro =
                    sensorManager.getDefaultSensor(
                            Sensor.TYPE_ACCELEROMETER
                    );

            if (acelerometro == null) {

                Toast.makeText(
                        this,
                        "Acelerómetro no disponible",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }


        // ============================================
        // SENSOR 2: UBICACIÓN
        // ============================================

        locationManager =
                (LocationManager)
                        getSystemService(
                                LOCATION_SERVICE
                        );


        locationListener =
                new LocationListener() {

                    @Override
                    public void onLocationChanged(
                            @NonNull Location location
                    ) {

                        mostrarUbicacion(location);
                    }
                };


        verificarPermisoUbicacion();
    }


    // =====================================================
    // UBICACIÓN
    // =====================================================

    private void verificarPermisoUbicacion() {

        if (
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                )
                        != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                    this,

                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },

                    PERMISO_UBICACION
            );

        } else {

            iniciarUbicacion();
        }
    }


    private void iniciarUbicacion() {

        if (
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                )
                        != PackageManager.PERMISSION_GRANTED

                        &&

                        ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                                != PackageManager.PERMISSION_GRANTED
        ) {

            return;
        }


        try {

            // ==============================
            // NETWORK PROVIDER
            // ==============================

            if (
                    locationManager.isProviderEnabled(
                            LocationManager.NETWORK_PROVIDER
                    )
            ) {

                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        2000,
                        1,
                        locationListener
                );


                Location ultima =
                        locationManager.getLastKnownLocation(
                                LocationManager.NETWORK_PROVIDER
                        );


                if (ultima != null) {

                    mostrarUbicacion(ultima);
                }
            }


            // ==============================
            // GPS PROVIDER
            // ==============================

            if (
                    locationManager.isProviderEnabled(
                            LocationManager.GPS_PROVIDER
                    )
            ) {

                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        2000,
                        1,
                        locationListener
                );


                Location ultimaGPS =
                        locationManager.getLastKnownLocation(
                                LocationManager.GPS_PROVIDER
                        );


                if (ultimaGPS != null) {

                    mostrarUbicacion(
                            ultimaGPS
                    );
                }
            }


        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Error de ubicación: "
                            + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }


    private void mostrarUbicacion(
            Location location
    ) {

        txtLatitud.setText(
                "Latitud: "
                        + location.getLatitude()
        );


        txtLongitud.setText(
                "Longitud: "
                        + location.getLongitude()
        );
    }


    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );


        if (
                requestCode
                        == PERMISO_UBICACION
        ) {

            if (
                    grantResults.length > 0

                            &&

                            grantResults[0]
                                    == PackageManager.PERMISSION_GRANTED
            ) {

                iniciarUbicacion();

            } else {

                Toast.makeText(
                        this,
                        "Permiso de ubicación no concedido",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }


    // =====================================================
    // ACELERÓMETRO
    // =====================================================

    @Override
    protected void onResume() {

        super.onResume();


        if (
                sensorManager != null
                        &&
                        acelerometro != null
        ) {

            sensorManager.registerListener(
                    this,
                    acelerometro,
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        }
    }


    @Override
    protected void onPause() {

        super.onPause();


        if (sensorManager != null) {

            sensorManager.unregisterListener(
                    this
            );
        }
    }


    @Override
    public void onSensorChanged(
            SensorEvent event
    ) {

        if (
                event.sensor.getType()
                        == Sensor.TYPE_ACCELEROMETER
        ) {

            float x =
                    event.values[0];

            float y =
                    event.values[1];

            float z =
                    event.values[2];


            // Primera lectura:
            // solo guardamos los valores iniciales.

            if (primeraLectura) {

                ultimoX = x;
                ultimoY = y;
                ultimoZ = z;

                primeraLectura = false;

                return;
            }


            // Calculamos cuánto cambió el movimiento
            // entre una lectura y la siguiente.

            float cambio =
                    Math.abs(
                            x - ultimoX
                    )
                            +
                            Math.abs(
                                    y - ultimoY
                            )
                            +
                            Math.abs(
                                    z - ultimoZ
                            );


            ultimoX = x;
            ultimoY = y;
            ultimoZ = z;


            // Umbral sensible para que funcione
            // correctamente en Android Emulator.

            if (cambio > 0.5f) {

                long ahora =
                        System.currentTimeMillis();


                if (
                        ahora - ultimoMovimiento
                                > 1000
                ) {

                    ultimoMovimiento =
                            ahora;


                    Toast.makeText(
                            this,
                            "Movimiento detectado",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        }
    }


    @Override
    public void onAccuracyChanged(
            Sensor sensor,
            int accuracy
    ) {

        // No requiere ninguna acción.
    }
}