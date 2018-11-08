package conversandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import conversandroid.talkback.R;


public class OptionsActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options_layout);

        Intent intent = getIntent();
        int curAccelThreshold = intent.getIntExtra("curAccelThreshold", 12);

        SeekBar shakeBar = findViewById(R.id.shake_bar);
        TextView shakeValue = findViewById(R.id.shake_value);
        shakeBar.setProgress(curAccelThreshold);
        shakeValue.setText(Integer.toString(curAccelThreshold));

        shakeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                shakeValue.setText(Integer.toString(progress));
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    @Override
    public void onBackPressed() {
        Intent previousScreen = new Intent(getApplicationContext(), MainActivity.class);
        SeekBar shakeBar = findViewById(R.id.shake_bar);
        int accelThreshold = shakeBar.getProgress();
        setResult(Activity.RESULT_OK, previousScreen.putExtra("accelThreshold", accelThreshold));
        finish();
    }
}
