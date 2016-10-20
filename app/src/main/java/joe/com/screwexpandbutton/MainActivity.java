package joe.com.screwexpandbutton;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import joe.com.screwbutton.ScrewExpandButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ScrewExpandButton btn = (ScrewExpandButton) findViewById(R.id.screwbtn);
        btn.addButton(getResources().getDrawable(R.drawable.ic_add_location_white_24dp), View.generateViewId());
        btn.addButton(getResources().getDrawable(R.drawable.ic_record_voice_over_white_24dp), View.generateViewId());
        btn.addButton(getResources().getDrawable(R.drawable.ic_add_location_white_24dp), View.generateViewId());
        btn.addButton(getResources().getDrawable(R.drawable.ic_record_voice_over_white_24dp), View.generateViewId());
        btn.addButton(getResources().getDrawable(R.drawable.ic_add_location_white_24dp), View.generateViewId());
        btn.addButton(getResources().getDrawable(R.drawable.ic_record_voice_over_white_24dp), View.generateViewId());
        btn.addButton(getResources().getDrawable(R.drawable.ic_add_location_white_24dp), View.generateViewId());
        btn.addButton(getResources().getDrawable(R.drawable.ic_record_voice_over_white_24dp), View.generateViewId());
        btn.addButton(getResources().getDrawable(R.drawable.ic_add_location_white_24dp), View.generateViewId());
        btn.addButton(getResources().getDrawable(R.drawable.ic_record_voice_over_white_24dp), View.generateViewId());
        btn.addButton(getResources().getDrawable(R.drawable.ic_add_location_white_24dp), View.generateViewId());
        btn.addButton(getResources().getDrawable(R.drawable.ic_record_voice_over_white_24dp), View.generateViewId());
        btn.addButton(getResources().getDrawable(R.drawable.ic_add_location_white_24dp), View.generateViewId());
    }
}
