//
//  SaleListViewModel.swift
//  PlugPagCafe-Swift
//
//  Created by Hildequias Junior on 18/11/19.
//  Copyright © 2019 UOL Pagseguro. All rights reserved.
//

import UIKit

// MARK: - SaleListViewModel protocol
protocol SaleListViewModelDelegate {
    
    func showAlert(messenger: String)
    func updateData()
}

class SaleListViewModel: NSObject, TransactionManagerDelegate {

    // MARK: - Variables
    let MSG_SUCESS              = "Transação Efetuada com sucesso!"
    let MSG_DEVICE              = "Informe um Peripheral!"
    let MSG_TRANSACTION         = "Selecione uma venda!"
    
    let manager = TransactionManager()
    var delegate: SaleListViewModelDelegate?
    var transaction: PlugPagTransactionResult?
    
    func doRefund() {
        
        guard let data = transaction else {
            delegate?.showAlert(messenger: MSG_TRANSACTION)
            return
        }
        
        guard let device = PlugPagCafe.shared().deviceSelected else {
            delegate?.showAlert(messenger: MSG_DEVICE)
            return
        }
        
        let voidData = PlugPagVoidData()
        voidData.mTransactionId = data.mTransactionId
        voidData.mTransactionCode = data.mTransactionCode
        
        manager.delegate = self
        manager.doReverse(refund: voidData, device: device)
    }
    
    func removeVoidData() {
        
        var index = -1
        
        for i in 0..<PlugPagCafe.shared().sales.count {
            
            let data = PlugPagCafe.shared().sales[i]
            
            if data.mTransactionId == transaction?.mTransactionId {
                index = i
            }
        }
        
        if index >= 0 {
            PlugPagCafe.shared().sales.remove(at: index)
            transaction = nil
            delegate?.updateData()
        }
    }
    
    // MARK: - TransactionManager delegates
    
    func transactionDidComplete(result: PlugPagTransactionResult) {
        manager.delegate = nil
        if result.mResult == RET_OK && (result.mErrorCode.elementsEqual("") || result.mErrorCode.elementsEqual("0000")) {
            removeVoidData()
            delegate?.showAlert(messenger: MSG_SUCESS)
        }else{
            delegate?.showAlert(messenger: String(describing: "\(result.mErrorCode ?? "") \(result.mMessage ?? "")"))
        }
    }
    
    func insertCard(messenger: String) {
        print("PlugPag Event : \(messenger)")
    }
    
    func removeCard(messenger: String) {
        print("PlugPag Event : \(messenger)")
    }
    
    func processing(messenger: String) {
        print("PlugPag Event : \(messenger)")
    }
    
    func insertPassword(messenger: String) {
        print("PlugPag Event : \(messenger)")
    }
}
