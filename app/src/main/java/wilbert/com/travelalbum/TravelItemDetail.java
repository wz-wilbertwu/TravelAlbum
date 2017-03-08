package wilbert.com.travelalbum;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import database.DataBaseManager;
import model.TravelItem;
import util.BitmapUti;
import util.LogUti;

public class TravelItemDetail extends AppCompatActivity {
    private Uri photoUri;
    private ImageView imageView;
    private EditText descriptionEditText;
    private DataBaseManager dataBaseManager;
    private String travelId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_item_detail);

        travelId = getIntent().getStringExtra(TravelDetail.TRAVEL_ID);

        imageView = (ImageView)findViewById(R.id.itemImageView);
        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);

        dataBaseManager = DataBaseManager.getDataBaseManager(this);

        String photoString = (String) getIntent().getExtras().getSerializable(TravelDetail.PHOTO_URI_KEY);
        photoUri = Uri.parse(photoString);
        float px = 200 * (getResources().getDisplayMetrics().densityDpi / 160f);
        Bitmap bitmap = BitmapUti.getBitmapFromUri(this, photoUri, px);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionConfirm:
                /*插入*/
                TravelItem travelItem = new TravelItem(travelId, descriptionEditText.getText().toString(),
                        photoUri.toString());
                String sql = travelItem.getTravelItemInsertSql();
                dataBaseManager.execSQL(sql);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
