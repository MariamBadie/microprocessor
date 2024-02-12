import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Queue {
    public ArrayList<String> readInstructions() throws IOException {
        BufferedReader br = null;
        String itemsPath = "instructions.txt";
        ArrayList<String> res = new ArrayList<>();
        br = new BufferedReader(new FileReader(itemsPath));
        String line = br.readLine();
        while (line != null) {
            if (line.trim().equals("")) {
                line = br.readLine();
                continue;
            }
            res.add(line);
            line = br.readLine();
        }
        return res;
    }

}
