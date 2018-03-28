//
//  PP_ChooseViewController.swift
//  Example-iOS-Swift
//
//  Created by Hildequias Junior on 8/23/17.
//  Copyright © 2017 UOL Pagseguro. All rights reserved.
//

import UIKit

class PP_ChooseViewController: UITableViewController {

    var models: NSArray! = NSArray()
    
    // MARK: - UIViewController Cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        
        PlugPag.sharedInstance().setVersionName("UnitTest", withVersion: "V001")
        models = PlugPag.sharedInstance().getListModels()! as NSArray
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    // MARK: - Table view data source
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return models.count
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let CellIdentifier = "CellID"
        
        // Configure the cell...
        let cell = tableView.dequeueReusableCell(withIdentifier: CellIdentifier, for: indexPath)
        cell.accessoryType = .disclosureIndicator
        cell.textLabel?.text = models.object(at: indexPath.row) as? String
        
        return cell
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        PlugPag.sharedInstance().setModel(models.object(at: indexPath.row) as! String)
        
        tableView.deselectRow(at: indexPath, animated: true)
        self.performSegue(withIdentifier: "goSegue", sender: nil)
    }

}
