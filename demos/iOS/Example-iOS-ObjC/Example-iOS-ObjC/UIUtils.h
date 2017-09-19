//
//  UIUtils.h
//  Example-iOS-ObjC
//
//  Created by Hildequias Junior on 8/8/17.
//  Copyright © 2017 UOL Pagseguro. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface UIUtils : NSObject

+ (NSMutableAttributedString *) formatTextGetLastApprovedTransaction;
+ (NSMutableAttributedString *) formatTextError:(int) error withMessage:(NSString *) msg;
+ (NSMutableAttributedString *) formatTextResult:(NSString *) message;
+ (NSMutableAttributedString *) formatCurrency:(float) value;
+ (void) showProgress;
+ (void) hideProgress;
+ (void) showAlert:(UIViewController *) view withMessage:(NSString *) message;

@end
