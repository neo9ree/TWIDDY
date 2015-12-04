#include <SoftwareSerial.h>
#include <Servo.h>

#define rxPin 2 
#define txPin 3 
#define leftArmPin 5
#define rightArmPin 6
#define ANGRY 'a'
#define HAPPY 'h'
#define NORMAL 'n'

Servo leftArm;
Servo rightArm;

SoftwareSerial swSerial(txPin, rxPin);

char data;
int angle;

void setup() {
  swSerial.begin(115200);
  Serial.begin(9600);
  leftArm.attach(leftArmPin);
  rightArm.attach(rightArmPin);
}

void loop() {
  // 데이터가 들어올때 까지 대기.
  while(!swSerial.available());
  
  if(swSerial.available()) {    
    data = swSerial.read();
    Serial.write(data);
    // Angle 범위: 20 ~ 200 (180도)
    switch(data)
    {
      case ANGRY-1: 
      case ANGRY: 
      case ANGRY+1:
        moveServo(60, 120, 15);
        moveServo(120, 60, 3);
        moveServo(60, 120, 15);
        moveServo(120, 60, 3);
        moveServo(60, 120, 15);
        moveServo(120, 60, 3);
        break;
      case HAPPY-1:
      case HAPPY:
      case HAPPY+1:
        moveServoRev(40, 140, 3);
        moveServoRev(140, 40, 3);
        moveServoRev(40, 140, 3);
        moveServoRev(140, 40, 3);
        moveServoRev(40, 140, 3);
        moveServoRev(140, 40, 3);
        moveServo(40, 60, 5);
        break;
      case NORMAL-1:
      case NORMAL:
      case NORMAL+1:
        moveServo(60, 120, 15);
        delay(500);
        moveServo(120, 60, 15);
        break;
      default:
        break;
    }
    
    // 블루투스를 통하여 받은 데이터를 되돌려준다.
    swSerial.write(data);    
  }  
}

void moveServo(int s, int e, int t)
{ 
  if(s < e)
  {
      for(angle = s; angle < e; angle ++)
      {
        leftArm.write(angle);
        rightArm.write(180-angle);
        delay(t);
      }  
  }
  else
  {
      for(angle = s; angle > e; angle --)
      {
        leftArm.write(angle);
        rightArm.write(180-angle);
        delay(t);
      }  
  }
}

void moveServoRev(int s, int e, int t)
{ 
  if(s < e)
  {
      for(angle = s; angle < e; angle ++)
      {
        leftArm.write(angle);
        rightArm.write(angle);
        delay(t);
      }  
  }
  else
  {
      for(angle = s; angle > e; angle --)
      {
        leftArm.write(angle);
        rightArm.write(angle);
        delay(t);
      }  
  }
}
