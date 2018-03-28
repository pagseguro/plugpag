//
//  UIUtils.swift
//  Example-iOS-Swift
//
//  Created by Hildequias Junior on 8/22/17.
//  Copyright © 2017 UOL Pagseguro. All rights reserved.
//

import UIKit

extension String {
    func nsRange(from range: Range<String.Index>) -> NSRange {
        let from = range.lowerBound.samePosition(in: utf16)
        let to = range.upperBound.samePosition(in: utf16)
        return NSRange(location: utf16.distance(from: utf16.startIndex, to: from),
                       length: utf16.distance(from: from, to: to))
    }
}

extension String {
    func range(from nsRange: NSRange) -> Range<String.Index>? {
        guard
            let from16 = utf16.index(utf16.startIndex, offsetBy: nsRange.location, limitedBy: utf16.endIndex),
            let to16 = utf16.index(utf16.startIndex, offsetBy: nsRange.location + nsRange.length, limitedBy: utf16.endIndex),
            let from = from16.samePosition(in: self),
            let to = to16.samePosition(in: self)
            else { return nil }
        return from ..< to
    }
}

class UIUtils: NSObject {
    
    class func formatTextGetLastApprovedTransaction() -> NSMutableAttributedString {
        
        let txMensagem = "Mensagem:"
        let txTrCode = "Tr. Code:"
        let txDate = "Date:"
        let txTime = "Time:"
        let txHost = "Host NSU:"
        let txCardBrand = "Card Brand:"
        let txBin = "Bin:"
        let txHolder = "Holder:"
        let txUserReference = "User Reference:"
        let txTerSerial = "Terminal Serial:"
        
        let strTextView = String("\(txMensagem) \(PlugPag.sharedInstance().message!) \n \(txTrCode) \(PlugPag.sharedInstance().transactionCode!) \n \(txDate) \(PlugPag.sharedInstance().date!) \n \(txTime) \(PlugPag.sharedInstance().time!) \n \(txHost) \(PlugPag.sharedInstance().hostNsu!) \n \(txCardBrand) \(PlugPag.sharedInstance().cardBrand!) \n \(txBin) \(PlugPag.sharedInstance().bin!) \n \(txHolder) \(PlugPag.sharedInstance().holder!) \n \(txUserReference) \(PlugPag.sharedInstance().userReference!) \n \(txTerSerial) \(PlugPag.sharedInstance().terminalSerialNumber!)")
        
        let rangeMensagem           = strTextView?.range(of: txMensagem)
        let rangeTrCode             = strTextView?.range(of: txTrCode)
        let rangeDate               = strTextView?.range(of: txDate)
        let rangeTime               = strTextView?.range(of: txTime)
        let rangeHost               = strTextView?.range(of: txHost)
        let rangeCardBrand          = strTextView?.range(of: txCardBrand)
        let rangeBin                = strTextView?.range(of: txBin)
        let rangeHolder             = strTextView?.range(of: txHolder)
        let rangeUserReference      = strTextView?.range(of: txUserReference)
        let rangeTerSerial          = strTextView?.range(of: txTerSerial)
        
        let fontText = UIFont.boldSystemFont(ofSize: 16.0)
        
        let dictBoldText: [String : Any] = [
            NSForegroundColorAttributeName : UIColor.black,
            NSFontAttributeName : fontText
        ]
        
        let mutAttrTextViewString = NSMutableAttributedString(string: strTextView!)
        mutAttrTextViewString.setAttributes(dictBoldText, range: (strTextView?.nsRange(from: rangeMensagem!))!)
        mutAttrTextViewString.setAttributes(dictBoldText, range: (strTextView?.nsRange(from: rangeTrCode!))!)
        mutAttrTextViewString.setAttributes(dictBoldText, range: (strTextView?.nsRange(from: rangeDate!))!)
        mutAttrTextViewString.setAttributes(dictBoldText, range: (strTextView?.nsRange(from: rangeTime!))!)
        mutAttrTextViewString.setAttributes(dictBoldText, range: (strTextView?.nsRange(from: rangeHost!))!)
        mutAttrTextViewString.setAttributes(dictBoldText, range: (strTextView?.nsRange(from: rangeCardBrand!))!)
        mutAttrTextViewString.setAttributes(dictBoldText, range: (strTextView?.nsRange(from: rangeBin!))!)
        mutAttrTextViewString.setAttributes(dictBoldText, range: (strTextView?.nsRange(from: rangeHolder!))!)
        mutAttrTextViewString.setAttributes(dictBoldText, range: (strTextView?.nsRange(from: rangeUserReference!))!)
        mutAttrTextViewString.setAttributes(dictBoldText, range: (strTextView?.nsRange(from: rangeTerSerial!))!)
        
        return mutAttrTextViewString;
    }
    
    class func formatTextError(error: Int, msg: String) -> NSMutableAttributedString {
        
        let message = "Erro: \(error) - \(msg)"
        
        let range = message.range(of: message)
        let font = UIFont.systemFont(ofSize: 19.0)
        let styleMsg = NSMutableParagraphStyle()
        styleMsg.alignment = NSTextAlignment.center
        
        let dictText: [String : Any] = [
            NSForegroundColorAttributeName : UIColor.black,
            NSParagraphStyleAttributeName: styleMsg,
            NSFontAttributeName : font
        ]
        
        let txErro = "Erro:"
        let rangeErro = message.range(of: txErro)
        let fontErro = UIFont.boldSystemFont(ofSize: 20.0)
        let styleErro = NSMutableParagraphStyle()
        styleErro.alignment = NSTextAlignment.center
        
        let dictBoldText: [String : Any] = [
            NSForegroundColorAttributeName : UIColor.black,
            NSParagraphStyleAttributeName: styleErro,
            NSFontAttributeName : fontErro
        ]
        
        let mutAttrTextViewString = NSMutableAttributedString(string: message)
        mutAttrTextViewString.setAttributes(dictText, range: (message.nsRange(from: range!)))
        mutAttrTextViewString.setAttributes(dictBoldText, range: (message.nsRange(from: rangeErro!)))
        
        return mutAttrTextViewString;
    }
    
    class func formatTextResult(message: String) -> NSMutableAttributedString {
        
        let range = message.range(of: message)
        let font = UIFont.systemFont(ofSize: 19.0)
        let styleMsg = NSMutableParagraphStyle()
        styleMsg.alignment = NSTextAlignment.center
        
        let dictText: [String : Any] = [
            NSForegroundColorAttributeName : UIColor.black,
            NSParagraphStyleAttributeName: styleMsg,
            NSFontAttributeName : font
        ]
        
        let mutAttrTextViewString = NSMutableAttributedString(string: message)
        mutAttrTextViewString.setAttributes(dictText, range: (message.nsRange(from: range!)))
        
        return mutAttrTextViewString;
    }
    
    class func formatCurrency(value: Float) -> NSMutableAttributedString {
        
        let formatter = NumberFormatter()
        formatter.locale = NSLocale(localeIdentifier: "pt_BR") as Locale!
        formatter.numberStyle = .currency
        
        let msg = formatter.string(from: value as NSNumber)!
        
        let range = msg.range(of: msg)
        let fontText = UIFont.boldSystemFont(ofSize: 20.0)
        let style = NSMutableParagraphStyle()
        style.alignment = NSTextAlignment.center
        
        let dictBoldText: [String : Any] = [
            NSForegroundColorAttributeName : UIColor.black,
            NSParagraphStyleAttributeName: style,
            NSFontAttributeName : fontText
        ]
        
        let mutAttrTextViewString = NSMutableAttributedString(string: msg)
        mutAttrTextViewString.setAttributes(dictBoldText, range: (msg.nsRange(from: range!)))
        
        return mutAttrTextViewString;
    }
    
    class func showProgress () {
        
        SVProgressHUD.show()
    }
    
    class func hideProgress () {
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
            SVProgressHUD.dismiss()
        }
    }
    
    class func showAlert (view: UIViewController, message: String) {
        
        let alert=UIAlertController(title: "Aviso", message: message, preferredStyle: UIAlertControllerStyle.alert)
        
        alert.addAction(UIAlertAction(title: "Ok", style: UIAlertActionStyle.default, handler: {(action:UIAlertAction) in }))
        view.present(alert, animated: true, completion: nil)
    }
    
}
