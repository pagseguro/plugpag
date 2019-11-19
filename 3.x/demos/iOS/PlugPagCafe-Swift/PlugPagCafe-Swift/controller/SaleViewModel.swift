//
//  SaleViewModel.swift
//  PlugPagCafe-Swift
//
//  Created by Hildequias Junior on 18/11/19.
//  Copyright © 2019 UOL Pagseguro. All rights reserved.
//

import UIKit

// MARK: - SaleViewModel protocol
protocol SaleViewModelDelegate {
    func showInsertCard(messenger: String)
    func showRemoveCard(messenger: String)
    func showProcessing(messenger: String)
    func showInsertPassword(messenger: String)
    func showAlert(messenger: String)
    func showTransactionData(result: PlugPagTransactionResult)
    func updateAmount(amount: Float)
}

class SaleViewModel: NSObject, TransactionManagerDelegate {

    // MARK: - Variables
    let MSG_SUCESS              = "Transação Efetuada com sucesso!"
    let MSG_VALUE               = "Informe um valor válido!"
    let MSG_DEVICE              = "Informe um Peripheral!"
    
    let manager = TransactionManager()
    var delegate: SaleViewModelDelegate?
    var amount = 0.00
    
    // MARK: - Business Methods
    
    func setAmountValue(value: Double) {
        amount = value
        delegate?.updateAmount(amount: Float(amount))
    }
    
    func incrementAmountValue(value: Double) {
        setAmountValue(value: amount + value)
    }
    
    func paymentTransaction(paymentData: PlugPagPaymentData) {
        
        if amount < 1 {
            delegate?.showAlert(messenger: MSG_VALUE)
            return
        }
        
        guard let device = PlugPagCafe.shared().deviceSelected else {
            delegate?.showAlert(messenger: MSG_DEVICE)
            return
        }
        
        var value = String(format: "%.02f", amount)
        value = value.replacingOccurrences(of: ".", with: "")
        paymentData.mAmount = Int32(value) ?? 000
        paymentData.mUserReference = value
        
        manager.delegate = self
        manager.doPayment(sale: paymentData, device: device)
    }
    
    func abortTransaction() {
        manager.doAbort()
    }
    
    // MARK: - TransactionManager Delegates
    func transactionDidComplete(result: PlugPagTransactionResult) {
        manager.delegate = nil
        if result.mResult == RET_OK && (result.mErrorCode.elementsEqual("") || result.mErrorCode.elementsEqual("0000")) {
            setAmountValue(value: 0.00)
            PlugPagCafe.shared().sales.append(result)
            delegate?.showTransactionData(result: result)
        } else {
            delegate?.showAlert(messenger: String(describing: "\(result.mErrorCode ?? "") \(result.mMessage ?? "")"))
        }
    }
    
    func insertCard(messenger: String) {
        delegate?.showInsertCard(messenger: messenger)
    }
    
    func removeCard(messenger: String) {
        delegate?.showRemoveCard(messenger: messenger)
    }
    
    func processing(messenger: String) {
        delegate?.showProcessing(messenger: messenger)
    }
    
    func insertPassword(messenger: String) {
        delegate?.showInsertPassword(messenger: messenger)
    }
}
