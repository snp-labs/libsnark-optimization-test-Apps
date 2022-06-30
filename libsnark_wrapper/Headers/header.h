

#define USING_SWIFT_WRAPPER
#define USING_C_WRAPPER
#define CIRCUIT_BUILDER_CRV


#pragma once

#include <stdio.h>
#include <string.h>


#ifdef __cplusplus
extern "C" {
#endif

    /** \anchor all_a */
    
    #define R1CS_GG         1
    #define R1CS_ROM_SE     2
    

    /** \mainpage LibSnark API
     * \ref grp1_a "Circuit Initialization and Construction Functions." \n
     * \ref grp2_a "Core Snark Functions." \n
     * \ref grp3_a "Primary Inputs Update Functions." \n
     * \ref grp4_a "Serialize Data Structures (proof key, verify key, and proof data)." \n
     * \ref all_a "All API Functions." \n
     */

    /** @defgroup grp1 Circuit Initialization and Construction Functions
     * \anchor grp1_a
     * @{
     */
    
    /**
     * Initialize a circuit context.
     *
     * The API allows users to concurrently
     * create and use multiple circuits. Use this function
     * to create a separate circuit instance.
     *
     * @param circuit_name - name of circuit. Also selects which of the embedded circuit generator to use
     *
     * @param proof_system - select the proof system : {@link #R1CS_GG} or {@link #R1CS_ROM_SE}
     *
     * @return An identifier to the circuit instance
     */
    int createCircuitContext(const char * circuit_name , int proof_system );
    
    /**
     * Construct the embedded circuit
     *
     * \b circuit_name parameter in {@link #createCircuitContext} specifies the constructed circuit
     *
     * @param context_id - circuit instance identifier. returned by {@link #createCircuitContext}
     *
     * @return  0 : success\n
     *         -1 : invalid \b context_id \n
     *          1 : error occurred, get the error description with {@link #getLastFunctionMsg}
     */
    int buildCircuit(int context_id );

    /**
     * Construct circuit using constraint system files
     *
     * @param context_id - circuit instance identifier. returned by {@link #createCircuitContext}
     *
     * @param cs_file_name - constraint system file ( generated with the {@link #writeConstraintSystem} function )
     *
     * @return 0 : \b success \n
     *        -1 : invalid \b context_id \n
     *         1 : error occurred, get the error description with {@link #getLastFunctionMsg}
     */
    int buildCircuitFromCSFile(int context_id , const char * cs_file_name );
    
    /**
     * Construct circuit using arith/inputs text files
     *
     * \b circuit_name argument in {@link #createCircuitContext} specifies the constructed embedded circuit
     *
     * @param context_id - circuit instance identifier. returned by {@link #createCircuitContext}
     *
     * @param arith_text_path - circuit text file
     *
     * @param inputs_text_path - primary/auxiliary inputs text file
     *
     * @return 0 : \b success \n
     *        -1 : invalid \b context_id \n
     *         1 : error occurred, get the error description with {@link #getLastFunctionMsg}
     */
    int buildCircuitFromFile(int context_id ,
                             const char * arith_text_path,
                             const char * inputs_text_path);

    /**
     * Construct circuit using arith/inputs text files
     *
     * \b circuit_name argument in {@link #createCircuitContext} specifies the constructed embedded circuit
     *
     * @param context_id - circuit instance identifier. returned by {@link #createCircuitContext}
     *
     * @param args - specifies circuit arguments
     *
     * @return 0 : \b success \n
     *        -1 : invalid \b context_id \n
     *         1 : error occurred, get the error description with {@link #getLastFunctionMsg}
     */
    int assignCircuitArguments(
            int context_id ,
            const char ** args);
    /** @} */

    


    /** @defgroup grp2 Core Snark Functions
     * \anchor grp2_a
     * @param context_id - circuit instance identifier. returned by {@link #createCircuitContext}
     *
     * @return 0 : \b success \n
     *        -1 : invalid \b context_id \n
     *         1 : error occurred , get the error description with {@link #getLastFunctionMsg}
     * @{
     */
    int runSetup( int context_id );
    int runProof( int context_id );
    int runVerify( int context_id );
    /** @} */



    /** @defgroup grp3 Primary Inputs Update Functions
     * \anchor grp3_a
     * Following functions only works with the embedded circuit. \n
     * They update a key/value (string/BigInterger) mapping maintained by the constructed circuit \n
     * Circuits constructed with arith/inputs files not support.
     *
     * @param context_id - circuit instance identifier. returned by {@link #createCircuitContext}
     *
     * @param input_name - primary input key
     *
     * @param value - new value
     *
     * @param value_str - new value : a BigIngeter value in hexadecimal string
     *
     * @param array_index - index of a vector based primary input to update
     *
     * @return  0 : \b success \n
     *         -1 : invalid \b context_id \n
     *          1 : invalid \b input_name \n
     *          1 : \b array_index out-of-range
     * @{
     */
    int updatePrimaryInput(int context_id , const char* input_name , int value );
    int updatePrimaryInputStr(int context_id , const char* input_name , const char * value_str );
    int updatePrimaryInputArray(int context_id , const char* input_name , int array_index, int value );
    int updatePrimaryInputArrayStr(int context_id , const char* input_name , int array_index, const char * value_str );
    int updatePrimaryInputFromJson(int context_id , const char* input_json_string );
    /** @} */



    


/** @defgroup grp4 Serialize Data Structures (proof key, verify key, and proof data) to/from file or json string
    * \anchor grp4_a
    * @param context_id - circuit instance identifier. returned by {@link #createCircuitContext}
    *
    * @param file_name - the file name to read or write to .
    *
    * @param json_string - a JSON object used to de-serialize (re-construct) the respective object. Probably generated by their serialize function.
    *
    * @return 0 : \b success \n
    *        -1 : invalid \b context_id \n
    *         1 : error occurred , get the error description with {@link #getLastFunctionMsg}
    * @{
    */
   int writeConstraintSystem(int context_id , const char* file_name , int use_compression , const char* checksum_prefix );
   int verifyConstraintSystemFileChecksum(int context_id , const char* file_name , const char* checksum_prefix ) ;
   int writeCircuitToFile(int context_id , const char* file_name);
   int writeInputsToFile(int context_id , const char* file_name);

    int writeVK(int context_id , const char* document_dir);
    int readVK(int context_id , const char* document_dir);
    
    int writePK(int context_id , const char* document_dir);
    int readPK(int context_id , const char* document_dir);
    
    int writeProof(int context_id , const char* document_dir);
    int readProof(int context_id , const char* document_dir);
    
    
    const char* serializeVerifyKey(int context_id );
    int deSerializeVerifyKey(int context_id , const char* json_string);

    const char* serializeProof(int context_id );
    int deSerializeProof(int context_id , const char* json_string);


    #define serializeFormatDefault  1
    #define serializeFormatCRV      2
    #define serializeFormatZKlay    3
    int serializeFormat( int context_id , int format ) ;
    /** @} */


    /**
     * Delete constructed circuit and free up used memory. \n
     * calling any other function afterwards with the same \b context_id will fail.
     *
     * @param context_id - circuit instance identifier. returned by {@link #createCircuitContext}
     *
     * @return 0 : \b success \n
     *        -1 : invalid \b context_id
     */
    int finalizeCircuit(int context_id );
    int finalizeAllCircuit();

    

    /**
     * Get the last error description
     *
     * @param context_id - circuit instance identifier. returned by {@link #createCircuitContext}
     *
     * @return 0 : \b success \n
     *        -1 : invalid \b context_id
     */
    const char * getLastFunctionMsg(int context_id);
    
    
    #ifdef CIRCUIT_BUILDER_ZKLAY
    void ECGroupExp(const char* baseX, const char* exp, char* ret);
    #endif
    
#ifdef __cplusplus
}
#endif




#ifdef __cplusplus
extern "C" {
#endif

    #include "gmp.h"

    //
    // MiMC7 Hash
    //
    void mimc7_hash( mpz_ptr dst , mpz_srcptr input , mpz_srcptr fieldPrime );
    
    void mimc7_hash_from_array_inputs( mpz_ptr dst , const MP_INT* inputs , int inputs_count , mpz_srcptr fieldPrime );
    
    //
    // Merkle Tree Path
    //
    void merkle_tree_path_make_root( mpz_ptr dst ,
                                     mpz_srcptr directionSelector ,
                                     mpz_srcptr leafWires ,
                                     const MP_INT* intermediateHashes ,
                                     int intermediateHashes_count ,
                                     int treeHeight ,
                                     mpz_srcptr FIELD_PRIME ) ;

    //
    //  Curve25519
    //
    void Curve25519_init ( mpz_ptr dst_x , mpz_ptr dst_y , mpz_ptr self_x , int isMatch_ressol  );
    void Curve25519_add ( mpz_ptr dst_x , mpz_ptr dst_y , mpz_ptr self_x , mpz_ptr self_y , mpz_ptr other_x , mpz_ptr other_y);
    void Curve25519_sub ( mpz_ptr dst_x , mpz_ptr dst_y , mpz_ptr self_x , mpz_ptr self_y , mpz_ptr other_x , mpz_ptr other_y );
    void Curve25519_mul ( mpz_ptr dst_x , mpz_ptr dst_y , mpz_ptr self_x , mpz_ptr self_y , mpz_srcptr exp_mpz ) ;
    int Curve25519_isMatchRessol ( mpz_srcptr self_x , mpz_srcptr self_y ) ;

#ifdef __cplusplus
}
#endif
