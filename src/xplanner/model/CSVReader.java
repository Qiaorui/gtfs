package xplanner.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by qiaorui on 19/11/16.
 */
public class CSVReader {
    private String[] header;
    private BufferedReader br;
    private String buffer;


    public CSVReader(String file) {
        try {
            br = new BufferedReader(new FileReader(file));
            header = br.readLine().split(",");
            System.out.println(Arrays.toString(header));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> read() {
        Map<String, String> data = new HashMap<>();
        String item = "";
        int index = 0;
        for (String s : buffer.split(",")) {
            if (!s.isEmpty() && s.charAt(s.length() - 1) == '/') {
                item += s.replaceAll("/", "") + ",";
            } else {
                item += s.replaceAll("/", "");
                data.put(header[index], item);
                item = "";
                index++;
            }
        }

        return data;
    }

    public boolean hasNext() {
        buffer = "";
        String line = "";
        try {
            while ((line = br.readLine()) != null) {
                buffer += line;
                if (line.substring(line.lastIndexOf(',') + 1).equals("False") || line.substring(line.lastIndexOf(',')
                        + 1).equals("True")) {
                    break;
                }

                buffer += "\n";

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return !buffer.equals("");

    }



}
