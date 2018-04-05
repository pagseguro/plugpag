//
//  DeviceViewController.swift
//  PlugPagCafe-Swift
//
//  Created by Hildequias Junior on 2/27/18.
//  Copyright © 2018 UOL Pagseguro. All rights reserved.
//

import UIKit

class DeviceViewController: UIViewController, UITableViewDelegate, UITableViewDataSource, PP_PlugPagDelegate {

    // MARK: - Variables
    var peripherals: [PlugPagDevice] = []
    
    // MARK: - IBOutlet
    @IBOutlet weak var tableView: UITableView!
    var refreshControl: UIRefreshControl!
    
    // MARK: - UIViewController Cycle
    override func viewDidLoad() {
        super.viewDidLoad()

        PlugPag.sharedInstance().plugPagAppIdentification("PlugPagCafe", withVersion: "1.0")
        PlugPag.sharedInstance().delegate = self
        
        refreshControl = UIRefreshControl()
        refreshControl?.backgroundColor = UIColor.yellow
        refreshControl?.tintColor = UIColor.black
        refreshControl?.addTarget(self, action: #selector(startScan), for: .valueChanged)
        tableView.addSubview(refreshControl)
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)

    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - @IBActions
    @objc func startScan(sender:AnyObject) {
        peripherals.removeAll()
        tableView.reloadData()
        PlugPag.sharedInstance().startScanForPeripherals()
    }
    
    @IBAction func pairPeripheral(_ sender: UIButton) {
    
        if PlugPagCafe.shared().deviceSelected != nil {
            PlugPag.sharedInstance().pairPeripheral(PlugPagCafe.shared().deviceSelected)
        }else{
            UIUtils.showAlert(view: self, message: "Selecione um Peripheral!")
        }
    }
    
    @IBAction func loginAction(_ sender: UIBarButtonItem) {
        
        PlugPag.sharedInstance().requestAuthentication(self)
    }
    
    // MARK: - PlugPag Delegate
    func peripheralDiscover(_ plugPagDevice: PlugPagDevice!) {
        
        peripherals.append(plugPagDevice)
        refreshControl.endRefreshing()
        tableView.reloadData()
    }
    
    func pairPeripheralStatus(_ status: Int32) {
        
        switch status {
        
        case BT_PAIR_STATE_OK:
            UIUtils.showAlert(view: self, message: "Pareamento efetuado com sucesso!")
            break
            
        case BT_PAIR_STATE_FAIL:
            UIUtils.showAlert(view: self, message: "Pareamento Falhou!")
            break
            
        default:
            break
        }
    }
    
    // MARK: - Table view data source

    func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return peripherals.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "CellID", for: indexPath)

        let peripheral = peripherals[indexPath.row]
        
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
        
        PlugPagCafe.shared().deviceSelected = peripherals[indexPath.row]
        self.tableView.reloadData()
    }

}
