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

        return image
    
    def rgb2gray(self,image):

        gray_image =  cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

        return gray_image
        
  
        
    def threshold(self,image,value1,value2,type_th):

        ret,thresh= cv2.threshold(image, value1, value2, type_th)
        return thresh

        
    def rotation2(self,image,degre_value):

        rotated = imutils.rotate_bound(image, degre_value)       
        return rotated
        
        
    def text_detected(self, image):
        
        try:
            from PIL import Image
        except ImportError:
            import Image
        import pytesseract
        pytesseract.pytesseract.tesseract_cmd = '/app/.apt/usr/bin/tesseract'
        try:
            text_from_image = pytesseract.image_to_string(image,lang='ara', timeout=2)
        except RuntimeError as timeout_error:
            text_from_image = ""
      
        sys.stdout.write("text detected --------------------------: " + text_from_image + "\n")
        

        return text_from_image
    
    
    def to_json(self,text_detected):
        data ={
    
        
        'text_detected' : text_detected
         }



        json_string = json.dumps(data,ensure_ascii=False)
        return json_string
        
    def base64_to_image(self, base64str):
        string = base64str
        decoded_data=base64.b64decode(string)
        np_data=np.frombuffer(decoded_data,np.uint8)
        img=cv2.imdecode(np_data,cv2.IMREAD_UNCHANGED)

        return img
        

    def handleRotation(self, base64str):
        image = self.base64_to_image(base64str)
        nbr_rots = 8
        angle=0
        d={}
        image_gray=self.rgb2gray(image)
        image_thresh=self.threshold(image_gray,162,255,cv2.THRESH_BINARY)
        for x in range(nbr_rots):
            image0=self.rotation2(image_thresh,angle)
            text_det=self.text_detected(image0)
            d[angle]=[len(text_det),text_det]
            angle+= (360 / nbr_rots)
        max_angle = max(d, key=d.get)
      
        
        sys.stdout.write("handle rotation 1-")
        text_det_max=d[max_angle][1]
        sys.stdout.write("handle rotation 2-")
        json_detected=self.to_json(text_det_max)
   
        
       
        return json_detected
    
    


    
