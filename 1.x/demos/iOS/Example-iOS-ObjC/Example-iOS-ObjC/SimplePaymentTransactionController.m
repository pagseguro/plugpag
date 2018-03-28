//
//  SimplePaymentTransactionController.m
//  Example-iOS-ObjC
//
//  Created by Hildequias Junior on 8/8/17.
//  Copyright © 2017 UOL Pagseguro. All rights reserved.
//

#import "SimplePaymentTransactionController.h"
#import "PlugPag.h"
#import "UIUtils.h"

#define MSG_ERROR_DEVICE_NULL   @"Não foi possível efetuar uma conexão com o dispositivo pareado: %i"
#define MSG_ERROR_GENERIC       @"Não foi possível efetuar a operação"

@interface SimplePaymentTransactionController () {
    float amount;
    int cafe;
}

@end

@implementation SimplePaymentTransactionController

#pragma mark - UIViewController Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    amount = 0;
    cafe = 0;
    [self updateAmount];
    
    [[PlugPag sharedInstance] SetVersionName:@"UnitTest" withVersion:@"V001"];
}

- (void) viewDidAppear:(BOOL)animated {
    
    [super viewDidAppear:animated];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - IBAction Buttons
- (IBAction)pagar:(UIButton *)sender {
    
    [sender setEnabled:NO];
    [self updateAmount];
    [UIUtils showProgress];
    
    int ret = [[PlugPag sharedInstance] InitBTConnection];
    
    switch (ret) {
        case RET_OK:
            [self paymentTransaction];
            break;
            
        case ERROR_DEVICE_NULL:
            _txtResult.attributedText = [UIUtils formatTextError:ret withMessage: MSG_ERROR_DEVICE_NULL];
            break;
            
        default:
            _txtResult.attributedText = [UIUtils formatTextError:ret withMessage: MSG_ERROR_GENERIC];
            break;
    }
    
    [UIUtils hideProgress];
    [sender setEnabled:YES];
}

- (IBAction)insertCafe:(UIButton *)sender {
    
    amount = amount + 2.65;
    cafe = cafe + 1;
    [self updateAmount];
}

- (IBAction)deleteCafe:(UIButton *)sender {
    
    if (amount > 0) amount = amount - 2.65;
    if (cafe > 0) cafe = cafe - 1;
    [self updateAmount];
}

#pragma mark - Business Methods
- (void) paymentTransaction {
    
    NSString *value = [NSString stringWithFormat:@"%.02f", amount];
    value = [value stringByReplacingOccurrencesOfString:@"." withString:@""];
    
    int ret = [[PlugPag sharedInstance] SimplePaymentTransaction:CREDIT withInstallmentType:A_VISTA andInstallments:1 andAmount:value andUserReference:@"Pagcafe"];
    
    if (ret == RET_OK) {
        _txtResult.attributedText = [UIUtils formatTextGetLastApprovedTransaction];
    }else{
        _txtResult.attributedText = [UIUtils formatTextError:ret withMessage: MSG_ERROR_GENERIC];
    }
    
    amount = 0;
    cafe = 0;
}

-(void) updateAmount {
    
    if (amount < 1) amount = 2.65;
    if (cafe < 1) cafe = 1;
    
    NSString *msgCafe = cafe < 2 ? @"Café" : @"Cafés";
    
    _txtResult.attributedText = [UIUtils formatCurrency:amount];
    _lblCafe.text = [NSString stringWithFormat:@"%d %@", cafe, msgCafe];
}

@end
