//
//  DeviceViewController.swift
//  PlugPagCafe-Swift
//
//  Created by Hildequias Junior on 2/27/18.
//  Copyright © 2018 UOL Pagseguro. All rights reserved.
//

import UIKit

class DeviceViewController: UIViewController, UITableViewDelegate, UITableViewDataSource, DeviceViewModelDelegate {

    // MARK: - Variables
    let viewModel = DeviceViewModel()
    
    // MARK: - IBOutlet
    @IBOutlet weak var tableView: UITableView!
    var refreshControl: UIRefreshControl!
    
    // MARK: - UIViewController Cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        
        viewModel.delegate = self
        
        refreshControl = UIRefreshControl()
        refreshControl?.backgroundColor = UIColor.yellow
        refreshControl?.tintColor = UIColor.black
        refreshControl?.addTarget(self, action: #selector(startScan), for: .valueChanged)
        tableView.addSubview(refreshControl)
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        refreshControl.endRefreshing()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - @IBActions
    @objc func startScan(sender:AnyObject) {
        viewModel.startScan()
    }
    
    @IBAction func pairPeripheral(_ sender: UIButton) {
        viewModel.pair()
    }
    
    @IBAction func loginAction(_ sender: UIBarButtonItem) {
        viewModel.login(view: self)
    }
    
    // MARK: - Table view data source

    func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return viewModel.peripherals.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "CellID", for: indexPath)

        let peripheral = viewModel.peripherals[indexPath.row]
        
        // Configure the cell...
        cell.textLabel?.text = peripheral.mPeripheralName
        
        let peripheralName = PlugPagCafe.shared().deviceSelected?.mPeripheralName
        
        if peripheralName == peripheral.mPeripheralName  {
            cell.accessoryType = .checkmark
        }else{
            cell.accessoryType = .none
        }
        
        return cell
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        viewModel.setPeripheralSelected(index: indexPath.row)
        self.tableView.reloadData()
    }
    
    // MARK: - DeviceViewModel Delegate
    func updateData() {
        refreshControl.endRefreshing()
        tableView.reloadData()
    }
    
    func showAlert(messenger: String) {
        refreshControl.endRefreshing()
        UIUtils.showAlert(view: self, message: messenger)
    }
}
