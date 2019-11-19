//
//  PlugPagActivationData.h
//  PlugPag
//
//  Created by Hildequias Junior on 4/22/19.
//  Copyright © 2019 UOL Pagseguro. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface PlugPagActivationData : NSObject

@property (nonatomic, strong) NSString *mActivationCode;
@property (nonatomic, strong) NSString *mSerialNumber;

@end

NS_ASSUME_NONNULL_END
