package net.nexia.nexiaapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressWarnings("unused")
public class Versioner
{

    public static String getLatestVersion(int resourceId) throws IOException
    {
        URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null)
        {
            response.append(inputLine);
        }

        in.close();

        return response.toString();
    }

}
