package com.example.libsnarktestbench;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.HelloWorld;
import com.zKrypto.Proof;
import com.zKrypto.libSnarkJNI ;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private static final String TAG = "SNARK_LOG" ;
    private String circuit_name = "Register" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ((Spinner) (findViewById(R.id.spr_select_task))).setOnItemSelectedListener(this);

    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        if (parent.getId() == R.id.spr_select_task ){

            circuit_name = parent.getItemAtPosition(pos).toString() ;

            Log.d(TAG, "onTaskItemSelected: " + circuit_name );
        }


    }

    public void onNothingSelected(AdapterView<?> parent) { }

    public void onClickTemp(View view) {
        System.out.println("123123");

//        byte[] arr = {1,2};
        String output = Proof.encrypt("hello1");
        System.out.printf("output: " + output);
        System.out.println("234234");
//        Foo.bar1("power");
//        System.out.println("1111 " + a);
    }

    public void onRunTestClick( View view  ){

        // run snark in background thread
        new Thread(new Runnable() {
            public void run() {

                try {

                    String doc_loc = getFilesDir().toString() + "/" ;
                    File cs_file_path = new File(doc_loc + circuit_name.toLowerCase() + "_constraint_system.dat") ;
                    String cs_file_checksum = "android test app" ;
                    File pk_file_path = new File(doc_loc + circuit_name.toLowerCase() + "_crs_pk.dat") ;
                    File vk_file_path = new File(doc_loc + circuit_name.toLowerCase() + "_crs_vk.dat") ;
                    File proof_file_path = new File(doc_loc + circuit_name.toLowerCase() + "_proof.dat") ;
                    String vk_json , proof_json ;
                    String inputs_json = loadPackageTextFile(
                                            (circuit_name.compareToIgnoreCase("Register") == 0 ) ?  R.raw.sample_input_register :
                                            (circuit_name.compareToIgnoreCase("Tally")    == 0 ) ?  R.raw.sample_input_tally :
                                            (circuit_name.compareToIgnoreCase("Vote")     == 0 ) ?  R.raw.sample_input_vote :
                                                                                                        R.raw.sample_input_zklay ) ;

                    libSnarkJNI context ;


                    //
                    // create embedded circuit and write
                    //  -   constraint system
                    //  -   proving key
                    //  -   verify key
                    //  to file
                    context = new libSnarkJNI(circuit_name );
                    context.buildCircuit();
                    context.runSetup();
                    context.getConstraintSystem( cs_file_path,true, cs_file_checksum) ;
                    context.getProofKey( pk_file_path ) ;
                    context.getVerifyKey( vk_file_path ) ;
                    vk_json = context.getVerifyKey() ;
                    context.close();


                    //
                    // create embedded circuit and run proof
                    //
                    context = new libSnarkJNI(circuit_name );
                    context.buildCircuit();
                    context.setInput(inputs_json );
                    context.setProofKey( pk_file_path );
                    context.runProof();
                    proof_json = context.getProof();
                    context.close();


                    //
                    // re-construct circuit from constraint system file and run proof
                    //
                    context = new libSnarkJNI(circuit_name , cs_file_path.toString() );
                    context.buildCircuit();
                    context.setInput(inputs_json );
                    context.setProofKey( pk_file_path );
                    context.runProof();
                    context.getProof( proof_file_path );
                    context.close();


                    //
                    // verify proofs
                    //
                    context = new libSnarkJNI(circuit_name );
                    context.buildCircuit();
                    context.setInput( inputs_json );
                    context.setVerifyKey( vk_file_path );
                    context.setProof( proof_json );
                    context.runVerify();
                    context.close();
                    Log.d  ( TAG ,"\n <<< Verify 1 Success >>>\n" );

                    context = new libSnarkJNI(circuit_name , cs_file_path.toString() );
                    context.buildCircuit();
                    context.setInput( inputs_json );
                    context.setVerifyKey(vk_json );
                    context.setProof( proof_file_path  );
                    context.runVerify();
                    context.close();
                    Log.d  ( TAG ,"\n <<< Verify 2 Success >>>\n" );


                }catch ( Exception e ){
                    Log.d  ( TAG ," <<< *** Error : " + e.toString() + "  ***" );
                }

                // update UI in main thread
                runOnUiThread( new Runnable() {
                    public void run() {
                        Log.d  ( TAG ,"\n\n Test Complete \n" );
                    }
                });

            }
        }).start();

    }


    private String loadPackageTextFile(int resID) throws IOException {
        InputStream in = getResources().openRawResource( resID );
        int size = in.available();
        byte[] buffer = new byte[size];
        in.read(buffer);
        in.close();
        return new String(buffer, "UTF-8");
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}