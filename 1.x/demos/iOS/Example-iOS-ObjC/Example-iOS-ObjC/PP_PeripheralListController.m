//
//  PP_PeripheralListController.m
//  Example-iOS-ObjC
//
//  Created by Hildequias Junior on 8/8/17.
//  Copyright © 2017 UOL Pagseguro. All rights reserved.
//

#import "PP_PeripheralListController.h"
#import "PlugPag.h"
#import "UIUtils.h"

#define MSG_PAIR_SUCESS       @"Pareamento efetuado com sucesso!"

@interface PP_PeripheralListController () {
    NSArray *devices;
}

@end

@implementation PP_PeripheralListController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [[PlugPag sharedInstance] SetVersionName:@"UnitTest" withVersion:@"V001"];
}

- (void) viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return [devices count];
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIdentifier = @"CellID";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];
    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    NSString *messageAtIndexPath = [devices objectAtIndex :[indexPath row]];
    [[cell textLabel] setText:messageAtIndexPath];
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
    NSString *deviceChosen = [devices objectAtIndex:indexPath.row];
    
    [[PlugPag sharedInstance] SetPeripheralName:deviceChosen];
    [self pairPeripheralStatus];
    
    [self.tableView deselectRowAtIndexPath:indexPath animated:YES];
}

- (IBAction)scan:(id)sender {
    
    [self scanPeripherals];
}

- (void) scanPeripherals {
    
    [UIUtils showProgress];
    
    devices = [[PlugPag sharedInstance] GetListPeripheral];
    [self.tableView reloadData];
    
    [UIUtils hideProgress];
}

- (void) pairPeripheralStatus {
    
    int ret = [[PlugPag sharedInstance] GetPairPeripheralStatus];
    
    switch (ret) {
        case BT_PAIR_STATE_PROCESSING:
            [self performSelector:@selector(pairPeripheralStatus) withObject:nil afterDelay:1.5];
            [UIUtils showProgress];
            break;
        
        case BT_PAIR_STATE_OK:
            [UIUtils hideProgress];
            [NSObject cancelPreviousPerformRequestsWithTarget:self selector:@selector(pairPeripheralStatus) object:nil];
            [UIUtils showAlert:self withMessage: [NSString stringWithFormat:@"Pair Ok"]];
            break;
            
        case BT_PAIR_STATE_FAIL:
            [UIUtils hideProgress];
            [NSObject cancelPreviousPerformRequestsWithTarget:self selector:@selector(pairPeripheralStatus) object:nil];
            [UIUtils showAlert:self withMessage: [NSString stringWithFormat:@"Pair Failed"]];
            
            break;
            
        default:
            [UIUtils hideProgress];
            break;
    }
    
}

@end
