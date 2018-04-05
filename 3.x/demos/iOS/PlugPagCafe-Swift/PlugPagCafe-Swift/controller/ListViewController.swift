//
//  ListViewController.swift
//  PlugPagCafe-Swift
//
//  Created by Hildequias Junior on 2/28/18.
//  Copyright © 2018 UOL Pagseguro. All rights reserved.
//

import UIKit

class ListViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    // MARK: - IBOutlet
    @IBOutlet weak var tableView: UITableView!
    
    // MARK: - Variables
    var transactionSelected: PlugPagTransactionResult? = nil
    let MSG_SUCESS              = "Transação Efetuada com sucesso!"
    
    // MARK: - UIViewController Cycle
    override func viewDidLoad() {
        super.viewDidLoad()

        PlugPag.sharedInstance().plugPagAppIdentification("PlugPagCafe", withVersion: "1.0")
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        self.tableView.reloadData()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // MARK: - Table view data source
    
    func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return PlugPagCafe.shared().sales.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "CellID", for: indexPath)
        
        let transaction = PlugPagCafe.shared().sales[indexPath.row]
        
        // Configure the cell...
        cell.backgroundColor = UIColor.clear
        cell.textLabel?.text = transaction.mUserReference
        cell.detailTextLabel?.text = String(transaction.mTransactionId)
        
        if transaction.mTransactionId == transactionSelected?.mTransactionId  {
            cell.accessoryType = .checkmark
        }else{
            cell.accessoryType = .none
        }
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        transactionSelected = PlugPagCafe.shared().sales[indexPath.row]
        self.tableView.reloadData()
    }
    
    // MARK: - @IBActions
    
    @IBAction func voidAction(_ sender: UIBarButtonItem) {
        
        if transactionSelected != nil {
            
            sender.isEnabled = false
            UIUtils.showProgress()
            
            let deviceSelected = PlugPagCafe.shared().deviceSelected
            let ret = PlugPag.sharedInstance().setInitBTConnection(deviceSelected)
            
            switch ret?.mResult {
            case RET_OK?:
                voidTransaction()
                break
                
            default:
                UIUtils.showAlert(view: self, message: "Erro: \(ret!.mResult)")
                break
            }
            
            UIUtils.hideProgress()
            sender.isEnabled = true
            
        }else{
            UIUtils.showAlert(view: self, message: "Informe uma transação")
        }
    }
    
    // MARK: - Business Methods
    func voidTransaction() {
        
        let voidData = createVoidData()
        
        let result = PlugPag.sharedInstance().voidPayment(voidData)
        let ret = result?.mResult
        
        switch ret {
            
        case RET_OK?:
            removeVoidData()
            UIUtils.showAlert(view: self, message: MSG_SUCESS)
            break
            
        default:
            UIUtils.showAlert(view: self, message: "Erro: \(result!.mResult)")
        }
    }
    
    func createVoidData() -> PlugPagVoidData {
        
        let voidData = PlugPagVoidData()
        voidData.mTransactionId = transactionSelected?.mTransactionId
        voidData.mTransactionCode = transactionSelected?.mTransactionCode
        
        return voidData
    }
    
    func removeVoidData() {
        
        var index = -1
        
        for i in 0..<PlugPagCafe.shared().sales.count {
            
            let transaction = PlugPagCafe.shared().sales[i]
            
            if transaction.mTransactionId == transactionSelected?.mTransactionId {
                
                index = i
            }
        }
        
        if index >= 0 {
            PlugPagCafe.shared().sales.remove(at: index)
            self.tableView.reloadData()
        }
    }

}
