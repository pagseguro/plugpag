//
//  PlugPagCafe.swift
//  PlugPagCafe-Swift
//
//  Created by Hildequias Junior on 2/28/18.
//  Copyright © 2018 UOL Pagseguro. All rights reserved.
//

import UIKit

class PlugPagCafe: NSObject {
    
    var deviceSelected: PlugPagDevice? = nil
    var sales: [PlugPagTransactionResult] = []
    
    private static var sharedCafe: PlugPagCafe = {
        let ppCafe = PlugPagCafe()
        return ppCafe
    }()
    
    class func shared() -> PlugPagCafe {
        return sharedCafe
    }
}
