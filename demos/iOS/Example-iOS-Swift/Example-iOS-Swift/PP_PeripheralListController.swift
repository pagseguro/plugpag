//
//  PP_PeripheralListController.swift
//  Example-iOS-Swift
//
//  Created by Hildequias Junior on 8/23/17.
//  Copyright © 2017 UOL Pagseguro. All rights reserved.
//

import UIKit

class PP_PeripheralListController: UITableViewController {

    var devices: NSArray! = NSArray()
    
    // MARK: - UIViewController Cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        
        PlugPag.sharedInstance().setVersionName("UnitTest", withVersion: "V001")
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    // MARK: - Table view data source
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return devices.count
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let CellIdentifier = "CellID"
        
        // Configure the cell...
        let cell = tableView.dequeueReusableCell(withIdentifier: CellIdentifier, for: indexPath)
        cell.accessoryType = .disclosureIndicator
        cell.textLabel?.text = devices.object(at: indexPath.row) as? String
        
        return cell
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        let deviceChosen = devices.object(at: indexPath.row) as! String
        
        PlugPag.sharedInstance().setPeripheralName(deviceChosen)
        
        UIUtils.showAlert(view: self, message: "Selecionado: \(deviceChosen)")
        
        tableView.deselectRow(at: indexPath, animated: true)
    }
    
    // MARK: - IBAction Buttons
    @IBAction func scan(sender: UIBarButtonItem) {
        
        scanPeripherals()
    }
    
    // MARK: - Business Methods
    func scanPeripherals() {
        
        UIUtils.showProgress()
        
        devices = PlugPag.sharedInstance().getListPeripheral()! as NSArray
        
        self.tableView.reloadData()
        UIUtils.hideProgress()
    }
}
