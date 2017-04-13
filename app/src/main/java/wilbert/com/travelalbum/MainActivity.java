package wilbert.com.travelalbum;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import adapter.TravelAdapter;
import database.DataBaseHelper;
import model.Travel;
import model.TravelItem;
import model.User;
import model.VolleySingleton;
import util.CustomConstans;
import util.FileUti;
import util.LogUti;
import util.NetworkUtil;

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

    TravelAdapter.IOnItemLongClick iOnItemLongClick = new TravelAdapter.IOnItemLongClick() {
        @Override
        public void onItemLongClick(View view) {
            int itemPosition = recyclerView.getChildLayoutPosition(view);
            LogUti.d(String.format("itemPosition:%d", itemPosition));
            final Travel travel = (Travel) travelList.get(itemPosition);
            titleEditText = new EditText(MainActivity.this);
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this).
                    setTitle("修改标题")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setView(titleEditText)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String input = titleEditText.getText().toString();
                            if (input.equals("")) {
                                Toast.makeText(getApplicationContext(), "标题内容不能为空！" + input, Toast.LENGTH_LONG).show();
                            }
                            else {
                                travel.setTitle(input);
                                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String time = format.format(new Date());
                                travel.setTime(time);
                                database.execSQL(travel.getTravelUpdateSql());

                                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                        CustomConstans.url + "Travel/update",responseListener,
                                        errorListener) {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        return travel.getMap();
                                    }
                                };
                                requestQueue.add(stringRequest);

                                Toast.makeText(getApplicationContext(), "旅途开始！" + input, Toast.LENGTH_LONG).show();
                                travelAdapter.setDataSet(readTravelListFromSql());
                                travelAdapter.notifyDataSetChanged();
                            }
                        }
                    })
                    .setNegativeButton("取消", null);
            builder.show();
        }
    };

    Response.Listener<String> responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Travel travel = new Travel(response);
            if (travel != null && travel.getStatus() != null && travel.getStatus().equals("succ")) {
                LogUti.d("操作成功：" + response);
                String sql = null;
                if (travel.getOperation().equals("D")) {
                    sql = travel.getTravelRemoveSql();
                } else {
                    sql = travel.getTravelUpdateStateSql();
                }
                database.execSQL(sql);
            } else {
                LogUti.d("操作失败：" + response);
            }
        }
    };
    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            LogUti.d("错误" + error.toString());
        }
    };
    private ArrayList travelList = new ArrayList();
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String userMessage = getIntent().getStringExtra(LoginActivity.USERMESSAGE);
        LogUti.d("userMessage:" + userMessage);
        user = User.getUserFromJson(userMessage);
        DataBaseHelper helper = new DataBaseHelper(this, "TravelAlbum.db", null, 2);
        database = helper.getWritableDatabase();


        layoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView)findViewById(R.id.travelRecyclerView);
        recyclerView.setLayoutManager(layoutManager);

        travelAdapter = new TravelAdapter(readTravelListFromSql(), iOnItemClick, iOnItemLongClick);
        recyclerView.setAdapter(travelAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        /*requestQueue = Volley.newRequestQueue(this);*/
        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
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
                                    final Travel travel = new Travel(user.getId(), input);
                                    database.execSQL(travel.getTravelInsertSql());

                                    StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                            CustomConstans.url + "Travel/add",responseListener,
                                            errorListener) {
                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            return travel.getMap();
                                        }
                                    };
                                    requestQueue.add(stringRequest);

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
        String sql = String.format("select * from tb_travel where user_id = '%s' and state != '-1'", user.getId());
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

    ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP|
                            ItemTouchHelper.DOWN,ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            Travel travel = (Travel) travelAdapter.getTravelList().get(position);
            String sql = travel.getTravelMarkRemoveSql();
            database.execSQL(sql);
            travelAdapter.getTravelList().remove(position);
            travelAdapter.notifyItemRemoved(position);

            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    CustomConstans.url + "Travel/delete/" + travel.getId(),responseListener,
                    errorListener);
            requestQueue.add(stringRequest);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        List<Travel> notSyncTravelList = readNotSyncTravelFromSql();
        List<TravelItem> notSyncTravelItemList = readNotSyncTravelItemFromSql();
        Response.Listener<String> syncTravelListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Travel travel = new Travel(response);
                database.execSQL(travel.getTravelUpdateStateSql());
                LogUti.d(response);
            }
        };
        Response.Listener<String> syncTravelItemListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                TravelItem travel = new TravelItem(response);
                database.execSQL(travel.getTravelItemUpdateStateSql());
                LogUti.d(response);
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUti.d(error.toString());
            }
        };
        if (notSyncTravelList.size() != 0) {
            for (final Travel travel:notSyncTravelList) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        CustomConstans.url + "sync/travel", syncTravelListener, errorListener){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return travel.getMap();
                    }
                };
                requestQueue.add(stringRequest);
            }
        }
        if (notSyncTravelItemList.size() != 0) {
            for (final TravelItem travelItem: notSyncTravelItemList) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        CustomConstans.url + "sync/travelItem", syncTravelItemListener, errorListener) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return travelItem.getMap();
                    }
                };
                requestQueue.add(stringRequest);
                File file = FileUti.getTheFile(travelItem.getImage());
                NetworkUtil.upload(file.getName(), file, new NetworkUtil.NetworkCallBack() {
                    @Override
                    public void onResponse(okhttp3.Response response) {
                        try{
                            LogUti.d(response.body().string());
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private List<Travel> readNotSyncTravelFromSql() {
        List<Travel>notSyncTravelList = new ArrayList();
        String sql = String.format("select * from tb_travel t where t.state != '9'");
        Cursor cursor = database.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String user_id = cursor.getString(cursor.getColumnIndex("user_id"));
            String time = cursor.getString(cursor.getColumnIndex("time"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String state = cursor.getString(cursor.getColumnIndex("state"));
            Travel travel = new Travel(id,user_id, title, time, state);
            notSyncTravelList.add(travel);
        }
        return notSyncTravelList;
    }
    private List<TravelItem> readNotSyncTravelItemFromSql() {
        List<TravelItem> notSyncTravelItemList = new ArrayList();
        String sql = "select * from tb_travel_item t where t.state != '9'";
        Cursor cursor = database.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String travel_id = cursor.getString(cursor.getColumnIndex("travel_id"));
            String description = cursor.getString(cursor.getColumnIndex("description"));
            String time = cursor.getString(cursor.getColumnIndex("time"));
            String image = cursor.getString(cursor.getColumnIndex("image"));
            TravelItem travelItem = new TravelItem(id, travel_id, description, image, time);
            notSyncTravelItemList.add(travelItem);
        }
        return notSyncTravelItemList;
    }
}
