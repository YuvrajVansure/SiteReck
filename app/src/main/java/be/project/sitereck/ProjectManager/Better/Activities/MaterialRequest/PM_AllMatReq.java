package be.project.sitereck.ProjectManager.Better.Activities.MaterialRequest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.project.sitereck.GeneralClasses.SetSharedPrefrences;
import be.project.sitereck.GeneralClasses.URL_STRINGS;
import be.project.sitereck.ProjectManager.Better.CustomMenu.SlideMenu;
import be.project.sitereck.ProjectManager.POJO.MatReqItemClass;
import be.project.sitereck.ProjectManager.POJO.PmMiscData;
import static be.project.sitereck.ProjectManager.POJO.PmMiscData.getProjectlist;
import be.project.sitereck.R;

public class PM_AllMatReq extends AppCompatActivity  implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener{

    //UI declaration
    RelativeLayout lay_reqfilter;
    ImageView burgerimg;
    TextView toptitle, errormsg;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;

    //filter dialog
    Dialog dialog;
    //nested list data
    List<MatReqItemClass> allitemlist = new ArrayList<>();
    HashMap<String, List<MatReqItemClass>> mapfornesteddata = new HashMap<>();

    //    sharedpreferences
    SetSharedPrefrences prefrences = new SetSharedPrefrences(this);
    private String u_id = null;

    //Nested List
    NestedListData nestedListData;
    HashMap<String, String> TProjectlist = new HashMap<>();
    List<MatReqItemClass> matlist = new ArrayList<>();
    //Volley
    MatReqItemClass data;
    RequestQueue rq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pmallmatreq);

        //UI declaration
        lay_reqfilter = findViewById(R.id.lay_req_filterlist);
        toptitle = findViewById(R.id.title_top);    toptitle.setText("Material Requests");
        burgerimg = findViewById(R.id.menu_icon);

        TProjectlist = getProjectlist();

        errormsg = findViewById(R.id.textemptymatreq);
        swipeRefreshLayout = findViewById(R.id.container_reqList);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView = findViewById(R.id.rec_matreq);
        //swipeRefresh color scheme
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        //swiperefresh post method
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                JSON_DATA_WEB_CALL();
            }
        });

        //


        //UI clicklistners
        lay_reqfilter.setOnClickListener(this);
        burgerimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PM_AllMatReq.this, "burger", Toast.LENGTH_SHORT).show();
                CreateSlideMenu();
            }
        });
    }

    private void JSON_DATA_WEB_CALL() {
        swipeRefreshLayout.setRefreshing(true);
        u_id = prefrences.getVar_User_id();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_STRINGS.getCALL_AllMatReq(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.i("tagconvertstr", "["+response+"]");
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("success").equals("1")){

                        errormsg.setVisibility(View.GONE);
                        JSONArray array= jsonObject.getJSONArray("requests");

                        for (int i = 0 ; i<array.length(); i++){
                            JSONObject object = array.getJSONObject(i);
                            data = new MatReqItemClass(object.getString("req_id"),
                                    object.getString("proj_id"),
                                    object.getString("req_date"),
                                    object.getString("req_required_date"),
                                    object.getString("req_material"),
                                    object.getString("req_status"),
                                    object.getString("note")
                            );
                            allitemlist.add(data);
                            FilterNestedData(data);
//                            System.out.println("data -"+data);
                        }

                    }else if(jsonObject.getString("success").equals("0")){
                        Toast.makeText(PM_AllMatReq.this, "There Are No Requests YET !", Toast.LENGTH_LONG).show();
                        errormsg.setVisibility(View.VISIBLE);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println(e.toString());
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
                swipeRefreshLayout.setRefreshing(false);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("u_id",u_id);
                return params;
            }
        };
        rq = Volley.newRequestQueue(this);
        rq.add(stringRequest);
    }

    private void FilterNestedData(MatReqItemClass data) {
        matlist.add(data);
        mapfornesteddata.put(data.getPId(),matlist);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lay_req_filterlist:
                MakeFilterDialog();
                break;

        }
    }

    private void CreateSlideMenu() {
        SlideMenu slideMenu = new SlideMenu();
        ViewGroup viewGroup = findViewById(android.R.id.content);
        slideMenu.FullDialog(PM_AllMatReq.this,PM_AllMatReq.this,viewGroup);
    }

    private void MakeFilterDialog() {
        dialog = new Dialog(PM_AllMatReq.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_matreq_filter);
        LinearLayout l1 = dialog.findViewById(R.id.ll);
        l1.setBackgroundResource(R.drawable.dialog_bg);
        dialog.show();
        dialog.setCancelable(true);
        TextView all;
        final ListView listView ;
        listView = dialog.findViewById(R.id.list_proname);
        all = dialog.findViewById(R.id.item_allproject);
        final String[] valuelist  = getProjectlist().values().toArray(new String[0]);
        ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.item_proname, valuelist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(PM_AllMatReq.this, "name -"+valuelist[position], Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRefresh() {

    }
}