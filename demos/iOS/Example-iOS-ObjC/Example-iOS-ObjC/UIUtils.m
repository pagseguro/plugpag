//
//  UIUtils.m
//  Example-iOS-ObjC
//
//  Created by Hildequias Junior on 8/8/17.
//  Copyright © 2017 UOL Pagseguro. All rights reserved.
//

#import "UIUtils.h"
#import "PlugPag.h"
#import "SVProgressHUD.h"

@implementation UIUtils

+ (NSMutableAttributedString *) formatTextGetLastApprovedTransaction {
    
    NSString *txMensagem = @"Mensagem:";
    NSString *txTrCode = @"Tr. Code:";
    NSString *txDate = @"Date:";
    NSString *txTime = @"Time:";
    NSString *txHost = @"Host NSU:";
    NSString *txCardBrand = @"Card Brand:";
    NSString *txBin = @"Bin:";
    NSString *txHolder = @"Holder:";
    NSString *txUserReference = @"User Reference:";
    NSString *txTerSerial = @"Terminal Serial:";
    
    NSString *strTextView = [NSString stringWithFormat:@"%@ %@ \n%@ %@ \n%@ %@ \n%@ %@ \n%@ %@ \n%@ %@ \n%@ %@ \n%@ %@ \n%@ %@ \n%@ %@ ",        txMensagem, [PlugPag sharedInstance].message,
                             txTrCode, [PlugPag sharedInstance].transactionCode,
                             txDate, [PlugPag sharedInstance].date,
                             txTime, [PlugPag sharedInstance].time,
                             txHost, [PlugPag sharedInstance].hostNsu,
                             txCardBrand, [PlugPag sharedInstance].cardBrand,
                             txBin, [PlugPag sharedInstance].bin,
                             txHolder, [PlugPag sharedInstance].holder,
                             txUserReference, [PlugPag sharedInstance].userReference,
                             txTerSerial, [PlugPag sharedInstance].terminalSerialNumber];
    
    NSRange rangeMensagem       = [strTextView rangeOfString:txMensagem];
    NSRange rangeTrCode         = [strTextView rangeOfString:txTrCode];
    NSRange rangeDate           = [strTextView rangeOfString:txDate];
    NSRange rangeTime           = [strTextView rangeOfString:txTime];
    NSRange rangeHost           = [strTextView rangeOfString:txHost];
    NSRange rangeCardBrand      = [strTextView rangeOfString:txCardBrand];
    NSRange rangeBin            = [strTextView rangeOfString:txBin];
    NSRange rangeHolder         = [strTextView rangeOfString:txHolder];
    NSRange rangeUserReference  = [strTextView rangeOfString:txUserReference];
    NSRange rangeTerSerial      = [strTextView rangeOfString:txTerSerial];
    
    UIFont *fontText = [UIFont boldSystemFontOfSize:16];
    NSDictionary *dictBoldText = [NSDictionary dictionaryWithObjectsAndKeys:fontText, NSFontAttributeName, nil];
    
    NSMutableAttributedString *mutAttrTextViewString = [[NSMutableAttributedString alloc] initWithString:strTextView];
    [mutAttrTextViewString setAttributes:dictBoldText range:rangeMensagem];
    [mutAttrTextViewString setAttributes:dictBoldText range:rangeTrCode];
    [mutAttrTextViewString setAttributes:dictBoldText range:rangeDate];
    [mutAttrTextViewString setAttributes:dictBoldText range:rangeTime];
    [mutAttrTextViewString setAttributes:dictBoldText range:rangeHost];
    [mutAttrTextViewString setAttributes:dictBoldText range:rangeCardBrand];
    [mutAttrTextViewString setAttributes:dictBoldText range:rangeBin];
    [mutAttrTextViewString setAttributes:dictBoldText range:rangeHolder];
    [mutAttrTextViewString setAttributes:dictBoldText range:rangeUserReference];
    [mutAttrTextViewString setAttributes:dictBoldText range:rangeTerSerial];
    
    return mutAttrTextViewString;
}

+ (NSMutableAttributedString *) formatTextError:(int) error withMessage:(NSString *) msg {
    
    NSString *message = [NSString stringWithFormat:@"Erro: %d - %@", error, msg];
    
    NSRange range = [message rangeOfString:message];
    UIFont *fontText = [UIFont systemFontOfSize:19];
    NSMutableParagraphStyle *style = [NSMutableParagraphStyle new];
    style.alignment = NSTextAlignmentCenter;
    
    NSDictionary *dictText = [NSDictionary dictionaryWithObjectsAndKeys:fontText, NSFontAttributeName,
                              [UIColor blackColor], NSForegroundColorAttributeName,
                              style, NSParagraphStyleAttributeName,
                              nil];
    
    NSMutableAttributedString *mutAttrTextViewString = [[NSMutableAttributedString alloc] initWithString:message];
    [mutAttrTextViewString setAttributes:dictText range:range];
    
    return mutAttrTextViewString;
}

+ (NSMutableAttributedString *) formatTextResult:(NSString *) message {
    
    NSRange range = [message rangeOfString:message];
    UIFont *fontText = [UIFont systemFontOfSize:19];
    NSMutableParagraphStyle *style = [NSMutableParagraphStyle new];
    style.alignment = NSTextAlignmentCenter;
    
    NSDictionary *dictText = [NSDictionary dictionaryWithObjectsAndKeys:fontText, NSFontAttributeName,
                              [UIColor blackColor], NSForegroundColorAttributeName,
                              style, NSParagraphStyleAttributeName,
                              nil];
    
    NSMutableAttributedString *mutAttrTextViewString = [[NSMutableAttributedString alloc] initWithString:message];
    [mutAttrTextViewString setAttributes:dictText range:range];
    
    return mutAttrTextViewString;
}

+ (NSMutableAttributedString *)formatCurrency:(float) value {
    
    NSNumberFormatter *n = [[NSNumberFormatter alloc] init];
    [n setNumberStyle:NSNumberFormatterCurrencyStyle];
    NSLocale *locale = [[NSLocale alloc] initWithLocaleIdentifier:@"pt_BR"];
    [n setLocale:locale];
    NSString *message = [n stringFromNumber:[NSNumber numberWithFloat:value]];
    
    NSRange range    = [message rangeOfString:message];
    UIFont *fontText = [UIFont boldSystemFontOfSize:20.0];
    NSMutableParagraphStyle *style = [NSMutableParagraphStyle new];
    style.alignment  = NSTextAlignmentCenter;
    
    NSDictionary *dictText = [NSDictionary dictionaryWithObjectsAndKeys:fontText, NSFontAttributeName,
                              [UIColor blackColor], NSForegroundColorAttributeName,
                              style, NSParagraphStyleAttributeName,
                              nil];
    
    NSMutableAttributedString *mutAttrTextViewString = [[NSMutableAttributedString alloc] initWithString:message];
    [mutAttrTextViewString setAttributes:dictText range:range];
    
    return mutAttrTextViewString;
}

+ (void) showProgress {
    
    [SVProgressHUD show];
}

+ (void) hideProgress {
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        // time-consuming task
        dispatch_async(dispatch_get_main_queue(), ^{
            [SVProgressHUD dismiss];
        });
    });
}

+ (void) showAlert:(UIViewController *) view withMessage:(NSString *) message {
    
    UIAlertController * alert=[UIAlertController alertControllerWithTitle:@"Aviso" message:message preferredStyle:UIAlertControllerStyleAlert];
    
    UIAlertAction* yesButton = [UIAlertAction
                                actionWithTitle:@"Ok"
                                style:UIAlertActionStyleDefault
                                handler:^(UIAlertAction * action)
                                {}];
    
    [alert addAction:yesButton];
    
    [view presentViewController:alert animated:YES completion:nil];
}



@end
