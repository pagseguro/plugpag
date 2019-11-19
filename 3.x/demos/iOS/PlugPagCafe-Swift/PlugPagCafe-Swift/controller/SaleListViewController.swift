//
//  SaleListViewController.swift
//  PlugPagCafe-Swift
//
//  Created by Hildequias Junior on 2/28/18.
//  Copyright © 2018 UOL Pagseguro. All rights reserved.
//

import UIKit

class SaleListViewController: UIViewController, UITableViewDelegate, UITableViewDataSource, SaleListViewModelDelegate {
    
    // MARK: - IBOutlet
    @IBOutlet weak var tableView: UITableView!
    
    // MARK: - Variables
    let viewModel = SaleListViewModel()
    
    // MARK: - UIViewController Cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        viewModel.delegate = self
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
        
        if transaction.mTransactionId == viewModel.transaction?.mTransactionId  {
            cell.accessoryType = .checkmark
        }else{
            cell.accessoryType = .none
        }
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        viewModel.transaction = PlugPagCafe.shared().sales[indexPath.row]
        self.tableView.reloadData()
    }
    
    @IBAction func voidAction(_ sender: UIBarButtonItem) {
        UIUtils.showProgress()
        viewModel.doRefund()
    }
    
    // MARK: - SaleListViewModel delegates
    func showAlert(messenger: String) {
        UIUtils.hideProgress()
        UIUtils.showAlert(view: self, message: messenger)
    }
    
    func updateData() {
        self.tableView.reloadData()
    }
}
