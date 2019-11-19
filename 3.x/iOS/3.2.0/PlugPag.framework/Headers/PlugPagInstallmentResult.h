//
//  PlugPagInstallmentResult.h
//  PlugPag
//
//  Created by Hildequias Junior on 6/17/19.
//  Copyright © 2019 UOL Pagseguro. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface PlugPagInstallmentResult : NSObject

@property (nonatomic, assign) int mResult;
@property (nonatomic, strong) NSString *mMessage;
@property (nonatomic, strong) NSString *mErrorCode;
@property (nonatomic, strong) NSMutableArray *installments;
@property (nonatomic, strong) NSString *rate;

@end

NS_ASSUME_NONNULL_END
