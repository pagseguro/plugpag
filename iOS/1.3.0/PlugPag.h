//
//  PlugPag.h
//  PlugPag
//
//  Created by Hildequias Junior on 7/26/17.
//  Copyright © 2017 Pagseguro. All rights reserved.
//

#import <Foundation/Foundation.h>

#define RET_OK                          0
#define ERR_BASE                        -1000
#define BUFF_SIZE                       ERR_BASE-1
#define NULL_PTR                        ERR_BASE-2
#define POS_NOT_READY                   ERR_BASE-3
#define TRANS_DENIED                    ERR_BASE-4
#define DATA_INV_RESULT_MESSAGE         ERR_BASE-5

#define INV_AMOUNT_PARAM                ERR_BASE-6
#define INV_TOT_AMOUNT_PARAM            ERR_BASE-7
#define INV_USER_REF_PARAM              ERR_BASE-8
#define INV_TRS_RESULT_PARAM            ERR_BASE-9
#define DRIVER_NOT_FOUND                ERR_BASE-10
#define DRIVER_FUNCTION_ERROR           ERR_BASE-11
#define INV_FORMAT_AMOUNT_PARAM         ERR_BASE-12
#define INV_LEN_USER_REF_PARAM          ERR_BASE-13
#define INVALID_BUFFER                  ERR_BASE-14
#define INV_APP_NAME_PARAM              ERR_BASE-15
#define INV_APP_VERSION_PARAM           ERR_BASE-16
#define APP_NAME_VERSION_NOT_SET        ERR_BASE-17
#define TRANS_NODATA                    ERR_BASE-18
#define COMMUNICATION_ERROR             ERR_BASE-19
#define ERROR_DEVICE_NULL               ERR_BASE-1023
#define SHARE_MODE_NOT_ALLOWED          ERR_BASE-20

#define S920      @"S920"
#define D200      @"D200"

#define CREDIT                          1
#define DEBIT                           2

#define A_VISTA                         1
#define PARC_VENDEDOR                   2

@interface PlugPag : NSObject

/*!
 @brief Creates this as a singleton
 */
+ (PlugPag *) sharedInstance;

-(void) SetModel: (NSString *) model;

-(void) SetPeripheralName: (NSString *) name;

-(void) SetPeripheral: (NSString *) model withName: (NSString *) name;

-(NSArray *) GetListPeripheral;
-(NSArray *) GetListPeripheral:(NSString *) model;

-(NSArray *) GetListModels;

-(const char*) GetVersionLib;

-(int) InitBTConnection;

-(int) InitBTConnection:(NSString *)comPort;

-(void) InitDummyConnection;

-(void) UnloadDriverConnection;

-(int) SimplePaymentTransaction:(int) paymentMethod
            withInstallmentType:(int) installmentType
               andInstallments:(int) installments
                     andAmount:(NSString *) amount
              andUserReference:(NSString *) userReference;

-(int) CancelTransaction;

-(int) GetLastApprovedTransactionStatus;

-(int) SetVersionName:(NSString *)appName withVersion:(NSString *) appVersion;

@property (nonatomic, assign) unsigned char rawBuffer;
@property (nonatomic, strong) NSString *message;
@property (nonatomic, strong) NSString *transactionCode;
@property (nonatomic, strong) NSString *date;
@property (nonatomic, strong) NSString *time;
@property (nonatomic, strong) NSString *hostNsu;
@property (nonatomic, strong) NSString *cardBrand;
@property (nonatomic, strong) NSString *bin;
@property (nonatomic, strong) NSString *holder;
@property (nonatomic, strong) NSString *userReference;
@property (nonatomic, strong) NSString *terminalSerialNumber;

@end
