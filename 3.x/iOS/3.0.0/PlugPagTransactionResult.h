//
//  PlugPagTransactionResult.h
//  PlugPag
//
//  Created by Hildequias Junior on 1/31/18.
//  Copyright © 2018 UOL Pagseguro. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface PlugPagTransactionResult : NSObject

@property (nonatomic, assign) int mResult;
@property (nonatomic, strong) NSString *mMessage;
@property (nonatomic, strong) NSString *mTransactionCode;
@property (nonatomic, strong) NSString *mDate;
@property (nonatomic, strong) NSString *mTime;
@property (nonatomic, strong) NSString *mHostNsu;
@property (nonatomic, strong) NSString *mCardBrand;
@property (nonatomic, strong) NSString *mBin;
@property (nonatomic, strong) NSString *mHolder;
@property (nonatomic, strong) NSString *mUserReference;
@property (nonatomic, strong) NSString *mTerminalSerialNumber;
@property (nonatomic, strong) NSString *mTransactionId;
@property (nonatomic, strong) NSString *mAmount;
@property (nonatomic, strong) NSString *mAvailableBalance;

@end
