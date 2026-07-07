package ru.sheraAn.service.interfaces;

import java.io.IOException;

public interface GeocoderService {
    double[] getCoordinates(String city) throws IOException, InterruptedException;
    String getCityNameByCoords(double lat, double lon);
}
