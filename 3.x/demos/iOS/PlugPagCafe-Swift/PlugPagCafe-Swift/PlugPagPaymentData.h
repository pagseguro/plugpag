//
//  PlugPagPaymentData.h
//  PlugPag
//
//  Created by Hildequias Junior on 1/31/18.
//  Copyright © 2018 UOL Pagseguro. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface PlugPagPaymentData : NSObject

typedef enum PaymentMethodTypes
{
    CREDIT  = 1,
    DEBIT   = 2,
    VOUCHER = 3
} PaymentMethod;

typedef enum InstallmentTypes
{
    A_VISTA         = 1,
    PARC_VENDEDOR   = 2
} InstallmentType;

@property (nonatomic, assign) PaymentMethod mType;
@property (nonatomic, assign) int mAmount;
@property (nonatomic, assign) InstallmentType mInstallmentType;
@property (nonatomic, assign) int mInstallment;
@property (nonatomic, strong) NSString *mUserReference;

@end
