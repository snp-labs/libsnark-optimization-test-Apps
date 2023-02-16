//
//  ViewController.swift
//  test-app
//
//  Created by Zkrypto on 2023/02/16.
//

import UIKit

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        hello_devworld()
        let result = do_encryption()
//             print("5 of letter A: \(five_a)")
        if let five_a_cstr = result {
            let five_a = String.init(cString: five_a_cstr)
            print("result is: \(five_a)ms")
        } else {
            print("Returned string was null!")
        }
        
//        let answer = add_numbers(15, 25)
//        print("The result is: \(result)")
//        let string = "for an anvil, this library sure is lightweight"
//        print("The length is: \(string_length(string))")
        
    }


}

