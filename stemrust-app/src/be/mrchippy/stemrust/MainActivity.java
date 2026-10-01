package be.mrchippy.stemrust;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.Locale;

public class MainActivity extends Activity {
    private TextView display;
    private TextView status;
    private TextToSpeech tts;
    private boolean ttsReady = false;
    private String pendingText = null;

    private static final String[][] PHRASES = new String[][] {
        {"💧 DORST", "Ik heb dorst. Kan ik iets te drinken krijgen?"},
        {"🍽 HONGER", "Ik heb honger. Kan ik iets eten krijgen?"},
        {"🚻 TOILET", "Ik moet naar het toilet."},
        {"😣 PIJN", "Ik heb veel pijn."},
        {"💊 PIJNSTILLING", "Mag ik mijn pijnstilling als het tijd is?"},
        {"🧊 IJS / KOUD", "Kan ik wat ijs of iets kouds krijgen?"},
        {"🤢 MISSELIJK", "Ik ben misselijk."},
        {"😵 DUIZELIG", "Ik ben duizelig."},
        {"🥶 KOUD", "Ik heb het koud."},
        {"🥵 WARM", "Ik heb het warm."},
        {"😴 RUSTEN", "Ik wil graag even rusten of slapen."},
        {"👋 KOM EVEN", "Kan je even bij mij komen?"},
        {"✅ JA", "Ja."},
        {"❌ NEE", "Nee."},
        {"🙏 DANK JE", "Dank je."},
        {"⏳ WACHT", "Wacht even alsjeblieft."},
        {"🆘 DRINGEND HULP", "Ik heb dringend hulp nodig. Kom nu alsjeblieft."},
        {"🩸 IK BLOED", "Ik bloed. Kom nu alsjeblieft."}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(10, 10, 12));
        buildUi();
    }

    private void buildUi() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.rgb(15, 15, 18));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(14), dp(14), dp(14), dp(24));
        scroll.addView(root, new ScrollView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        TextView title = new TextView(this);
        title.setText("STEMRUST");
        title.setTextColor(Color.WHITE);
        title.setTextSize(27);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title, fullWidth(dp(52)));

        TextView subtitle = new TextView(this);
        subtitle.setText("Tik op een knop. De zin verschijnt groot en wordt uitgesproken.");
        subtitle.setTextColor(Color.rgb(190, 190, 198));
        subtitle.setTextSize(15);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(dp(8), 0, dp(8), dp(14));
        root.addView(subtitle, fullWidth(ViewGroup.LayoutParams.WRAP_CONTENT));

        display = new TextView(this);
        display.setText("Klaar wanneer jij bent.");
        display.setTextColor(Color.WHITE);
        display.setTextSize(25);
        display.setTypeface(Typeface.DEFAULT_BOLD);
        display.setGravity(Gravity.CENTER);
        display.setPadding(dp(16), dp(18), dp(16), dp(18));
        display.setBackgroundColor(Color.rgb(35, 35, 42));
        LinearLayout.LayoutParams displayParams = fullWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        displayParams.setMargins(0, 0, 0, dp(14));
        root.addView(display, displayParams);

        status = new TextView(this);
        status.setText("Spraak start pas bij je eerste druk op een knop.");
        status.setTextColor(Color.rgb(155, 155, 165));
        status.setTextSize(13);
        status.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams statusParams = fullWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        statusParams.setMargins(0, 0, 0, dp(10));
        root.addView(status, statusParams);

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(2);
        grid.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        grid.setUseDefaultMargins(false);
        root.addView(grid, fullWidth(ViewGroup.LayoutParams.WRAP_CONTENT));

        for (int i = 0; i < PHRASES.length; i++) {
            final String spoken = PHRASES[i][1];
            Button b = makeButton(PHRASES[i][0], i >= 16);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    say(spoken);
                }
            });
            GridLayout.LayoutParams gp = new GridLayout.LayoutParams();
            gp.width = 0;
            gp.height = dp(76);
            gp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            gp.setMargins(dp(4), dp(4), dp(4), dp(4));
            grid.addView(b, gp);
        }

        TextView customLabel = new TextView(this);
        customLabel.setText("Iets anders zeggen");
        customLabel.setTextColor(Color.WHITE);
        customLabel.setTextSize(18);
        customLabel.setTypeface(Typeface.DEFAULT_BOLD);
        customLabel.setPadding(dp(4), dp(18), dp(4), dp(8));
        root.addView(customLabel, fullWidth(ViewGroup.LayoutParams.WRAP_CONTENT));

        final EditText custom = new EditText(this);
        custom.setHint("Typ hier wat je wilt zeggen…");
        custom.setHintTextColor(Color.rgb(130, 130, 138));
        custom.setTextColor(Color.WHITE);
        custom.setTextSize(18);
        custom.setSingleLine(false);
        custom.setMinLines(2);
        custom.setPadding(dp(14), dp(12), dp(14), dp(12));
        custom.setBackgroundColor(Color.rgb(35, 35, 42));
        root.addView(custom, fullWidth(ViewGroup.LayoutParams.WRAP_CONTENT));

        Button speakCustom = makeButton("🔊 SPREEK TEKST UIT", false);
        LinearLayout.LayoutParams customButtonParams = fullWidth(dp(64));
        customButtonParams.setMargins(0, dp(10), 0, 0);
        root.addView(speakCustom, customButtonParams);
        speakCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = custom.getText().toString().trim();
                if (text.length() == 0) {
                    display.setText("Typ eerst een zin.");
                    return;
                }
                say(text);
            }
        });

        Button stop = makeButton("⏹ STOP SPRAAK", false);
        LinearLayout.LayoutParams stopParams = fullWidth(dp(58));
        stopParams.setMargins(0, dp(8), 0, 0);
        root.addView(stop, stopParams);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tts != null) tts.stop();
                status.setText("Spraak gestopt.");
            }
        });

        setContentView(scroll);
    }

    private Button makeButton(String label, boolean urgent) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextColor(Color.WHITE);
        b.setTextSize(15);
        b.setTypeface(Typeface.DEFAULT_BOLD);
        b.setGravity(Gravity.CENTER);
        b.setAllCaps(false);
        b.setPadding(dp(8), dp(6), dp(8), dp(6));
        b.setBackgroundColor(urgent ? Color.rgb(150, 28, 32) : Color.rgb(48, 48, 58));
        b.setMinHeight(dp(64));
        return b;
    }

    private void say(final String text) {
        display.setText(text);
        if (ttsReady && tts != null) {
            int result = tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "stemrust");
            status.setText(result == TextToSpeech.ERROR ? "Kon de spraakmotor niet starten. De tekst staat wel groot in beeld." : "Uitgesproken.");
            return;
        }

        pendingText = text;
        status.setText("Spraakmotor wordt gestart…");
        try {
            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int state) {
                    if (state != TextToSpeech.SUCCESS || tts == null) {
                        status.setText("Spraakmotor niet beschikbaar. De tekst blijft groot zichtbaar.");
                        return;
                    }
                    int lang = tts.setLanguage(new Locale("nl", "BE"));
                    if (lang == TextToSpeech.LANG_MISSING_DATA || lang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        tts.setLanguage(new Locale("nl", "NL"));
                    }
                    tts.setSpeechRate(0.92f);
                    ttsReady = true;
                    status.setText("Spraak klaar.");
                    if (pendingText != null) {
                        String p = pendingText;
                        pendingText = null;
                        tts.speak(p, TextToSpeech.QUEUE_FLUSH, null, "stemrust");
                    }
                }
            });
        } catch (Throwable e) {
            status.setText("Spraakmotor niet beschikbaar. De tekst blijft groot zichtbaar.");
        }
    }

    private LinearLayout.LayoutParams fullWidth(int height) {
        return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
