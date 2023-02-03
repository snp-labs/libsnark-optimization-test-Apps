
// change package name to match your app package name
package com.zKrypto ;

import android.util.Log;
import java.io.File;

public class libSnarkJNI {

    private static final String TAG = "SNARK_LOG" ;

    public static final int R1CS_GG = 1 ;
    public static final int R1CS_ROM_SE = 2 ;

    public static final int EC_ALT_BN128 = 1 ;
    public static final int EC_BLS12_381 = 2 ;

    public static final int serializeFormatDefault  = 1 ;
    public static final int serializeFormatCRV      = 2 ;
    public static final int serializeFormatZKlay    = 3 ;

    static {
        System.loadLibrary("Snark");
    }

    private int context_id ;
    private String circuit_name ;
    private Boolean circuit_is_ready ;
    private Object RuntimeException ;

    private void init_ftn( String circuitName ,  int serializeFormat , int ecSelection , String cs_file_path ){
        circuit_name = circuitName ;
        context_id = createCircuitContext( circuit_name , libSnarkJNI.R1CS_GG , ecSelection , "" , "" , cs_file_path ) ;
        serializeFormat(context_id , serializeFormat );
        circuit_is_ready = false ;
        Log.d  ( TAG ,"context id for " + circuitName + " : " + context_id + " , last msg : " + getLastFunctionMsg(context_id) );
    }

    public libSnarkJNI( String circuitName ){
        init_ftn( circuitName , serializeFormatDefault , libSnarkJNI.EC_BLS12_381 , "" ) ;
    }

    public libSnarkJNI( String circuitName , String cs_file_path ){
        init_ftn( circuitName , serializeFormatDefault , libSnarkJNI.EC_BLS12_381 , cs_file_path ) ;
    }
    public libSnarkJNI( String circuitName ,  int serializeFormat , int ecSelection , String cs_file_path ){
        init_ftn( circuitName , serializeFormat , ecSelection , cs_file_path ) ;
    }


    public void buildCircuit() {
        if ( !circuit_is_ready ) {
            int rtn = buildCircuit( context_id ) ;
            if (rtn != 0) { throw new RuntimeException("build circuit error") ; }
            circuit_is_ready = true ;
            Log.d  ( TAG ,"buildCircuit : " + context_id + " , " + circuit_name + " , " + rtn + " , " + getLastFunctionMsg(context_id) );
        }
    }

    public void runSetup()  {
        int rtn = runSetup(context_id) ;
        if (rtn != 0) { throw new RuntimeException("run setup error") ;  }
        Log.d  ( TAG ,"runSetup : " + context_id + " , " + circuit_name + " , " + rtn + " , " + getLastFunctionMsg(context_id) );
    }

    public void runProof()  {
        int rtn = runProof(context_id) ;
        if (rtn != 0) { throw new RuntimeException("run proof error") ; }
        Log.d  ( TAG ,"runProof : " + context_id + " , " + circuit_name + " , " + rtn + " , " + getLastFunctionMsg(context_id) );
    }

    public void runVerify() {
        int rtn = runVerify(context_id) ;
        if (rtn != 0) { throw new RuntimeException("run verify error") ; }
        Log.d  ( TAG ,"runVerify : " + context_id + " , " + circuit_name + " , " + rtn + " , " + getLastFunctionMsg(context_id) );
    }


    public void getConstraintSystem( File path, Boolean use_compression , String checksum_prefix )  {
        int rtn = writeConstraintSystem(
                context_id ,
                path.toString() ,
                (use_compression) ? 1 : 0 ,
                checksum_prefix );
        if (rtn != 0) { throw new RuntimeException("writeConstraintSystemError") ; }
    }


    public void getProofKey( File path ) {
        int rtn = writePK( context_id , path.toString() ) ;
        if (rtn != 0) { throw new RuntimeException("writeProofKeyToFileError") ; }
    }

    public void setProofKey( File path ) {
        int rtn = readPK( context_id , path.toString() ) ;
        if (rtn != 0) { throw new RuntimeException("") ; }
    }


    public void getVerifyKey( File path ) {
        int rtn = writeVK( context_id , path.toString() ) ;
        if (rtn != 0) { throw new RuntimeException("writeVerifyKeyToFileError") ; }
    }

    public void setVerifyKey( File path ) {
        int rtn = readVK( context_id , path.toString() ) ;
        if (rtn != 0) { throw new RuntimeException("readVerifyKeyFromFileError") ; }
    }

    public String getVerifyKey() {
        return serializeVerifyKey( context_id ) ;
    }

    public void setVerifyKey( String json ) {
        int rtn = deSerializeVerifyKey( context_id , json ) ;
        if (rtn != 0) { throw new RuntimeException("deSerializeVerifyKeyError") ; }
    }


    public void getProof( File path ) {
        int rtn = writeProof( context_id , path.toString() ) ;
        if (rtn != 0) { throw new RuntimeException("writeProofToFileError") ; }
    }

    public void setProof( File path ) {
        int rtn = readProof( context_id , path.toString() ) ;
        if (rtn != 0) { throw new RuntimeException("readProofFromFileError") ; }
    }

    public String getProof()  {
        return serializeProof( context_id );
    }

    public void setProof( String json  )  {
        int rtn = deSerializeProof( context_id , json ) ;
        if (rtn != 0) { throw new RuntimeException("deSerializeProofError") ; }
    }



    public void setInput( String inputs_json  ) {
        int rtn = updatePrimaryInputFromJson( context_id , inputs_json ) ;
        if (rtn != 0) { throw new RuntimeException("updatePrimaryInputFromJsonError") ; }
    }

    public void setInput( String key , String hexValue ) {
        int rtn = updatePrimaryInputStr( context_id , key , hexValue );
        if (rtn == -1) {
            throw new RuntimeException("updatePrimaryInputStr") ;
        }else if (rtn == 1){
            throw new RuntimeException("invalidPrimaryInputKey (invalidKey: " + key + " )" );
        }
    }

    public void setInput( String key  , int arrayIdx , String hexValue ) {

        int rtn = updatePrimaryInputArrayStr(context_id, key, arrayIdx, hexValue );

        if (rtn == -1) {
            throw new RuntimeException("updatePrimaryInputArrayStr") ;
        }else if (rtn == 1){
            throw new RuntimeException("invalidPrimaryInputKey (invalidKey: " + key + " )" );
        }else if (rtn == 2) {
            throw new RuntimeException("invalidPrimaryInputIndex (invalidIndex: " + arrayIdx + " forkey : " + key + " )");
        }

    }



    public String lastMsg()  {
        return getLastFunctionMsg( context_id ) ;
    }

    public void close() {
        if (context_id > 0) {
            Log.d  ( TAG ,"close context : " + context_id ) ;
            finalizeCircuit(context_id);
            context_id = -1 ;
        }
    }

    protected void finalize() {
        if (context_id > 0) {
            Log.d  ( TAG ,"finalize : " + context_id ) ;
            finalizeCircuit(context_id);
        }
    }


    /*
     *
     *  See https://github.com/snp-labs/libsnark-optimization/blob/master/api.hpp
     *
     */

    private native int createCircuitContext(String circuit_name ,
                                            int proof_system ,
                                            int ecSelection ,
                                            String arith_text_path ,
                                            String inputs_text_path ,
                                            String cs_file_path);

    private native int serializeFormat(int circuit_context_id , int format  );
    private native int assignCircuitArgumentsByJson(int circuit_context_id , String args_json_string );
    private native int buildCircuit ( int circuit_context_id  ) ;
    private native int runSetup (int circuit_context_id  ) ;
    private native int runProof (int circuit_context_id  ) ;
    private native int runVerify (int circuit_context_id  ) ;
    private native int updatePrimaryInput(int context_id , String input_name , int value );
    private native int updatePrimaryInputStr(int context_id , String input_name , String value_str );
    private native int updatePrimaryInputArray(int context_id , String input_name , int array_index, int value );
    private native int updatePrimaryInputArrayStr(int context_id , String input_name , int array_index, String value_str );
    private native int updatePrimaryInputFromJson(int context_id , String json_sting);
    private native int writeConstraintSystem(int context_id , String file_name , int use_compression , String checksum_prefix );
    private native int verifyConstraintSystemFileChecksum(int context_id , String file_name , String checksum_prefix ) ;
    private native int writeCircuitToFile (int circuit_context_id  , String document_dir) ;
    private native int writeInputsToFile (int circuit_context_id  , String document_dir) ;
    private native int writePK (int circuit_context_id  , String document_dir) ;
    private native int readPK (int circuit_context_id  , String document_dir) ;
    private native int writeVK (int circuit_context_id  , String document_dir) ;
    private native int readVK (int circuit_context_id  , String document_dir) ;
    private native int writeProof (int circuit_context_id  , String document_dir) ;
    private native int readProof (int circuit_context_id  , String document_dir) ;

    private native String serializeVerifyKey(int circuit_context_id );
    private native int deSerializeVerifyKey(int circuit_context_id, String json_string);
    private native String serializeProof(int circuit_context_id );
    private native int deSerializeProof(int circuit_context_id, String json_string);
    private native int finalizeCircuit(int circuit_context_id  ) ;
    private native String getLastFunctionMsg(int circuit_context_id) ;

}

