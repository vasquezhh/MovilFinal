package com.example.vasquezh.stuff;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;


public class CreateAssigment extends Fragment {

    private EditText name;
    private ImageButton picker, picker2;
    private TextView startDate, finishDate;
    private Button btnCreate;
    private Firebase m_fb;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ID_KEY = "idKey";

    // TODO: Rename and change types of parameters
    private String keyProfessor;

    public CreateAssigment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param idKey Parameter 1.
     * @return A new instance of fragment CreateAssigment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateAssigment newInstance(String idKey) {
        CreateAssigment fragment = new CreateAssigment();
        Bundle args = new Bundle();
        args.putString(ID_KEY, idKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            keyProfessor = getArguments().getString(ID_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_create_assigment, container, false);
        name=(EditText)v.findViewById(R.id.input_name_create);
        startDate = (TextView)v.findViewById(R.id.start_date_create);
        finishDate = (TextView)v.findViewById(R.id.finish_date_create);
        picker = (ImageButton)v.findViewById(R.id.picker);
        picker2 = (ImageButton)v.findViewById(R.id.picker2);
        btnCreate = (Button) v.findViewById(R.id.create_assign);
        m_fb = new Firebase("https://vasquezhproyectofinal.firebaseio.com/");

        picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDatePicker mdp=new MyDatePicker();
                        //MyDatePicker.newInstance(startDate.getId());
                mdp.show(getFragmentManager(),"DatePicker");
                mdp.setId(startDate.getId());
            }
        });

        picker2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDatePicker mdp = new MyDatePicker();
                mdp.show(getFragmentManager(),"DatePicker");
                mdp.setId(finishDate.getId());

            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(name.getText().toString().isEmpty() || startDate.getText().toString().isEmpty() || finishDate.getText().toString().isEmpty()) {
                        Snackbar.make(getView(),"Some field are empty",Snackbar.LENGTH_SHORT).show();
                    }else {
                        Firebase ref = m_fb.child("assigments");
                        Firebase pushRef = ref.push();

                        Map<String, String> data = new HashMap<String, String>();
                        data.put("createdBy", keyProfessor);
                        data.put("name", name.getText().toString());
                        data.put("startDate", startDate.getText().toString());
                        data.put("finishDate", finishDate.getText().toString());
                        pushRef.setValue(data);

                        Snackbar.make(getView(), "Successfully created assigment", Snackbar.LENGTH_SHORT).show();
                        getFragmentManager().popBackStack();
                    }
                }catch (Exception e){
                    Log.e("Error","Creating Assigment");
                }
                //LocationStuff ls = new LocationStuff();
                //getFragmentManager().beginTransaction().replace(R.id.fragment_container,ls).addToBackStack(null).commit();

            }
        });

        return v;
    }

}
