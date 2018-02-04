package com.jnj.devicetracker.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;


import com.devicetracker.R;
import com.jnj.devicetracker.adapter.DeviceListAdapter;
import com.jnj.devicetracker.adapter.TransactionListAdapter;
import com.jnj.devicetracker.db.DeviceDataPersistence;
import com.jnj.devicetracker.db.TransactionDataPersistence;
import com.jnj.devicetracker.sync.SyncService;
import com.jnj.devicetracker.sync.event.EventBusManager;
import com.jnj.devicetracker.sync.event.SyncEvent;
import com.jnj.devicetracker.sync.event.SyncStatus;
import com.jnj.devicetracker.sync.event.SyncType;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.realm.Realm;

/**
 * An activity representing a list of Devices. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link DeviceDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    public boolean mTwoPane;
    private Realm realm;
    private SwipeRefreshLayout swipeView;

    private TransactionListAdapter listAdapter;
    private RecyclerView recyclerView;
    private  FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SyncService.start(getApplicationContext());
        realm = Realm.getDefaultInstance();
        EventBusManager.register(this);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        getSupportFragmentManager().addOnBackStackChangedListener(this);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, R.string.create_device, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                createNewDevice();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.device_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setupRecyclerView(recyclerView);

        if (findViewById(R.id.device_detail_container) != null) {
            // The detail container view will be present only in the
            // landscape layouts. If this view is present, then the1
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        swipeView = (SwipeRefreshLayout) findViewById(R.id.swipeView);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SyncService.request(SyncType.TRANSACTION);
            }
        });
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        listAdapter = new TransactionListAdapter (TransactionDataPersistence.getAll(realm), this);

        recyclerView.setAdapter(listAdapter);
    }



    private void createNewDevice(){
        NewDeviceFragment fragment = new NewDeviceFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frameLayout, fragment)
                .addToBackStack("")
                .commit();

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_all) {
            DeviceDataPersistence.removeAll();
            SyncEvent.send(SyncType.DEVICE, SyncStatus.COMPLETED); // tell all related screens to reload data from realm

            SyncService.request(SyncType.DEVICE);

        }

        return super.onOptionsItemSelected(item);
    }





    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SyncEvent event) {
        if (event.getType() == SyncType.TRANSACTION && event.getStatus() == SyncStatus.IN_PROGRESS) {
            if (!swipeView.isRefreshing()) {
                swipeView.setRefreshing(true);
            }
        } else if (event.getType() == SyncType.TRANSACTION && event.getStatus() == SyncStatus.COMPLETED) {
            swipeView.setRefreshing(false);
            listAdapter.notifyDataSetChanged();
        }
    }
    @Override
    protected void onDestroy() {
        realm.close();
        EventBusManager.unregister(this);
        super.onDestroy();
    }


    @Override
    public void onBackStackChanged() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            fab.setVisibility(View.GONE);
        }

        else{
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            fab.setVisibility(View.VISIBLE);

        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        //This method is called when the up button is pressed. Just the pop back stack.
        getSupportFragmentManager().popBackStack();
        return true;
    }
}
