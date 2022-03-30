import cv2
import numpy as np 
import imutils
import io
import base64
from PIL import Image
import json
import pytesseract
import sys


class ImageProcessing :
    
    def __init__(self,name):
        self.name=name
  
    def image_read(self,path):
        image=cv2.imread(path)
##        cv2.imshow('Hello word',image)
##        cv2.waitKey(0)
##        cv2.destroyAllWindows()
        return image
    
    def rgb2gray(self,image):
##        filepath=fr'./images/{self.name}.jpg'.format(self.name)
##        print(filepath)
        gray_image =  cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
##        cv2.imshow('Gray image', gray_image)
##        cv2.imwrite(filepath, gray_image)
##        cv2.waitKey(0)
##        cv2.destroyAllWindows()
        return gray_image
        
  
        
    def threshold(self,image,value1,value2,type_th):
##        filepath=fr'./images/{self.name}.jpg'.format(self.name)
##        print(filepath)
        ret,thresh= cv2.threshold(image, value1, value2, type_th)
##        cv2.imshow('Threshold',thresh)
##        cv2.imwrite(filepath, thresh)
##        cv2.waitKey(0)
##        cv2.destroyAllWindows()
        
        
    def adaptive(self,image,value1,methode,type_th,value2,value3):
##        filepath=fr'./images/{self.name}.jpg'.format(self.name)
##        print(filepath)
        thresh = cv2.adaptiveThreshold(image, value1, methode, 
                               type_th, value2, value3) 
##        cv2.imshow("Adaptive Thresholding", thresh) 
##        cv2.imwrite(filepath, thresh)
##        cv2.waitKey(0) 
##        cv2.destroyAllWindows()
        
    def CannyEdge(self,image,value1,value2):
##        filepath=fr'./images/{self.name}.jpg'.format(self.name)
##        print(filepath)
        canny = cv2.Canny(image,20,170)
        
##        cv2.imshow('Canny', canny)
##        cv2.imwrite(filepath, canny)
##        cv2.waitKey(0)
##
##        cv2.destroyAllWindows()
        
    def rotation2(self,image,degre_value):
##        filepath=fr'./images/{self.name}.jpg'.format(self.name)
        rotated = imutils.rotate_bound(image, degre_value)
        #cv2.imshow("Rotated Degrees", rotated)
##        cv2.imwrite(filepath, rotated)
        #cv2.waitKey()
        #cv2.destroyAllWindows()
        return rotated
        

        
    def scaling(self,image,fxvalue,fyvalue,type_inter):
##        filepath=fr'./images/{self.name}.jpg'.format(self.name)
        image_scaled = cv2.resize(image, None, fx=fxvalue, fy=fyvalue,interpolation=type_inter)
##        cv2.imshow('Scaling - Linear Interpolation', image_scaled) 
##        cv2.imwrite(filepath, image_scaled)
##        cv2.waitKey()
##        cv2.destroyAllWindows()
        return image_scaled
    
    def cropping(self,image,body):
        
##        filepath=fr'./images/{self.name}.jpg'.format(self.name)
##        cv2.imshow("Body", body)
##        cv2.waitKey(0)
        pass
        
    def translation(self,image,value_trans1,value_trans2):
##        filepath=fr'./images/{self.name}.jpg'.format(self.name)
        shifted = imutils.translate(image, value_trans1, value_trans2)
##        cv2.imshow("Shifted Down", shifted)
##        cv2.imwrite(filepath, shifted)
##        cv2.waitKey(0)
        
    def Blur(self,image):
##        filepath=fr'./images/{self.name}.jpg'.format(self.name)
##        print(filepath)
        image_blur = cv2.GaussianBlur(image, (3, 3), 0)
##        cv2.imwrite(filepath, image_blur)
##        cv2.waitKey(0) 
##        cv2.destroyAllWindows()
        
        
    def text_detected(self, image):
##        filepath=fr'./images/{self.name}.jpg'.format(self.name)
        sys.stdout.write('text_detected: 1')
        try:
            from PIL import Image
        except ImportError:
            import Image
        import pytesseract
        sys.stdout.write('text_detected: 2')
        pytesseract.pytesseract.tesseract_cmd = '/app/.apt/usr/bin/tesseract'
        sys.stdout.write('text_detected: 3')
        ##tessdata_dir_config=r'--tessdata-dir "C:/Users/pc/anaconda3/envs/tesseract/tessdata"''
        try:
            text_from_image = pytesseract.image_to_string(image,lang='ara', timeout=2.5)
        except RuntimeError as timeout_error:
            text_from_image = ""
        sys.stdout.write('text_detected: 4\n')
        #print(type(text_from_image))
        sys.stdout.write("text detected --------------------------: " + text_from_image + "\n")
        #text_strip=text_from_image.strip('\n')
        #print(text_strip)
        #print(len(text_strip))
        

        return text_from_image
    
    
    def to_json(self,text_detected):
        data ={
    
        
        'text_detected' : text_detected
         }
