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
#import "PlugPagInitializationResult.h"
#import "PlugPagActivationData.h"
#import "PlugPagInstallmentResult.h"
#import "PlugPagInstallments.h"

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
#define APPL_NOT_SUPORTED               ERR_BASE-21
#define INVALID_CARD                    ERR_BASE-22
#define PSCINIT_EXECERR                 ERR_BASE-23
#define INVALID_TABLES                  ERR_BASE-24
#define PINPADERROR                     ERR_BASE-25
#define INV_TRANS_TYPE_PARAM            ERR_BASE-26
#define INV_PARAM                       ERR_BASE-27
#define OPERATION_ABORTED               ERR_BASE-28
#define MISSING_TOKEN                   ERR_BASE-30
#define INVALID_AMOUNT                  ERR_BASE-31
#define INVALID_INSTALLMENT             ERR_BASE-32
#define AUTHENTICATION_FAILED           ERR_BASE-33
#define MISSING_COEFFICIENTS            ERR_BASE-34
#define DEVICE_IDENTIFICATION           ERR_BASE-35
#define PINPAD_NOT_INITIALIZED          ERR_BASE-36
#define INVALID_READER                  ERR_BASE-37
#define INSTALLMENT_ERROR               ERR_BASE-39
#define DOING_TRANSACTION               ERR_BASE-47
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
#define PP_STATUS_CVV_REQUESTED         9
#define PP_STATUS_CVV_OK                10
#define PP_STATUS_CAR_BIN_REQUESTED     11
#define PP_STATUS_CAR_BIN_OK            12
#define PP_STATUS_CAR_HOLDER_REQUESTED  13
#define PP_STATUS_CAR_HOLDER_OK         14
#define PP_STATUS_ACTIVATION_COMPLETED  15
#define PP_STATUS_DIGIT_PASSWORD        16
#define PP_STATUS_NO_PASSWORD           17
#define PP_STATUS_SALE_APROVATED        18
#define PP_STATUS_SALE_NOT_APPROVED     19

#define BT_SCAN_STATE_STOP              2
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

-(void) cancelPairPeripheral;

-(void) startScanForPeripherals;

-(void) stopScanForPeripherals;

-(BOOL) isPlugPagDeviceConnected:(PlugPagDevice *) device;

-(const char*) getVersionLib;

-(int) setInitBTConnection:(PlugPagDevice *)peripheral;

-(PlugPagInitializationResult *) initializeAndActivate: (PlugPagActivationData *) activationData;

-(PlugPagTransactionResult *) doPayment:(PlugPagPaymentData *) paymentData;

-(PlugPagTransactionResult *) voidPayment:(PlugPagVoidData *) voidData;

-(PlugPagAbortResult *) abort;

-(PlugPagTransactionResult *) getLastApprovedTransaction;

-(NSArray *) calculateInstallments:(NSString *) saleValue;

-(PlugPagInstallmentResult *) calculateInstallments:(NSString *) saleValue type:(InstallmentType) installmentType;

-(int) plugPagAppIdentification :(NSString *)appName withVersion:(NSString *) appVersion;

-(BOOL) isAuthenticated;

-(void) invalidateAuthentication;

-(void) requestAuthentication:(UIViewController *) viewController;

@property (nonatomic, retain) id <PP_PlugPagDelegate> delegate;

@end
