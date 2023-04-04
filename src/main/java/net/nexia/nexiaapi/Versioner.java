package net.nexia.nexiaapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressWarnings("unused")
public class Versioner
{

    /**
     * Gets the latest version of a Resource using the Resource ID.
     * @param resourceId The Resource ID.
     * @return Returns the version String.
     * @throws IOException Throws IOException.
     */
    public static String getLatestVersion(int resourceId) throws IOException
    {
        URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);

        in.close();

        return response.toString();
    }

    /**
     * Compares the current to the latest version of a Resource from Spigot.
     * @param currentVersion The current version of the Resource.
     * @param resourceID The Resource ID to get the latest version from.
     * @return Returns true if there is a new version available
     */
    public static boolean isNewVersionAvailable(String currentVersion, int resourceID) throws IOException
    {
        int currentVersionNumber = Integer.parseInt(currentVersion.replace(".", ""));
        String latestVersion = getLatestVersion(resourceID);
        int latestVersionNumber = Integer.parseInt(latestVersion.replace(".", ""));

        System.out.println(currentVersionNumber);
        System.out.println(latestVersionNumber);
        return currentVersionNumber < latestVersionNumber;
    }

}
