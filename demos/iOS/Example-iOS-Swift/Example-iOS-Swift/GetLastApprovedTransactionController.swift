//
//  GetLastApprovedTransactionController.swift
//  Example-iOS-Swift
//
//  Created by Hildequias Junior on 8/23/17.
//  Copyright © 2017 UOL Pagseguro. All rights reserved.
//

import UIKit

class GetLastApprovedTransactionController: UIViewController {

    @IBOutlet weak var txtResult: UITextView?
    @IBOutlet weak var btSearch: UIButton?
    
    let MSG_ERROR_DEVICE_NULL   = "Não foi possível efetuar uma conexão com o dispositivo pareado:"
    let MSG_ERROR_GENERIC       = "Não foi possível efetuar a operação"
    
    // MARK: - UIViewController Cycle
    override func viewDidLoad() {
        super.viewDidLoad()

        PlugPag.sharedInstance().setVersionName("UnitTest", withVersion: "V001")
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // MARK: - IBAction Buttons
    @IBAction func getLastApprovedTransaction(sender: UIButton) {
    
        sender.isEnabled = false
        UIUtils.showProgress()
        
        let ret = PlugPag.sharedInstance().initBTConnection()
        
        switch ret {
            case RET_OK:
                getTransaction()
                break
            
            case -2023:
                txtResult?.attributedText = UIUtils.formatTextError(error: Int(ret), msg: "\(MSG_ERROR_DEVICE_NULL)")
                break
        
            default:
                txtResult?.attributedText = UIUtils.formatTextError(error: Int(ret), msg: "\(MSG_ERROR_GENERIC)")
                break
        }
        
        UIUtils.hideProgress()
        sender.isEnabled = true
    }
    
    // MARK: - Business Methods
    func getTransaction() {
    
        let ret = PlugPag.sharedInstance().getLastApprovedTransactionStatus()
    
        if (ret == RET_OK) {
            txtResult?.attributedText = UIUtils.formatTextGetLastApprovedTransaction()
        }else{
            txtResult?.attributedText = UIUtils.formatTextError(error: Int(ret), msg: "\(MSG_ERROR_GENERIC)")
        }
    }

}
