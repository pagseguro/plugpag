//
//  SimplePaymentTransactionController.swift
//  Example-iOS-Swift
//
//  Created by Hildequias Junior on 8/23/17.
//  Copyright © 2017 UOL Pagseguro. All rights reserved.
//

import UIKit

class SimplePaymentTransactionController: UIViewController {

    @IBOutlet weak var lblCafe: UILabel!
    @IBOutlet weak var txtResult: UITextView!
    @IBOutlet weak var btPayment: UIButton?
    @IBOutlet weak var scInstallmentType: UISegmentedControl!
    
    let MSG_ERROR_DEVICE_NULL   = "Não foi possível efetuar uma conexão com o dispositivo pareado"
    let MSG_ERROR_GENERIC       = "Não foi possível efetuar a operação"
    
    var amount = 0.0
    var cafe = 0
    
    // MARK: - UIViewController Cycle
    override func viewDidLoad() {
        super.viewDidLoad()

        PlugPag.sharedInstance().setVersionName("UnitTest", withVersion: "V001")
        updateAmount()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // MARK: - IBAction Buttons
    @IBAction func pagar(sender: UIButton) {
        
        sender.isEnabled = false
        updateAmount()
        UIUtils.showProgress()
        
        let ret = PlugPag.sharedInstance().initBTConnection()
        
        switch ret {
        case RET_OK:
            paymentTransaction()
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
    
    @IBAction func insertCafe(sender: UIButton) {
    
        amount = amount + 2.65
        cafe = cafe + 1
        updateAmount()
    }
    
    @IBAction func deleteCafe(sender: UIButton) {
    
        if (amount > 0) { amount = amount - 2.65 }
        if (cafe > 0) { cafe = cafe - 1 }
        updateAmount()
    }
    
    // MARK: - Business Methods
    func paymentTransaction() {
        
        var value = String(format: "%.02f", amount)
        value = value.replacingOccurrences(of: ".", with: "")
        
        let paymentMethod = scInstallmentType.selectedSegmentIndex == 0 ? CREDIT : DEBIT
        
        let ret = PlugPag.sharedInstance().simplePaymentTransaction(paymentMethod, withInstallmentType: A_VISTA, andInstallments: 1, andAmount: value, andUserReference: "Pagcafe")
        
        switch ret {
        
            case RET_OK:
                txtResult?.attributedText = UIUtils.formatTextGetLastApprovedTransaction()
                break
        
            case -1019:
                searchResult()
                break
            
            default:
                txtResult?.attributedText = UIUtils.formatTextError(error: Int(ret), msg: "\(MSG_ERROR_GENERIC)")
        }
        
        amount = 0;
        cafe = 0;
    }
    
    func searchResult() {
        
        let ret = PlugPag.sharedInstance().getLastApprovedTransactionStatus()
        
        if ret == RET_OK {
            txtResult?.attributedText = UIUtils.formatTextGetLastApprovedTransaction()
        }else{
            txtResult?.attributedText = UIUtils.formatTextError(error: Int(ret), msg: "\(MSG_ERROR_GENERIC)")
        }
        
    }
    
    func updateAmount() {
        
        if(amount < 1) { amount = 2.65 }
        if(cafe < 1) { cafe = 1 }
        
        let msgCafe = cafe < 2 ? "Café" : "Cafés"
        
        txtResult?.attributedText = UIUtils.formatCurrency(value: Float(amount))
        lblCafe?.text = "\(cafe) \(msgCafe)"
    }
}
