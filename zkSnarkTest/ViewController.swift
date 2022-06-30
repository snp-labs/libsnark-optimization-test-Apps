//
//  ViewController.swift
//  zkSnarkTest
//
//  Created by Thomas Haywood Dadzie on 2021/12/02.
//

import UIKit
import zkSnark


class ViewController: UIViewController , UIPickerViewDelegate , UIPickerViewDataSource {

    @IBOutlet weak var Picker: UIPickerView!
    @IBOutlet weak var runButton: UIButton!
    
    let pickerData : [[String]] = [["Register","Tally","Vote","ZKlay"]]
        
    override func viewDidLoad() {
        super.viewDidLoad()
        self.Picker.delegate = self
        self.Picker.dataSource = self
    }


    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return pickerData[component].count
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return pickerData[component][row]
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {}
    
    
    @IBAction func OnClick(_ sender: UIButton, forEvent event: UIEvent) {
        
        let circuit_name = pickerData[0][ Picker.selectedRow(inComponent: 0 ) ] ;
        let title = " ** Running All for \(circuit_name) ** "
        print(title)
        
        sender.setTitle( title , for: UIControl.State.disabled )  ;
        sender.isEnabled = false ;
        
        // run in background
        DispatchQueue.background(background: {
            
            do {
            
                guard let doc_loc = NSSearchPathForDirectoriesInDomains( .documentDirectory , .userDomainMask , true ).first
                else { print ( "Unable to acquire document path " ) ; return ; }
            
                let cs_file_path = "\(doc_loc)/\(circuit_name.lowercased())_constraint_system.dat"
                let cs_file_checksum = "test_app"
                let pk_file_path = "\(doc_loc)/\(circuit_name.lowercased())_crs_pk.dat"
                let vk_file_path = "\(doc_loc)/\(circuit_name.lowercased())_crs_vk.dat"
                let proof_file_path = "\(doc_loc)/\(circuit_name.lowercased())_proof.dat"
                var vk_json , proof_json : String
                let inputs_json = ViewController.loadPackageTextFile(forResource: "sample_input_" + circuit_name  , ofType: ".json")
                
                var context : CircuitContext
                
                
                //
                // create embedded circuit and write
                //  -   constraint system
                //  -   proving key
                //  -   verify key
                //  to file
                context = zkSnark.CircuitContext(circuit_name )
                try context.buildCircuit()
                try context.runSetup()
                try context.getConstraintSystem(path: cs_file_path, checksum_prefix: cs_file_checksum )
                try context.getProofKey(path: pk_file_path )
                try context.getVerifyKey(path: vk_file_path )
                vk_json = try context.getVerifyKey()
                
                //
                // create embedded circuit and run proof
                //
                context = zkSnark.CircuitContext(circuit_name )
                try context.buildCircuit()
                try context.setInput(inputs_json: inputs_json )
                try context.setProofKey(path: pk_file_path )
                try context.runProof()
                try context.getProof(path: proof_file_path )
                
                
                //
                // re-construct circuit from constraint system file and run proof
                //
                context = zkSnark.CircuitContext(circuit_name )
                try context.buildCircuit(cs_file_path: cs_file_path , checksum_prefix: cs_file_checksum)
                try context.setInput(inputs_json: inputs_json )
                try context.setProofKey(path: pk_file_path )
                try context.runProof()
                proof_json = try context.getProof()
                
                
                //
                // verify proofs
                //
                context = zkSnark.CircuitContext(circuit_name )
                try context.buildCircuit(cs_file_path: cs_file_path , checksum_prefix: cs_file_checksum)
                try context.setInput(inputs_json: inputs_json )
                try context.setVerifyKey(path: vk_file_path )
                try context.setProof(path: proof_file_path )
                try context.runVerify()
                print ( "\n <<< Verify 1 Success >>>\n")
                
                context = zkSnark.CircuitContext(circuit_name )
                try context.buildCircuit(cs_file_path: cs_file_path , checksum_prefix: cs_file_checksum)
                try context.setInput(inputs_json: inputs_json )
                try context.setVerifyKey(json: vk_json )
                try context.setProof(json: proof_json )
                try context.runVerify()
                print ( "\n <<< Verify 2 Success >>>\n")
                
                
            } catch {
                print ( "\n *** Error : \(error.localizedDescription) : \(error) ***\n")
            }
            
        }, completion:{
            sender.setTitle( " Test Complete " , for : UIControl.State.normal )  ;
            sender.isEnabled = true ;
        })
        
    }
    
    
    private static func loadPackageTextFile( forResource: String , ofType: String ) -> String {
        let path = Bundle.main.path (forResource: forResource , ofType: ofType) ?? ""
        let ret_val = try! String(contentsOfFile: path)
        return ret_val
    }
    
}


extension DispatchQueue {

    static func background(delay: Double = 0.0, background: (()->Void)? = nil, completion: (() -> Void)? = nil) {
        DispatchQueue.global(qos: .userInitiated).async {
            background?()
            if let completion = completion {
                DispatchQueue.main.asyncAfter(deadline: .now() + delay, execute: {
                    completion()
                })
            }
        }
    }

}
