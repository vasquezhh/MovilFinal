package com.example.vasquezh.stuff;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends Fragment {

    private EditText fullNameEdt, passwordEdt, emailEdt;
    private Button signUpButton;
    private ImageView photoView;
    protected Firebase m_fb;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignUp() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignUp.
     */
    // TODO: Rename and change types and number of parameters
    public static SignUp newInstance(String param1, String param2) {
        SignUp fragment = new SignUp();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        fullNameEdt = (EditText)view.findViewById(R.id.input_name);
        passwordEdt = (EditText)view.findViewById(R.id.input_password);
        emailEdt    = (EditText)view.findViewById(R.id.input_email);
        signUpButton = (Button)view.findViewById(R.id.btn_signup);
        m_fb = new Firebase("https://vasquezhproyectofinal.firebaseio.com/");

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Firebase ref=m_fb.child("users");
                Firebase pushRef=ref.push();
                if(fullNameEdt.getText().toString().isEmpty() || emailEdt.getText().toString().isEmpty() || passwordEdt.getText().toString().isEmpty()) {
                    Snackbar.make(getView(),"Some text in blank",Snackbar.LENGTH_SHORT).show();

                }else{
                    Map<String, String> data = new HashMap<String, String>();
                    data.put("name", fullNameEdt.getText().toString());
                    data.put("email", emailEdt.getText().toString());
                    data.put("password", passwordEdt.getText().toString());
                    pushRef.setValue(data);
                    getFragmentManager().popBackStack();
                }
                /*m_fb.createUser(emailEdt.getText().toString(), passwordEdt.getText().toString(), new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result) {
                        //System.out.println("Successfully created user account with uid: " + result.get("uid"));
                        Snackbar.make(getView(),"Successfully created user account", Snackbar.LENGTH_SHORT ).show();
                        getFragmentManager().popBackStack();
                    }
                    @Override
                    public void onError(FirebaseError firebaseError) {
                        Snackbar.make(getView(),"An error ocurred", Snackbar.LENGTH_SHORT ).show();
                    }
                });*/

            }
        });

        return view;
    }
}
