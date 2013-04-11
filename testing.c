#include <stdio.h>
#include <stdlib.h>
#include <avr/io.h>
#include <avr/sleep.h>
#include <avr/interrupt.h>

#define F_CPU 8000000UL

#include <util/delay.h>
#include <string.h>

#define DELAY_AMOUNT 1

#define FALSE 0
#define TRUE 1

#define PORT_1 PORTC
#define PORT_2 PORTD
#define PORT_3 PORTB

void serial_init(void);
void send_char_serial (char data);
char receive_char_serial (void);
void send_string_serial(char* s);

void setSpeed3(int speed);
void setSpeed2(int speed);
void setSpeed1(int speed);

volatile unsigned int speed1;
volatile unsigned int speed2;
volatile unsigned int speed3;
volatile unsigned int speed4;

volatile unsigned int counter1;
volatile unsigned int counter2;
volatile unsigned int counter3;
volatile unsigned int counter4;

volatile unsigned char needState1;
volatile unsigned char needState2;
volatile unsigned char needState3;

volatile unsigned char currentState1;
volatile unsigned char currentState2;
volatile unsigned char currentState3;

volatile unsigned char movingForward1;
volatile unsigned char movingForward2;
volatile unsigned char movingForward3;

volatile unsigned char motorState1;
volatile unsigned char motorState2;
volatile unsigned char motorState3;

void setupTimers();

const unsigned int speed[3][32] = {
    //{ 10, 10, 10, 10, 10, 10, 10, 10},
    //{ 10, 10, 10, 10, 10, 10, 10, 10},
    //{ 10, 10, 10, 10, 10, 10, 10, 10}
     {20000, 99, 51, 35, 28, 24, 21, 20, 20, 21, 22, 25, 31, 41, 67,198,-198,-67,-41,-31,-25,-22,-21,-20,-20,-21,-24,-28,-35,-51,-99, 2000},
     {-23, -21, -20, -20, -21, -23, -26, -32, -44, -75,-296, 148,60,39,30,25,22,20,20,20,22,24,29,37,55, 119,-592, -85, -47, -33, -27, -23},
     {23, 27, 33, 47, 85,592,-119,-55,-37,-29,-24,-22,-20,-20,-20,-22,-25,-30,-39,-60,-148,296, 75,44, 32, 26, 23, 21, 20, 20, 21, 23},
    /*{ 5000, 98, 50, 33, 25, 20, 20, 20, 20, 20, 20,
        20, 25, 33, 50, 98, 5000, 98, 50, 33, 25, 20,
        20, 20, 20, 20, 20, 25, 33, 50, 98, 5000},
    { 20, 20, 20, 20, 20, 20, 25, 33, 50, 98, 5000,
      5000, 98, 50, 33, 25, 20, 20, 20, 20, 20, 20,
      25, 33, 50, 98, 5000, 5000, 98, 50, 33, 25},
    { 20, 25, 33, 50, 98, 5000, 98, 50, 33, 25, 20,
      20, 20, 20, 20, 20, 20, 25, 33, 50, 98, 5000,
      98, 50, 33, 25, 20, 20, 20, 20, 20, 20},*/
};
unsigned char currentSpeedIndex = 0;
volatile unsigned char needsNewSpeed = FALSE;

const unsigned char motorSpeed[256] = {0x01, 0x03, 0x02, 0x06, 0x04, 0x0C, 0x08, 0x09};

unsigned char getMotorState(unsigned char state) {
    return motorSpeed[(int)(state & 0x07)];
}

unsigned char getNextState(unsigned char currentState, unsigned char movingForward) {
    if (movingForward) {
        ++currentState;
    } else {
        --currentState;
    }
    //if (currentState == 8) currentState = 0;
    //if (currentState > 8) currentState = 7;
    return currentState & 7;
}

int main(int argc, char* argv[]) {

    DDRC = 0x0f;
    DDRD = 0xf0;
    DDRB = 0xC3;
    PORTB = 0;
    PORTC = 0;
    PORTD = 0;
    serial_init();
    send_string_serial("Starting up...\n");
    _delay_ms(500);

    speed1 = 20;
    speed2 = 20;
    speed3 = 1000;
    speed4 = 5000;
    counter1 = 1;
    counter2 = 1;
    counter3 = 1;
    counter4 = 1;

    needState1 = FALSE;
    needState2 = FALSE;
    needState3 = FALSE;

    movingForward1 = FALSE;
    movingForward2 = TRUE;
    movingForward3 = FALSE;

    currentState1 = 0;
    currentState2 = 0;
    currentState3 = 0;

    motorState1 = getMotorState(currentState1);
    motorState2 = getMotorState(currentState2) << 4;
    unsigned char state = getMotorState(currentState3);
    motorState3 = ((state & 0x0C) << 4) | (state & 0x03);

    setupTimers();

    while (1) {
        if (needState1) {
            currentState1 = getNextState(currentState1, movingForward1);
            motorState1 = getMotorState(currentState1);
            needState1 = FALSE;
        }
        if (needState2) {
            currentState2 = getNextState(currentState2, movingForward2);
            motorState2 = getMotorState(currentState2) << 4;
            needState2 = FALSE;
        }
        if (needState3) {
            currentState3 = getNextState(currentState3, movingForward3);
            state = getMotorState(currentState3);
            motorState3 = ((state & 0x0C) << 4) | (state & 0x03);
            needState3 = FALSE;
        }
        if (needsNewSpeed) {
            currentSpeedIndex = (currentSpeedIndex + 1) & 0x1f;
            setSpeed1(speed[0][currentSpeedIndex]);
            setSpeed2(speed[1][currentSpeedIndex]);
            setSpeed3(speed[2][currentSpeedIndex]);
            needsNewSpeed = FALSE;
        }
    }

	return 0;
}

void setSpeed1(int speed) {
    if (speed > 0) {
        movingForward1 = 1;
        speed1 = speed;
    } else {
        movingForward1 = 0;
        speed1 = -speed;
    }
}

void setSpeed2(int speed) {
    if (speed > 0) {
        movingForward2 = 1;
        speed2 = speed;
    } else {
        movingForward2 = 0;
        speed2 = -speed;
    }
}

void setSpeed3(int speed) {
    if (speed > 0) {
        movingForward3 = 1;
        speed3 = speed;
    } else {
        movingForward3 = 0;
        speed3 = -speed;
    }
}

void serial_init(void) {
    UBRRH=0;
    UBRRL=103; // 9600 BAUD FOR 8MHZ SYSTEM CLOCK
    UCSRC= (1<<URSEL)|(1<<USBS)|(3<<UCSZ0); // 8 BIT NO PARITY 2 STOP
    UCSRB=(1<<RXEN) | (1 << TXEN);        // ENABLE TX, RX
    UCSRA |= (1<<U2X);      // double baud rate for less error
    char c = UDR; // to clear the RXC flag
}

void send_char_serial (char data) {
    /* Wait for empty transmit buffer */
    while (!(UCSRA & (1<<UDRE)));
    /* Put data into buffer, sends the data */
    UDR = data;
    if (data == '\n') send_char_serial('\r');
    /* Wait for empty transmit buffer */
    //while (!(UCSRA & (1<<UDRE)));
}

char receive_char_serial (void) {
    // Wait for data to be received 
    while (!(UCSRA & (1<<RXC)));
    // Get and return received data from buffer 
    return UDR;
}

void send_string_serial(char* s) {
    int i;
    for (i = 0; i < strlen(s); i++) {
         send_char_serial(s[i]);
    }
    //send_char_serial('\n');
    //send_char_serial('\r');
}

void setupTimers() {
    TCNT1 = 99;
    TCCR1A = 0;
    OCR1A = 100;
    TIFR |= (1 << OCF1A);
    TIMSK = 1 << OCIE1A;
    sei();
    TCCR1B = (1 << CS11); // CLKIO / 8
}

ISR(TIMER1_COMPA_vect) {
    TCNT1 = 0;
    if (--counter1 == 0) {
        PORT_1 = motorState1;
        needState1 = TRUE;
        counter1 = speed1;
    }
    if (--counter2 == 0) {
        PORT_2 = motorState2;
        needState2 = TRUE;
        counter2 = speed2;
    }
    if (--counter3 == 0) {
        PORT_3 = motorState3;
        needState3 = TRUE;
        counter3 = speed3;
    }
    if (--counter4 == 0) {
        needsNewSpeed = TRUE;
        counter4 = speed4;
    }
}

ISR(TIMER1_OVF_vect) {
    send_string_serial("OVF");
}

ISR(TIMER1_COMPB_vect) {
    send_string_serial("COM");
}

ISR(TIMER1_CAPT_vect) {
    send_string_serial("CAP");
}
