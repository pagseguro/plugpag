//
//  PlugPagDevice.h
//  PlugPag
//
//  Created by Hildequias Junior on 2/6/18.
//  Copyright © 2018 UOL Pagseguro. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface PlugPagDevice : NSObject

typedef enum PlugPagDeviceTypes
{
    TYPE_UNDEFINED  = -1,
    TYPE_PINPAD     = 0,
    TYPE_NOT_PINPAD = 1
} DeviceTypes;

@property (nonatomic, assign) DeviceTypes mType;
@property (nonatomic, strong) NSString *mPeripheralName;
@property (nonatomic, strong) NSString *mPeripheralModel;

@end
