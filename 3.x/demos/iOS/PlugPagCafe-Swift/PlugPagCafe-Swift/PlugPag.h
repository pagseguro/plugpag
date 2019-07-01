//
//  PlugPag.h
//  PlugPag
//
//  Created by Hildequias Junior on 7/26/17.
//  Copyright © 2017 Pagseguro. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "PlugPagPaymentData.h"
#import "PlugPagTransactionResult.h"
#import "PlugPagVoidData.h"
#import "PlugPagAbortResult.h"
#import "PlugPagDevice.h"

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
#define SHARE_MODE_NOT_ALLOWED          ERR_BASE-20
#define ERROR_DEVICE_NULL               ERR_BASE-1023

#define PP_STATUS_WAITING_CARD          0
#define PP_STATUS_INSERTED_CARD         1
#define PP_STATUS_PIN_REQUESTED         2
#define PP_STATUS_PIN_OK                3
#define PP_STATUS_SALE_END              4
#define PP_STATUS_AUTHORIZING           5
#define PP_STATUS_INSERTED_KEY          6
#define PP_STATUS_WAITING_REMOVE_CARD   7
#define PP_STATUS_REMOVED_CARD          8

#define BT_PAIR_STATE_OK                1
#define BT_PAIR_STATE_FAIL              0

@protocol PP_PlugPagDelegate <NSObject>

@optional
- (void) peripheralDiscover:(PlugPagDevice *) plugPagDevice;
- (void) userEventsInterface:(int) event;
- (void) pairPeripheralStatus:(int) status;
@end

@interface PlugPag : NSObject

/*!
 @brief Creates this as a singleton
 */
+ (PlugPag *) sharedInstance;

-(void) setDelegate:(id<PP_PlugPagDelegate>) delegate;

-(void) pairPeripheral: (PlugPagDevice *) plugPagDevice;

-(void) startScanForPeripherals;

-(const char*) getVersionLib;

-(PlugPagTransactionResult *) setInitBTConnection:(PlugPagDevice *) peripheral;

-(PlugPagTransactionResult *) doPayment:(PlugPagPaymentData *) paymentData;

-(PlugPagTransactionResult *) voidPayment:(PlugPagVoidData *) voidData;

-(PlugPagAbortResult *) abort;

-(PlugPagTransactionResult *) getLastApprovedTransaction;

-(NSArray *) calculateInstallments:(NSString *) saleValue;

-(int) plugPagAppIdentification :(NSString *)appName withVersion:(NSString *) appVersion;

-(BOOL) isAuthenticated;

-(void) invalidateAuthentication;

-(void) requestAuthentication:(UIViewController *) viewController;

@property (nonatomic, retain) id <PP_PlugPagDelegate> delegate;

@end
