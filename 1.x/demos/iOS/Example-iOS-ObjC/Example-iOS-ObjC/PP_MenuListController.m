//
//  PP_MenuListController.m
//  Example-iOS-ObjC
//
//  Created by Hildequias Junior on 8/8/17.
//  Copyright © 2017 UOL Pagseguro. All rights reserved.
//

#import "PP_MenuListController.h"
#import "PlugPag.h"

@interface PP_MenuListController () {
    
    NSArray *menus;
}

@end

@implementation PP_MenuListController

#pragma mark - View Controller cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    
    menus = @[@"Fazer uma venda",
              @"Consultar a última venda",
              @"Parear (conectar) um leitor",
              @"Fazer Estorno"];
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
    
    return [menus count];
}

#pragma mark - Table view delegate
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    static NSString *CellIdentifier = @"CellID";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];
    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    NSString *messageAtIndexPath = [menus objectAtIndex :[indexPath row]];
    [[cell textLabel] setText:messageAtIndexPath];
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
    switch (indexPath.row) {
        
        case 0:
            [self performSegueWithIdentifier:@"goToSale" sender:nil];
            break;
        
        case 1:
            [self performSegueWithIdentifier:@"goToLastTransaction" sender:nil];
            break;
            
        case 2:
            [self performSegueWithIdentifier:@"goSegue" sender:nil];
            break;
            
        case 3:
            [self performSegueWithIdentifier:@"goToCancel" sender:nil];
            break;
            
        default:
            break;
    }
    [self.tableView deselectRowAtIndexPath:indexPath animated:YES];
}

@end
