#include <Arduino.h>

#include <LiquidCrystal_I2C.h> //Thu vien lcd
#include <Keypad.h>
#include <ESP32Servo.h>
#include <EEPROM.h>
#include <WiFi.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>

#define PIN_SG90 16 // GPIO Output pin used

// Wifi information
const char* ssid = "userIduserId";          
const char* wifi_password = "passwordpassword";

// MQTT Broker
IPAddress mqttBroker(192, 168, 2, 13);  // IP Of MQTT Broker
const int mqttPort = 1883;       // Port of MQTT Broker

// Use 2 topics 
const char* publishTopic = "esp32/data";       // Send data from ESP32
const char* subscribeTopic = "esp32/command"; // Receive order from server

// Content of Message
const char* modelId = "ESP32_001";
const char* devicePassword = "my_secret_password";

bool responseReceived = false;
char*responseResult;

// Status
unsigned char index_t = 0;
unsigned char error_in = 0;

// init keypad
const byte ROWS = 4; 
const byte COLS = 4; 

char hexaKeys[ROWS][COLS] = {
    {'1', '2', '3', 'A'},
    {'4', '5', '6', 'B'},
    {'7', '8', '9', 'C'},
    {'*', '0', '#', 'D'}};
byte rowPins[ROWS] = {19, 18, 5, 17}; // GPIO row pinouts 
byte colPins[COLS] = {13, 14, 15, 16}; // GPIO col pinouts 

int addr = 0;
char password[7] = "123456";
char pass_def[7] = "123456";
char mode_changePass[7] = "*#01#";
char mode_resetPass[7] = "*#02#";

char data_input[7];
char new_pass1[7];
char new_pass2[7];

unsigned char in_num = 0, error_pass = 0, isMode = 0;
WiFiClient espClient;
PubSubClient client(espClient);     
LiquidCrystal_I2C lcd(0x27, 16, 2);
Keypad keypad = Keypad(makeKeymap(hexaKeys), rowPins, colPins, ROWS, COLS);
Servo sg90;

void sendMessage(char* password, char*method); // Send message JSON to MQTT Broker

void callback(char* topic, byte* message, unsigned int length); // Process received message

void connectToMqttBroker(); // Connect ESP32 to MQTT Broker

void connectToWiFi();   // Connect Wifi 

void writeEpprom(char data[]); // Save pass to EEPROM(not lost memory when lost power sourcesource)

void readEpprom();  // read data from EEPROMEEPROM

void clear_data_input(); // delete current input value

unsigned char isBufferdata(char data[]); // Check if buffer has value

bool compareData(char data1[], char data2[]); // Check if 2 buffers are the same or not

bool verifyPassword(char *password);

void insertData(char data1[], char data2[]); // Transfer buffer 2 to buffer 1

void getData(); // get bufferbuffer from keyboard

void checkPass(); 

void openDoor();    // servo

void error();   // error if input Wrong too much

void changePass();

void resetPass(); 

void setup();

void loop();


void sendMessage(char* password, char*method) {
  StaticJsonDocument<200> jsonMessage;
  jsonMessage["modelId"] = modelId;
  jsonMessage["password"] = password;
  jsonMessage["method"] = method;

  char buffer[256];
  serializeJson(jsonMessage, buffer); // Convert JSON to string

  Serial.print("Publishing message: ");
  Serial.println(buffer);

  client.publish(publishTopic, buffer);  // Send message
  delay(5000); // Send each 5s
}

void callback(char* topic, byte* message, unsigned int length) {
  Serial.print("Received message from topic: ");
  Serial.println(topic);

  // Content of message
  Serial.print("Message: ");
  for (int i = 0; i < length; i++) {
    Serial.print((char)message[i]);
  }
  responseReceived = true;
  responseResult = (char*)message;
  Serial.println();

  // Perform message processing if necessary
}

void connectToMqttBroker() {
  while (!client.connected()) {
    lcd.clear();
    lcd.setCursor(1, 0);
    lcd.print("Connecting to MQTT");
    Serial.println("Connecting to MQTT Broker...");
    if (client.connect("ESP32Client")) {
      lcd.print("Connected to MQTT");
      Serial.println("Connected to MQTT Broker!");
      client.publish(subscribeTopic, "Hello from ESP32");
      if (client.subscribe(subscribeTopic)) {
        Serial.println("Subscribed to topic");
      } else {
        Serial.println("Failed to subscribe to topic");
      };
    } else {
      Serial.print("Failed to connect. State: ");
      Serial.println(client.state());
      delay(2000);
    }
  }
}

void connectToWiFi() {
  Serial.print("Connecting to WiFi");
  WiFi.begin(ssid, wifi_password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.print(".");
  }
  Serial.println("\nWiFi connected!");
  lcd.clear();
  lcd.setCursor(1, 0);
  lcd.print("WiFi connected!");
}

void writeEpprom(char data[])
{
    unsigned char i = 0;
    for (i = 0; i < 65; i++)
    {
        EEPROM.write(i, data[i]);
    }
    EEPROM.commit();
}

void readEpprom()
{
    unsigned char i = 0;
    for (i = 0; i < 6; i++)
    {
        password[i] = EEPROM.read(i);
    }
}

void clear_data_input() 
{
    int i = 0;
    for (i = 0; i < 7; i++)
    {
        data_input[i] = '\0';
    }
}

unsigned char isBufferdata(char data[]) 
{
    unsigned char i = 0;
    for (i = 0; i < 6; i++)
    {
        if (data[i] == '\0')
        {
            return 0;
        }
    }
    return 1;
}

bool compareData(char data1[], char data2[]) 
{
    unsigned char i = 0;
    for (i = 0; i < 6; i++)
    {
        if (data1[i] != data2[i])
        {
            return false;
        }
    }
    return true;
}


bool verifyPassword(char *password) {


  while (!responseReceived) {
    if (!client.connected()) {
      connectToMqttBroker();
    }

    // Gửi dữ liệu lên server
    sendMessage(password, "verify");
  }

  return responseResult == "true";
  
}

void insertData(char data1[], char data2[]) 
{
    unsigned char i = 0;
    for (i = 0; i < 6; i++)
    {
        data1[i] = data2[i];
    }
}

void getData() 
{
    char key = keypad.getKey(); // Read keyboard values
    if (key)
    {
        // Serial.println("key != 0");
        if (in_num == 0)
        {
            data_input[0] = key;
            lcd.setCursor(5, 1);
            lcd.print(data_input[0]);
            delay(200);
            lcd.setCursor(5, 1);
            lcd.print("*");
        }
        if (in_num == 1)
        {
            data_input[1] = key;
            lcd.setCursor(6, 1);
            lcd.print(data_input[1]);
            delay(200);
            lcd.setCursor(6, 1);
            lcd.print("*");
        }
        if (in_num == 2)
        {
            data_input[2] = key;
            lcd.setCursor(7, 1);
            lcd.print(data_input[2]);
            delay(200);
            lcd.setCursor(7, 1);
            lcd.print("*");
        }
        if (in_num == 3)
        {
            data_input[3] = key;
            lcd.setCursor(8, 1);
            lcd.print(data_input[3]);
            delay(200);
            lcd.setCursor(8, 1);
            lcd.print("*");
        }
        if (in_num == 4)
        {
            data_input[4] = key;
            lcd.setCursor(9, 1);
            lcd.print(data_input[4]);
            delay(200);
            lcd.setCursor(9, 1);
            lcd.print("*");
        }
           if (in_num == 5)
        {
            data_input[5] = key;
            lcd.setCursor(10, 1);
            lcd.print(data_input[5]);
            delay(200);
            lcd.setCursor(10, 1);
            lcd.print("*");
        }
        if (in_num == 5)
        {
            Serial.println(data_input);
            in_num = 0;
            delay(200);
            lcd.clear();
            lcd.setCursor(1, 0);
            lcd.print("Checking.....");
        }
        else
        {
            in_num++;
        }
    }
}

void checkPass() 
{
    getData();
    if (isBufferdata(data_input))
    {

         if (compareData(data_input, mode_changePass))
        {
            // Serial.print("mode_changePass");
            lcd.clear();
            clear_data_input();
            index_t = 1;
        }
        else if (compareData(data_input, mode_resetPass))
        {
            // Serial.print("mode_resetPass");
            lcd.clear();
            clear_data_input();
            index_t = 2;
        } else if (verifyPassword(data_input)) // Correct pass
        {
            lcd.clear();
            clear_data_input();
            index_t = 3;
        }
        else
        {
            if (error_pass == 2)
            {
                clear_data_input();
                lcd.clear();
                index_t = 4;
            }
            Serial.print("Error");
            lcd.clear();
            lcd.setCursor(1, 1);
            lcd.print("WRONG PASSWORD");
            clear_data_input();
            error_pass++;
            delay(1000);
            lcd.clear();
        }
    }
}

void openDoor()
{
    lcd.clear();
    lcd.setCursor(1, 0);
    lcd.print("---OPENDOOR---");
    sg90.write(180);
    delay(5000);
    sg90.write(0);
    lcd.clear();
    index_t = 0;
}

void error()
{
    lcd.clear();
    lcd.setCursor(1, 0);
    lcd.print("WRONG 3 TIME");
    lcd.setCursor(1, 1);
    lcd.print("Wait 1 minutes");
    unsigned char minute = 0;
    unsigned char i = 30;
    while (i > 0)
    {
        if (i == 1 && minute > 0)
        {
            minute--;
            i = 59;
        }
        if (i == 1 && minute == 0)
        {
            break;
        }
        i--;
        delay(1000);
    }
    lcd.clear();
    index_t = 0;
}

void changePass() 
{
    lcd.setCursor(0, 0);
    lcd.print("-- Change Pass --");
    delay(3000);
    lcd.setCursor(0, 0);
    lcd.print("--- New Pass ---");
    while (1)
    {
        getData();
        if (isBufferdata(data_input))
        {
            insertData(new_pass1, data_input);
            clear_data_input();
            break;
        }
    }
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("---- AGAIN ----");
    while (1)
    {
        getData();
        if (isBufferdata(data_input))
        {
            insertData(new_pass2, data_input);
            clear_data_input();
            break;
        }
    }
    delay(1000);
    if (compareData(new_pass1, new_pass2))
    {
        lcd.clear();
        lcd.setCursor(0, 0);
        lcd.print("--- Success ---");
        delay(1000);
        writeEpprom(new_pass2);
        insertData(password, new_pass2);
        lcd.clear();
        index_t = 0;
    }
    else
    {
        lcd.clear();
        lcd.setCursor(0, 0);
        lcd.print("-- Mismatched --");
        delay(1000);
        lcd.clear();
        index_t = 0;
    }
}

void resetPass()
{
    unsigned char choise = 0;
    // Serial.println("Pass reset");
    lcd.setCursor(0, 0);
    lcd.print("---Reset Pass---");
    getData();
    if (isBufferdata(data_input))
    {
        if (compareData(data_input, password))
        {
            lcd.clear();
            clear_data_input();
            while (1)
            {
                lcd.setCursor(0, 0);
                lcd.print("---Reset Pass---");
                char key = keypad.getKey();
                if (choise == 0)
                {
                    lcd.setCursor(0, 1);
                    lcd.print(">");
                    lcd.setCursor(2, 1);
                    lcd.print("YES");
                    lcd.setCursor(9, 1);
                    lcd.print(" ");
                    lcd.setCursor(11, 1);
                    lcd.print("NO");
                }
                if (choise == 1)
                {
                    lcd.setCursor(0, 1);
                    lcd.print(" ");
                    lcd.setCursor(2, 1);
                    lcd.print("YES");
                    lcd.setCursor(9, 1);
                    lcd.print(">");
                    lcd.setCursor(11, 1);
                    lcd.print("NO");
                }
                if (key == '*')
                {
                    if (choise == 1)
                    {
                        choise = 0;
                    }
                    else
                    {
                        choise++;
                    }
                }
                if (key == '#' && choise == 0)
                {
                    lcd.clear();
                    delay(1000);
                    writeEpprom(pass_def);
                    insertData(password, pass_def);
                    lcd.setCursor(0, 0);
                    lcd.print("---Reset ok---");
                    delay(1000);
                    lcd.clear();
                    break;
                }
                if (key == '#' && choise == 1)
                {
                    lcd.clear();
                    break;
                }
            }
            index_t = 0;
        }
        else
        {
            index_t = 0;
            lcd.clear();
        }
    }
}

void setup()
{
    Serial.begin(9600);
    sg90.setPeriodHertz(50); 
    sg90.attach(PIN_SG90, 500, 2400);
    lcd.init();
    lcd.backlight();
    lcd.print("   SYSTEM INIT   ");
    lcd.clear();
    lcd.setCursor(1, 0);
    lcd.print("CONNECTING WIFI");
    connectToWiFi();
    Serial.println(WiFi.localIP());
    lcd.clear();
    lcd.setCursor(1, 0);
    lcd.print("CONNECTING MQTT");
    client.setServer(mqttBroker, mqttPort);
    client.setCallback(callback);
    delay(2000);
    lcd.clear();

    Serial.print("PASSWORK: ");
    Serial.println(password);
}

void loop()
{
    // Reconnect if MQTT connection is lost
    if (!client.connected()) {
      connectToMqttBroker();
    }
    client.loop();
    lcd.setCursor(1, 0);
    lcd.print("Enter Password");
    checkPass();

    while (index_t == 1)
    {
        changePass();
    }

    while (index_t == 2)
    {
        resetPass();
    }

    while (index_t == 3)
    {
        openDoor();
        error_pass = 0;
    }

    while (index_t == 4)
    {
        error();
        error_pass = 0;
    }
}