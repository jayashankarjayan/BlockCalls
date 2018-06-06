package com.jayan.jayashankar.blockcalls;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jayan.jayashankar.blockcalls.telephony.LocalDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int READ_PHONE_STATE = 1, VIBRATE = 2;
    TextInputLayout textinputlayoutnumber;
    TextInputEditText textinputedittextnumber;
    Button buttonblock;
    SwipeRefreshLayout swiperefresh;

    private List<Numbers> numbersList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NumbersAdapter mAdapter;

    LocalDatabase localDatabase;
    List blockednumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissions();

        localDatabase = new LocalDatabase(getApplicationContext());
        blockednumbers = localDatabase.getBlockedNumbers();
        swiperefresh = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);

        textinputlayoutnumber = (TextInputLayout)findViewById(R.id.textinputlayoutnumber);
        textinputedittextnumber = (TextInputEditText)findViewById(R.id.textinputedittextnumber);
        buttonblock = (Button)findViewById(R.id.buttonblock);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new NumbersAdapter(numbersList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        prepareNumbers();

        buttonblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(blockednumbers.size() == 0)
                {
                    if(localDatabase.insertNumber(textinputedittextnumber.getText().toString()))
                    {
                        Toast toast = new Toast(getApplicationContext());
                        toast.makeText(getApplicationContext(), "Blocked", Toast.LENGTH_SHORT).show();
                        toast.cancel();
                    }
                    else
                    {

                        Toast.makeText(getApplicationContext(), "Not blocked", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {

                    for (int i=0;i<blockednumbers.size();i++)
                    {
                        if(!(blockednumbers.get(i).equals(textinputedittextnumber.getText().toString())))
                        {
                            if(localDatabase.insertNumber(textinputedittextnumber.getText().toString()))
                            {
                                Toast.makeText(getApplicationContext(), "Blocked", Toast.LENGTH_SHORT).show();
                                textinputedittextnumber.setText("");
                            }
                            else
                            {

                                Toast.makeText(getApplicationContext(), "Not blocked", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Number already blocked", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                clearViews();
                swiperefresh.setRefreshing(true);
                swiperefresh.setRefreshing(false);
            }
        });
        
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                clearViews();
                swiperefresh.setRefreshing(false);
            }
        });
    }

    private void clearViews()
    {
//        int size = numbersList.size();
        /*numbersList.clear();
        mAdapter.notifyItemRangeRemoved(0, size);*/
        prepareNumbers();
    }
    
    private void prepareNumbers() {

        numbersList.clear();
        for(int i=0;i<blockednumbers.size();i++)
        {
            Numbers numbers = new Numbers(blockednumbers.get(i).toString());
            numbersList.add(numbers);
        }

        mAdapter.notifyDataSetChanged();
    }


    private void permissions()
    {
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_PHONE_STATE)) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                        .setMessage("You need to provide permission to read phone state")
                        .setPositiveButton("Ok",null);
                builder.show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},READ_PHONE_STATE);
            }

        }

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.VIBRATE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.VIBRATE)) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                        .setMessage("You need to provide this permission to disable volume")
                        .setPositiveButton("Ok",null);;
                builder.show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.VIBRATE},VIBRATE);
            }

        }
    }

    private class NumbersAdapter extends RecyclerView.Adapter<NumbersAdapter.MyViewHolder> {

        private List<Numbers> moviesList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView number;
            public Button remove;

            public MyViewHolder(View view) {
                super(view);
                number = (TextView) view.findViewById(R.id.number);
                remove = (Button)view.findViewById(R.id.remove);
            }
        }


        public NumbersAdapter(List<Numbers> moviesList) {
            this.moviesList = moviesList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.number_list_row, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            Numbers numbers = moviesList.get(position);
            holder.number.setText(numbers.getNumbers());
            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(localDatabase.deleteTitle(holder.number.getText().toString()))
                    {
                        Toast.makeText(MainActivity.this, "Unblocked", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Not unblocked", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return moviesList.size();
        }
    }

    class Numbers {
        private String numbers;

        Numbers(String numbers) {
            this.numbers = numbers;
        }

        public String getNumbers() {
            return numbers;
        }

        public void setNumbers(String numbers) {
            this.numbers = numbers;
        }
    }
}

