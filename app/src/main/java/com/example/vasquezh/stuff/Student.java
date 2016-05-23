package com.example.vasquezh.stuff;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Student#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Student extends Fragment implements StudentViewAdapter.RecyclerClickListener{

    private RecyclerView myRecyclerView;
    private StudentViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Firebase m_fb, m_fbp;
    private ArrayList<Professor.Assigment> assigments;
    private ProgressDialog pDialog;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    static final String ID_KEY = "idKeyStudent";
    static final String ID_NAME = "idNameStudent";

    // TODO: Rename and change types of parameters
    private String keyStudent;
    private String nameStudent;


    public Student() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param key Parameter 1.
     * @param name Parameter 2.
     * @return A new instance of fragment Student.
     */
    // TODO: Rename and change types and number of parameters
    public static Student newInstance(String key, String name) {
        Student fragment = new Student();
        Bundle args = new Bundle();
        args.putString(ID_KEY, key);
        args.putString(ID_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            keyStudent = getArguments().getString(ID_KEY);
            nameStudent = getArguments().getString(ID_NAME);
        }
        Firebase.setAndroidContext(getContext());
        setHasOptionsMenu(true);
        m_fb  = new Firebase("https://vasquezhproyectofinal.firebaseio.com/assigments");
        m_fbp = new Firebase("https://vasquezhproyectofinal.firebaseio.com/professors");
        Log.i("onCreate","Student");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_student, container, false);
        myRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_student_view);
        mLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        myRecyclerView.setLayoutManager(mLayoutManager);
        myRecyclerView.setItemAnimator(new DefaultItemAnimator());
        assigments = new ArrayList<Professor.Assigment>();

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar_student_view);
        AppCompatActivity appca = ((AppCompatActivity)getActivity());
        appca.setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar ab=appca.getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true);
        ab.setIcon(R.drawable.pin2);
        ab.setTitle(nameStudent);

        Log.i("onCreateView","Student");
        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        new GetData().execute();
        Log.i("onResume","Student");
        assigments.clear();
    }

    /*
    * Metodo para saber cual item del recyclerview ha sido presionado con
    * la información pertinente sobre ese item
    *
    * */

    @Override
    public void itemClick(Professor.Assigment assigment) {
        Log.i("itemClick",assigment.getName());
        LocationStuff ls = LocationStuff.newInstance(keyStudent,nameStudent,assigment.getKeyFireBase());
        getFragmentManager().beginTransaction().replace(R.id.fragment_container,ls).addToBackStack(null).commit();
    }

    private class GetData extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog=new ProgressDialog(getContext());
            pDialog.setTitle("App Stuff");
            pDialog.setMessage("Checking...");
            pDialog.setIndeterminate(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            m_fb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        Professor.Assigment a = postSnapshot.getValue(Professor.Assigment.class);
                        a.setKeyFireBase(postSnapshot.getKey());
                        //System.out.println(a.getKeyFireBase()+" "+a.getCreatedBy()+" "+ a.getName() + " - " + a.getStartDate()+" - "+a.getFinishDate());
                        assigments.add(a);
                        System.out.println(assigments.size());
                    }
                    findProfessorsName();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            pDialog.dismiss();
        }

        public void findProfessorsName(){
            for (int i=0;i<assigments.size();i++) {
                Query queryRef = m_fbp.orderByKey().equalTo(assigments.get(i).getCreatedBy());
                final int finalI = i;
                queryRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        assigments.get(finalI).setCreatedByName(dataSnapshot.child("name").getValue().toString());
                        Log.e("findProfess...", dataSnapshot.child("name").getValue().toString());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
            mAdapter = new StudentViewAdapter(assigments);
            mAdapter.setRecyclerClickListener(Student.this);
            myRecyclerView.setAdapter(mAdapter);
        }
    }
}
