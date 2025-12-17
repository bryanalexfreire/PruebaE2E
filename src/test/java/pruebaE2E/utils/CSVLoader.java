package pruebaE2E.utils;

import pruebaE2E.model.PurchaseData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CSVLoader {

    public static List<PurchaseData> loadPurchaseData(String path) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(path));
        List<PurchaseData> result = new ArrayList<>();
        if (lines.size() <= 1) return result;
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            // split by comma, but allow quoted values containing commas
            List<String> partsList = new ArrayList<>();
            StringBuilder current = new StringBuilder();
            boolean inQuotes = false;
            for (char c : line.toCharArray()) {
                if (c == '"') {
                    inQuotes = !inQuotes;
                    continue;
                }
                if (c == ',' && !inQuotes) {
                    partsList.add(current.toString().trim());
                    current.setLength(0);
                } else {
                    current.append(c);
                }
            }
            partsList.add(current.toString().trim());

            String[] parts = partsList.toArray(new String[0]);

            // expected columns: name,country,city,card,month,year[,username,password]
            if (parts.length < 6) continue; // insufficient data

            String name = parts[0];
            String country = parts[1];
            String city = parts[2];
            String card = parts[3];
            String month = parts[4];
            String year = parts[5];

            if (parts.length >= 8) {
                String username = parts[6];
                String password = parts[7];
                result.add(new PurchaseData(name, country, city, card, month, year, username, password));
            } else {
                result.add(new PurchaseData(name, country, city, card, month, year));
            }
        }
        return result;
    }
}
