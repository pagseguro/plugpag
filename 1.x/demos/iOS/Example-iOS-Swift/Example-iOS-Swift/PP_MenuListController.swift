//
//  ViewController.swift
//  Example-iOS-Swift
//
//  Created by Hildequias Junior on 8/22/17.
//  Copyright © 2017 UOL Pagseguro. All rights reserved.
//

import UIKit

class PP_MenuListController: UITableViewController {

    var menus: NSArray! = NSArray()
    
    // MARK: - UIViewController Cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        
        menus = ["Fazer uma venda",
                 "Consultar a última venda",
                 "Parear (conectar) um leitor",
                 "Fazer Estorno"];
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    // MARK: - Table view data source
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return menus.count
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let CellIdentifier = "CellID"
        
        // Configure the cell...
        let cell = tableView.dequeueReusableCell(withIdentifier: CellIdentifier, for: indexPath)
        cell.accessoryType = .disclosureIndicator
        cell.textLabel?.text = menus.object(at: indexPath.row) as? String
        
        return cell
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        switch indexPath.row {
        case 0:
            self.performSegue(withIdentifier: "goToSale", sender: nil)
            break
            
        case 1:
            self.performSegue(withIdentifier: "goToLastTransaction", sender: nil)
            break
            
        case 2:
            self.performSegue(withIdentifier: "goSegue", sender: nil)
            break
            
        case 3:
            self.performSegue(withIdentifier: "goToCancel", sender: nil)
            break
            
        default:
            break
        }
    }

}

