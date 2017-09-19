//
//  CancelTransactionController.m
//  Example-iOS-ObjC
//
//  Created by Hildequias Junior on 8/8/17.
//  Copyright © 2017 UOL Pagseguro. All rights reserved.
//

#import "CancelTransactionController.h"
#import "PlugPag.h"
#import "UIUtils.h"

#define MSG_CANCEL_SUCESS       @"Transação cancelada com sucesso!"
#define MSG_ERROR_DEVICE_NULL   @"Não foi possível efetuar uma conexão com o dispositivo pareado: %i"
#define MSG_ERROR_GENERIC       @"Não foi possível efetuar a operação"

@interface CancelTransactionController ()

@end

@implementation CancelTransactionController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    [[PlugPag sharedInstance] SetVersionName:@"UnitTest" withVersion:@"V001"];
}

- (void) viewDidAppear:(BOOL)animated {
    
    [super viewDidAppear:animated];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)cancelTransaction:(UIButton *)sender {
    
    [sender setEnabled:NO];
    [UIUtils showProgress];
    
    int ret = [[PlugPag sharedInstance] InitBTConnection];
    
    switch (ret) {
        case RET_OK:
            [self cancelTransaction];
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

- (void) cancelTransaction {
    
    int ret = [[PlugPag sharedInstance] CancelTransaction];
    
    if (ret == RET_OK) {
        _txtResult.text = MSG_CANCEL_SUCESS;
    }else{
        _txtResult.attributedText = [UIUtils formatTextError:ret withMessage: MSG_ERROR_GENERIC];
    }
}

@end
