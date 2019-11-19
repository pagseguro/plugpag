//
//  TransactionManager.swift
//  ReverseProj
//
//  Created by Hildequias Junior on 10/21/19.
//  Copyright © 2019 Hildequias Junior. All rights reserved.
//

import Foundation

// MARK: - TransactionManager protocol
protocol TransactionManagerDelegate {
   
    func transactionDidComplete(result: PlugPagTransactionResult)
    func insertCard(messenger: String)
    func removeCard(messenger: String)
    func processing(messenger: String)
    func insertPassword(messenger: String)
}

class TransactionManager: NSObject, PP_PlugPagDelegate {
    
    // MARK: - TransactionManager properties
    var delegate: TransactionManagerDelegate?
    var EVENT_PROCESSING = "Processando"
    var EVENT_INSERT_CARD = "Insira o cartão"
    var EVENT_REMOVE_CARD = "Retire o cartão"
    var EVENT_INSERT_PASSWORD = "Insira a senha"
    let ACTIVATION_CODE = ""
    
    // MARK: - TransactionManager functions
    func doPayment(sale: PlugPagPaymentData, device: PlugPagDevice) {
        
        DispatchQueue.global(qos: .utility).async {
        
            let data = self.initialization(device: device)
            
            if data.mResult == RET_OK && (data.mErrorCode == "" || data.mErrorCode == "0000") {
                guard let result = PlugPag.sharedInstance()?.doPayment(sale) else { return }
                self.dispachetedTransactionComplete(result:  result)
            }
        }
    }

    func doReverse(refund: PlugPagVoidData, device: PlugPagDevice) {
        
        DispatchQueue.global(qos: .utility).async {
            
            let data = self.initialization(device: device)
            
            if data.mResult == RET_OK && (data.mErrorCode == "" || data.mErrorCode == "0000") {
                guard let result = PlugPag.sharedInstance()?.voidPayment(refund) else { return }
                self.dispachetedTransactionComplete(result:  result)
            }
        }
    }

    func doAbort() {
        PlugPag.sharedInstance()?.abort()
    }

    func initialization(device: PlugPagDevice) -> PlugPagInitializationResult {
        
        PlugPag.sharedInstance()?.plugPagAppIdentification("PagCafé", withVersion: "1.0")
        
        PlugPag.sharedInstance()?.delegate = self
        PlugPag.sharedInstance()?.setInitBTConnection(device)
        var result = PlugPagInitializationResult()
        result.mResult = 0
        result.mErrorMessage = ""
        result.mErrorCode = ""
        
        if device.mType == TYPE_PINPAD {
            let activation = PlugPagActivationData()
            activation.mActivationCode = ACTIVATION_CODE
            result = PlugPag.sharedInstance()?.initializeAndActivate(activation) ?? result
        }
        return result
    }
    
    // MARK: - TransactionManager dispach
    
    func dispachetedInsertCard() {
        DispatchQueue.main.async {
            guard let eventAction = self.delegate?.insertCard else { return }
            eventAction(self.EVENT_INSERT_CARD)
        }
    }
    
    func dispachetedRemoveCard() {
        DispatchQueue.main.async {
            guard let eventAction = self.delegate?.removeCard else { return }
            eventAction(self.EVENT_REMOVE_CARD)
        }
    }
    
    func dispachetedProcessing() {
        DispatchQueue.main.async {
            guard let eventAction = self.delegate?.processing else { return }
            eventAction(self.EVENT_PROCESSING)
        }
    }
    
    func dispachetedInsertPassword() {
        DispatchQueue.main.async {
            guard let eventAction = self.delegate?.insertPassword else { return }
            eventAction(self.EVENT_INSERT_PASSWORD)
        }
    }
    
    func dispachetedTransactionComplete(result: PlugPagTransactionResult) {
        PlugPag.sharedInstance()?.delegate = nil
        DispatchQueue.main.async {
            guard let eventAction = self.delegate?.transactionDidComplete else { return }
            eventAction(result)
            print("PlugPag Transaction Completed")
        }
    }
    
    // MARK: - PlugPag delegates
    func userEventsInterface(_ event: Int32) {
        
        print("PlugPag Event: \(event)")
        
        switch event {
        case PP_STATUS_WAITING_CARD:
            dispachetedInsertCard()
            
        case PP_STATUS_WAITING_REMOVE_CARD:
            dispachetedRemoveCard()
            
        case PP_STATUS_PIN_REQUESTED:
            dispachetedInsertPassword()
            
        default:
            dispachetedProcessing()
        }
    }
}
