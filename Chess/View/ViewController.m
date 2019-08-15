#import "ViewController.h"
#import "GameScene.h"

@implementation ViewController

- (void)viewDidLoad {
    const CGFloat square_size = 60;
    [super viewDidLoad];
    self.skView = (SKView *)self.view;
    [self.skView setFrame:NSMakeRect(0, 0,square_size*10, square_size*10.2)];
    //[self.skView setFrameOrigin:NSMakePoint(0, 0)];
    GameScene *scene = [[GameScene alloc]
                        initWithSize:CGSizeMake(self.skView.bounds.size.width,
                                                self.skView.bounds.size.height)];
    scene.scaleMode = SKSceneScaleModeAspectFit;
    [self.skView presentScene:scene];
    //self.skView.showsFPS = YES;
    //self.skView.showsNodeCount = YES;
}
- (void) viewDidAppear {
    //added in hopes that mouse moved events would be captured
    [self.skView.window setAcceptsMouseMovedEvents:YES];
    [self.skView.window setInitialFirstResponder:self.skView];
    [self.skView.window makeFirstResponder:self.skView.scene];
}
@end
