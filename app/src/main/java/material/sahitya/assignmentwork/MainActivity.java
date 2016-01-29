package material.sahitya.assignmentwork;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private TextView tvEmptyView;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private List<UserDetails> userDetailses;


    protected Handler handler;

    private InternetConnectionDetector connectionDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        tvEmptyView = (TextView) findViewById(R.id.empty_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        connectionDetector = new InternetConnectionDetector(MainActivity.this);
        if (!connectionDetector.isConnectingToInternet()) {
            Toast.makeText(MainActivity.this, "Kindly Connect to Internet", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Connected to Internet", Toast.LENGTH_SHORT).show();
        }

        //intializing user list
        userDetailses = new ArrayList<UserDetails>();
        handler = new Handler();
        //intializing toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("AppsDomeAssignment");

        }

        // loading dummy data
        loadData();

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerViewAdapter(userDetailses, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);


        if (userDetailses.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            tvEmptyView.setVisibility(View.VISIBLE);

        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            tvEmptyView.setVisibility(View.GONE);
        }

        mAdapter.setOnLoadMoreListener(new InfiniteScrollListener() {
            @Override
            public void onLoadMore() {
                userDetailses.add(null);
                mAdapter.notifyItemInserted(userDetailses.size() - 1);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        userDetailses.remove(userDetailses.size() - 1);
                        mAdapter.notifyItemRemoved(userDetailses.size());
                        int start = userDetailses.size();
                        int end = start + 5;

                        if (end <= 20) {
                            for (int i = start + 1; i <= end; i++) {
                                userDetailses.add(new UserDetails("Name " + i, "ID 10" + i, "Key 00" + i));
                                mAdapter.notifyItemInserted(userDetailses.size());
                            }
                        }
                        {
                            Toast.makeText(MainActivity.this, "No More Item to Feed", Toast.LENGTH_SHORT).show();
                        }
                        mAdapter.setLoaded();
                        //or you can add all at once but do not forget to call mAdapter.notifyDataSetChanged();
                    }
                }, 4000);

            }
        });

    }


    // load initial data
    private void loadData() {
        for (int i = 1; i <= 5; i++) {
            userDetailses.add(new UserDetails("Name " + i, "ID 10" + i, "Key 00" + i));

        }


    }


}
