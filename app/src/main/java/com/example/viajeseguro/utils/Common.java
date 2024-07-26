package com.example.viajeseguro.utils;

import com.example.viajeseguro.models.GoogleSheetsResponse;
import com.example.viajeseguro.models.IGoogleSheets;

public class Common {
    public static String BASE_URL = "https://script.google.com/macros/s/AKfycbwp81gqwF-vyTqAxaFcpzEmP75sI59ZMd__nPCBp2DXu1lUn7-dmYQEkqZL_bf4cyQ/";
    public static String GOOGLE_SHEET_ID = "1Lk2pu5e9QoZCSfTO2tGSwa2lTqA-9bAE5iP0DaZ-aUM";
    public static String SHEET_NAME = "choferes";


    public static IGoogleSheets iGSGetMethodClient(String baseUrl) {
        return GoogleSheetsResponse.getClientGetMethod(baseUrl)
                .create(IGoogleSheets.class);
    }
}
