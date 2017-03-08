package wilbert.com.travelalbum;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import adapter.TravelAdapter;
import database.DataBaseHelper;
import model.Travel;
import model.User;
import util.LogUti;

public class MainActivity extends AppCompatActivity {
    public static final String TRAVEL_KEY = "travel_key";
    private User user = new User("name","password", "password");
    private SQLiteDatabase database;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private TravelAdapter travelAdapter;
    private EditText titleEditText;

    TravelAdapter.IOnItemClick iOnItemClick = new TravelAdapter.IOnItemClick() {
        @Override
        public void onItemClick(View view) {
            int itemPosition = recyclerView.getChildLayoutPosition(view);
            LogUti.d(String.format("itemPosition:%d", itemPosition));
            Bundle bundle = new Bundle();
            bundle.putParcelable(TRAVEL_KEY, (Travel)travelList.get(itemPosition));
            Intent intent = new Intent(MainActivity.this, TravelDetail.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };
    private ArrayList travelList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String userMessage = getIntent().getStringExtra(LoginActivity.USERMESSAGE);
        LogUti.d("userMessage:" + userMessage);
        /*user = User.getUserFromJson(userMessage);*/
        DataBaseHelper helper = new DataBaseHelper(this, "TravelAlbum.db", null, 2);
        database = helper.getWritableDatabase();


        layoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView)findViewById(R.id.travelRecyclerView);
        recyclerView.setLayoutManager(layoutManager);

        travelAdapter = new TravelAdapter(readTravelListFromSql(), iOnItemClick);
        recyclerView.setAdapter(travelAdapter);

        FloatingActionButton btn = (FloatingActionButton)findViewById(R.id.addTravelBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleEditText = new EditText(MainActivity.this);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this).
                        setTitle("标题")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(titleEditText)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String input = titleEditText.getText().toString();
                                if (input.equals("")) {
                                    Toast.makeText(getApplicationContext(), "标题内容不能为空！" + input, Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Travel travel = new Travel(user.getId(), input);
                                    database.execSQL(travel.getTravelInsertSql());
                                    Toast.makeText(getApplicationContext(), "旅途开始！" + input, Toast.LENGTH_LONG).show();
                                    travelAdapter.setDataSet(readTravelListFromSql());
                                    travelAdapter.notifyDataSetChanged();
                                }
                            }
                        })
                        .setNegativeButton("取消", null);
                builder.show();

            }
        });
    }

    private List readTravelListFromSql() {
        travelList = new ArrayList();
        String sql = String.format("select * from tb_travel where user_id = '%s'", user.getId());
        Cursor cursor = database.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String user_id = cursor.getInt(cursor.getColumnIndex("user_id")) + "";
            String time = cursor.getInt(cursor.getColumnIndex("time")) + "";
            String title = cursor.getString(cursor.getColumnIndex("title"));
            Travel travel = new Travel(id,user_id, title, time);
            travelList.add(travel);
        }
        return travelList;
    }
}
