package src.com.manastaneja.qotd;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//import com.opencsv.CSVReader;//NOTE: need mavin repo for this in dependencies

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String quotesFile;
    private String infoFile;
    private List<String> lines1;
    private List<String> lines2;
    private BufferedReader reader1;
    private BufferedReader reader2;
    private TextView quoteText;
    private TextView infoText;
    private String empty;
    private AssetManager assManager;
    private InputStream inputStream1;
    private InputStream inputStream2;
    private String infoStarter;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        quotesFile = "qotdquotes.txt";
        infoFile = "qotdinfo.txt";
        lines1 = new ArrayList<>();
        lines2 = new ArrayList<>();
        quoteText = (TextView) findViewById(R.id.quoteTextView);
        infoText = (TextView) findViewById(R.id.infoTextView);
        Button button = (Button) findViewById(R.id.changeButton);
        String textForButton = "New Quote";
        empty = "";
        assManager = getApplicationContext().getAssets();
        infoStarter = "- ";

        button.setText(textForButton);
        quoteText.setText(empty);
        infoText.setText(empty);

        try {
            addQuoteToList();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("\nPopulating Lists failed.\n");
        }
    }

    private void setFile(){
        try {
            inputStream1 = assManager.open(quotesFile);
            inputStream2 = assManager.open(infoFile);
            reader1 = new BufferedReader(new InputStreamReader(inputStream1));
            reader2 = new BufferedReader(new InputStreamReader(inputStream2));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("\nFile assignment failure.\n");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("\nInput Stream failed.\n");
        }
    }

    private void addQuoteToList() throws IOException {
        setFile();
        String line1;
        String line2;

        while (((line1 = reader1.readLine()) != null) && ((line2 = reader2.readLine()) != null)){
            lines1.add(line1);

            if (line2.equals("0")){
                lines2.add(empty);
            } else {
                lines2.add(line2);
            }
        }
    }

    public void setQuoteAndInfo(View v) {
        int currentCSVLine;

        mPrefs = getSharedPreferences("csvValue", 0);
        SharedPreferences.Editor editor = mPrefs.edit();
        currentCSVLine = mPrefs.getInt("csvLine", 0);

        quoteText.setText(lines1.get(currentCSVLine));

        if (lines2.get(currentCSVLine).equals(empty)) {
            infoText.setText(empty);
        } else {
            infoText.setText(infoStarter);
            infoText.append(lines2.get(currentCSVLine));
        }

        if (currentCSVLine == 6){
            currentCSVLine = 0;
        } else {
            currentCSVLine++;
        }

        editor.putInt("csvLine", currentCSVLine);
        editor.apply();
    }
}

//TODO: UX/UI final touches (button!!!)
//TODO: optimization
//TODO: app icon
//TODO: state saving (basically works, just need to do some final checks)

/**
 * Either methods in main activity or separate class instantiated in main activity
 * separate classes would be for (as of rn): quote changing
 * possible quote changing mechanics (3):
 *      -run on 24 hr clock in background (display phone top bar in app, so user will see their
 *      standard time settings; change everyday at 0:00
 *      -every 24 hrs, change quote; countdown timer style
 *      -quote generation; instead of automatic change everyday, user can hit a button to
 *      view a new quote (the next one in the available dataset)
 * SELECTED MECHANIC: quote generation with button
 */