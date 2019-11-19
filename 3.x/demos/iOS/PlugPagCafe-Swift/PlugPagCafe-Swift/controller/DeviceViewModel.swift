//
//  DeviceViewModel.swift
//  PlugPagCafe-Swift
//
//  Created by Hildequias Junior on 18/11/19.
//  Copyright © 2019 UOL Pagseguro. All rights reserved.
//

import UIKit

// MARK: - DeviceViewModel protocol
protocol DeviceViewModelDelegate {
    func showAlert(messenger: String)
    func updateData()
}

class DeviceViewModel: NSObject, PP_PlugPagDelegate {

    // MARK: - Variables
    let MSG_PAIR_SUCESS = "Pareamento efetuado com sucesso!"
    let MSG_PAIR_FAIL   = "Pareamento Falhou!"
    let MSG_DEVICE      = "Selecione um Peripheral!"
    
    var delegate: DeviceViewModelDelegate?
    var peripherals: [PlugPagDevice] = []
    var peripheralSelected: PlugPagDevice?
    
    override init() {
        super.init()
        PlugPag.sharedInstance().plugPagAppIdentification("PlugPagCafe", withVersion: "1.0")
    }
    
    func login(view: UIViewController) {
        PlugPag.sharedInstance().requestAuthentication(view)
    }
    
    func startScan() {
        peripherals.removeAll()
        delegate?.updateData()
        PlugPag.sharedInstance().delegate = self
        PlugPag.sharedInstance().startScanForPeripherals()
    }
    
    func pair() {
        guard let device = peripheralSelected else {
            delegate?.showAlert(messenger: MSG_DEVICE)
            return
        }
        PlugPag.sharedInstance().pairPeripheral(device)
    }
    
    func setPeripheralSelected(index: Int) {
        peripheralSelected = peripherals[index]
        PlugPagCafe.shared().deviceSelected = peripheralSelected
    }
    
    // MARK: - PlugPag Delegate
    func peripheralDiscover(_ plugPagDevice: PlugPagDevice!) {
        peripherals.append(plugPagDevice)
        delegate?.updateData()
    }
    
    func pairPeripheralStatus(_ status: Int32) {
        
        switch status {
            
        case BT_PAIR_STATE_OK:
            delegate?.showAlert(messenger: MSG_PAIR_SUCESS)
            break
            
        case BT_PAIR_STATE_FAIL:
            delegate?.showAlert(messenger: MSG_PAIR_FAIL)
            break
            
        default:
            break
        }
    }
}
