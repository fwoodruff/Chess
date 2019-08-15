#import <SpriteKit/SpriteKit.h>

struct ui_packet;

const CGFloat square_size = 60;
const CGFloat animationSpeed = 1500;
const CGFloat animationFrameRate = 50;

@interface GameScene : SKScene
- (void) updateBoard;
- (void) animate;
- (void) stopAI;
@end

