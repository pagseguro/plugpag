//
//  MainViewController.swift
//  PlugPagCafe-Swift
//
//  Created by Hildequias Junior on 2/27/18.
//  Copyright © 2018 UOL Pagseguro. All rights reserved.
//

import UIKit

class MainViewController: UIViewController {

    // MARK: - IBOutlet
    @IBOutlet weak var lblValue: UILabel!
    @IBOutlet weak var lblInstallment: UILabel!
    @IBOutlet weak var scTypePayment: UISegmentedControl!
    @IBOutlet weak var scInstallmentType: UISegmentedControl!
    @IBOutlet weak var stInstallment: UIStepper!
    @IBOutlet weak var txtResult: UITextView!
    
    // MARK: - Variables
    let MSG_ERROR_DEVICE_NULL   = "Não foi possível efetuar uma conexão com o dispositivo pareado"
    let MSG_ERROR_GENERIC       = "Não foi possível efetuar a operação"
    let MSG_SUCESS              = "Transação Efetuada com sucesso!"
    var amount = 0.0
    var installments: [NSString] = []
    
    // MARK: - UIViewController Cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        
        if segue.identifier == "goInstallments" {
            let controller: InstallmentsTableViewController = segue.destination as! InstallmentsTableViewController
            controller.installments = installments
        }
    }

    // MARK: - @IBActions
    @IBAction func peripheralsTouch(_ sender: UIBarButtonItem) {
        
        self.performSegue(withIdentifier: "goSell", sender: nil)
        
    }
    
    @IBAction func zeroValueAction(_ sender: UIButton) {
        
        amount = 0.0
        updateAmount()
    }
    
    @IBAction func setValue(_ sender: UIButton) {
        
        amount = amount + Double(sender.tag)
        updateAmount()
    }
    
    @IBAction func stInstallmentChanged(_ sender: UIStepper) {
    
        lblInstallment.text = String(format: "%.0f", sender.value)
    }
    
    @IBAction func paymentAction(_ sender: UIButton) {
    
        if !checkFields() {
            return
        }
        
        sender.isEnabled = false
        UIUtils.showProgress()
        
        let deviceSelected = PlugPagCafe.shared().deviceSelected
        let ret = PlugPag.sharedInstance().setInitBTConnection(deviceSelected)
        
        switch ret?.mResult {
        case RET_OK:
            paymentTransaction()
            break
            
        default:
            UIUtils.showAlert(view: self, message: "Erro: \(ret?.mResult) - \(ret?.mMessage)")
            break
        }
        
        UIUtils.hideProgress()
        sender.isEnabled = true
    }
    
    @IBAction func calculateInstallmentsView(_ sender: UIButton) {
    
        var value = String(format: "%.02f", amount)
        value = value.replacingOccurrences(of: ".", with: "")
        
        installments = PlugPag.sharedInstance().calculateInstallments(value) as! [NSString]
        self.performSegue(withIdentifier: "goInstallments", sender: nil)
    }
    
    // MARK: - Business Methods
    func updateAmount() {
        
        if(amount < 1) { amount = 0.00 }
        
        lblValue.attributedText = UIUtils.formatCurrency(value: Float(amount))
    }
    
    func paymentTransaction() {
        
        let paymentData = createPaymentData()
        
        let result = PlugPag.sharedInstance().doPayment(paymentData)
        let ret = result?.mResult
        
        switch ret {
            
        case RET_OK?:
            amount = 0;
            updateAmount()
            PlugPagCafe.shared().sales.append(result!)
            txtResult?.attributedText = UIUtils.formatTextGetLastApprovedTransaction(transaction: result!)
            UIUtils.showAlert(view: self, message: (result?.mMessage)!)
            break
            
        default:
            UIUtils.showAlert(view: self, message: (result?.mMessage)!)
        }
    }
    
    func checkFields() -> Bool {
        
        if amount == 0 {
            UIUtils.showAlert(view: self, message: "Informe o valor!")
            return false
        }
        
        if PlugPagCafe.shared().deviceSelected == nil {
            UIUtils.showAlert(view: self, message: "Informe um Peripheral!")
            return false
        }
        
        return true
    }
    
    func createPaymentData() -> PlugPagPaymentData {
        
        var value = String(format: "%.02f", amount)
        value = value.replacingOccurrences(of: ".", with: "")
        
        let paymentData = PlugPagPaymentData()
        paymentData.mType = PaymentMethod(rawValue: PaymentMethodTypes.RawValue(scTypePayment.selectedSegmentIndex + 1))
        paymentData.mInstallmentType = InstallmentType(rawValue: InstallmentTypes.RawValue(scInstallmentType.selectedSegmentIndex + 1))
        paymentData.mAmount = Int32(value)!
        paymentData.mInstallment = Int32(stInstallment.value)
        paymentData.mUserReference = value
        return paymentData
    }
}

