#import "GameScene.h"
#include "ChessController.hpp"
#include <vector>
#include <iostream>

static NSString* pieceTypes[2][6] =
{{@"white_king.png", @"white_queen.png", @"white_rook.png",
    @"white_bishop.png", @"white_knight.png", @"white_pawn.png" },
    {@"black_king.png", @"black_queen.png", @"black_rook.png",
        @"black_bishop.png", @"black_knight.png", @"black_pawn.png"}};

@implementation GameScene {
    
    
    
    SKShapeNode* squareTiles[64];
    
    SKSpriteNode* grabbedPiece;
    SKSpriteNode* pieceSprites[32];
    CGPoint goalPositions[32];
    

    bool finishedMoving[32];
    
    SKSpriteNode* promotionSprites[4];
    SKSpriteNode* prom_pointer;
    int promID;
    
    bool isAnimating;
    bool mouseHeldAfterAnimation;
    
    SKLabelNode* drawLabel;
    SKLabelNode* undoLabel;
    SKLabelNode* resignLabel;
    SKShapeNode* drawButton;
    SKShapeNode* undoButton;
    SKShapeNode* resignButton;

    

    
    SKLabelNode* gamestatelabel;
    
    CGPoint grabOffset;
    CGPoint grabPoint;
    
    chs::Controller controller;
}

- (void) updateBoard  {
    isAnimating = true;
    for(const auto& i : controller.movers()) {
        finishedMoving[i] = false;
        
        const auto& UIpiece = pieceSprites[i];
        const auto& LGpiece = controller.pieceData(i);
        UIpiece.size = CGSizeMake(square_size,square_size);
        UIpiece.hidden = NO;
        UIpiece.zPosition = 200;
        
        if(LGpiece.isPlaced_) {
            const auto& squ = LGpiece.square_;
            goalPositions[i] = CGPointMake(square_size*(1.5+(squ%8)),square_size*(8.5-(squ/8)));
            
        } else {
            const bool side = LGpiece.colour_;
            const int pos = LGpiece.positionInTakens_;
            goalPositions[i] = CGPointMake(square_size*(1.2+ 0.43*(pos)), (0.5 + double(side)*9)*square_size);
            
        }
    }
    
    
    

    gamestatelabel.text = [NSString stringWithUTF8String:controller.board_state().c_str()];

    
    if(controller.canUndo()) {
        undoLabel.hidden = NO;
        undoButton.hidden = NO;
    } else {
        undoLabel.hidden = YES;
        undoButton.hidden = YES;
    }
    if(controller.canResign()) {
        resignLabel.hidden = NO;
        resignButton.hidden = NO;
    } else {
        resignLabel.hidden = YES;
        resignButton.hidden = YES;
    }
    if(controller.canDraw()) {
        drawLabel.hidden = NO;
        drawButton.hidden = NO;
    } else {
        drawLabel.hidden = YES;
        drawButton.hidden = YES;
    }
}

constexpr float prom_size = 0.52;
constexpr float emph_size = 1.03;
constexpr float prom_emphasize = 0.56;
constexpr float c_off = 0.27;



- (void)didMoveToView:(SKView *)view {
    
    
    [NSTimer scheduledTimerWithTimeInterval:(1/animationFrameRate)
                                     target:self
                                   selector:@selector(animate)
                                   userInfo:nil
                                    repeats:YES];
    [NSTimer scheduledTimerWithTimeInterval:1
                                     target:self
                                   selector:@selector(stopAI)
                                   userInfo:nil
                                    repeats:YES];

    isAnimating = false;
    mouseHeldAfterAnimation = false;
    self.backgroundColor = [SKColor colorWithRed:0.4 green:0.5 blue:0.3 alpha:1];
    
    
    for (int i=0; i<8; i++) {
        for (int j=0; j<8; j++) {
            squareTiles[8*i+j] =
            [SKShapeNode shapeNodeWithRect:CGRectMake(square_size*(i+1), square_size*(j+1), square_size, square_size)];
            if(((i+j)&1)==0) {
                squareTiles[8*i+j].fillColor = [SKColor darkGrayColor];
                squareTiles[8*i+j].strokeColor = [SKColor darkGrayColor];
            } else {
                squareTiles[8*i+j].fillColor = [SKColor whiteColor];
                squareTiles[8*i+j].strokeColor = [SKColor whiteColor];
            }
            [self addChild: squareTiles[8*i+j]];
        }
    }
    
    for(int i=0;i<32;i++) {
        const auto& ppp = controller.pieceData(i);
        pieceSprites[i] = [SKSpriteNode spriteNodeWithImageNamed: pieceTypes[ppp.colour_][ppp.type_]];
        pieceSprites[i].anchorPoint = CGPointMake(0.5,0.5);
        pieceSprites[i].position = CGPointMake(square_size*(1.5+(ppp.square_%8)),square_size*(8.5-(ppp.square_/8)));
        pieceSprites[i].size = CGSizeMake(square_size,square_size);
        pieceSprites[i].zPosition = 0;
        [self addChild: pieceSprites[i]];
    }
    
    
    drawButton = [SKShapeNode shapeNodeWithRect:CGRectMake(460, 2, 110, 15)];
    drawButton.fillColor = [SKColor darkGrayColor];
    drawButton.strokeColor = [SKColor darkGrayColor];
    drawButton.hidden = YES;
    [self addChild: drawButton];
    drawLabel = [SKLabelNode labelNodeWithFontNamed:@"Copperplate"];
    drawLabel.text = [NSString stringWithFormat:@"claim draw"];
    drawLabel.fontSize = 16;
    drawLabel.fontColor = [SKColor systemGreenColor];
    drawLabel.position = CGPointMake(516,5);
    drawLabel.hidden = YES;
    [self addChild: drawLabel];
    
    undoButton = [SKShapeNode shapeNodeWithRect:CGRectMake(460, 22, 110, 15)];
    undoButton.fillColor = [SKColor darkGrayColor];
    undoButton.strokeColor = [SKColor darkGrayColor];
    undoButton.hidden = YES;
    [self addChild: undoButton];
    undoLabel = [SKLabelNode labelNodeWithFontNamed:@"Copperplate"];
    undoLabel.text = [NSString stringWithFormat:@"undo"];
    undoLabel.fontSize = 16;
    undoLabel.fontColor = [SKColor systemGreenColor];
    undoLabel.position = CGPointMake(516,25);
    undoLabel.hidden = YES;
    [self addChild: undoLabel];
    
    resignButton = [SKShapeNode shapeNodeWithRect:CGRectMake(460, 42, 110, 15)];
    resignButton.fillColor = [SKColor darkGrayColor];
    resignButton.strokeColor = [SKColor darkGrayColor];
    resignButton.hidden = YES;
    [self addChild: resignButton];
    resignLabel = [SKLabelNode labelNodeWithFontNamed:@"Copperplate"];
    resignLabel.text = [NSString stringWithFormat:@"resign"];
    resignLabel.fontSize = 16;
    resignLabel.fontColor = [SKColor systemGreenColor];
    resignLabel.position = CGPointMake(516,45);
    resignLabel.hidden = YES;
    [self addChild: resignLabel];
    
    gamestatelabel = [SKLabelNode labelNodeWithFontNamed:@"Copperplate"];
    gamestatelabel.text = [NSString stringWithUTF8String:""];
    gamestatelabel.fontSize = 16;
    gamestatelabel.fontColor = [SKColor systemBlueColor];
    gamestatelabel.position = CGPointMake(20,square_size*10);
    gamestatelabel.horizontalAlignmentMode = SKLabelHorizontalAlignmentModeLeft;
    gamestatelabel.hidden = NO;
    [self addChild: gamestatelabel];

    grabbedPiece=nil;
    prom_pointer = nil;
    promID = -1;
    controller.uiState_ = chs::e_UIstates::clearBoard;
    controller.initOracle();
}

- (void)touchDownAtPoint:(CGPoint)pos {
    if(isAnimating) return;
    mouseHeldAfterAnimation = false;
    

    switch (controller.uiState_) {
        case chs::e_UIstates::clearBoard:
        {
            if([undoButton containsPoint:pos] and controller.canUndo()) {
                controller.uiState_ = chs::e_UIstates::undoPressed;
                undoLabel.fontSize = 17;
                return;
            }
            if([drawButton containsPoint:pos] and controller.canDraw()) {
                controller.uiState_ = chs::e_UIstates::drawPressed;
                drawLabel.fontSize = 17;
                return;
            }
            if([resignButton containsPoint:pos] and controller.canResign()) {
                controller.uiState_ = chs::e_UIstates::resignPressed;
                resignLabel.fontSize = 17;
                return;
            }
            const int& startRank = floor(9- pos.y/square_size);
            const int& startFile = floor(-1+ pos.x/square_size);
            if(startRank>=0 and startRank<=7 and startFile>=0 and startFile<=7) {
                const auto& startSquare = chs::e_boardSquare(startRank*8+startFile);
                const auto& pieceID = controller.get_piece_idx(startSquare);
                if(pieceID == chs::Controller::nullPiece) {
                    controller.uiState_= chs::e_UIstates::dormantAreaPressedWhileClear;
                } else {
                    const auto& piece = pieceSprites[pieceID];
                    grabbedPiece = piece;
                    grabbedPiece.zPosition = 40;
                    grabbedPiece.size = CGSizeMake(square_size*emph_size,square_size*emph_size);
                    grabOffset = {pos.x - piece.position.x,pos.y - piece.position.y};
                    grabPoint = piece.position;
                    controller.set_start(startSquare);
                    controller.uiState_= chs::e_UIstates::pieceGrabbedPressed;
                }
            } else {
                controller.uiState_ = chs::e_UIstates::dormantAreaPressedWhileClear;
            }
            break;
        }
        case chs::e_UIstates::promotionState:
        {
            bool promPieceWasPressed = false;
            for(int i=0;i<4;i++) {
                if([promotionSprites[i] containsPoint:pos]) {
                    promotionSprites[i].size = CGSizeMake(square_size*prom_emphasize,square_size*prom_emphasize);
                    prom_pointer = promotionSprites[i];
                    promID = i;
                    promPieceWasPressed = true;
                    controller.uiState_= chs::e_UIstates::promotionPiecePressed;
                    break;
                }
            }
            if(!promPieceWasPressed) controller.uiState_= chs::e_UIstates::dormantAreaPressedWhilePromotion;
            break;
        }
        case chs::e_UIstates::takingPromotionState:
        {
            bool promPieceWasPressed = false;
            for(int i=0;i<4;i++) {
                if([promotionSprites[i] containsPoint:pos]) {
                    promotionSprites[i].size = CGSizeMake(square_size*prom_emphasize,square_size*prom_emphasize);
                    prom_pointer = promotionSprites[i];
                    promID = i;
                    promPieceWasPressed = true;
                    controller.uiState_= chs::e_UIstates::takingPromotionPiecePressed;
                    break;
                }
            }
            if(!promPieceWasPressed) controller.uiState_= chs::e_UIstates::dormantAreaPressedWhileTakingPromotion;
            break;
        }
        case chs::e_UIstates::gameOver:
        {
            break;
        }
        default:
            assert(false);
            break;
    }
}

- (void)touchMovedToPoint:(CGPoint)pos {
    if(controller.uiState_== chs::e_UIstates::pieceGrabbedPressed) grabbedPiece.position = {pos.x - grabOffset.x, pos.y - grabOffset.y};
}

- (void)touchUpAtPoint:(CGPoint)pos {
    if(isAnimating) return;
    if(mouseHeldAfterAnimation) {
        mouseHeldAfterAnimation = false;
        return;
    }
    
    switch (controller.uiState_) {
        case chs::e_UIstates::dormantAreaPressedWhileClear:
            controller.uiState_= chs::e_UIstates::clearBoard;
            break;
        case chs::e_UIstates::dormantAreaPressedWhilePromotion:
            controller.uiState_= chs::e_UIstates::promotionState;
            break;
        case chs::e_UIstates::dormantAreaPressedWhileTakingPromotion:
            controller.uiState_= chs::e_UIstates::takingPromotionState;
            break;
        case chs::e_UIstates::pieceGrabbedPressed:
        {
            
            const int& endRank = floor(9-grabbedPiece.position.y/square_size);
            const int& endFile = floor(-1+grabbedPiece.position.x/square_size);
            
            bool outOfBounds = endRank<0 or endRank>7 or endFile<0 or endFile>7;
            if (!outOfBounds) { controller.set_end(chs::e_boardSquare(endRank*8+endFile));}
            switch(controller.pieceMoveWasLegal(outOfBounds)) {
                case chs::e_legality::legal:
                {
                    controller.update();
                    
                    [self updateBoard];
                    grabbedPiece.zPosition = 500;
                    break;
                }
                case chs::e_legality::illegal:
                {
                    const auto& piece = controller.pieceData();
                    grabbedPiece.position = CGPointMake(square_size*(1.5+(piece.square_%8)),square_size*(8.5-(piece.square_/8)));
                    controller.uiState_= chs::e_UIstates::clearBoard;
                    grabbedPiece.zPosition = 0;
                    break;
                }
                case chs::e_legality::promotion:
                {
                    constexpr CGFloat offsets[4][2] = {{-c_off,-c_off},{-c_off,c_off},{c_off,-c_off},{c_off,c_off}};
                    for(int i=0;i<4;i++) {
                        promotionSprites[i] = [SKSpriteNode spriteNodeWithImageNamed: pieceTypes[endRank==7][1+i]];
                        promotionSprites[i].anchorPoint = CGPointMake(0.5,0.5);
                        promotionSprites[i].position = CGPointMake(square_size*(1.5+endFile+offsets[i][0]),square_size*(8.5-endRank+offsets[i][1]));
                        promotionSprites[i].size = CGSizeMake(square_size*prom_size,square_size*prom_size);
                        promotionSprites[i].zPosition=0;
                        [self addChild: promotionSprites[i]];
                    }
                    grabbedPiece.hidden = YES;
                    grabbedPiece.zPosition = 0;
                    grabbedPiece = nil;
                    const auto endSquare = chs::e_boardSquare(endRank*8+endFile);
                    
                    const auto& pieceID = controller.get_piece_idx(endSquare);
                    
                    if(pieceID == chs::Controller::nullPiece) {
                        // quiet promotion
                        controller.uiState_ =chs::e_UIstates::promotionState;
                    } else {
                        // taking promotion
                        const auto& lgTakenPiece = controller.pieceData(pieceID);
                        const auto& uiTakenPiece = pieceSprites[pieceID];
                        
                        const int& pos = controller.getNextTakenPosition();
                        
                        const bool side = lgTakenPiece.colour_;
                        uiTakenPiece.position = CGPointMake(square_size*(1.5+0.6*pos), (0.5 + double(side)*9)*square_size);
                        uiTakenPiece.size = CGSizeMake(square_size,square_size);
                        uiTakenPiece.zPosition = pos;
                        controller.uiState_ =chs::e_UIstates::takingPromotionState;
                    }
                    
                    break;
                }
            }
            grabbedPiece.size = CGSizeMake(square_size,square_size);
            
            grabbedPiece=nil;
            break;
        }
        case chs::e_UIstates::undoPressed:
        {
            if([undoButton containsPoint:pos]) {
                assert(controller.canUndo());
                undoLabel.fontSize = 16;
                controller.undo();
                for(const auto& i : controller.movers()) {
                    [SKTexture textureWithImageNamed:
                     pieceTypes[controller.pieceData(i).colour_][controller.pieceData(i).type_]];
                }
                [self updateBoard];
            }
            break;
        }
        case chs::e_UIstates::drawPressed:
        {
            if([drawButton containsPoint:pos]) {
                assert(controller.canDraw());
                drawLabel.fontSize = 16;
                controller.takeDraw();
                [self updateBoard];
            }
            break;
        }
        case chs::e_UIstates::resignPressed:
        {
            if([resignButton containsPoint:pos]) {
                assert(controller.canResign());
                resignLabel.fontSize = 16;
                controller.resign();
                [self updateBoard ];
            }
            break;
        }
        case chs::e_UIstates::promotionPiecePressed:
        {
            if([prom_pointer containsPoint:pos]) {
                const chs::e_flagType promtyp[4] = {
                    chs::e_flagType::queenPromote,
                    chs::e_flagType::rookPromote,
                    chs::e_flagType::bishopPromote,
                    chs::e_flagType::knightPromote
                };
                controller.set_promotee(promtyp[promID]);
                for(int i=0;i<4;i++) {
                    [promotionSprites[i] removeFromParent];
                    promotionSprites[i] = nil;
                }
                controller.update();
                [self updateBoard];
            } else {
                prom_pointer.size = CGSizeMake(square_size*prom_size,square_size*prom_size);
                prom_pointer = nil;
                promID = -1;
                controller.uiState_ = chs::e_UIstates::promotionState;
            }
            break;
        }
        case chs::e_UIstates::takingPromotionPiecePressed:
        {
            if([prom_pointer containsPoint:pos]) {
                const chs::e_flagType promtyp[4] = {
                    chs::e_flagType::queenPromoteTake,
                    chs::e_flagType::rookPromoteTake,
                    chs::e_flagType::bishopPromoteTake,
                    chs::e_flagType::knightPromoteTake
                };
                controller.set_promotee(promtyp[promID]);
                for(int i=0;i<4;i++) {
                    [promotionSprites[i] removeFromParent];
                    promotionSprites[i] = nil;
                }
                controller.update();
                //movingPieces = controller.movers();
                [self updateBoard];
            } else {
                prom_pointer.size = CGSizeMake(square_size*prom_size,square_size*prom_size);
                prom_pointer = nil;
                promID = -1;
                controller.uiState_ = chs::e_UIstates::takingPromotionState;
            }
            break;
        }
        case chs::e_UIstates::gameOver:
            break;
        default:
            assert(false);
            break;
    }
}





-(void) animate {
    if(isAnimating) {
        bool allFinished = true;
        //for(int i=0; i<32;i++) {
        for(const auto& i : controller.movers()) {
            if(finishedMoving[i]) continue;
            allFinished = false;
            
            const CGFloat oldX = pieceSprites[i].position.x;
            const CGFloat oldY = pieceSprites[i].position.y;
            
            CGFloat xDiff = oldX - goalPositions[i].x;
            CGFloat yDiff = oldY - goalPositions[i].y;
            CGFloat absDiffSq = xDiff*xDiff + yDiff*yDiff;
            if(absDiffSq < 1+(animationSpeed*animationSpeed/(animationFrameRate*animationFrameRate))) {
                finishedMoving[i]=true;
                pieceSprites[i].position = goalPositions[i];
                continue;
            }
            
            const CGFloat absDiff = sqrt(absDiffSq);
            const CGFloat newX = oldX - (animationSpeed/animationFrameRate)* xDiff/absDiff;
            const CGFloat newY = oldY - (animationSpeed/animationFrameRate)* yDiff/absDiff;
            pieceSprites[i].position = CGPointMake(newX,newY);
        }
        
        if(allFinished) {
            isAnimating = false;
            mouseHeldAfterAnimation = true;
            for(const auto& i : controller.movers()) {
                const auto& piece = controller.pieceData(i);
                if(piece.isPlaced_){
                    pieceSprites[i].zPosition=0;
                    pieceSprites[i].texture =
                    [SKTexture textureWithImageNamed:pieceTypes[controller.pieceData(i).colour_][controller.pieceData(i).type_]];
                } else {
                    const int pos = piece.positionInTakens_;
                    pieceSprites[i].zPosition = pos;
                }
                
            }
        }
        
    }
}

- (void) stopAI { controller.eachSecond(); }

-(void)update:(CFTimeInterval)currentTime {
    // Called before each frame is rendered
}

- (void)mouseMoved:(NSEvent *)theEvent
{
    //NSLog(@"hello");
}

- (void)keyDown:(NSEvent *)theEvent {
}

- (void)mouseDown:(NSEvent *)theEvent {
    [self touchDownAtPoint:[theEvent locationInNode:self]];
}
- (void)mouseDragged:(NSEvent *)theEvent {
    [self touchMovedToPoint:[theEvent locationInNode:self]];
}
- (void)mouseUp:(NSEvent *)theEvent {
    [self touchUpAtPoint:[theEvent locationInNode:self]];
}

@end
