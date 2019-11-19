//
//  PlugPagInitializationResult.h
//  PlugPag
//
//  Created by Hildequias Junior on 4/22/19.
//  Copyright © 2019 UOL Pagseguro. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface PlugPagInitializationResult : NSObject

@property (nonatomic, assign) int mResult;
@property (nonatomic, strong) NSString *mErrorMessage;
@property (nonatomic, strong) NSString *mErrorCode;

@end

NS_ASSUME_NONNULL_END
