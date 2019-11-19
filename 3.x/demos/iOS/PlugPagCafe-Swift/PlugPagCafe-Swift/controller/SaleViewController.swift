//
//  SaleViewController.swift
//  PlugPagCafe-Swift
//
//  Created by Hildequias Junior on 2/27/18.
//  Copyright © 2018 UOL Pagseguro. All rights reserved.
//

import UIKit

class SaleViewController: UIViewController, SaleViewModelDelegate {
    
    // MARK: - IBOutlet
    @IBOutlet weak var lblValue: UILabel!
    @IBOutlet weak var lblInstallment: UILabel!
    @IBOutlet weak var scTypePayment: UISegmentedControl!
    @IBOutlet weak var scInstallmentType: UISegmentedControl!
    @IBOutlet weak var stInstallment: UIStepper!
    @IBOutlet weak var txtResult: UITextView!
    @IBOutlet weak var btPayment: UIButton!
    
    // MARK: - Variables
    let viewModel = SaleViewModel()
    
    // MARK: - UIViewController Cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        viewModel.delegate = self
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - @IBActions
    @IBAction func peripheralsTouch(_ sender: UIBarButtonItem) {
        self.performSegue(withIdentifier: "goSell", sender: nil)
    }
    
    @IBAction func zeroValueAction(_ sender: UIButton) {
        viewModel.setAmountValue(value: 0.00)
    }
    
    @IBAction func setValue(_ sender: UIButton) {
        viewModel.incrementAmountValue(value: Double(sender.tag))
    }
    
    @IBAction func stInstallmentChanged(_ sender: UIStepper) {
        lblInstallment.text = String(format: "%.0f", sender.value)
    }
    
    @IBAction func paymentAction(_ sender: UIButton) {
        btPayment.isEnabled = false
        UIUtils.showProgress()
        txtResult?.text = ""
        viewModel.paymentTransaction(paymentData: createPaymentData())
    }

    @IBAction func abortAction(_ sender: UIButton) {
        viewModel.abortTransaction()
    }
    
    func createPaymentData() -> PlugPagPaymentData {
        let paymentData = PlugPagPaymentData()
        paymentData.mType = PaymentMethod(rawValue: PaymentMethodTypes.RawValue(scTypePayment.selectedSegmentIndex + 1))
        paymentData.mInstallmentType = InstallmentType(rawValue: InstallmentTypes.RawValue(scInstallmentType.selectedSegmentIndex + 1))
        paymentData.mInstallment = Int32(stInstallment.value)
        return paymentData
    }
    
    // MARK: - SaleViewModel delegates
    
    func showInsertCard(messenger: String) {
        print("PlugPag Event : \(messenger)")
        txtResult?.attributedText = UIUtils.formatTextResult(message: messenger)
    }
    
    func showRemoveCard(messenger: String) {
        print("PlugPag Event : \(messenger)")
        txtResult?.attributedText = UIUtils.formatTextResult(message: messenger)
    }
    
    func showProcessing(messenger: String) {
        print("PlugPag Event : \(messenger)")
        txtResult?.attributedText = UIUtils.formatTextResult(message: messenger)
    }
    
    func showInsertPassword(messenger: String) {
        print("PlugPag Event : \(messenger)")
        txtResult?.attributedText = UIUtils.formatTextResult(message: messenger)
    }
    
    func showAlert(messenger: String) {
        btPayment.isEnabled = true
        UIUtils.hideProgress()
        txtResult?.text = ""
        UIUtils.showAlert(view: self, message: messenger)
    }
    
    func showTransactionData(result: PlugPagTransactionResult) {
        UIUtils.hideProgress()
        btPayment.isEnabled = true
        txtResult?.attributedText = UIUtils.formatTextGetLastApprovedTransaction(transaction: result)
        UIUtils.showAlert(view: self, message: result.mMessage)
    }
    
    func updateAmount(amount: Float) {
        lblValue.attributedText = UIUtils.formatCurrency(value: amount)
    }
}

