###########server code############

import socket 
import RPi.GPIO as GPIO
from time import sleep
import time
import speech_recognition as sr

from PIL import Image, ImageDraw, ImageFont

import threading
Nanumfont=ImageFont.truetype("NanumSquareRoundB.ttf",20)

from luma.core.interface.serial import i2c
from luma.core.render import canvas
from luma.oled.device import ssd1306, ssd1325, ssd1331, sh1106

import spidev #raspberry pi



import time

import random

#스위치, LED
GPIO.setwarnings(False)


Button= 18
LED=25
GPIO.setmode(GPIO.BCM)
GPIO.setup(Button, GPIO.IN)
GPIO.setup(LED,GPIO.OUT)


LCD=13 
#echo=19

GPIO.setup(LCD,GPIO.OUT)
#GPIO.setup(echo,GPIO.IN)


spi=spidev.SpiDev() 
spi.open(0,0) 



serial=i2c(port=1,address=0x3C)
device=ssd1306(serial)
device=ssd1306(serial,rotate=0)


#GPIO.setmode(GPIO.BOARD)
soundpin = 4
GPIO.setup(soundpin,GPIO.IN)
over_db = 0 

HOST = "192.168.0.15" 

 

PORT = 9000

 

BUFSIZE = 1024 #버퍼사이즈 설정

 

MAX_BUF = 1024

 

global data
global data_2
global re_data

global send_time
global recv_time
global wording
 
send_time=999999999999999

def time_out():
    global send_time
    global recv_time
    global re_data

    
    re_data="s"    
    while (True):
        recv_time=time.time()
        wait_time=recv_time-send_time
        #print("wait time :", wait_time)
        
        
        if (wait_time>=60):
            print("redata: ",re_data)
            if("s" in re_data):
                #re_data="s"
                sos_data="timeout"
                client_socket.send(sos_data.encode("utf-8"))
                print("timeout")
                re_data="1"
                GPIO.output(LED,True)
                with canvas(device) as draw:
                    draw.rectangle(device.bounding_box,outline="white",fill="black") #backgraoud
                    draw.text((17,25),"도와주세요",fill="white",font = Nanumfont,)
                
                print("exit")
                #quit()
                client_socket.close()
                sensor_thread.close()
                voice_thread.close()
                send_time=9999999999999
                
            else:
                send_time=9999999999
        else:
            continue
        


def voice():
    while True:
        global data_2
        global wording
        global send_time
        r =sr.Recognizer()
        with sr.Microphone() as source:
            print('Speak Anything: ')
            audio =r.listen(source)
            
        try:
            text = r.recognize_google(audio,language='ko-KR')
            print('you said : {}'.format(text))
            if text.find(wording)>=0:
                data_2 = 'help'
                client_socket.send(data_2.encode("utf-8"))  #클라이언트에게 데이터를 전송
                send_time=time.time()
                print("send : ",data_2)
                data_2=""
                print('need help')
                
                
        except:
            print('sry')


def sensor():
    while(True):
        global data
        global send_time
        data=""
        soundlevel = GPIO.input(soundpin)
        #print ("soundlevel", soundlevel)
        if GPIO.input(Button)== 0:
            data = "help"
            client_socket.send(data.encode("utf-8"))  #클라이언트에게 데이터를 전송
            send_time=time.time()
            print("send : ",data)
            print("Button ON")
            time.sleep(2)
            data=""
            continue
        if(soundlevel == 1):
            start = time.time()
            over_db = 1
            
            print (" timer start")
            while(time.time() - start < 30 ):
                soundlevel = GPIO.input(soundpin)
                
                if(soundlevel == 1):
                    over_db = over_db + 1
                    print ("over Desibel",over_db)
                    data="sos"
                if( over_db  == 2 ):
                    print ("Dangerous")
                    data="help"
                    client_socket.send(data.encode("utf-8"))  #클라이언트에게 데이터를 전송
                    send_time=time.time()
                    print("send : ",data)
                    
                    time.sleep(0.1)
                    data=""
                    break
                time.sleep(0.5)
        time.sleep(0.5)


def receive():
    global recv_time
    global send_time
    global re_data
    global data
    global data_2
    
    
    re_data="s"

    while True:

        recv_data = client_socket.recv(BUFSIZE)

        re_data=recv_data.decode("utf-8")

        print("\n recv : ",re_data,"\n ")
        
       
        if "1" in re_data:
            re_data="1"
            GPIO.output(LED,True)
            with canvas(device) as draw:
                draw.rectangle(device.bounding_box,outline="white",fill="black") #backgraoud
                
                draw.text((17,25),"도와주세요",fill="white",font = Nanumfont,)
                #draw.text((50,25),"help me",fill="white")
            print("exit")
            quit()
            client_socket.close()

            voice_thread.close()
            
        elif ("0" in re_data):
            re_data="s"
            data=""
            data_2=""
            send_time=99999999999
        else:
            
            data=""
            data_2=""
            

 

 

    #양방향 통신을 위한 서버와 클라이언트 연결과정

server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

 

server_socket.bind((HOST,PORT))

 

server_socket.listen(3)


while True:

    
    try:
        GPIO.output(LED,False)
        #client_socket, addr = server_socket.accept()

        
        
        
        client_socket, addr = server_socket.accept()
        word = client_socket.recv(BUFSIZE)
        print("Connected by", addr)
        word=word.decode("utf-8")
        wording = word.strip()
        print("\n word : ",wording,"\n ")
        
        

        recv_thread = threading.Thread(target = receive)
        recv_thread.start()
        
        timeout_thread=threading.Thread(target=time_out)
        timeout_thread.start()
        voice_thread = threading.Thread(target=voice)
        voice_thread.start()

        sensor_thread = threading.Thread(target=sensor)
        sensor_thread.start()

        
        if "1" in re_data:
            print("나가")
            quit()

    except: #Ctrl+c 누르면 종료
        GPIO.output(LED,False)
        GPIO.cleanup()
        client_socket.close()

        exit()
        
