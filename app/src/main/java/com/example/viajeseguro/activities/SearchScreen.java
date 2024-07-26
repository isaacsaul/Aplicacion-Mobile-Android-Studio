package com.example.viajeseguro.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.viajeseguro.R;
import com.example.viajeseguro.models.IGoogleSheets;
import com.example.viajeseguro.models.People;
import com.example.viajeseguro.utils.Common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchScreen extends AppCompatActivity {
    private SearchView searchView;
    private Button buttonSearch;
    ProgressDialog progressDialog;
    private List<People> peopleList;
    IGoogleSheets iGoogleSheets;
    private TextView mostrarSearch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);

        // Inicializar la instancia de la interfaz IGoogleSheets
        iGoogleSheets = Common.iGSGetMethodClient(Common.BASE_URL);

        // Obtener referencias a los elementos de la interfaz y agregar listener al botón
        searchView = findViewById(R.id.search_view);
        buttonSearch = findViewById(R.id.button_search);
        mostrarSearch = findViewById(R.id.mostrar_search);


        // Inicializar la lista de personas
        peopleList = new ArrayList<>();

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el texto ingresado en el SearchView
                String query = searchView.getQuery().toString().trim();

                // Log para mostrar el nombre ingresado en el buscador
                Log.d("SearchScreen", "Nombre buscado: " + query);

                // Realizar la búsqueda
                loadDataFromGoogleSheets(query);
            }
        });

        // Agregar el código para abrir WhatsApp cuando se haga clic en el botón de denuncia
        Button reportButton = findViewById(R.id.report_search);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = "59163116078"; // Número de teléfono con código de país sin el símbolo +
                String message = "Hola, este es un  mama mensaje de prueba";

                Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + Uri.encode(message));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    private void loadDataFromGoogleSheets(String searchPlate) {
        String pathUrl;
        // Mostrar un diálogo de progreso mientras se carga la información
        progressDialog = ProgressDialog.show(SearchScreen.this,
                "Cargando resultados",
                "Espere por favor",
                true,
                false);

        try {
            // Ajustar la URL para incluir la placa de búsqueda en la solicitud al servidor
            pathUrl = "exec?spreadsheetId=" + Common.GOOGLE_SHEET_ID + "&sheet=" + Common.SHEET_NAME + "&plate=" + searchPlate;
            // Realizar la solicitud al servidor utilizando Retrofit
            iGoogleSheets.getPeople(pathUrl).enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    try {
                        assert response.body() != null;
                        JSONObject responseObject = new JSONObject(response.body());
                        JSONArray peopleArray = responseObject.getJSONArray("personas");

                        // Verificar si se encontraron registros
                        boolean found = false;
                        for (int i = 0; i < peopleArray.length(); i++) {
                            JSONObject person = peopleArray.getJSONObject(i);
                            // Verificar si la placa coincide exactamente con la placa buscada
                            if (person.getString("placa").equalsIgnoreCase(searchPlate)) {
                                // Si coincide, mostrar el registro encontrado en los logs
                                Log.d("SearchScreen", "Registro encontrado: " + person.toString());
                                found = true;

                                // Crear una instancia de la tarjeta de detalles del registro
                                View cardView = getLayoutInflater().inflate(R.layout.card_person_details, null);

                                // Obtener referencias a los TextViews dentro de la tarjeta
                                TextView textViewName = cardView.findViewById(R.id.textViewName);
                                TextView textViewLastName = cardView.findViewById(R.id.textViewLastName);
                                TextView textViewLicense = cardView.findViewById(R.id.textViewLicense);
                                TextView textViewPlate = cardView.findViewById(R.id.textViewPlate);
                                TextView textViewBrand = cardView.findViewById(R.id.textViewBrand);
                                TextView textViewColor = cardView.findViewById(R.id.textViewColor);

                                // Establecer los detalles del registro en los TextViews correspondientes
                                textViewName.setText("Nombre: " + person.getString("nombre"));
                                textViewLastName.setText("Apellido: " + person.getString("apellido"));
                                textViewLicense.setText("Licencia: " + person.getString("licencia"));
                                textViewPlate.setText("Placa: " + person.getString("placa"));
                                textViewBrand.setText("Marca: " + person.getString("marca"));
                                textViewColor.setText("Color: " + person.getString("color"));

                                // Agregar la tarjeta al diseño principal de la actividad
                                ConstraintLayout layout = findViewById(R.id.main);
                                layout.addView(cardView);

                                // Cambiar la visibilidad de la tarjeta a VISIBLE
                                cardView.setVisibility(View.VISIBLE);

                                // Opcional: Ocultar el TextView que muestra el JSON
                                mostrarSearch.setVisibility(View.GONE);

                                break; // Salir del bucle una vez que se haya encontrado el registro
                            }
                        }

                        // Si no se encontraron registros, mostrar un mensaje en los logs
                        if (!found) {
                            Log.d("SearchScreen", "No se encontraron registros para la placa proporcionada: " + searchPlate);
                            // Mostrar un mensaje si no se encontraron resultados
                            mostrarSearch.setText(getString(R.string.no_results_found));
                        }

                        progressDialog.dismiss();
                    } catch (JSONException je) {
                        je.printStackTrace();
                        progressDialog.dismiss();
                    }
                }


                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    // Manejar el fallo de la consulta
                    // Mostrar un log con el mensaje de error
                    Log.e("SearchScreen", "Error al cargar los datos: " + t.getMessage());
                    // Descartar el diálogo de progreso en caso de fallo
                    progressDialog.dismiss();
                    // Imprimir el stack trace del error para depuración
                    t.printStackTrace();
                }


            });
        } catch (Exception e) {
            // Manejar cualquier excepción no esperada
            e.printStackTrace();
        }
    }

}