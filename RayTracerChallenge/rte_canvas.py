from rte_math import *
from PIL import Image
import numpy as np

class Canvas:
    def __init__(self, width, height):
        self.width = width
        self.height = height
        self.pixels = np.empty((width, height, 3), dtype=float)
        self.pixels_mapped = np.empty((width, height, 3), dtype=np.uint8)
        self.img = None

    # note valid range for 256 by 256 canvas is [0-255],[0-255]
    def write_pixel(self, x, y, p):
        if x < self.width and y < self.height and x >= 0 and y >= 0:
            self.pixels[x][y] = p # our color system uses rgb between 0 and 1

    def pixel_at(self, x, y):
        if not (x < self.width and y < self.height and x >= 0 and y >= 0):
            print("Trying to access pixel value outside of Canvas range:", x, y)
            return None
        
        return tuple(self.pixels[x][y])

    # actually creates image, populates pixels_mapped with values between 0 and 255
    # NOTE: it would be more efficient to simply update a mapped array while writing to the regular array
    def create_image(self):
        for x in range(self.width):
            for y in range(self.height):
                p = self.pixels[x][y]
                p[0] = int(np.floor(p[0]*255))
                p[1] = int(np.floor(p[1]*255))
                p[2] = int(np.floor(p[2]*255))
                self.pixels_mapped[x][y] = p

        self.img = Image.fromarray(self.pixels_mapped)

    # shows image in window
    def show(self):
        if self.img == None:
            print("Please call 'Canvas.create_image' first")
        else:
            self.img.show()

    # saves image TODO
    #def save(filepath):
