package network.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Downloader {



    //Download from URL
    public String download(String u, String outputDir, String outputFile) {
        System.out.println("Start download " + outputFile);
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;


        final File folder = new File(outputDir);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        try {
            URL url = new URL(u);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(outputDir + outputFile);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            int progress = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....

                if (fileLength > 0) { // only if total length is known
                    int newProgress = (int) (total * 100 / fileLength);
                    if (newProgress > progress) {
                        progress = newProgress;
                    }
                }

                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }

        return null;
    }
}
