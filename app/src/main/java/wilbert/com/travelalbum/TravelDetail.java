package wilbert.com.travelalbum;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import adapter.TravelAdapter;
import adapter.TravelItemAdapter;
import database.DataBaseHelper;
import model.Travel;
import model.TravelItem;
import util.LogUti;

public class TravelDetail extends AppCompatActivity {
    private Travel travel;
    private SQLiteDatabase database;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private TravelItemAdapter travelItemAdapter;
    private RequestQueue requestQueue;
    private TravelItemAdapter.IOnItemClick iOnItemClick = new TravelItemAdapter.IOnItemClick() {
        @Override
        public void onItemClick(View view) {

        }
    };

    private List travelItemList;
    private File mediaStorageDir;
    private Uri photoURI;
    private String imageString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_detail);
        travel = getIntent().getExtras().getParcelable(MainActivity.TRAVEL_KEY);
        LogUti.d(travel.getTitle());
        DataBaseHelper helper = new DataBaseHelper(this, "TravelAlbum.db", null, 2);
        database = helper.getWritableDatabase();

        layoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView)findViewById(R.id.travelItemRecyclerView);
        recyclerView.setLayoutManager(layoutManager);

        travelItemAdapter = new TravelItemAdapter(this, readTravelItemListFromSql(), iOnItemClick);
        recyclerView.setAdapter(travelItemAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        requestQueue = Volley.newRequestQueue(this);

        FloatingActionButton btn = (FloatingActionButton)findViewById(R.id.addTravelItemBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    @Override
    protected void onResume() {
        travelItemAdapter = new TravelItemAdapter(this, readTravelItemListFromSql(), iOnItemClick);
        recyclerView.setAdapter(travelItemAdapter);
        super.onResume();
    }

    private List readTravelItemListFromSql() {
        travelItemList = new ArrayList();
        String sql = TravelItem.getTravelItemQueryAllSql(travel.getId());
        Cursor cursor = database.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String travel_id = cursor.getString(cursor.getColumnIndex("travel_id"));
            String description = cursor.getString(cursor.getColumnIndex("description"));
            String time = cursor.getInt(cursor.getColumnIndex("time")) + "";
            String image = cursor.getString(cursor.getColumnIndex("image"));
            TravelItem travelItem = new TravelItem(id, travel_id, description, image, time);
            travelItemList.add(travelItem);
        }
        return travelItemList;
    }


    public static final String KEY = "key";
    public static final String PHOTO_URI_KEY = "photo_uri_key";
    public static final String TRAVEL_ID = "travel_id";
    public static final String IMG = "img";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String result = "%d\n%d\n%s";
        LogUti.d("photo uri:" +photoURI.toString());
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            String photoString = photoURI.toString();
            Bundle bundle = new Bundle();
            bundle.putSerializable(PHOTO_URI_KEY, photoString);
            Intent intent = new Intent(this, TravelItemDetail.class);
            intent.putExtra(TRAVEL_ID, travel.getId());
            intent.putExtra(IMG, imageString);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        return;
    }
    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            photoURI = getOutImageFileUri();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }

    private Uri getOutImageFileUri() {
        return Uri.fromFile(getOutputImageFile());
    }

    private File getOutputImageFile() {
        mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "TravelAlbum");
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        File mediaFile;
        imageString = UUID.randomUUID().toString().replaceAll("-", "") + "-" + timeStamp  + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                imageString);
        return mediaFile;
    }

    ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP|
            ItemTouchHelper.DOWN,ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            TravelItem travelItem = (TravelItem) travelItemAdapter.getTravelItemList().get(position);
            String sql = travelItem.getTravelItemRemoveSql();
            database.execSQL(sql);
            travelItemAdapter.getTravelItemList().remove(position);
            travelItemAdapter.notifyItemRemoved(position);
        }
        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                //滑动时改变Item的透明度
                final float alpha = 1 - Math.abs(dX) / (float)viewHolder.itemView.getWidth();
                viewHolder.itemView.setAlpha(alpha);
                viewHolder.itemView.setTranslationX(dX);

            }
        }
    };
}
